#
# Common description and properties of the schedulix packages
#
Name:		schedulix
Version:	2.6.1
Release:	1%{?dist}
Summary:	schedulix is an open source enterprise job scheduling system

Group:		Applications/System
License:	AGPL
URL:		http://www.schedulix.org
Source0:	schedulix-%{?version}.tgz

Vendor:		independIT Integrative Technologies GmbH
Packager:	Ronald Jeninga <ronald.jeninga@schedulix.org>

BuildRequires:	jflex jna gcc-c++ java-1.7.0-openjdk-devel rpm-build rpm-libs rpmdevtools


%define commonDescription \
schedulix is a production proof open source enterprise job scheduling system. \
It consists of a central scheduling server and several agents called jobservers,  \
as well as a Zope application server which provides the access to the system \
by a standard web browser. \
For operation schedulix requires an installed RDBMS and a suitable JDBC driver. \
The server package to install should reflect the RDBMS that is installed. \
 \
On installation a user called 'schedulix' is created. The password is set to 'schedulix' \
(same as user name) and should be changed after installation. \
If one of the server packages is installed, it will restart the required DBMS. \
This might cause side effects if some other software that uses the database system is running.

%define commonPreScript \
if [ $1 -eq 1 ]; then \
	if [ ! grep schedulix /etc/passwd ]; then \
		useradd schedulix -d /opt/schedulix -m -s /bin/bash -U -p "RJteetpJ9UFeQ"; \
	fi; \
	if [ ! -d /opt/schedulix ]; then \
		mkdir -p /opt/schedulix; \
		chmod 755 /opt/schedulix; \
	fi; \
fi

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
Requires:		java-1.7.0-openjdk jna eclipse-swt

%description base
%commonDescription

The schedulix base package provides all commonly used files

%pre base
%{commonPreScript}

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
chmod 644 /opt/schedulix/etc/bicsuite.conf /opt/schedulix/etc/java.conf /opt/schedulix/etc/SETTINGS

echo '
# source schedulix environment
. /opt/schedulix/etc/SETTINGS' >> ~schedulix/.bashrc

cd /opt/schedulix
if [ -l schedulix ]; then
	rm schedulix
fi
ln -s schedulix-%{version} schedulix

%files base
%ghost /opt/schedulix/schedulix
%doc%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/AGPL.TXT
%doc%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/README.md
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/scrolllog
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdmsctl
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdmsh
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/bicsuite.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/java.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/lib/BICsuite.jar
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/lib/functions.sh
%ghost %config /opt/schedulix/etc/bicsuite.conf
%ghost %config /opt/schedulix/etc/java.conf 
%ghost %config /opt/schedulix/etc/SETTINGS
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
Requires:		schedulix-base postgresql-server postgresql-jdbc
Conflicts:		schedulix-server-mariadb

%description server-pg
%commonDescription

The schedulix server pg package installs a schedulix server based on an underlying Postgres RDBMS
It loads the convenience package, but does not load the examples.

%pre server-pg
%{commonPreScript}

%post server-pg
# create a valid server.conf
echo "creating server.conf file"
echo "creating database"
echo "populating database"

%files server-pg
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-restart
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-start
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-stop
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/server.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/MASTER_STATE.sql
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/MASTER_STATE.sql
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/REPOSITORY_LOCK.sql
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/init.sql
%defattr(644, schedulix, schedulix, 755)
/opt/schedulix/schedulix-%{version}/sql/pg
/opt/schedulix/schedulix-%{version}/sql/pg_gen
%ghost %config /opt/schedulix/etc/server.conf

%package server-mariadb
# ----------------------------------------------------------------------------------------
#
# server + mariadb/mysql package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix server mariadb package installs a schedulix server based on an underlying MariaDB od MySQL RDBMS
Group:			Applications/System
# Requires: schedulix-base mysql-server mysql-connector-java
Requires:		schedulix-base mariadb-server mysql-connector-java
Conflicts:		schedulix-server-pg

%description server-mariadb
%commonDescription

The schedulix server mariadb package installs a schedulix server based on an underlying MariaDB od MySQL RDBMS
It loads the convenience package, but does not load the examples.

%pre server-mariadb
%{commonPreScript}

%post server-mariadb
# create a valid server.conf
echo "creating server.conf file"
echo "creating database"
echo "populating database"

%files server-mariadb
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-restart
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-start
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/server-stop
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/server.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/MASTER_STATE.sql
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/MASTER_STATE.sql
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/REPOSITORY_LOCK.sql
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/sql/init.sql
%defattr(644, schedulix, schedulix, 755)
/opt/schedulix/schedulix-%{version}/sql/mysql
/opt/schedulix/schedulix-%{version}/sql/mysql_gen
%ghost %config /opt/schedulix/etc/server.conf

#
# exclude ingres sql files for now
#
%exclude   /opt/schedulix/schedulix-2.6.1/sql/ing
%exclude   /opt/schedulix/schedulix-2.6.1/sql/ing_gen

%package client
# ----------------------------------------------------------------------------------------
#
# client package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix client package installs everything needed to setup a jobserver
Group:			Applications/System

%description client
The schedulix client package installs everything needed to setup a jobserver

%pre client
%commonDescription

%{commonPreScript}

%files client
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/jobexecutor
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/jobserver-run
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-auto_restart
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-get_variable
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-rerun
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-set_state
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-set_variable
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-set_warning
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/sdms-submit
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/Images/Bullit.png
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/Images/Logo.png
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/SDMSpopup.sh
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/dynsubmit.sh
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/start_example_jobservers.sh
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/stop_example_jobservers.sh
%attr(755, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/bin/watch.sh
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/host_1.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/host_2.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/jobserver.conf.template
%attr(644, schedulix, schedulix)   /opt/schedulix/schedulix-%{version}/etc/localhost.conf.template

%package zope
# ----------------------------------------------------------------------------------------
#
# zope FE package
#
# ----------------------------------------------------------------------------------------
Summary:		The schedulix zope package installs the zope application server and configures it to access a locally installed server
Group:			Applications/System
Requires:		python python-devel python-setuptools wget

%description zope
%commonDescription

The schedulix zope package installs the zope application server and configures it to access a locally installed server

%pre zope
%{commonPreScript}

%post zope
echo "fetching zope"
echo "building zope instance"
echo "loading application"

%files zope
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

# ----------------------------------------------------------------------------------------
#
# documentation package
#
# ----------------------------------------------------------------------------------------
%package doc
Summary:		The schedulix doc package installs the schedulix documentation
Group:			Documentation

%description doc
%commonDescription

The schedulix doc package installs the schedulix documentation

%files doc
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
# disable debug package
%global debug_package %{nil}
SDMSHOME=`pwd`
cd src
make 
cd ..


%install
echo "starting the installation of schedulix"
mkdir -p %{buildroot}/opt/schedulix/schedulix-%{version}
cp -r bin etc lib sql zope AGPL.TXT README.md %{buildroot}/opt/schedulix/schedulix-%{version}
mkdir %{buildroot}/opt/schedulix/etc
mkdir %{buildroot}/opt/schedulix/bin
mkdir %{buildroot}/opt/schedulix/log
mkdir -p %{buildroot}/usr/share/doc/schedulix-%{version}
cp doc/* %{buildroot}/usr/share/doc/schedulix-%{version}


echo "End of the installation of schedulix"


%changelog

