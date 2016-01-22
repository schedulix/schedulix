#
# Common description and properties of the schedulix packages
#
Name:		schedulix
Version:	2.6.1
Release:	2%{?dist}
Summary:	schedulix is an open source enterprise job scheduling system

Group:		Applications/System
License:	AGPL
URL:		http://www.schedulix.org
Source0:	file://localhost/%{_topdir}/SOURCES/schedulix-%{version}.tgz

Vendor:		independIT Integrative Technologies GmbH
Packager:	Ronald Jeninga <ronald.jeninga@schedulix.org>

BuildRequires:	jflex jna gcc-c++ java-1.7.0-openjdk-devel rpm-build rpm-libs rpmdevtools rpm-sign

# disable debug package
%global debug_package %{nil}

%define zope2version 2.13.22

#
# this description will be the first part of every package description
# any specialties regarding the specific package will follow this common description
#
%define commonDescription \
schedulix is a production proof open source enterprise job scheduling system. \
It consists of a central scheduling server and several agents called jobservers,  \
as well as a Zope application server which provides the access to the system \
by a standard web browser. \
For operation schedulix requires an installed RDBMS and a suitable JDBC driver. \
The server package to install should reflect the RDBMS that is installed. \
 \
On installation a user called 'schedulix' is created. The password is set to \
'schedulix' (same as user name) and should be changed after installation. \
If one of the server packages is installed, it will restart the required DBMS. \
This might cause side effects if some other software that uses the database \
system is running.

#
# In case of a server installation there are a few important notes that
# are true independent of the exact server package installed
#
%define serverNotes \
Within the scheduling system there will be a user called 'SYSTEM' with password 'G0H0ME' \
installed. It is advisable to change the password as soon as possible.  This is done by \
changing the SysPasswd property in the /opt/schedulix/etc/server.conf file. \
Afterwards it will be a good idea to change the corresponding entry in the ~/.sdmshrc \
file too. \
 \
Another issue can be firewall related. In order to be able to access the scheduling \
server you might need to add a rule to the iptables like: \
 \
-A INPUT -p tcp -m state --state NEW -m tcp --dport 2506 -j ACCEPT \
 \
This will make the port 2506 accessible from other computers. \
(We don't do this, because we don't want to automagically introduce holes into \
your security concept). \


%description
%commonDescription

%package base
# ----------------------------------------------------------------------------------------
#
# base package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix base package installs all files that are used both by the server and the client
Group:			Applications/System
Requires:		java-1.7.0-openjdk jna

%description base
%commonDescription

The schedulix base package provides the files that are used by most other packages

%pre base
if [ ! -d /opt/schedulix ]; then \
	mkdir -p /opt/schedulix; \
	chmod 755 /opt/schedulix; \
fi; \
if ! grep schedulix /etc/passwd >/dev/null 2>&1; then \
	useradd schedulix -d /opt/schedulix -m -s /bin/bash -U -p "RJteetpJ9UFeQ"; \
fi; \
chown schedulix.schedulix /opt/schedulix


%post base
# we basically write the configuration files bicsuite.conf, java.conf and a settings file here
sed '
s!JNAJAR=.*!JNAJAR=/usr/share/java/jna.jar!
s!SWTJAR=.*!SWTJAR=/usr/share/java/swt.jar!' < /opt/schedulix/schedulix-%{version}/etc/java.conf.template > /opt/schedulix/etc/java.conf

cp /opt/schedulix/schedulix-%{version}/etc/bicsuite.conf.template /opt/schedulix/etc/bicsuite.conf
echo '
BICSUITEHOME=/opt/schedulix/schedulix
BICSUITECONFIG=/opt/schedulix/etc
BICSUITELOGDIR=/opt/schedulix/log
PATH=$BICSUITEHOME/bin:$PATH
export BICSUITEHOME BICSUITECONFIG BICSUITELOGDIR PATH
' > /opt/schedulix/etc/SETTINGS

