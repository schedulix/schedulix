#
# Common description and properties of the schedulix packages
#
Name:		schedulix
Version:	2.9
Release:	24%{?dist}
Summary:	schedulix is an open source enterprise job scheduling system

Group:		Applications/System
License:	AGPLv1
URL:		http://www.schedulix.org
Source0:	file://localhost/%{_topdir}/SOURCES/schedulix-%{version}.tgz

Vendor:		independIT Integrative Technologies GmbH
Packager:	Ronald Jeninga <ronald.jeninga@schedulix.org>

BuildRequires:	jna gcc-c++ java-1.7.0-openjdk-devel rpm-build rpm-libs rpmdevtools rpm-sign

# disable debug package
%global debug_package %{nil}

%define zope2version 2.13.29

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
Requires:		java-1.8.0-openjdk jna

%description base
%commonDescription

The schedulix base package provides the files that are used by most other packages

%pre base
%include ../lib/base_pre.script


%post base
%include ../lib/base_post.script


%postun base
echo "executing postun base -- %version-%release"
if [ "$1" == "0" ]; then
	userdel schedulix
	rm -rf /var/spool/mail/schedulix
	rm -rf /opt/schedulix
fi

%preun base
echo "executing preun base -- %version-%release"

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
%doc %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/LICENSE
%doc %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/CONTRIBUTING.md
%doc %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/README.md
     %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/scrolllog
     %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/sdmsctl
     %attr(755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/sdmsh
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc/bicsuite.conf.template
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc/java.conf.template
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/BICsuite.jar
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/functions.sh
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc/zope_requirements-2.13.26.txt
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/etc/zope_requirements-2.13.29.txt
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/Waffle.Windows.AuthProvider.dll
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/guava-20.0.jar
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/jna-platform-4.3.0.jar
     %attr(644, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/lib/waffle-jna-1.8.3.jar
%ghost %config(noreplace) %attr(644, schedulix, schedulix) /opt/schedulix/etc/bicsuite.conf
%ghost %config(noreplace) %attr(644, schedulix, schedulix) /opt/schedulix/etc/java.conf 
%ghost %config(noreplace) %attr(644, schedulix, schedulix) /opt/schedulix/etc/SETTINGS
#
# exclude this spec file as it isn't required in any binary package
#
%exclude /opt/schedulix/schedulix-%{version}/lib/centos7.spec
%exclude /opt/schedulix/schedulix-%{version}/lib/centos8.spec
%exclude /opt/schedulix/schedulix-%{version}/lib/base_pre.script
%exclude /opt/schedulix/schedulix-%{version}/lib/base_post.script
%exclude /opt/schedulix/schedulix-%{version}/lib/server-mariadb_post.script
%exclude /opt/schedulix/schedulix-%{version}/lib/server-mariadb_pre.script
%exclude /opt/schedulix/schedulix-%{version}/lib/server-pg_post.script
%exclude /opt/schedulix/schedulix-%{version}/lib/server-pg_pre.script
%exclude /opt/schedulix/schedulix-%{version}/lib/server-rmt_post.script
%exclude /opt/schedulix/schedulix-%{version}/lib/server-rmt_pre.script
%exclude /opt/schedulix/schedulix-%{version}/lib/zope_post.script
%exclude /opt/schedulix/schedulix-%{version}/lib/zope_pre.script

%package server-rmt
# ----------------------------------------------------------------------------------------
#
# server without setup of a database
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix server rmt package installs the schedulix server software but omits the creation of a database
Group:			Applications/System
Requires:		schedulix-base = %{version}-%{release} coreutils psmisc
Provides:		schedulix-server %{version}-%{release}
Conflicts:		schedulix-server-mariadb schedulix-server-pg

%description server-rmt
%commonDescription

The schedulix server rmt package installs a schedulix server without any initialisation or configuration of a database system.
These steps have to be performed by the user himself. The installation guide describes all required steps.

%serverNotes


%pre server-rmt
%include ../lib/server-rmt_pre.script

%post server-rmt
%include ../lib/server-rmt_post.script


%preun server-rmt
echo "executing preun server-rmt -- %version-%release"
if [ "$1" == "0" ]; then
	echo "Stopping server ..."
	if service schedulix-server status; then
		service schedulix-server stop || true
		chkconfig schedulix-server off
	fi
fi

%postun server-rmt
echo "executing postun server-rmt -- %version-%release"
if [ "$1" == "0" ]; then
	echo "Don't forget to manually clean up the database"
fi


%files server-rmt
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
%attr(0744, root, root)             /etc/init.d/schedulix-server-pg
%attr(0744, root, root)             /etc/init.d/schedulix-server-mariadb
/opt/schedulix/schedulix-%{version}/sql/pg
/opt/schedulix/schedulix-%{version}/sql/pg_gen
/opt/schedulix/schedulix-%{version}/sql/mysql
/opt/schedulix/schedulix-%{version}/sql/mysql_gen
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/etc/server.conf
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/.sdmshrc
%ghost %attr(-, root, root) /etc/init.d/schedulix-server

#
# exclude ingres sql files for now
#
%exclude   /opt/schedulix/schedulix-%{version}/sql/ing
%exclude   /opt/schedulix/schedulix-%{version}/sql/ing_gen

#
# exclude the buildhash file
# This file is only required to get a correct build outside of a git repository
#
%exclude /opt/schedulix/schedulix-%{version}/buildhash


%package server-pg
# ----------------------------------------------------------------------------------------
#
# server + postgresql package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix server pg package installs a schedulix server based on an underlying Postgres RDBMS
Group:			Applications/System
Requires:		schedulix-base = %{version}-%{release} postgresql-server postgresql-jdbc coreutils psmisc
Provides:		schedulix-server %{version}-%{release}
Conflicts:		schedulix-server-mariadb schedulix-server-rmt

%description server-pg
%commonDescription

The schedulix server pg package installs a schedulix server based on an underlying Postgres RDBMS.
It loads the convenience package, but does not load the examples.

%serverNotes

The configuration file will be changed. Instead of the 'ident' method we need the 'md5' method
in order to be able to connect by jdbc.


%pre server-pg
%include ../lib/server-pg_pre.script

%post server-pg
%include ../lib/server-pg_post.script


%preun server-pg
echo "executing preun server-pg -- %version-%release"
if [ "$1" == "0" ]; then
	echo "Stopping server ..."
	service schedulix-server stop || true
	chkconfig schedulix-server off
fi

%postun server-pg
echo "executing postun server-pg -- %version-%release"
if [ "$1" == "0" ]; then
	echo "Dropping database"
	su - schedulix -c "dropdb schedulixdb" || echo "drop of schedulixdb failed; continuing anyway"
	su - postgres -c 'echo "drop user schedulix;" | psql' || echo "drop role schedulix failed; continuing anyway"
fi


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
%attr(0744, root, root)             /etc/init.d/schedulix-server-pg
/opt/schedulix/schedulix-%{version}/sql/pg
/opt/schedulix/schedulix-%{version}/sql/pg_gen
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/etc/server.conf
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/.sdmshrc
%ghost %attr(-, root, root) /etc/init.d/schedulix-server
%ghost %attr(0600, schedulix, schedulix) /opt/schedulix/.pgpass



%package server-mariadb
# ----------------------------------------------------------------------------------------
#
# server + mariadb/mysql package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix server mariadb package installs a schedulix server based on an underlying MariaDB od MySQL RDBMS
Group:			Applications/System
# Requires: schedulix-base mysql-server mysql-connector-java
Requires:		schedulix-base = %{version}-%{release} mariadb mariadb-libs mariadb-server mysql-connector-java coreutils psmisc
Provides:		schedulix-server %{version}-%{release}
Conflicts:		schedulix-server-pg schedulix-server-rmt

%description server-mariadb
%commonDescription

The schedulix server mariadb package installs a schedulix server based on an
underlying MariaDB or MySQL RDBMS.
A DBMS user 'schedulix' with password 'schedulix' will be created, which will
create a database called 'schedulixdb'.

It will load the convenience package, but does not load the examples.
(use the schedulix-examples package to do so).

%serverNotes

%pre server-mariadb
%include ../lib/server-mariadb_pre.script

%post server-mariadb
%include ../lib/server-mariadb_post.script


%preun server-mariadb
echo "executing preun server-mariadb -- %version-%release"
if [ "$1" == "0" ]; then
	echo "Stopping server ..."
	service schedulix-server stop || true
	chkconfig schedulix-server off
fi

%postun server-mariadb
echo "executing postun server-mariadb -- %version-%release"
if [ "$1" == "0" ]; then
	echo "Dropping database"
	mysql --user=root << ENDMYSQL
	drop user schedulix@localhost;
	drop database schedulixdb;
	quit
ENDMYSQL
fi


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
%attr(0744, root, root)             /etc/init.d/schedulix-server-mariadb
%dir %attr(0755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/install
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/convenience.sdms
/opt/schedulix/schedulix-%{version}/sql/mysql
/opt/schedulix/schedulix-%{version}/sql/mysql_gen
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/etc/server.conf
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/.sdmshrc
%ghost %attr(-, root, root) /etc/init.d/schedulix-server

#
# exclude ingres sql files for now
#
%exclude   /opt/schedulix/schedulix-%{version}/sql/ing
%exclude   /opt/schedulix/schedulix-%{version}/sql/ing_gen

#
# exclude the buildhash file
# This file is only required to get a correct build outside of a git repository
#
%exclude /opt/schedulix/schedulix-%{version}/buildhash

%package client
# ----------------------------------------------------------------------------------------
#
# client package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix client package installs everything needed to setup a jobserver
Group:			Applications/System
Requires:		schedulix-base = %{version}-%{release} coreutils psmisc

%description client
%commonDescription

The schedulix client package installs everything needed to setup a jobserver

%pre
echo "executing pre client -- %version-%release"
if [ "$1" == "1" ]; then
	: nothing to do on initial install
else
	service schedulix-client stop || true
fi

%post
echo "executing post client -- %version-%release"
if [ "$1" == "1" ]; then
	chkconfig schedulix-client on
fi
service schedulix-client start || true


%preun
echo "executing preun client -- %version-%release"
if [ "$1" == "0" ]; then
	service schedulix-client stop || true
	chkconfig schedulix-client off
fi

%postun
echo "executing postun client -- %version-%release"

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
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/setup_jobserver
%dir %attr(0755, schedulix, schedulix) /opt/schedulix/schedulix-%{version}/bin/Images
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/Images/Bullit.png
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/Images/Logo.png
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/SDMSpopup.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/dynsubmit.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/start_example_jobservers.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/stop_example_jobservers.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/watch.sh
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/jobserver.conf.template
%attr(0744, root, root)             /etc/init.d/schedulix-client
%dir %attr(1777, schedulix, schedulix) /opt/schedulix/taskfiles

%package zope
# ----------------------------------------------------------------------------------------
#
# zope FE package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix zope package installs the zope application server and configures it to access a locally installed server
Group:			Applications/System
Requires:		schedulix-base = %{version}-%{release} gcc python python-devel python-setuptools python-virtualenv wget

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

%pre zope
%include ../lib/zope_pre.script


%post zope
%include ../lib/zope_post.script


%preun zope
echo "executing preun zope -- %version-%release"
if [ "$1" == "0" ]; then
	/etc/init.d/schedulix-zope stop || true
	chkconfig schedulix-zope off
fi


%postun zope
echo "executing postun zope -- %version-%release"
if [ "$1" == "0" ]; then
	rm -rf /opt/schedulix/software
	rm -rf /opt/schedulix/schedulixweb
fi


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
%attr(0744, root, root)             /etc/init.d/schedulix-zope

%package zope4
# ----------------------------------------------------------------------------------------
#
# zope4 FE package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix zope4 package installs the zope4 application server and configures it to access a locally installed server
Group:			Applications/System
Requires:		schedulix-base = %{version}-%{release} gcc python3 python3-devel python3-setuptools wget

%description zope4
%commonDescription

The schedulix zope4 package installs the zope application server and configures 
it to access a locally installed server.
Note: installing this package requires a working Internet connection as the Zope4 
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

%pre zope4
%include ../lib/zope4_pre.script


%post zope4
%include ../lib/zope4_post.script


%preun zope4
echo "executing preun zope4 -- %version-%release"
if [ "$1" == "0" ]; then
	/etc/init.d/schedulix-zope4 stop || true
	chkconfig schedulix-zope4 off
fi


%postun zope4
echo "executing postun zope -- %version-%release"
if [ "$1" == "0" ]; then
	rm -rf /opt/schedulix/Zope4
	rm -rf /opt/schedulix/schedulixweb4
fi


%files zope4
%defattr(0644, schedulix, schedulix, 0755)
%dir /opt/schedulix/schedulix-%{version}/zope4
%dir /opt/schedulix/schedulix-%{version}/zope4/BICsuiteSubmitMemory
%dir /opt/schedulix/schedulix-%{version}/zope4/import
%dir /opt/schedulix/schedulix-%{version}/zope4/Extensions
%dir /opt/schedulix/schedulix-%{version}/zope4/StringFixer
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/import/SDMS.zexp
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/Extensions/bicsuite_tx.py
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/Extensions/myeval.py
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/Extensions/sdms.py
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/StringFixer/__init__.py
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/BICsuiteSubmitMemory/BICsuiteSubmitMemory.py
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/zope4/BICsuiteSubmitMemory/__init__.py
%ghost %attr(0755, schedulix, schedulix) /opt/schedulix/Zope4
%ghost %attr(0755, schedulix, schedulix) /opt/schedulix/schedulixweb4
%attr(0744, root, root)             /etc/init.d/schedulix-zope4
# exclude all compiled python files
%exclude   /opt/schedulix/schedulix-2.9/lib/zope4_post.script
%exclude   /opt/schedulix/schedulix-2.9/lib/zope4_pre.script
%exclude   /opt/schedulix/schedulix-2.9/zope4/BICsuiteSubmitMemory/BICsuiteSubmitMemory.pyc
%exclude   /opt/schedulix/schedulix-2.9/zope4/BICsuiteSubmitMemory/BICsuiteSubmitMemory.pyo
%exclude   /opt/schedulix/schedulix-2.9/zope4/BICsuiteSubmitMemory/__init__.pyc
%exclude   /opt/schedulix/schedulix-2.9/zope4/BICsuiteSubmitMemory/__init__.pyo
%exclude   /opt/schedulix/schedulix-2.9/zope4/Extensions/bicsuite_tx.pyc
%exclude   /opt/schedulix/schedulix-2.9/zope4/Extensions/bicsuite_tx.pyo
%exclude   /opt/schedulix/schedulix-2.9/zope4/Extensions/myeval.pyc
%exclude   /opt/schedulix/schedulix-2.9/zope4/Extensions/myeval.pyo
%exclude   /opt/schedulix/schedulix-2.9/zope4/Extensions/sdms.pyc
%exclude   /opt/schedulix/schedulix-2.9/zope4/Extensions/sdms.pyo
%exclude   /opt/schedulix/schedulix-2.9/zope4/StringFixer/__init__.pyc
%exclude   /opt/schedulix/schedulix-2.9/zope4/StringFixer/__init__.pyo


%package examples
# ----------------------------------------------------------------------------------------
#
# Examples package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix examples package installs a few local jobservers and loads a bunch of examples into the system
Group:			Applications/System
Requires:		schedulix-base >= %{version} schedulix-server >= %{version} schedulix-client eclipse-swt
BuildArch:		noarch

%description examples
%commonDescription

The schedulix examples package installs a few local jobservers and loads a bunch of examples into the system.
The logs can be found in the $BICSUITEHOME/install directory.
It is assumed that a valid .sdmshrc file is installed in $HOME.

%pre examples
echo "executing pre examples -- %version-%release"

%post examples
echo "executing post examples -- %version-%release"
if [ "$1" == "1" ]; then
	su - schedulix -c '
	. ~/.bashrc;
	cd $BICSUITEHOME/install;
	./setup_example_jobservers.sh > setup_example_jobservers.log 2>&1;
	sdmsh < setup_examples.sdms > setup_examples.log 2>&1
	'
	chkconfig schedulix-examples on
	service schedulix-examples start
else
	su - schedulix -c '
	. ~/.bashrc
	cd $BICSUITEHOME/install
	sdmsh < setup_examples.sdms > setup_examples.log 2>&1
	'
fi

%preun examples
echo "executing preun examples -- %version-%release"
if [ "$1" == "0" ]; then
	service schedulix-examples stop || true
	chkconfig schedulix-examples off
	cd /opt/schedulix/schedulix/install
	rm -f *.log
fi

%postun examples
echo "executing postun examples -- %version-%release"

%files examples
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/host_1.conf.template
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/host_2.conf.template
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/localhost.conf.template
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/etc/host_1.conf
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/etc/host_2.conf
%ghost %config(noreplace) %attr(0600, schedulix, schedulix) /opt/schedulix/etc/localhost.conf
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/setup_example_jobservers.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/dog.sh
%attr(0755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/frosch.sh
%attr(0644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/install/setup_examples.sdms
%attr(0744, root, root)             /etc/init.d/schedulix-examples

%package doc
# ----------------------------------------------------------------------------------------
#
# documentation package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix doc package installs the schedulix documentation
Group:			Documentation
BuildArch:		noarch

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

%package repo
Summary:		The repo package installs a repo file to get automated access to the rpm repository
Group:			System/Packages
BuildArch:		noarch

%description repo

This package installs a repo file to get automated access to the schedulix rpm repository

%files repo
%attr(0644, root, root) /etc/yum.repos.d/schedulix.repo



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
cp -r bin etc install lib sql zope LICENSE README.md CONTRIBUTING.md buildhash zope4 %{buildroot}/opt/schedulix/schedulix-%{version}
mkdir %{buildroot}/opt/schedulix/etc
mkdir %{buildroot}/opt/schedulix/bin
mkdir %{buildroot}/opt/schedulix/log
mkdir %{buildroot}/opt/schedulix/taskfiles
mkdir -p %{buildroot}/usr/share/doc/schedulix-%{version}
cp doc/* %{buildroot}/usr/share/doc/schedulix-%{version}
mkdir -p %{buildroot}/etc/init.d
mkdir -p %{buildroot}/etc/yum.repos.d
# here we move the init.d scripts from the bin directory to the init.d directory
mv %{buildroot}/opt/schedulix/schedulix-%{version}/bin/schedulix-server-mariadb %{buildroot}/etc/init.d/schedulix-server-mariadb
mv %{buildroot}/opt/schedulix/schedulix-%{version}/bin/schedulix-server-pg %{buildroot}/etc/init.d/schedulix-server-pg
mv %{buildroot}/opt/schedulix/schedulix-%{version}/bin/schedulix-zope %{buildroot}/etc/init.d/schedulix-zope
mv %{buildroot}/opt/schedulix/schedulix-%{version}/bin/schedulix-zope4 %{buildroot}/etc/init.d/schedulix-zope4
mv %{buildroot}/opt/schedulix/schedulix-%{version}/bin/schedulix-examples %{buildroot}/etc/init.d/schedulix-examples
mv %{buildroot}/opt/schedulix/schedulix-%{version}/bin/schedulix-client %{buildroot}/etc/init.d/schedulix-client
# 
mv %{buildroot}/opt/schedulix/schedulix-%{version}/lib/schedulix.repo %{buildroot}/etc/yum.repos.d/schedulix.repo


echo "End of the installation of schedulix"


%changelog

