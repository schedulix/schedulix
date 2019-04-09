schedulix
=========

schedulix is an open source enterprise job scheduling system.
Instructions for compilation and installation can be found in doc/installtion_en.pdf (English)
or doc/installation.pdf (German).

Documentation of the user interface can be found in doc/online_en.pdf (English) or
doc/online_de.pdf (German).

All the progress will be twittered (https://twitter.com/schedulix).

-------------------------------------------------------------------------------------------

The last stable branch is v2.9
It can be obtained by doing a

git clone https://github.com/schedulix/schedulix.git -b v2.9

-------------------------------------------------------------------------------------------

Important notice:
Due to a changed read strategy in the code generated by jflex 1.6.x, we cannot recommend
to use this version of jflex at the moment, unless you like code digging or deadlocks. 

After contacting the jflex guys, the flaw has been fixed. This fix will be part of jflex 1.7
It will also be part of the current development release.
Due to the fact that a different skeleton is required, the 1.7 version isn't supported yet.
Support will be added soon.

**for now, it is highly recommended to use one of the 1.4.x versions of jflex.**

-------------------------------------------------------------------------------------------

To upgrade from an older version to the current version, some database schema changes have
to be made. SQL scripts are found in the sql/*_gen directories.

Happy Hacking :-)