# make the three files readable for world
chown schedulix.schedulix /opt/schedulix/etc/bicsuite.conf /opt/schedulix/etc/java.conf /opt/schedulix/etc/SETTINGS
chmod 644 /opt/schedulix/etc/bicsuite.conf /opt/schedulix/etc/java.conf /opt/schedulix/etc/SETTINGS

echo '
# source schedulix environment
. /opt/schedulix/etc/SETTINGS' >> ~schedulix/.bashrc
chown schedulix.schedulix ~schedulix/.bashrc

cd /opt/schedulix
if [ -L schedulix ]; then
	rm schedulix
fi
su schedulix -c "ln -s schedulix-%{version} schedulix"

%postun base
userdel schedulix
rm -rf /var/spool/mail/schedulix
rm -rf /opt/schedulix

%files base
%ghost %attr(-, schedulix, schedulix) /opt/schedulix/schedulix
%defattr(644, schedulix, schedulix, 755)
%dir %attr(755, schedulix, schedulix) /opt/schedulix
%dir %attr(755, schedulix, schedulix) /opt/schedulix/etc
%dir %attr(755, schedulix, schedulix) /opt/schedulix/log
%dir %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}
%dir %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc
%dir %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin
%dir %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib
%doc %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/AGPL.TXT
%doc %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/README.md
     %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/scrolllog
     %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/sdmsctl
     %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/sdmsh
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc/bicsuite.conf.template
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc/java.conf.template
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/BICsuite.jar
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/functions.sh
%ghost %config %attr(644, schedulix, schedulix) /opt/schedulix/etc/bicsuite.conf
%ghost %config %attr(644, schedulix, schedulix) /opt/schedulix/etc/java.conf 
%ghost %config %attr(644, schedulix, schedulix) /opt/schedulix/etc/SETTINGS
#
# exclude this spec file as it isn't required in any binary package
#
%exclude /opt/schedulix/schedulix-%{version}/lib/centos7.spec

%package server-pg
# ----------------------------------------------------------------------------------------
#
# server + postgresql package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix server pg package installs a schedulix server based on an underlying Postgres RDBMS
Group:			Applications/System
Requires:		schedulix-base >= %{version} postgresql-server postgresql-jdbc
Provides:		schedulix-server %{version}
Conflicts:		schedulix-server-mariadb

%description server-pg
%commonDescription

The schedulix server pg package installs a schedulix server based on an underlying Postgres RDBMS.
It loads the convenience package, but does not load the examples.

%serverNotes

The configuration file will be changed. Instead of the 'ident' method we need the 'md5' method
in order to be able to connect by jdbc.


%post server-pg
# create a valid server.conf
echo "creating server.conf file"
HOSTNAME=`hostname`
su - schedulix -c "
. ~/.bashrc;
sed '
	s:DbPasswd=.*:DbPasswd=schedulix:
	s!DbUrl=.*!DbUrl=jdbc:postgresql:schedulixdb!
	s:DbUser=.*:DbUser=schedulix:
	s:Hostname=.*:Hostname=$HOSTNAME:
	s:JdbcDriver=.*:JdbcDriver=org.postgresql.Driver:
' < /opt/schedulix/schedulix/etc/server.conf.template > /opt/schedulix/etc/server.conf;
chmod 600 /opt/schedulix/etc/server.conf;
cp /opt/schedulix/etc/java.conf /tmp/$$.tmp;
sed '
	s:JDBCJAR=.*:JDBCJAR=/usr/share/java/postgresql-jdbc.jar:
' < /tmp/$$.tmp > /opt/schedulix/etc/java.conf;
rm -f /tmp/$$.tmp
"

# if this is a new postgresql installation, we'll have to do an initdb first
if [ ! -d /var/lib/pgsql ]; then
	postgresql-setup initdb
fi

echo "creating database user"
su - postgres -c 'echo "create user schedulix with password '"'schedulix'"' createdb;" | psql'

# write the password file in order to be able to connect without a password prompt
echo "127.0.0.1:5432:schedulixdb:schedulix:schedulix" > /opt/schedulix/.pgpass

