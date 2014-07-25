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

git clone https://github.com/schedulix/schedulix.git -b v2.5.1

-------------------------------------------------------------------------------------------

When upgrading from the stable 2.5.1 version to the current _unstable_ master branch,
don't forget to upgrade the database schema (adapt these statements to the dialect of
the dbms used):

ALTER TABLE USERS
ADD COLUMN SALT VARCHAR(64) WITH NULL;

ALTER TABLE USERS
ADD COLUMN METHOD INTEGER NOT NULL WITH DEFAULT; /* 0 */

ALTER TABLE SCOPE
ADD COLUMN SALT VARCHAR(64) WITH NULL;

ALTER TABLE SCOPE
ADD COLUMN METHOD INTEGER NOT NULL WITH DEFAULT; /* 0 */

ALTER TABLE RESOURCE_REQUIREMENT
ADD COLUMN STICKY_NAME VARCHAR(64) WITH NULL;

ALTER TABLE RESOURCE_REQUIREMENT
ADD COLUMN STICKY_PARENT DECIMAL(20) WITH NULL;

ALTER TABLE RESOURCE_ALLOCATION
ADD COLUMN STICKY_NAME VARCHAR(64) WITH NULL;

ALTER TABLE RESOURCE_ALLOCATION
ADD COLUMN STICKY_PARENT DECIMAL(20) WITH NULL;

-------------------------------------------------------------------------------------------

We fixed a (more or less severe) bug in the sticky handling.
In this context the previously defined table MASTER_ALLOCATION was eliminated.
This means that anyone having 2.6 running will have to take care a bit.

The general idea is:
- Make sure no jobs with sticky resource requests are submitted and not final or cancelled
- shut down the server
- recompile
- drop the table MASTER_ALLOCATION
- start up the server

-------------------------------------------------------------------------------------------

Not yet documented: For building the 2.6 release a jna.jar is required.
This jar file which is released under the LGPL can be obtained from

https://maven.java.net/content/repositories/releases/net/java/dev/jna/jna/4.0.0/jna-4.0.0.jar

The github repository of jna.jar can be found at

https://github.com/twall/jna

Similar to the swt.jar an environment variable JNAJAR must be set and point to the jna.jar file.

In order to run the system the jna.jar file is required for jobservers. The swt.jar file is
required for the examples. This can be configured in the $BICSUITEHOME/etc/java.conf file.

-------------------------------------------------------------------------------------------

There has been a change in the object loading strategy. Effectively the user has more
control over the objects loaded. This results in reduced memory consumption, shorter start up
times and more valuable information.

The loading strategy is controled by three additional server parameters. See server.conf.template
for details.

-------------------------------------------------------------------------------------------

Password hashes are now stored as SHA256 hash with a 64 byte salt.
Adding the salt was definitely necessary to improve security.
The use of SHA256 wasn't that necessary, but the security of this algorithm is regarded higher
than the security of the formerly used MD5.
There's no direct need to change all passwords (although it would improve security of course),
since the type of hash is automatically recognized.
Scripts using the RAWPASSWORD option continue to work.

Happy Hacking :-)
