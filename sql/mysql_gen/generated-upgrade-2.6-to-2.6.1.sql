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
CREATE TABLE ARC_AUDIT_TRAIL (
    ID                             decimal(20) NOT NULL
    , `USER_ID`                      decimal(20)      NULL
    , `TS`                           decimal(20)      NULL
    , `TXID`                         decimal(20)      NULL
    , `ACTION`                       integer          NULL
    , `OBJECT_TYPE`                  integer          NULL
    , `OBJECT_ID`                    decimal(20)      NULL
    , `ORIGIN_ID`                    decimal(20)      NULL
    , `IS_SET_WARNING`               integer          NULL
    , `ACTION_INFO`                  varchar(1024)    NULL
    , `ACTION_COMMENT`               varchar(1024)    NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
CREATE TABLE ARC_DEPENDENCY_INSTANCE (
    ID                             decimal(20) NOT NULL
    , `DD_ID`                        decimal(20)      NULL
    , `DEPENDENT_ID`                 decimal(20)      NULL
    , `DEPENDENT_ID_ORIG`            decimal(20)      NULL
    , `DEPENDENCY_OPERATION`         integer          NULL
    , `REQUIRED_ID`                  decimal(20)      NULL
    , `STATE`                        integer          NULL
    , `IGNORE`                       integer          NULL
    , `DI_ID_ORIG`                   decimal(20)      NULL
    , `SE_VERSION`                   decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
CREATE TABLE ARC_ENTITY_VARIABLE (
    ID                             decimal(20) NOT NULL
    , `SME_ID`                       decimal(20)      NULL
    , `NAME`                         varchar(64)      NULL
    , `VALUE`                        varchar(256)     NULL
    , `IS_LOCAL`                     integer          NULL
    , `EV_LINK`                      decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
CREATE TABLE ARC_HIERARCHY_INSTANCE (
    ID                             decimal(20) NOT NULL
    , `PARENT_ID`                    decimal(20)      NULL
    , `CHILD_ID`                     decimal(20)      NULL
    , `SH_ID`                        decimal(20)      NULL
    , `NICE`                         integer          NULL
    , `CHILD_ESD_ID`                 decimal(20)      NULL
    , `CHILD_ES_PREFERENCE`          integer          NULL
    , `SE_VERSION`                   decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
CREATE TABLE ARC_KILL_JOB (
    ID                             decimal(20) NOT NULL
    , `SE_ID`                        decimal(20)      NULL
    , `SE_VERSION`                   decimal(20)      NULL
    , `SME_ID`                       decimal(20)      NULL
    , `SCOPE_ID`                     decimal(20)      NULL
    , `STATE`                        integer          NULL
    , `EXIT_CODE`                    integer          NULL
    , `COMMANDLINE`                  varchar(512)     NULL
    , `LOGFILE`                      varchar(512)     NULL
    , `ERRLOGFILE`                   varchar(512)     NULL
    , `PID`                          varchar(32)      NULL
    , `EXTPID`                       varchar(32)      NULL
    , `ERROR_MSG`                    varchar(256)     NULL
    , `RUNNABLE_TS`                  decimal(20)      NULL
    , `START_TS`                     decimal(20)      NULL
    , `FINSH_TS`                     decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE `NICE_PROFILE` (
    `ID`                           decimal(20) NOT NULL
    , `NAME`                         varchar(64)     NOT NULL
    , `IS_ACTIVE`                    integer         NOT NULL
    , `ACTIVE_TS`                    decimal(20)         NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_NICE_PROFILE
ON `NICE_PROFILE`(`ID`);
DROP VIEW SCI_NICE_PROFILE;
CREATE VIEW SCI_NICE_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , CASE `IS_ACTIVE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ACTIVE`
    , `ACTIVE_TS`                    AS `ACTIVE_TS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `NICE_PROFILE`;
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE `NICE_PROFILE_ENTRY` (
    `ID`                           decimal(20) NOT NULL
    , `NP_ID`                        decimal(20)     NOT NULL
    , `PREFERENCE`                   integer         NOT NULL
    , `FOLDER_ID`                    decimal(20)         NULL
    , `IS_SUSPENDED`                 integer         NOT NULL
    , `RENICE`                       integer         NOT NULL
    , `IS_ACTIVE`                    integer         NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_NICE_PROFILE_ENTRY
ON `NICE_PROFILE_ENTRY`(`ID`);
DROP VIEW SCI_NICE_PROFILE_ENTRY;
CREATE VIEW SCI_NICE_PROFILE_ENTRY AS
SELECT
    ID
    , `NP_ID`                        AS `NP_ID`
    , `PREFERENCE`                   AS `PREFERENCE`
    , `FOLDER_ID`                    AS `FOLDER_ID`
    , CASE `IS_SUSPENDED` WHEN 0 THEN 'NOSUSPEND' WHEN 1 THEN 'SUSPEND' WHEN 2 THEN 'ADMINSUSPEND' END AS `IS_SUSPENDED`
    , `RENICE`                       AS `RENICE`
    , CASE `IS_ACTIVE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ACTIVE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `NICE_PROFILE_ENTRY`;
CREATE TABLE ARC_SUBMITTED_ENTITY (
    ID                             decimal(20) NOT NULL
    , `ACCESS_KEY`                   decimal(20)      NULL
    , `MASTER_ID`                    decimal(20)      NULL
    , `SUBMIT_TAG`                   varchar(32)      NULL
    , `UNRESOLVED_HANDLING`          integer          NULL
    , `SE_ID`                        decimal(20)      NULL
    , `CHILD_TAG`                    varchar(70)      NULL
    , `SE_VERSION`                   decimal(20)      NULL
    , `OWNER_ID`                     decimal(20)      NULL
    , `PARENT_ID`                    decimal(20)      NULL
    , `SCOPE_ID`                     decimal(20)      NULL
    , `IS_STATIC`                    integer          NULL
    , `MERGE_MODE`                   integer          NULL
    , `STATE`                        integer          NULL
    , `JOB_ESD_ID`                   decimal(20)      NULL
    , `JOB_ESD_PREF`                 integer          NULL
    , `JOB_IS_FINAL`                 integer          NULL
    , `JOB_IS_RESTARTABLE`           integer          NULL
    , `FINAL_ESD_ID`                 decimal(20)      NULL
    , `EXIT_CODE`                    integer          NULL
    , `COMMANDLINE`                  varchar(512)     NULL
    , `RR_COMMANDLINE`               varchar(512)     NULL
    , `RERUN_SEQ`                    integer          NULL
    , `IS_REPLACED`                  integer          NULL
    , `IS_CANCELLED`                 integer          NULL
    , `BASE_SME_ID`                  decimal(20)      NULL
    , `REASON_SME_ID`                decimal(20)      NULL
    , `FIRE_SME_ID`                  decimal(20)      NULL
    , `FIRE_SE_ID`                   decimal(20)      NULL
    , `TR_ID`                        decimal(20)      NULL
    , `TR_SD_ID_OLD`                 decimal(20)      NULL
    , `TR_SD_ID_NEW`                 decimal(20)      NULL
    , `TR_SEQ`                       integer          NULL
    , `WORKDIR`                      varchar(512)     NULL
    , `LOGFILE`                      varchar(512)     NULL
    , `ERRLOGFILE`                   varchar(512)     NULL
    , `PID`                          varchar(32)      NULL
    , `EXTPID`                       varchar(32)      NULL
    , `ERROR_MSG`                    varchar(256)     NULL
    , `KILL_ID`                      decimal(20)      NULL
    , `KILL_EXIT_CODE`               integer          NULL
    , `IS_SUSPENDED`                 integer          NULL
    , `IS_SUSPENDED_LOCAL`           integer          NULL
    , `PRIORITY`                     integer          NULL
    , `RAW_PRIORITY`                 integer          NULL
    , `NICE`                         integer          NULL
    , `NP_NICE`                      integer          NULL
    , `MIN_PRIORITY`                 integer          NULL
    , `AGING_AMOUNT`                 integer          NULL
    , `PARENT_SUSPENDED`             integer          NULL
    , `CHILD_SUSPENDED`              integer          NULL
    , `WARN_COUNT`                   integer          NULL
    , `WARN_LINK`                    decimal(20)      NULL
    , `SUBMIT_TS`                    decimal(20)      NULL
    , `RESUME_TS`                    decimal(20)      NULL
    , `SYNC_TS`                      decimal(20)      NULL
    , `RESOURCE_TS`                  decimal(20)      NULL
    , `RUNNABLE_TS`                  decimal(20)      NULL
    , `START_TS`                     decimal(20)      NULL
    , `FINSH_TS`                     decimal(20)      NULL
    , `FINAL_TS`                     decimal(20)      NULL
    , `CNT_SUBMITTED`                integer          NULL
    , `CNT_DEPENDENCY_WAIT`          integer          NULL
    , `CNT_SYNCHRONIZE_WAIT`         integer          NULL
    , `CNT_RESOURCE_WAIT`            integer          NULL
    , `CNT_RUNNABLE`                 integer          NULL
    , `CNT_STARTING`                 integer          NULL
    , `CNT_STARTED`                  integer          NULL
    , `CNT_RUNNING`                  integer          NULL
    , `CNT_TO_KILL`                  integer          NULL
    , `CNT_KILLED`                   integer          NULL
    , `CNT_CANCELLED`                integer          NULL
    , `CNT_FINISHED`                 integer          NULL
    , `CNT_FINAL`                    integer          NULL
    , `CNT_BROKEN_ACTIVE`            integer          NULL
    , `CNT_BROKEN_FINISHED`          integer          NULL
    , `CNT_ERROR`                    integer          NULL
    , `CNT_UNREACHABLE`              integer          NULL
    , `CNT_RESTARTABLE`              integer          NULL
    , `CNT_WARN`                     integer          NULL
    , `CNT_PENDING`                  integer          NULL
    , `IDLE_TS`                      integer          NULL
    , `IDLE_TIME`                    integer          NULL
    , `STATISTIC_TS`                 integer          NULL
    , `DEPENDENCY_WAIT_TIME`         integer          NULL
    , `SUSPEND_TIME`                 integer          NULL
    , `SYNC_TIME`                    integer          NULL
    , `RESOURCE_TIME`                integer          NULL
    , `JOBSERVER_TIME`               integer          NULL
    , `RESTARTABLE_TIME`             integer          NULL
    , `CHILD_WAIT_TIME`              integer          NULL
    , `OP_SUSRES_TS`                 decimal(20)      NULL
    , `NPE_ID`                       decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
ALTER TABLE `SUBMITTED_ENTITY`
    ADD `RAW_PRIORITY` integer NOT NULL DEFAULT 0,
    ADD `NP_NICE` integer NOT NULL DEFAULT 0,
    ADD `IDLE_TS` integer,
    ADD `IDLE_TIME` integer,
    ADD `STATISTIC_TS` integer,
    ADD `DEPENDENCY_WAIT_TIME` integer,
    ADD `SUSPEND_TIME` integer,
    ADD `SYNC_TIME` integer,
    ADD `RESOURCE_TIME` integer,
    ADD `JOBSERVER_TIME` integer,
    ADD `RESTARTABLE_TIME` integer,
    ADD `CHILD_WAIT_TIME` integer,
    ADD `OP_SUSRES_TS` decimal(20),
    ADD `NPE_ID` decimal(20);
DROP VIEW SCI_SUBMITTED_ENTITY;
CREATE VIEW SCI_SUBMITTED_ENTITY AS
SELECT
    ID
    , `MASTER_ID`                    AS `MASTER_ID`
    , `SUBMIT_TAG`                   AS `SUBMIT_TAG`
    , CASE `UNRESOLVED_HANDLING` WHEN 1 THEN 'UH_IGNORE' WHEN 3 THEN 'UH_SUSPEND' WHEN 2 THEN 'UH_ERROR' END AS `UNRESOLVED_HANDLING`
    , `SE_ID`                        AS `SE_ID`
    , `CHILD_TAG`                    AS `CHILD_TAG`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , CASE `IS_STATIC` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STATIC`
    , CASE `MERGE_MODE` WHEN 1 THEN 'MERGE_LOCAL' WHEN 2 THEN 'MERGE_GLOBAL' WHEN 3 THEN 'NOMERGE' WHEN 4 THEN 'FAILURE' END AS `MERGE_MODE`
    , CASE `STATE` WHEN 0 THEN 'SUBMITTED' WHEN 1 THEN 'DEPENDENCY_WAIT' WHEN 2 THEN 'SYNCHRONIZE_WAIT' WHEN 3 THEN 'RESOURCE_WAIT' WHEN 4 THEN 'RUNNABLE' WHEN 5 THEN 'STARTING' WHEN 6 THEN 'STARTED' WHEN 7 THEN 'RUNNING' WHEN 8 THEN 'TO_KILL' WHEN 9 THEN 'KILLED' WHEN 10 THEN 'CANCELLED' WHEN 11 THEN 'FINISHED' WHEN 12 THEN 'FINAL' WHEN 13 THEN 'BROKEN_ACTIVE' WHEN 14 THEN 'BROKEN_FINISHED' WHEN 15 THEN 'ERROR' WHEN 16 THEN 'UNREACHABLE' END AS `STATE`
    , `JOB_ESD_ID`                   AS `JOB_ESD_ID`
    , `JOB_ESD_PREF`                 AS `JOB_ESD_PREF`
    , CASE `JOB_IS_FINAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `JOB_IS_FINAL`
    , CASE `JOB_IS_RESTARTABLE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `JOB_IS_RESTARTABLE`
    , `FINAL_ESD_ID`                 AS `FINAL_ESD_ID`
    , `EXIT_CODE`                    AS `EXIT_CODE`
    , `COMMANDLINE`                  AS `COMMANDLINE`
    , `RR_COMMANDLINE`               AS `RR_COMMANDLINE`
    , `RERUN_SEQ`                    AS `RERUN_SEQ`
    , CASE `IS_REPLACED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_REPLACED`
    , CASE `IS_CANCELLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_CANCELLED`
    , `BASE_SME_ID`                  AS `BASE_SME_ID`
    , `REASON_SME_ID`                AS `REASON_SME_ID`
    , `FIRE_SME_ID`                  AS `FIRE_SME_ID`
    , `FIRE_SE_ID`                   AS `FIRE_SE_ID`
    , `TR_ID`                        AS `TR_ID`
    , `TR_SD_ID_OLD`                 AS `TR_SD_ID_OLD`
    , `TR_SD_ID_NEW`                 AS `TR_SD_ID_NEW`
    , `TR_SEQ`                       AS `TR_SEQ`
    , `WORKDIR`                      AS `WORKDIR`
    , `LOGFILE`                      AS `LOGFILE`
    , `ERRLOGFILE`                   AS `ERRLOGFILE`
    , `PID`                          AS `PID`
    , `EXTPID`                       AS `EXTPID`
    , `ERROR_MSG`                    AS `ERROR_MSG`
    , `KILL_ID`                      AS `KILL_ID`
    , `KILL_EXIT_CODE`               AS `KILL_EXIT_CODE`
    , CASE `IS_SUSPENDED` WHEN 2 THEN 'ADMINSUSPEND' WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS `IS_SUSPENDED`
    , CASE `IS_SUSPENDED_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_SUSPENDED_LOCAL`
    , `PRIORITY`                     AS `PRIORITY`
    , `RAW_PRIORITY`                 AS `RAW_PRIORITY`
    , `NICE`                         AS `NICE`
    , `NP_NICE`                      AS `NP_NICE`
    , `MIN_PRIORITY`                 AS `MIN_PRIORITY`
    , `AGING_AMOUNT`                 AS `AGING_AMOUNT`
    , `PARENT_SUSPENDED`             AS `PARENT_SUSPENDED`
    , `CHILD_SUSPENDED`              AS `CHILD_SUSPENDED`
    , `WARN_COUNT`                   AS `WARN_COUNT`
    , `WARN_LINK`                    AS `WARN_LINK`
    , from_unixtime((`SUBMIT_TS` & ~1125899906842624)/1000) AS `SUBMIT_TS`
    , from_unixtime((`RESUME_TS` & ~1125899906842624)/1000) AS `RESUME_TS`
    , from_unixtime((`SYNC_TS` & ~1125899906842624)/1000) AS `SYNC_TS`
    , from_unixtime((`RESOURCE_TS` & ~1125899906842624)/1000) AS `RESOURCE_TS`
    , from_unixtime((`RUNNABLE_TS` & ~1125899906842624)/1000) AS `RUNNABLE_TS`
    , from_unixtime((`START_TS` & ~1125899906842624)/1000) AS `START_TS`
    , from_unixtime((`FINSH_TS` & ~1125899906842624)/1000) AS `FINSH_TS`
    , from_unixtime((`FINAL_TS` & ~1125899906842624)/1000) AS `FINAL_TS`
    , `IDLE_TIME`                    AS `IDLE_TIME`
    , `DEPENDENCY_WAIT_TIME`         AS `DEPENDENCY_WAIT_TIME`
    , `SUSPEND_TIME`                 AS `SUSPEND_TIME`
    , `SYNC_TIME`                    AS `SYNC_TIME`
    , `RESOURCE_TIME`                AS `RESOURCE_TIME`
    , `JOBSERVER_TIME`               AS `JOBSERVER_TIME`
    , `RESTARTABLE_TIME`             AS `RESTARTABLE_TIME`
    , `CHILD_WAIT_TIME`              AS `CHILD_WAIT_TIME`
    , `OP_SUSRES_TS`                 AS `OP_SUSRES_TS`
    , `NPE_ID`                       AS `NPE_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , ((IFNULL(`FINAL_TS`, UNIX_TIMESTAMP(NOW()) * 1000) - `SUBMIT_TS`) / 1000) - `DEPENDENCY_WAIT_TIME` AS `PROCESS_TIME`
  FROM `SUBMITTED_ENTITY`;
UPDATE `SUBMITTED_ENTITY` SET `RAW_PRIORITY` = `PRIORITY`;
