schedulix
=========

schedulix is an open source enterprise job scheduling system.
Instructions for compilation and installation can be found in doc/installtion_en.pdf (English)
or doc/installation.pdf (German).

Documentation of the user interface can be found in doc/online_en.pdf (English) or
doc/online_de.pdf (German).

All the progress will be twittered (https://twitter.com/schedulix).

-------------------------------------------------------------------------------------------

The last stable branch is v2.5.1
It can be obtained by doing a

git clone https://github.org/schedulix/schedulix.git -b v2.5.1

-------------------------------------------------------------------------------------------

When upgrading from the stable 2.5.1 version to the current _unstable_ master branch,
don't forget to upgrade the database schema (adapt these statements to the dialect of
the dbms used):

ALTER TABLE RESOURCE_REQUIREMENT
ADD COLUMN STICKY_NAME VARCHAR(64) WITH NULL;

ALTER TABLE RESOURCE_REQUIREMENT
ADD COLUMN STICKY_PARENT DECIMAL(20) WITH NULL;

ALTER TABLE RESOURCE_ALLOCATION
ADD COLUMN STICKY_NAME VARCHAR(64) WITH NULL;

ALTER TABLE RESOURCE_ALLOCATION
ADD COLUMN STICKY_PARENT DECIMAL(20) WITH NULL;

And create the new table MASTER_ALLOCATION (see sql directory)

-------------------------------------------------------------------------------------------

Not yet documented: For building the 2.6 release a jna.jar is required.
This jar file which is released under the LGPL can be obtained from

https://maven.java.net/content/repositories/releases/net/java/dev/jna/jna/4.0.0/jna-4.0.0.jar

The github repository of jna.jar can be found at

https://github.com/twall/jna

Similar to the swt.jar an environment variable JNAJAR must be set and point to the jna.jar file.

In order to run the system the jna.jar file is required for jobservers. The swt.jar file is
required for the examples. This can be configured in the $BICSUITEHOME/etc/java.conf file.

Happy Hacking :-)
