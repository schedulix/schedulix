schedulix
=========

schedulix is an open source enterprise job scheduling system.
Instructions for compilation and installation can be found in doc/installtion_en.pdf (English)
or doc/installation.pdf (German).

Documentation of the user interface can be found in doc/online_en.pdf (English) or
doc/online_de.pdf (German).

-------------------------------------------------------------------------------------------

The last stable branch is v2.9
It can be obtained by doing a

git clone https://github.com/schedulix/schedulix.git -b v2.9

-------------------------------------------------------------------------------------------

All the jflex issues are resolved. But please advise if anyone runs into jflex related
problems.

-------------------------------------------------------------------------------------------

We're currently adding support for building rpms for RHEL8/CentOS8. This might show effect
when building and installing rpms for RHEL7/CentOS7. Again, if problems arise, please tell
us.

-------------------------------------------------------------------------------------------

If someone has questions or any problems, we'd appreciate it if they are reported in the
schedulix Google group: https://groups.google.com/forum/#!forum/schedulix/categories
If this is not possible, the second option is to open an issue within github.

-------------------------------------------------------------------------------------------

To upgrade from an older version to the current version, some database schema changes have
to be made. SQL scripts are found in the sql/*_gen directories.

Happy Hacking :-)
