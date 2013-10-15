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


Happy Hacking :-)
