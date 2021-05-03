schedulix
=========

schedulix is an open source enterprise job scheduling system.
Instructions for compilation and installation can be found in doc/installtion_en.pdf (English)
or doc/installation.pdf (German).

Documentation of the user interface can be found in doc/online_en.pdf (English) or
doc/online_de.pdf (German).

-------------------------------------------------------------------------------------------

The last stable branch is v2.10
It can be obtained by doing a

git clone https://github.com/schedulix/schedulix.git -b v2.10

-------------------------------------------------------------------------------------------

All the jflex issues are resolved. But please advise if anyone runs into jflex related
problems.

-------------------------------------------------------------------------------------------

We've added support for building rpms for RHEL8/CentOS8.
Instead of Zope 2 it is possible to setup the GUI with Zope 4/5 which uses Python3.
Since the support for Python2 has terminated we recommend to use the new Zope release.
The downside is that we don't have an automated way of migrating the user information yet.
As an interesting new feature we've added support for setting up single sign on in
combination with Active Directory. The procedure to set up single sign on is described
in the installation guide.

-------------------------------------------------------------------------------------------

For those not so very interested in compiling the software themselves there are RPM 
packages available that can be installed on RHEL/CentOS 7 and 8.
The repository information can be downloaded from
https://www.independit.de/Downloads/schedulix-repo.rpm

-------------------------------------------------------------------------------------------

If someone has questions or any problems, we'd appreciate it if they are reported in the
schedulix Google group: https://groups.google.com/forum/#!forum/schedulix/categories
If this is not possible, the second option is to open an issue within github.

-------------------------------------------------------------------------------------------

To upgrade from an older version to the current version, some database schema changes have
to be made. SQL scripts are found in the sql/*_gen directories.

Happy Hacking :-)
