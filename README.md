schedulix
=========

schedulix is an open source enterprise job scheduling system.
Instructions for compilation and installation can be found in doc/installtion_en.pdf (English)
or doc/installation.pdf (German).

Documentation of the user interface can be found in doc/online_en.pdf (English) or
doc/online_de.pdf (German).

-------------------------------------------------------------------------------------------

The last stable branch is v2.11
It can be obtained by doing a

git clone https://github.com/schedulix/schedulix.git -b v2.11

-------------------------------------------------------------------------------------------

All the jflex issues are resolved. But please advise if anyone runs into jflex related
problems.

-------------------------------------------------------------------------------------------

With this release we start to reduce the support for Zope2.
There won't be any rpms that will install a new Zope2 instance, but the schedulix-zope
rpms will be able to upgrade the Zope2 GUI.
Instead of Zope 2 it is reccomended to setup the GUI with Zope 4/5 which uses Python3.
How to move from Zope2 to Zope5 is described in the installation guide.
The next release probably won't support Zope2 any more.

-------------------------------------------------------------------------------------------

With this release we also publish a new GUI.
Within the zope4 directory there is a compressed zexp file called schedulix-fe.zexp.gz.
After gunzipping it it can be imported into a Zope 5 instance.
With an URL like http://localhost:8080/schedulix-fe the new GUI will be opened.

This GUI is still under development and should be regarded as an alpha release.
It is entirely open source and the source code can be obtained by

git clone https://gitlab.com/schedulix/schedulix-fe.git -b dev

-------------------------------------------------------------------------------------------

For those not so very interested in compiling the software themselves there are RPM 
packages available that can be installed on RHEL/CentOS 7 and 8. RPMs for RHEL9 are in 
development. The required files for building the packages will soon be published both
in the v2.11 branch as well in the master branch.
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
