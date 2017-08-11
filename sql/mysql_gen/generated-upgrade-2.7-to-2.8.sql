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
ALTER TABLE ENTITY_VARIABLE
    ADD `IS_LONG` integer NOT NULL DEFAULT 0;
ALTER TABLE ARC_ENTITY_VARIABLE
    ADD `IS_LONG` integer;
DROP VIEW SCI_ENTITY_VARIABLE;
CREATE VIEW SCI_ENTITY_VARIABLE AS
SELECT
    ID
    , `SME_ID`                       AS `SME_ID`
    , `NAME`                         AS `NAME`
    , `VALUE`                        AS `VALUE`
    , CASE `IS_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LOCAL`
    , CASE `IS_LONG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LONG`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM ENTITY_VARIABLE;
CREATE TABLE ARC_EXTENTS (
    ID                             decimal(20) NOT NULL
    , `O_ID`                         decimal(20)      NULL
    , `SME_ID`                       decimal(20)      NULL
    , `SEQUENCE`                     integer          NULL
    , `EXTENT`                       varchar(256)     NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE EXTENTS (
    `ID`                           decimal(20) NOT NULL
    , `O_ID`                         decimal(20)     NOT NULL
    , `SME_ID`                       decimal(20)     NOT NULL
    , `SEQUENCE`                     integer         NOT NULL
    , `EXTENT`                       varchar(256)    NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_EXTENTS
ON EXTENTS(`ID`);
DROP VIEW SCI_EXTENTS;
CREATE VIEW SCI_EXTENTS AS
SELECT
    ID
    , `O_ID`                         AS `O_ID`
    , `SME_ID`                       AS `SME_ID`
    , `SEQUENCE`                     AS `SEQUENCE`
    , `EXTENT`                       AS `EXTENT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM EXTENTS;
ALTER TABLE PARAMETER_DEFINITION
    ADD `IS_LONG` integer NOT NULL DEFAULT 0;
DROP VIEW SCI_C_PARAMETER_DEFINITION;
DROP VIEW SCI_V_PARAMETER_DEFINITION;
CREATE VIEW SCI_C_PARAMETER_DEFINITION AS
SELECT
    ID
    , `SE_ID`                        AS `SE_ID`
    , `NAME`                         AS `NAME`
    , CASE `TYPE` WHEN 10 THEN 'REFERENCE' WHEN 20 THEN 'CHILDREFERENCE' WHEN 30 THEN 'CONSTANT' WHEN 40 THEN 'RESULT' WHEN 50 THEN 'PARAMETER' WHEN 60 THEN 'EXPRESSION' WHEN 70 THEN 'IMPORT' WHEN 80 THEN 'DYNAMIC' WHEN 81 THEN 'DYNAMICVALUE' WHEN 90 THEN 'LOCAL_CONSTANT' WHEN 91 THEN 'RESOURCEREFERENCE' END AS `TYPE`
    , CASE `AGG_FUNCTION` WHEN 0 THEN 'NONE' WHEN 61 THEN 'AVG' WHEN 62 THEN 'COUNT' WHEN 63 THEN 'MIN' WHEN 64 THEN 'MAX' WHEN 65 THEN 'SUM' END AS `AGG_FUNCTION`
    , `DEFAULTVALUE`                 AS `DEFAULTVALUE`
    , CASE `IS_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LOCAL`
    , `LINK_PD_ID`                   AS `LINK_PD_ID`
    , `EXPORT_NAME`                  AS `EXPORT_NAME`
    , CASE `IS_LONG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LONG`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM PARAMETER_DEFINITION
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_PARAMETER_DEFINITION AS
SELECT
    ID
    , `SE_ID`                        AS `SE_ID`
    , `NAME`                         AS `NAME`
    , CASE `TYPE` WHEN 10 THEN 'REFERENCE' WHEN 20 THEN 'CHILDREFERENCE' WHEN 30 THEN 'CONSTANT' WHEN 40 THEN 'RESULT' WHEN 50 THEN 'PARAMETER' WHEN 60 THEN 'EXPRESSION' WHEN 70 THEN 'IMPORT' WHEN 80 THEN 'DYNAMIC' WHEN 81 THEN 'DYNAMICVALUE' WHEN 90 THEN 'LOCAL_CONSTANT' WHEN 91 THEN 'RESOURCEREFERENCE' END AS `TYPE`
    , CASE `AGG_FUNCTION` WHEN 0 THEN 'NONE' WHEN 61 THEN 'AVG' WHEN 62 THEN 'COUNT' WHEN 63 THEN 'MIN' WHEN 64 THEN 'MAX' WHEN 65 THEN 'SUM' END AS `AGG_FUNCTION`
    , `DEFAULTVALUE`                 AS `DEFAULTVALUE`
    , CASE `IS_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LOCAL`
    , `LINK_PD_ID`                   AS `LINK_PD_ID`
    , `EXPORT_NAME`                  AS `EXPORT_NAME`
    , CASE `IS_LONG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LONG`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM PARAMETER_DEFINITION;
CREATE TABLE ARC_SUBMITTED_ENTITY_STATS (
    ID                             decimal(20) NOT NULL
    , `SME_ID`                       decimal(20)      NULL
    , `RERUN_SEQ`                    integer          NULL
    , `SCOPE_ID`                     decimal(20)      NULL
    , `JOB_ESD_ID`                   decimal(20)      NULL
    , `EXIT_CODE`                    integer          NULL
    , `COMMANDLINE`                  varchar(512)     NULL
    , `WORKDIR`                      varchar(512)     NULL
    , `LOGFILE`                      varchar(512)     NULL
    , `ERRLOGFILE`                   varchar(512)     NULL
    , `EXTPID`                       varchar(32)      NULL
    , `SYNC_TS`                      decimal(20)      NULL
    , `RESOURCE_TS`                  decimal(20)      NULL
    , `RUNNABLE_TS`                  decimal(20)      NULL
    , `START_TS`                     decimal(20)      NULL
    , `FINISH_TS`                    decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE SUBMITTED_ENTITY_STATS (
    `ID`                           decimal(20) NOT NULL
    , `SME_ID`                       decimal(20)     NOT NULL
    , `RERUN_SEQ`                    integer         NOT NULL
    , `SCOPE_ID`                     decimal(20)         NULL
    , `JOB_ESD_ID`                   decimal(20)         NULL
    , `EXIT_CODE`                    integer             NULL
    , `COMMANDLINE`                  varchar(512)        NULL
    , `WORKDIR`                      varchar(512)        NULL
    , `LOGFILE`                      varchar(512)        NULL
    , `ERRLOGFILE`                   varchar(512)        NULL
    , `EXTPID`                       varchar(32)         NULL
    , `SYNC_TS`                      decimal(20)         NULL
    , `RESOURCE_TS`                  decimal(20)         NULL
    , `RUNNABLE_TS`                  decimal(20)         NULL
    , `START_TS`                     decimal(20)         NULL
    , `FINISH_TS`                    decimal(20)         NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_SUBMITTED_ENTITY_STATS
ON SUBMITTED_ENTITY_STATS(`ID`);
DROP VIEW SCI_SUBMITTED_ENTITY_STATS;
CREATE VIEW SCI_SUBMITTED_ENTITY_STATS AS
SELECT
    ID
    , `SME_ID`                       AS `SME_ID`
    , `RERUN_SEQ`                    AS `RERUN_SEQ`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , `JOB_ESD_ID`                   AS `JOB_ESD_ID`
    , `EXIT_CODE`                    AS `EXIT_CODE`
    , `COMMANDLINE`                  AS `COMMANDLINE`
    , `WORKDIR`                      AS `WORKDIR`
    , `LOGFILE`                      AS `LOGFILE`
    , `ERRLOGFILE`                   AS `ERRLOGFILE`
    , `EXTPID`                       AS `EXTPID`
    , from_unixtime((`SYNC_TS` & ~1125899906842624)/1000) AS `SYNC_TS`
    , from_unixtime((`RESOURCE_TS` & ~1125899906842624)/1000) AS `RESOURCE_TS`
    , from_unixtime((`RUNNABLE_TS` & ~1125899906842624)/1000) AS `RUNNABLE_TS`
    , from_unixtime((`START_TS` & ~1125899906842624)/1000) AS `START_TS`
    , from_unixtime((`FINISH_TS` & ~1125899906842624)/1000) AS `FINISH_TS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM SUBMITTED_ENTITY_STATS;
ALTER TABLE USERS
    ADD `CONNECTION_TYPE` integer NOT NULL DEFAULT 0;
DROP VIEW SCI_USERS;
CREATE VIEW SCI_USERS AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , CASE `IS_ENABLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ENABLED`
    , `DEFAULT_G_ID`                 AS `DEFAULT_G_ID`
    , CASE `CONNECTION_TYPE` WHEN 0 THEN 'PLAIN' WHEN 1 THEN 'SSL' WHEN 2 THEN 'SSL_AUTH' END AS `CONNECTION_TYPE`
    , `DELETE_VERSION`               AS `DELETE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM USERS;
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE USER_EQUIV (
    `ID`                           decimal(20) NOT NULL
    , `U_ID`                         decimal(20)     NOT NULL
    , `ALT_U_TYPE`                   integer         NOT NULL
    , `ALT_U_ID`                     decimal(20)     NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_USER_EQUIV
ON USER_EQUIV(`ID`);
DROP VIEW SCI_USER_EQUIV;
CREATE VIEW SCI_USER_EQUIV AS
SELECT
    ID
    , `U_ID`                         AS `U_ID`
    , CASE `ALT_U_TYPE` WHEN 0 THEN 'USER' WHEN 1 THEN 'SERVER' END AS `ALT_U_TYPE`
    , `ALT_U_ID`                     AS `ALT_U_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM USER_EQUIV;
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE VERSIONED_EXTENTS (
    `ID`                           decimal(20) NOT NULL
    , `O_ID`                         decimal(20)     NOT NULL
    , `SEQUENCE`                     integer         NOT NULL
    , `EXTENT`                       varchar(256)    NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
    , `VALID_FROM`                 decimal(20) NOT NULL
    , `VALID_TO`                   decimal(20) NOT NULL
) ENGINE = INNODB;
CREATE INDEX PK_VERSIONED_EXTENTS
ON VERSIONED_EXTENTS(`ID`);
DROP VIEW SCI_C_VERSIONED_EXTENTS;
DROP VIEW SCI_V_VERSIONED_EXTENTS;
CREATE VIEW SCI_C_VERSIONED_EXTENTS AS
SELECT
    ID
    , `O_ID`                         AS `O_ID`
    , `SEQUENCE`                     AS `SEQUENCE`
    , `EXTENT`                       AS `EXTENT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM VERSIONED_EXTENTS
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_VERSIONED_EXTENTS AS
SELECT
    ID
    , `O_ID`                         AS `O_ID`
    , `SEQUENCE`                     AS `SEQUENCE`
    , `EXTENT`                       AS `EXTENT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM VERSIONED_EXTENTS;