# modify /var/lib/pgsql/data/pg_hba.conf in order to allow jdbc connects
sed --in-place=.save '
     s!^host *all * all *127.0.0.1/32 *.*!host    all             all             127.0.0.1/32            md5!
     s!^host *all * all *::1/128 *.*!host    all             all             ::1/128                 md5!
' /var/lib/pgsql/data/pg_hba.conf

# we now restart the DBMS to make our config change effective
if ps -fu postgres | grep 'postgres: checkpointer process'; then
	service postgresql restart
else
	service postgresql start
fi

echo "populating database"
su - schedulix -c '
	. ~/.bashrc;
	cd $BICSUITEHOME/sql
	createdb schedulixdb
	psql -f pg/install.sql schedulixdb
	ret=$?
	if [ $ret != 0 ]
	then
		echo "Error initializing repository database schedulixdb -- exit code $ret"
		exit 1
	fi
'

echo "Setting up /opt/schedulix/.sdmshrc"
su - schedulix -c "
echo 'User=SYSTEM
Password=G0H0ME
Timeout=0' > /opt/schedulix/.sdmshrc
chmod 600 /opt/schedulix/.sdmshrc
"

echo "Setting up /opt/schedulix/etc/sdmshrc"

su - schedulix -c "
echo 'Host=$HOSTNAME
Port=2506' > /opt/schedulix/etc/sdmshrc
chmod 644 /opt/schedulix/etc/sdmshrc
"

echo "Loading convenience package"
su - schedulix -c "
. ~/.bashrc;
/opt/schedulix/schedulix/bin/server-start;
sdmsh < /opt/schedulix/schedulix/install/convenience.sdms;
"



%preun server-pg
echo "Stopping server ..."
su - schedulix -c "
. ~/.bashrc;
/opt/schedulix/schedulix/bin/server-stop;
"

%postun server-pg
echo "Dropping database"
su - schedulix -c "dropdb schedulixdb"
su - postgres -c 'echo "drop user schedulix;" | psql'



