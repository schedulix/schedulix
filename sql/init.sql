/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software: 
you can redistribute it and/or modify it under the terms of the 
GNU Affero General Public License as published by the 
Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/


--------------------------------------------------------------------------------
--	Table "OBJECTCOUNTER"
--------------------------------------------------------------------------------

INSERT INTO OBJECTCOUNTER VALUES (1000);

--------------------------------------------------------------------------------
--	Table "VERSIONCOUNTER"
--------------------------------------------------------------------------------

INSERT INTO VERSIONCOUNTER VALUES (1);

--------------------------------------------------------------------------------
--	Table "GROUP"
--------------------------------------------------------------------------------

INSERT INTO GROUPS (ID, NAME,     DELETE_VERSION, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES     (80, 'PUBLIC', 0,              0,            0,         0,            0);

-- IMPORTANT!!!
--	If this ID is changed, it also has to be changed in SDMSObject.java
INSERT INTO GROUPS (ID, NAME,    DELETE_VERSION, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES     (81, 'ADMIN', 0,              0,            0,         0,            0);

--------------------------------------------------------------------------------
--	Table "user"
--------------------------------------------------------------------------------

INSERT INTO USERS (ID, NAME,     PASSWD,                                SALT, METHOD, IS_ENABLED, DEFAULT_G_ID, DELETE_VERSION, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES    (0,  'SYSTEM', 'ba143b7e 75dde00e 8f0fd6f4 6cf65faf', null, 0,      1,          81,           0,              0,            0,         0,            0);

INSERT INTO USERS (ID, NAME,     PASSWD,                                SALT, METHOD, IS_ENABLED, DEFAULT_G_ID, DELETE_VERSION, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES    (1,  'NOBODY', 'xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx', null, 0,      0,          80,           0,              0,            0,         0,            0);
 
INSERT INTO USERS (ID, NAME,       PASSWD,                              SALT, METHOD, IS_ENABLED, DEFAULT_G_ID, DELETE_VERSION, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES    (2,  'INTERNAL', 'xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx', null, 0,    0,          81,           0,              0,            0,         0,            0);

--------------------------------------------------------------------------------
--	Table "SCHEDULING HIERARCHY"
--------------------------------------------------------------------------------

-- Create Dummy scheduling hierarchy for triggers without child definition
-- IMPORTANT!!!
--	If this ID is changed, it also has to be changed in SDMSObject.java
INSERT INTO SCHEDULING_HIERARCHY (
	ID, SE_PARENT_ID, SE_CHILD_ID, ALIAS_NAME, IS_STATIC, IS_DISABLED, PRIORITY, SUSPEND, MERGE_MODE, ESTP_ID,
	CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS, VALID_FROM, VALID_TO)
VALUES (
	30, NULL,         NULL,        NULL,       0,         0,           0,        1,       3,          NULL,
	0, 0, 0, 0, 0, 9223372036854775807);

--------------------------------------------------------------------------------
--	Table "FOLDER"
--------------------------------------------------------------------------------

-- Create SYSTEM folder owned by ADMIN without parent
INSERT INTO FOLDER (ID, NAME,     OWNER_ID, ENV_ID, PARENT_ID, VALID_FROM, VALID_TO,            CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS, INHERIT_PRIVS)
	VALUES     (40, 'SYSTEM', 81,       NULL,   NULL,      0,          9223372036854775807, 0,            0,         0,            0,         0);

--------------------------------------------------------------------------------
--	Table "SCOPE"
--------------------------------------------------------------------------------

-- Create GLOBAL scope owned by ADMIN without parent
INSERT INTO SCOPE (ID, NAME,     OWNER_ID, TYPE, METHOD, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS, INHERIT_PRIVS)
	VALUES    (50, 'GLOBAL', 81,       1,    0,      0,            0,         0,            0        , 0);

--------------------------------------------------------------------------------
--	Table "NAMED_RESOURCE"
--------------------------------------------------------------------------------

-- Create RESOURCE category owned by ADMIN without parent
INSERT INTO NAMED_RESOURCE (ID, NAME,       OWNER_ID, PARENT_ID, USAGE, RSP_ID, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS, INHERIT_PRIVS)
	VALUES             (60, 'RESOURCE', 81,       NULL,      8,     NULL,   0,            0,         0,            0,         0);

--------------------------------------------------------------------------------
--	Table "SCHEDULE"
--------------------------------------------------------------------------------

-- Create ROOT schedule owned by ADMIN without parent
-- IMPORTANT!!!
--	If this ID is changed, it also has to be changed in BICsuite!web SDMS/SDMSDesigner/Schedules/NavigatorQueryMethod
INSERT INTO SCHEDULE (ID, NAME,   OWNER_ID, INT_ID, PARENT_ID, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS, ACTIVE, INHERIT_PRIVS, TIME_ZONE)
	VALUES       (70, 'ROOT', 81,       NULL,   NULL,      0,            0,         0,            0,         1,      0,             'GMT'    );

--------------------------------------------------------------------------------
--	Table "MEMBER"
--------------------------------------------------------------------------------

INSERT INTO MEMBER (ID, G_ID, U_ID, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES     (90, 81,   0,    0,            0,         0,            0);

INSERT INTO MEMBER (ID, G_ID, U_ID, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES     (91, 80,   0,    0,            0,         0,            0);

INSERT INTO MEMBER (ID, G_ID, U_ID, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES     (92, 80,   1,    0,            0,         0,            0);

INSERT INTO MEMBER (ID, G_ID, U_ID, CREATOR_U_ID, CREATE_TS, CHANGER_U_ID, CHANGE_TS)
	VALUES     (93, 81,   2,    0,            0,         0,            0);