%files server-pg
%defattr(644, schedulix, schedulix, 755)
%dir /opt/schedulix/schedulix-%{version}/sql
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-restart
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-start
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-stop
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/server.conf.template
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/MASTER_STATE.sql
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/REPOSITORY_LOCK.sql
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/init.sql
%dir %attr(0755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/install
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/convenience.sdms
/opt/schedulix/schedulix-%{version}/sql/pg
/opt/schedulix/schedulix-%{version}/sql/pg_gen
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/etc/server.conf
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/.sdmshrc



%package server-mariadb
# ----------------------------------------------------------------------------------------
#
# server + mariadb/mysql package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix server mariadb package installs a schedulix server based on an underlying MariaDB od MySQL RDBMS
Group:			Applications/System
# Requires: schedulix-base mysql-server mysql-connector-java
Requires:		schedulix-base >= %{version} mariadb mariadb-libs mariadb-server mysql-connector-java
Provides:		schedulix-server %{version}
Conflicts:		schedulix-server-pg

%description server-mariadb
%commonDescription

The schedulix server mariadb package installs a schedulix server based on an
underlying MariaDB or MySQL RDBMS.
A DBMS user 'schedulix' with password 'schedulix' will be created, which will
create a database called 'schedulixdb'.

It will load the convenience package, but does not load the examples.
(use the schedulix-examples package to do so).

%serverNotes

%post server-mariadb
echo "creating server.conf file"
HOSTNAME=`hostname`
su - schedulix -c "
. ~/.bashrc;
sed '
	s:DbPasswd=.*:DbPasswd=schedulix:
	s:DbUrl=.*:DbUrl=jdbc\:mysql\:///schedulixdb:
	s:DbUser=.*:DbUser=schedulix:
	s:Hostname=.*:Hostname=$HOSTNAME:
	s:JdbcDriver=.*:JdbcDriver=com.mysql.jdbc.Driver:
' < /opt/schedulix/schedulix/etc/server.conf.template > /opt/schedulix/etc/server.conf;
chmod 600 /opt/schedulix/etc/server.conf;
cp /opt/schedulix/etc/java.conf /tmp/$$.tmp;
sed '
	s:JDBCJAR=.*:JDBCJAR=/usr/share/java/mysql-connector-java.jar:
' < /tmp/$$.tmp > /opt/schedulix/etc/java.conf;
rm -f /tmp/$$.tmp
"

echo "creating database"
service mariadb start
mysql --user=root << ENDMYSQL
create user schedulix@localhost identified by 'schedulix';
create database schedulixdb;
grant all on schedulixdb.* to schedulix;
quit
ENDMYSQL

echo "populating database"
su - schedulix -c '
	. ~/.bashrc;
	cd $BICSUITEHOME/sql
	mysql --user=schedulix --password=schedulix --database=schedulixdb --execute "source mysql/install.sql"
	ret=$?
	if [ $ret != 0 ]
	then
		echo "Error initializing repository database schedulixdb -- exit code $ret"
		exit 1
	fi
'

echo "Setting up /opt/schedulix/.sdmshrc"

su - schedulix -c "
echo 'User=SYSTEM
Password=G0H0ME
Timeout=0' > /opt/schedulix/.sdmshrc
chmod 600 /opt/schedulix/.sdmshrc
"

echo "Setting up /opt/schedulix/etc/sdmshrc"

su - schedulix -c "
echo 'Host=$HOSTNAME
Port=2506' > /opt/schedulix/etc/sdmshrc
chmod 644 /opt/schedulix/etc/sdmshrc
"

echo "Loading convenience package"
su - schedulix -c "
. ~/.bashrc;
/opt/schedulix/schedulix/bin/server-start;
sdmsh < /opt/schedulix/schedulix/install/convenience.sdms;
"



%preun server-mariadb
echo "Stopping server ..."
su - schedulix -c "
. ~/.bashrc;
/opt/schedulix/schedulix/bin/server-stop;
"

%postun server-mariadb
echo "Dropping database"
mysql --user=root << ENDMYSQL
drop user schedulix@localhost;
drop database schedulixdb;
quit
ENDMYSQL


%files server-mariadb
%defattr(644, schedulix, schedulix, 755)
%dir /opt/schedulix/schedulix-%{version}/sql
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-restart
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-start
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-stop
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/server.conf.template
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/MASTER_STATE.sql
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/REPOSITORY_LOCK.sql
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/init.sql
%dir %attr(0755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/install
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/convenience.sdms
/opt/schedulix/schedulix-%{version}/sql/mysql
/opt/schedulix/schedulix-%{version}/sql/mysql_gen
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/etc/server.conf
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/.sdmshrc

#
# exclude ingres sql files for now
#
%exclude   /opt/schedulix/schedulix-%{version}/sql/ing
%exclude   /opt/schedulix/schedulix-%{version}/sql/ing_gen

%package client
# ----------------------------------------------------------------------------------------
#
# client package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix client package installs everything needed to setup a jobserver
Group:			Applications/System
Requires:		schedulix-base >= %{version}

%description client
%commonDescription

The schedulix client package installs everything needed to setup a jobserver

%files client
%defattr(0644, schedulix, schedulix, 0755)
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/jobexecutor
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/jobserver-run
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-auto_restart
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-get_variable
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-rerun
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-set_state
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-set_variable
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-set_warning
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-submit
%dir %attr(0755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/Images
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/Images/Bullit.png
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/Images/Logo.png
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/SDMSpopup.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/dynsubmit.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/start_example_jobservers.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/stop_example_jobservers.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/watch.sh
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/jobserver.conf.template
%dir %attr(777, schedulix, schedulix) /opt/schedulix/taskfiles

%package zope
# ----------------------------------------------------------------------------------------
#
# zope FE package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix zope package installs the zope application server and configures it to access a locally installed server
Group:			Applications/System
Requires:		schedulix-base >= %{version} python python-devel python-setuptools python-virtualenv wget

%description zope
%commonDescription

The schedulix zope package installs the zope application server and configures 
it to access a locally installed server.
Note: installing this package requires a working Internet connection as the Zope 
software will be downloaded from http://download.zope.org.

The initial user is 'sdmsadm' with a password that equals the user name.
The Zope server will listen on port 8080.

Due to the nature of the Zope software, there will be some messages like

warning: no previously-included files matching '*.dll' found anywhere in distribution
warning: no previously-included files matching '*.pyc' found anywhere in distribution
warning: no previously-included files matching '*.pyo' found anywhere in distribution
warning: no previously-included files matching '*.so' found anywhere in distribution

which can be ignored. (Or, even better, tell me how to circumvent these).

Another issue can be firewall related. In order to be able to access the Zope
server you might need to add a rule to the iptables like:

-A INPUT -p tcp -m state --state NEW -m tcp --dport 8080 -j ACCEPT

This will make the port 8080 accessible from other computers.
(We don't do this, because we don't want to automagically introduce holes into
your security concept).

%post zope
echo "fetching zope"
su schedulix -c "mkdir -p /opt/schedulix/software"
cd /opt/schedulix/software
su schedulix -c "virtualenv --no-site-packages Zope"
if [ $? != 0 ]; then
	echo "Error creating python virtualenv environment"
	exit 1
fi
cd /opt/schedulix/software/Zope
su schedulix -c "bin/easy_install -i http://download.zope.org/Zope2/index/%{zope2version} Zope2"
ret=$?
if [ $ret != 0 ]; then
	echo "Error during easy_install of Zope2 version %{zope2version}"
	exit 1
fi

echo "building zope instance"
su schedulix -c "bin/mkzopeinstance -d /opt/schedulix/schedulixweb -u sdmsadm:sdmsadm"
ret=$?
if [ $ret != 0 ]; then
	echo "Error creating Zope instance for schedulix!web"
	exit 1
fi

echo "loading application"
su schedulix -c "
cd /opt/schedulix/schedulixweb;
mkdir Extensions;
cd Extensions;
ln -s /opt/schedulix/schedulix/zope/*.py .;
cd ../Products;
ln -s /opt/schedulix/schedulix/zope/BICsuiteSubmitMemory .;
cd ../import;
ln -s /opt/schedulix/schedulix/zope/SDMS.zexp .;
cd ..;
"

su schedulix -c "/opt/schedulix/schedulixweb/bin/zopectl start"
ret=$?
if [ $ret != 0 ]; then
	echo "Error starting Zope instance"
	exit 1	
fi
sleep 5

CNT=0
QUIET="--quiet"
while [ $CNT -lt 5 ]; do
	wget $QUIET --user=sdmsadm --password=sdmsadm --output-document=/dev/null "http://localhost:8080/manage_importObject?file=SDMS.zexp"
	ret=$?
	if [ $ret != 0 ]; then
		echo "Error importing SDMS.zexp"
		QUIET="--verbose"
	else
		break;
	fi
	CNT=`expr $CNT + 1`
done
rm -f cookies.txt

wget --quiet --user=sdmsadm --password=sdmsadm --output-document=/dev/null --keep-session-cookies --save-cookies cookies.txt "http://localhost:8080/SDMS/Install?manage_copyObjects:method=Copy&ids:list=User&ids:list=Custom"
ret=$?
if [ $ret != 0 ]; then
	echo "Error copying User and Custom Zope template folders from Zope /SDMS/install"
	exit 1
fi

wget --quiet --user=sdmsadm --password=sdmsadm --output-document=/dev/null --load-cookies cookies.txt "http://localhost:8080?manage_pasteObjects:method=Paste"
ret=$?
if [ $ret != 0 ]; then
	echo "Error pasting User and Custom Zope folders into Zope"
	exit 1
fi
rm -f cookies.txt

%preun zope
su schedulix -c "/opt/schedulix/schedulixweb/bin/zopectl stop"

%postun zope
rm -rf /opt/schedulix/software
rm -rf /opt/schedulix/schedulixweb


%files zope
%defattr(0644, schedulix, schedulix, 0755)
%dir /opt/schedulix/schedulix-%{version}/zope
%dir /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory
# we skip the compiled python files. Doesn't really make sense to compile them on the source system
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/SDMS.zexp
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory/BICsuiteSubmitMemory.py
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/patch.sh
%exclude   /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory/BICsuiteSubmitMemory.pyc
%exclude   /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory/BICsuiteSubmitMemory.pyo
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory/__init__.py
%exclude   /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory/__init__.pyc
%exclude   /opt/schedulix/schedulix-%{version}/zope/BICsuiteSubmitMemory/__init__.pyo
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/bicsuite_tx.py
%exclude   /opt/schedulix/schedulix-%{version}/zope/bicsuite_tx.pyc
%exclude   /opt/schedulix/schedulix-%{version}/zope/bicsuite_tx.pyo
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/myeval.py
%exclude   /opt/schedulix/schedulix-%{version}/zope/myeval.pyc
%exclude   /opt/schedulix/schedulix-%{version}/zope/myeval.pyo
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope/sdms.py
%exclude   /opt/schedulix/schedulix-%{version}/zope/sdms.pyc
%exclude   /opt/schedulix/schedulix-%{version}/zope/sdms.pyo
%ghost %attr(0755, schedulix, schedulix) /opt/schedulix/software
%ghost %attr(0755, schedulix, schedulix) /opt/schedulix/schedulixweb

%package examples
# ----------------------------------------------------------------------------------------
#
# Examples package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix examples package installs a few local jobservers and loads a bunch of examples into the system
Group:			Applications/System
Requires:		schedulix-base >= %{version} schedulix-server >= %{version} schedulix-client eclipse-swt

%description examples
%commonDescription

The schedulix examples package installs a few local jobservers and loads a bunch of examples into the system.
The logs can be found in the $BICSUITEHOME/install directory.
It is assumed that a valid .sdmshrc file is installed in $HOME.

%post examples
su - schedulix -c '
. ~/.bashrc;
cd $BICSUITEHOME/install;
./setup_example_jobservers.sh > setup_example_jobservers.log 2>&1;
sdmsh < setup_examples.sdms > setup_examples.log 2>&1
'

%preun examples
cd /opt/schedulix/schedulix/install
rm -f *.log

%files examples
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/host_1.conf.template
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/host_2.conf.template
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/localhost.conf.template
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/etc/host_1.conf
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/etc/host_2.conf
%ghost %config %attr(0600, schedulix, schedulix) /opt/schedulix/etc/localhost.conf
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/setup_example_jobservers.sh
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/setup_examples.sdms

%package doc
# ----------------------------------------------------------------------------------------
#
# documentation package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix doc package installs the schedulix documentation
Group:			Documentation

%description doc
%commonDescription

The schedulix doc package installs the schedulix documentation.

%files doc
%defattr(0644, root, root, 0755)
%dir /usr/share/doc/schedulix-%{version}
%doc /usr/share/doc/schedulix-%{version}/installation_de.pdf
%doc /usr/share/doc/schedulix-%{version}/installation_en.pdf
%doc /usr/share/doc/schedulix-%{version}/online_de.pdf
%doc /usr/share/doc/schedulix-%{version}/online_en.pdf
%doc /usr/share/doc/schedulix-%{version}/syntax_de.pdf
%doc /usr/share/doc/schedulix-%{version}/syntax_en.pdf



# ----------------------------------------------------------------------------------------
#
# rpm build scripts
#
# ----------------------------------------------------------------------------------------

%prep
%setup -q


%build
SDMSHOME=`pwd`
cd src
make 
cd ..


%install
echo "starting the installation of schedulix"
mkdir -p %{buildroot}/opt/schedulix/schedulix-%{version}
cp -r bin etc install lib sql zope AGPL.TXT README.md %{buildroot}/opt/schedulix/schedulix-%{version}
mkdir %{buildroot}/opt/schedulix/etc
mkdir %{buildroot}/opt/schedulix/bin
mkdir %{buildroot}/opt/schedulix/log
mkdir %{buildroot}/opt/schedulix/taskfiles
mkdir -p %{buildroot}/usr/share/doc/schedulix-%{version}
cp doc/* %{buildroot}/usr/share/doc/schedulix-%{version}


echo "End of the installation of schedulix"


%changelog

