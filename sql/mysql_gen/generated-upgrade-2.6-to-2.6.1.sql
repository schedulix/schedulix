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
DROP VIEW SCI_AUDIT_TRAIL;
CREATE VIEW SCI_AUDIT_TRAIL AS
SELECT
    ID
    , `USER_ID`                      AS `USER_ID`
    , from_unixtime((`TS` & ~1125899906842624)/1000) AS `TS`
    , `TXID`                         AS `TXID`
    , CASE `ACTION` WHEN 1 THEN 'RERUN' WHEN 2 THEN 'RERUN_RECURSIVE' WHEN 3 THEN 'CANCEL' WHEN 4 THEN 'SUSPEND' WHEN 5 THEN 'RESUME' WHEN 6 THEN 'SET_STATE' WHEN 7 THEN 'SET_EXIT_STATE' WHEN 8 THEN 'IGNORE_DEPENDENCY' WHEN 9 THEN 'IGNORE_DEP_RECURSIVE' WHEN 10 THEN 'IGNORE_RESOURCE' WHEN 11 THEN 'KILL' WHEN 12 THEN 'ALTER_RUN_PROGRAM' WHEN 13 THEN 'ALTER_RERUN_PROGRAM' WHEN 14 THEN 'COMMENT_JOB' WHEN 15 THEN 'SUBMITTED' WHEN 16 THEN 'TRIGGER_FAILED' WHEN 17 THEN 'TRIGGER_SUBMIT' WHEN 18 THEN 'JOB_RESTARTABLE' WHEN 19 THEN 'CHANGE_PRIORITY' WHEN 20 THEN 'RENICE' WHEN 21 THEN 'SUBMIT_SUSPENDED' WHEN 22 THEN 'IGNORE_NAMED_RESOURCE' WHEN 23 THEN 'TIMEOUT' WHEN 24 THEN 'SET_RESOURCE_STATE' WHEN 25 THEN 'JOB_IN_ERROR' WHEN 26 THEN 'CLEAR_WARNING' WHEN 27 THEN 'SET_WARNING' WHEN 28 THEN 'JOB_UNREACHABLE' WHEN 29 THEN 'SET_PARAMETERS' WHEN 30 THEN 'DISABLE' WHEN 31 THEN 'ENABLE' WHEN 32 THEN 'CLONE' WHEN 33 THEN 'APPROVE' WHEN 34 THEN 'REJECT' WHEN 35 THEN 'APPROVAL_REQUEST' WHEN 36 THEN 'REVIEW_REQUEST' WHEN 37 THEN 'KILL_RECURSIVE' END AS `ACTION`
    , CASE `OBJECT_TYPE` WHEN 17 THEN 'JOB' END AS `OBJECT_TYPE`
    , `OBJECT_ID`                    AS `OBJECT_ID`
    , `ORIGIN_ID`                    AS `ORIGIN_ID`
    , CASE `IS_SET_WARNING` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_SET_WARNING`
    , `ACTION_INFO`                  AS `ACTION_INFO`
    , `ACTION_COMMENT`               AS `ACTION_COMMENT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `AUDIT_TRAIL`;
DROP VIEW SCI_CALENDAR;
CREATE VIEW SCI_CALENDAR AS
SELECT
    ID
    , `SCEV_ID`                      AS `SCEV_ID`
    , from_unixtime((`STARTTIME` & ~1125899906842624)/1000) AS `STARTTIME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `CALENDAR`;
DROP VIEW SCI_C_DEPENDENCY_DEFINITION;
DROP VIEW SCI_V_DEPENDENCY_DEFINITION;
CREATE VIEW SCI_C_DEPENDENCY_DEFINITION AS
SELECT
    ID
    , `SE_DEPENDENT_ID`              AS `SE_DEPENDENT_ID`
    , `SE_REQUIRED_ID`               AS `SE_REQUIRED_ID`
    , `NAME`                         AS `NAME`
    , CASE `UNRESOLVED_HANDLING` WHEN 1 THEN 'IGNORE' WHEN 2 THEN 'ERROR' WHEN 3 THEN 'SUSPEND' WHEN 4 THEN 'DEFER' WHEN 5 THEN 'DEFER_IGNORE' END AS `UNRESOLVED_HANDLING`
    , CASE `DMODE` WHEN 1 THEN 'ALL_FINAL' WHEN 2 THEN 'JOB_FINAL' END AS `DMODE`
    , CASE `STATE_SELECTION` WHEN 0 THEN 'FINAL' WHEN 1 THEN 'ALL_REACHABLE' WHEN 2 THEN 'UNREACHABLE' WHEN 3 THEN 'DEFAULT' END AS `STATE_SELECTION`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `DEPENDENCY_DEFINITION`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_DEPENDENCY_DEFINITION AS
SELECT
    ID
    , `SE_DEPENDENT_ID`              AS `SE_DEPENDENT_ID`
    , `SE_REQUIRED_ID`               AS `SE_REQUIRED_ID`
    , `NAME`                         AS `NAME`
    , CASE `UNRESOLVED_HANDLING` WHEN 1 THEN 'IGNORE' WHEN 2 THEN 'ERROR' WHEN 3 THEN 'SUSPEND' WHEN 4 THEN 'DEFER' WHEN 5 THEN 'DEFER_IGNORE' END AS `UNRESOLVED_HANDLING`
    , CASE `DMODE` WHEN 1 THEN 'ALL_FINAL' WHEN 2 THEN 'JOB_FINAL' END AS `DMODE`
    , CASE `STATE_SELECTION` WHEN 0 THEN 'FINAL' WHEN 1 THEN 'ALL_REACHABLE' WHEN 2 THEN 'UNREACHABLE' WHEN 3 THEN 'DEFAULT' END AS `STATE_SELECTION`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `DEPENDENCY_DEFINITION`;
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
DROP VIEW SCI_DEPENDENCY_INSTANCE;
CREATE VIEW SCI_DEPENDENCY_INSTANCE AS
SELECT
    ID
    , `DD_ID`                        AS `DD_ID`
    , `DEPENDENT_ID`                 AS `DEPENDENT_ID`
    , `DEPENDENT_ID_ORIG`            AS `DEPENDENT_ID_ORIG`
    , CASE `DEPENDENCY_OPERATION` WHEN 1 THEN 'AND' WHEN 2 THEN 'OR' END AS `DEPENDENCY_OPERATION`
    , `REQUIRED_ID`                  AS `REQUIRED_ID`
    , CASE `STATE` WHEN 0 THEN 'OPEN' WHEN 1 THEN 'FULFILLED' WHEN 2 THEN 'FAILED' WHEN 3 THEN 'BROKEN' WHEN 4 THEN 'DEFERRED' WHEN 8 THEN 'CANCELLED' END AS `STATE`
    , CASE `IGNORE` WHEN 0 THEN 'NO' WHEN 1 THEN 'YES' WHEN 2 THEN 'RECURSIVE' END AS `IGNORE`
    , `DI_ID_ORIG`                   AS `DI_ID_ORIG`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `DEPENDENCY_INSTANCE`;
DROP VIEW SCI_C_DEPENDENCY_STATE;
DROP VIEW SCI_V_DEPENDENCY_STATE;
CREATE VIEW SCI_C_DEPENDENCY_STATE AS
SELECT
    ID
    , `DD_ID`                        AS `DD_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `DEPENDENCY_STATE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_DEPENDENCY_STATE AS
SELECT
    ID
    , `DD_ID`                        AS `DD_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `DEPENDENCY_STATE`;
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
DROP VIEW SCI_ENTITY_VARIABLE;
CREATE VIEW SCI_ENTITY_VARIABLE AS
SELECT
    ID
    , `SME_ID`                       AS `SME_ID`
    , `NAME`                         AS `NAME`
    , `VALUE`                        AS `VALUE`
    , CASE `IS_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LOCAL`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `ENTITY_VARIABLE`;
DROP VIEW SCI_C_ENVIRONMENT;
DROP VIEW SCI_V_ENVIRONMENT;
CREATE VIEW SCI_C_ENVIRONMENT AS
SELECT
    ID
    , `NE_ID`                        AS `NE_ID`
    , `NR_ID`                        AS `NR_ID`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `ENVIRONMENT`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_ENVIRONMENT AS
SELECT
    ID
    , `NE_ID`                        AS `NE_ID`
    , `NR_ID`                        AS `NR_ID`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `ENVIRONMENT`;
DROP VIEW SCI_EVENT;
CREATE VIEW SCI_EVENT AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `SE_ID`                        AS `SE_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EVENT`;
DROP VIEW SCI_EVENT_PARAMETER;
CREATE VIEW SCI_EVENT_PARAMETER AS
SELECT
    ID
    , `KEY`                          AS `KEY`
    , `VALUE`                        AS `VALUE`
    , `EVT_ID`                       AS `EVT_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EVENT_PARAMETER`;
DROP VIEW SCI_C_EXIT_STATE;
DROP VIEW SCI_V_EXIT_STATE;
CREATE VIEW SCI_C_EXIT_STATE AS
SELECT
    ID
    , `PREFERENCE`                   AS `PREFERENCE`
    , CASE `IS_FINAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_FINAL`
    , CASE `IS_RESTARTABLE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_RESTARTABLE`
    , CASE `IS_UNREACHABLE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_UNREACHABLE`
    , CASE `IS_BROKEN` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_BROKEN`
    , CASE `IS_BATCH_DEFAULT` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_BATCH_DEFAULT`
    , CASE `IS_DEPENDENCY_DEFAULT` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_DEPENDENCY_DEFAULT`
    , `ESP_ID`                       AS `ESP_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE AS
SELECT
    ID
    , `PREFERENCE`                   AS `PREFERENCE`
    , CASE `IS_FINAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_FINAL`
    , CASE `IS_RESTARTABLE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_RESTARTABLE`
    , CASE `IS_UNREACHABLE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_UNREACHABLE`
    , CASE `IS_BROKEN` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_BROKEN`
    , CASE `IS_BATCH_DEFAULT` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_BATCH_DEFAULT`
    , CASE `IS_DEPENDENCY_DEFAULT` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_DEPENDENCY_DEFAULT`
    , `ESP_ID`                       AS `ESP_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE`;
DROP VIEW SCI_C_EXIT_STATE_DEFINITION;
DROP VIEW SCI_V_EXIT_STATE_DEFINITION;
CREATE VIEW SCI_C_EXIT_STATE_DEFINITION AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE_DEFINITION`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE_DEFINITION AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE_DEFINITION`;
DROP VIEW SCI_C_EXIT_STATE_MAPPING;
DROP VIEW SCI_V_EXIT_STATE_MAPPING;
CREATE VIEW SCI_C_EXIT_STATE_MAPPING AS
SELECT
    ID
    , `ESMP_ID`                      AS `ESMP_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `ECR_START`                    AS `ECR_START`
    , `ECR_END`                      AS `ECR_END`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE_MAPPING`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE_MAPPING AS
SELECT
    ID
    , `ESMP_ID`                      AS `ESMP_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `ECR_START`                    AS `ECR_START`
    , `ECR_END`                      AS `ECR_END`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE_MAPPING`;
DROP VIEW SCI_C_EXIT_STATE_MAP_PROFILE;
DROP VIEW SCI_V_EXIT_STATE_MAP_PROFILE;
CREATE VIEW SCI_C_EXIT_STATE_MAP_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE_MAPPING_PROFILE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE_MAP_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE_MAPPING_PROFILE`;
DROP VIEW SCI_C_EXIT_STATE_PROFILE;
DROP VIEW SCI_V_EXIT_STATE_PROFILE;
CREATE VIEW SCI_C_EXIT_STATE_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `DEFAULT_ESMP_ID`              AS `DEFAULT_ESMP_ID`
    , CASE `IS_VALID` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_VALID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE_PROFILE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `DEFAULT_ESMP_ID`              AS `DEFAULT_ESMP_ID`
    , CASE `IS_VALID` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_VALID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE_PROFILE`;
DROP VIEW SCI_C_EXIT_STATE_TRANSLATION;
DROP VIEW SCI_V_EXIT_STATE_TRANSLATION;
CREATE VIEW SCI_C_EXIT_STATE_TRANSLATION AS
SELECT
    ID
    , `ESTP_ID`                      AS `ESTP_ID`
    , `FROM_ESD_ID`                  AS `FROM_ESD_ID`
    , `TO_ESD_ID`                    AS `TO_ESD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE_TRANSLATION`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE_TRANSLATION AS
SELECT
    ID
    , `ESTP_ID`                      AS `ESTP_ID`
    , `FROM_ESD_ID`                  AS `FROM_ESD_ID`
    , `TO_ESD_ID`                    AS `TO_ESD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE_TRANSLATION`;
DROP VIEW SCI_C_EXIT_STATE_TRANS_PROFILE;
DROP VIEW SCI_V_EXIT_STATE_TRANS_PROFILE;
CREATE VIEW SCI_C_EXIT_STATE_TRANS_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `EXIT_STATE_TRANS_PROFILE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_EXIT_STATE_TRANS_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `EXIT_STATE_TRANS_PROFILE`;
DROP VIEW SCI_C_FOLDER;
DROP VIEW SCI_V_FOLDER;
CREATE VIEW SCI_C_FOLDER AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `ENV_ID`                       AS `ENV_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
  FROM `FOLDER`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_FOLDER AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `ENV_ID`                       AS `ENV_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
    , VALID_FROM
    , VALID_TO
  FROM `FOLDER`;
DROP VIEW SCI_FOOTPRINT;
CREATE VIEW SCI_FOOTPRINT AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `FOOTPRINT`;
DROP VIEW SCI_GRANTS;
CREATE VIEW SCI_GRANTS AS
SELECT
    ID
    , `OBJECT_ID`                    AS `OBJECT_ID`
    , `G_ID`                         AS `G_ID`
    , CASE `OBJECT_TYPE` WHEN 1 THEN 'ENVIRONMENT' WHEN 18 THEN 'EVENT' WHEN 6 THEN 'FOLDER' WHEN 19 THEN 'INTERVAL' WHEN 17 THEN 'JOB' WHEN 9 THEN 'JOB_DEFINITION' WHEN 10 THEN 'NAMED_RESOURCE' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 15 THEN 'SCOPE' WHEN 21 THEN 'GROUP' WHEN 11 THEN 'RESOURCE' WHEN 2 THEN 'EXIT_STATE_DEFINITION' WHEN 31 THEN 'NICE_PROFILE' WHEN 3 THEN 'EXIT_STATE_PROFILE' WHEN 4 THEN 'EXIT_STATE_MAPPING' WHEN 5 THEN 'EXIT_STATE_TRANSLATION' WHEN 13 THEN 'RESOURCE_STATE_DEFINITION' WHEN 14 THEN 'RESOURCE_STATE_PROFILE' WHEN 29 THEN 'WATCH_TYPE' WHEN 12 THEN 'RESOURCE_STATE_MAPPING' WHEN 7 THEN 'FOOTPRINT' WHEN 8 THEN 'USER' WHEN 30 THEN 'OBJECT_MONITOR' WHEN 0 THEN 'SYSTEM' END AS `OBJECT_TYPE`
    , `PRIVS`                        AS `PRIVS`
    , `DELETE_VERSION`               AS `DELETE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `GRANTS`;
DROP VIEW SCI_GROUPS;
CREATE VIEW SCI_GROUPS AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `DELETE_VERSION`               AS `DELETE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `GROUPS`;
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
DROP VIEW SCI_HIERARCHY_INSTANCE;
CREATE VIEW SCI_HIERARCHY_INSTANCE AS
SELECT
    ID
    , `PARENT_ID`                    AS `PARENT_ID`
    , `CHILD_ID`                     AS `CHILD_ID`
    , `SH_ID`                        AS `SH_ID`
    , `NICE`                         AS `NICE`
    , `CHILD_ESD_ID`                 AS `CHILD_ESD_ID`
    , `CHILD_ES_PREFERENCE`          AS `CHILD_ES_PREFERENCE`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `HIERARCHY_INSTANCE`;
DROP VIEW SCI_C_IGNORED_DEPENDENCY;
DROP VIEW SCI_V_IGNORED_DEPENDENCY;
CREATE VIEW SCI_C_IGNORED_DEPENDENCY AS
SELECT
    ID
    , `SH_ID`                        AS `SH_ID`
    , `DD_NAME`                      AS `DD_NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `IGNORED_DEPENDENCY`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_IGNORED_DEPENDENCY AS
SELECT
    ID
    , `SH_ID`                        AS `SH_ID`
    , `DD_NAME`                      AS `DD_NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `IGNORED_DEPENDENCY`;
DROP VIEW SCI_INSTANCE_VALUE;
CREATE VIEW SCI_INSTANCE_VALUE AS
SELECT
    ID
    , `VALUE`                        AS `VALUE`
    , `OI_ID`                        AS `OI_ID`
    , `WTP_ID`                       AS `WTP_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `INSTANCE_VALUE`;
DROP VIEW SCI_INTERVALL;
CREATE VIEW SCI_INTERVALL AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , from_unixtime((`START_TIME` & ~1125899906842624)/1000) AS `START_TIME`
    , from_unixtime((`END_TIME` & ~1125899906842624)/1000) AS `END_TIME`
    , `DELAY`                        AS `DELAY`
    , CASE `BASE_INTERVAL` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `BASE_INTERVAL`
    , `BASE_INTERVAL_MULTIPLIER`     AS `BASE_INTERVAL_MULTIPLIER`
    , CASE `DURATION` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `DURATION`
    , `DURATION_MULTIPLIER`          AS `DURATION_MULTIPLIER`
    , from_unixtime((`SYNC_TIME` & ~1125899906842624)/1000) AS `SYNC_TIME`
    , CASE `IS_INVERSE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_INVERSE`
    , CASE `IS_MERGE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_MERGE`
    , `EMBEDDED_INT_ID`              AS `EMBEDDED_INT_ID`
    , `SE_ID`                        AS `SE_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `INTERVALL`;
DROP VIEW SCI_INTERVAL_HIERARCHY;
CREATE VIEW SCI_INTERVAL_HIERARCHY AS
SELECT
    ID
    , `CHILD_ID`                     AS `CHILD_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `INTERVAL_HIERARCHY`;
DROP VIEW SCI_INTERVAL_SELECTION;
CREATE VIEW SCI_INTERVAL_SELECTION AS
SELECT
    ID
    , `INT_ID`                       AS `INT_ID`
    , `VALUE`                        AS `VALUE`
    , `PERIOD_FROM`                  AS `PERIOD_FROM`
    , `PERIOD_TO`                    AS `PERIOD_TO`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `INTERVAL_SELECTION`;
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
DROP VIEW SCI_KILL_JOB;
CREATE VIEW SCI_KILL_JOB AS
SELECT
    ID
    , `SE_ID`                        AS `SE_ID`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `SME_ID`                       AS `SME_ID`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , CASE `STATE` WHEN 4 THEN 'RUNNABLE' WHEN 5 THEN 'STARTING' WHEN 6 THEN 'STARTED' WHEN 7 THEN 'RUNNING' WHEN 11 THEN 'FINISHED' WHEN 13 THEN 'BROKEN_ACTIVE' WHEN 14 THEN 'BROKEN_FINISHED' WHEN 15 THEN 'ERROR' END AS `STATE`
    , `EXIT_CODE`                    AS `EXIT_CODE`
    , `COMMANDLINE`                  AS `COMMANDLINE`
    , `LOGFILE`                      AS `LOGFILE`
    , `ERRLOGFILE`                   AS `ERRLOGFILE`
    , `PID`                          AS `PID`
    , `EXTPID`                       AS `EXTPID`
    , `ERROR_MSG`                    AS `ERROR_MSG`
    , from_unixtime((`RUNNABLE_TS` & ~1125899906842624)/1000) AS `RUNNABLE_TS`
    , from_unixtime((`START_TS` & ~1125899906842624)/1000) AS `START_TS`
    , from_unixtime((`FINSH_TS` & ~1125899906842624)/1000) AS `FINSH_TS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `KILL_JOB`;
DROP VIEW SCI_C_NAMED_ENVIRONMENT;
DROP VIEW SCI_V_NAMED_ENVIRONMENT;
CREATE VIEW SCI_C_NAMED_ENVIRONMENT AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `NAMED_ENVIRONMENT`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_NAMED_ENVIRONMENT AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `NAMED_ENVIRONMENT`;
DROP VIEW SCI_NAMED_RESOURCE;
CREATE VIEW SCI_NAMED_RESOURCE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , CASE `USAGE` WHEN 1 THEN 'STATIC' WHEN 2 THEN 'SYSTEM' WHEN 4 THEN 'SYNCHRONIZING' WHEN 8 THEN 'CATEGORY' WHEN 3 THEN 'POOL' END AS `USAGE`
    , `RSP_ID`                       AS `RSP_ID`
    , `FACTOR`                       AS `FACTOR`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
  FROM `NAMED_RESOURCE`;
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
DROP VIEW SCI_MEMBER;
CREATE VIEW SCI_MEMBER AS
SELECT
    ID
    , `G_ID`                         AS `G_ID`
    , `U_ID`                         AS `U_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `MEMBER`;
DROP VIEW SCI_C_OBJECT_COMMENT;
DROP VIEW SCI_V_OBJECT_COMMENT;
CREATE VIEW SCI_C_OBJECT_COMMENT AS
SELECT
    ID
    , `OBJECT_ID`                    AS `OBJECT_ID`
    , CASE `OBJECT_TYPE` WHEN 9 THEN 'JOB_DEFINITION' WHEN 2 THEN 'EXIT_STATE_DEFINITION' WHEN 3 THEN 'EXIT_STATE_PROFILE' WHEN 4 THEN 'EXIT_STATE_MAPPING' WHEN 5 THEN 'EXIT_STATE_TRANSLATION' WHEN 6 THEN 'FOLDER' WHEN 15 THEN 'SCOPE' WHEN 10 THEN 'NAMED_RESOURCE' WHEN 31 THEN 'NICE_PROFILE' WHEN 11 THEN 'RESOURCE' WHEN 1 THEN 'ENVIRONMENT' WHEN 7 THEN 'FOOTPRINT' WHEN 13 THEN 'RESOURCE_STATE_DEFINITION' WHEN 14 THEN 'RESOURCE_STATE_PROFILE' WHEN 12 THEN 'RESOURCE_STATE_MAPPING' WHEN 8 THEN 'USER' WHEN 16 THEN 'TRIGGER' WHEN 17 THEN 'JOB' WHEN 18 THEN 'EVENT' WHEN 19 THEN 'INTERVAL' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 21 THEN 'GROUP' WHEN 23 THEN 'PARAMETER' WHEN 24 THEN 'POOL' WHEN 25 THEN 'DISTRIBUTION' WHEN 29 THEN 'WATCH_TYPE' WHEN 30 THEN 'OBJECT_MONITOR' END AS `OBJECT_TYPE`
    , CASE `INFO_TYPE` WHEN 0 THEN 'TEXT' WHEN 1 THEN 'URL' END AS `INFO_TYPE`
    , `SEQUENCE_NUMBER`              AS `SEQUENCE_NUMBER`
    , `DESCRIPTION`                  AS `DESCRIPTION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `OBJECT_COMMENT`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_OBJECT_COMMENT AS
SELECT
    ID
    , `OBJECT_ID`                    AS `OBJECT_ID`
    , CASE `OBJECT_TYPE` WHEN 9 THEN 'JOB_DEFINITION' WHEN 2 THEN 'EXIT_STATE_DEFINITION' WHEN 3 THEN 'EXIT_STATE_PROFILE' WHEN 4 THEN 'EXIT_STATE_MAPPING' WHEN 5 THEN 'EXIT_STATE_TRANSLATION' WHEN 6 THEN 'FOLDER' WHEN 15 THEN 'SCOPE' WHEN 10 THEN 'NAMED_RESOURCE' WHEN 31 THEN 'NICE_PROFILE' WHEN 11 THEN 'RESOURCE' WHEN 1 THEN 'ENVIRONMENT' WHEN 7 THEN 'FOOTPRINT' WHEN 13 THEN 'RESOURCE_STATE_DEFINITION' WHEN 14 THEN 'RESOURCE_STATE_PROFILE' WHEN 12 THEN 'RESOURCE_STATE_MAPPING' WHEN 8 THEN 'USER' WHEN 16 THEN 'TRIGGER' WHEN 17 THEN 'JOB' WHEN 18 THEN 'EVENT' WHEN 19 THEN 'INTERVAL' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 21 THEN 'GROUP' WHEN 23 THEN 'PARAMETER' WHEN 24 THEN 'POOL' WHEN 25 THEN 'DISTRIBUTION' WHEN 29 THEN 'WATCH_TYPE' WHEN 30 THEN 'OBJECT_MONITOR' END AS `OBJECT_TYPE`
    , CASE `INFO_TYPE` WHEN 0 THEN 'TEXT' WHEN 1 THEN 'URL' END AS `INFO_TYPE`
    , `SEQUENCE_NUMBER`              AS `SEQUENCE_NUMBER`
    , `DESCRIPTION`                  AS `DESCRIPTION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `OBJECT_COMMENT`;
DROP VIEW SCI_OBJECT_EVENT;
CREATE VIEW SCI_OBJECT_EVENT AS
SELECT
    ID
    , `TR_ID`                        AS `TR_ID`
    , `OI_ID`                        AS `OI_ID`
    , CASE `EVENT_TYPE` WHEN 1 THEN 'CREATE' WHEN 2 THEN 'CHANGE' WHEN 3 THEN 'DELETE' END AS `EVENT_TYPE`
    , `SME_ID`                       AS `SME_ID`
    , `SE_ID`                        AS `SE_ID`
    , from_unixtime((`SUBMIT_TS` & ~1125899906842624)/1000) AS `SUBMIT_TS`
    , from_unixtime((`FINAL_TS` & ~1125899906842624)/1000) AS `FINAL_TS`
    , `FINAL_ESD_ID`                 AS `FINAL_ESD_ID`
    , `MAIN_SME_ID`                  AS `MAIN_SME_ID`
    , `MAIN_SE_ID`                   AS `MAIN_SE_ID`
    , from_unixtime((`MAIN_FINAL_TS` & ~1125899906842624)/1000) AS `MAIN_FINAL_TS`
    , `MAIN_FINAL_ESD_ID`            AS `MAIN_FINAL_ESD_ID`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `OBJECT_EVENT`;
DROP VIEW SCI_OBJECT_INSTANCE;
CREATE VIEW SCI_OBJECT_INSTANCE AS
SELECT
    ID
    , `UNIQUE_NAME`                  AS `UNIQUE_NAME`
    , `OM_ID`                        AS `OM_ID`
    , from_unixtime((`MODIFY_TS` & ~1125899906842624)/1000) AS `MODIFY_TS`
    , from_unixtime((`REMOVE_TS` & ~1125899906842624)/1000) AS `REMOVE_TS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `OBJECT_INSTANCE`;
DROP VIEW SCI_OBJECT_MONITOR;
CREATE VIEW SCI_OBJECT_MONITOR AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `WT_ID`                        AS `WT_ID`
    , CASE `RECREATE_HANDLING` WHEN 0 THEN 'NONE' WHEN 1 THEN 'CREATE' WHEN 2 THEN 'CHANGE' END AS `RECREATE_HANDLING`
    , `WATCH_SE_ID`                  AS `WATCH_SE_ID`
    , `DELETE_AMOUNT`                AS `DELETE_AMOUNT`
    , CASE `DELETE_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `DELETE_BASE`
    , `EVENT_DELETE_AMOUNT`          AS `EVENT_DELETE_AMOUNT`
    , CASE `EVENT_DELETE_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `EVENT_DELETE_BASE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `OBJECT_MONITOR`;
DROP VIEW SCI_OBJECT_MONITOR_PARAMETER;
CREATE VIEW SCI_OBJECT_MONITOR_PARAMETER AS
SELECT
    ID
    , `VALUE`                        AS `VALUE`
    , `OM_ID`                        AS `OM_ID`
    , `WTP_ID`                       AS `WTP_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `OBJECT_MONITOR_PARAMETER`;
DROP VIEW SCI_C_PARAMETER_DEFINITION;
DROP VIEW SCI_V_PARAMETER_DEFINITION;
CREATE VIEW SCI_C_PARAMETER_DEFINITION AS
SELECT
    ID
    , `SE_ID`                        AS `SE_ID`
    , `NAME`                         AS `NAME`
    , CASE `TYPE` WHEN 10 THEN 'REFERENCE' WHEN 20 THEN 'CHILDREFERENCE' WHEN 30 THEN 'CONSTANT' WHEN 40 THEN 'RESULT' WHEN 50 THEN 'PARAMETER' WHEN 60 THEN 'EXPRESSION' WHEN 70 THEN 'IMPORT' WHEN 71 THEN 'IMPORT_UNRESOLVED' WHEN 80 THEN 'DYNAMIC' WHEN 81 THEN 'DYNAMICVALUE' WHEN 90 THEN 'LOCAL_CONSTANT' WHEN 91 THEN 'RESOURCEREFERENCE' END AS `TYPE`
    , CASE `AGG_FUNCTION` WHEN 0 THEN 'NONE' WHEN 61 THEN 'AVG' WHEN 62 THEN 'COUNT' WHEN 63 THEN 'MIN' WHEN 64 THEN 'MAX' WHEN 65 THEN 'SUM' END AS `AGG_FUNCTION`
    , `DEFAULTVALUE`                 AS `DEFAULTVALUE`
    , CASE `IS_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LOCAL`
    , `LINK_PD_ID`                   AS `LINK_PD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `PARAMETER_DEFINITION`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_PARAMETER_DEFINITION AS
SELECT
    ID
    , `SE_ID`                        AS `SE_ID`
    , `NAME`                         AS `NAME`
    , CASE `TYPE` WHEN 10 THEN 'REFERENCE' WHEN 20 THEN 'CHILDREFERENCE' WHEN 30 THEN 'CONSTANT' WHEN 40 THEN 'RESULT' WHEN 50 THEN 'PARAMETER' WHEN 60 THEN 'EXPRESSION' WHEN 70 THEN 'IMPORT' WHEN 71 THEN 'IMPORT_UNRESOLVED' WHEN 80 THEN 'DYNAMIC' WHEN 81 THEN 'DYNAMICVALUE' WHEN 90 THEN 'LOCAL_CONSTANT' WHEN 91 THEN 'RESOURCEREFERENCE' END AS `TYPE`
    , CASE `AGG_FUNCTION` WHEN 0 THEN 'NONE' WHEN 61 THEN 'AVG' WHEN 62 THEN 'COUNT' WHEN 63 THEN 'MIN' WHEN 64 THEN 'MAX' WHEN 65 THEN 'SUM' END AS `AGG_FUNCTION`
    , `DEFAULTVALUE`                 AS `DEFAULTVALUE`
    , CASE `IS_LOCAL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_LOCAL`
    , `LINK_PD_ID`                   AS `LINK_PD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `PARAMETER_DEFINITION`;
DROP VIEW SCI_PERSISTENT_VALUE;
CREATE VIEW SCI_PERSISTENT_VALUE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `INT_VALUE`                    AS `INT_VALUE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `PERSISTENT_VALUE`;
DROP VIEW SCI_POOL;
CREATE VIEW SCI_POOL AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `MANAGER_ID`                   AS `MANAGER_ID`
    , `DIST_ID`                      AS `DIST_ID`
    , `DEFINED_AMOUNT`               AS `DEFINED_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , `FREE_AMOUNT`                  AS `FREE_AMOUNT`
    , `CHILD_ALLOCATED`              AS `CHILD_ALLOCATED`
    , `EVALUATION_CYCLE`             AS `EVALUATION_CYCLE`
    , `NEXT_EVALUATION_TIME`         AS `NEXT_EVALUATION_TIME`
    , `TAG`                          AS `TAG`
    , `TRACE_INTERVAL`               AS `TRACE_INTERVAL`
    , `TRACE_BASE`                   AS `TRACE_BASE`
    , `TRACE_BASE_MULTIPLIER`        AS `TRACE_BASE_MULTIPLIER`
    , `TD0_AVG`                      AS `TD0_AVG`
    , `TD1_AVG`                      AS `TD1_AVG`
    , `TD2_AVG`                      AS `TD2_AVG`
    , `LW_AVG`                       AS `LW_AVG`
    , from_unixtime((`LAST_EVAL` & ~1125899906842624)/1000) AS `LAST_EVAL`
    , from_unixtime((`LAST_WRITE` & ~1125899906842624)/1000) AS `LAST_WRITE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `POOL`;
DROP VIEW SCI_POOL_DIST_CONFIG;
CREATE VIEW SCI_POOL_DIST_CONFIG AS
SELECT
    ID
    , `PLD_ID`                       AS `PLD_ID`
    , `PR_ID`                        AS `PR_ID`
    , CASE `IS_MANAGED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_MANAGED`
    , `NOM_PCT`                      AS `NOM_PCT`
    , `FREE_PCT`                     AS `FREE_PCT`
    , `MIN_PCT`                      AS `MIN_PCT`
    , `MAX_PCT`                      AS `MAX_PCT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `POOL_DIST_CONFIG`;
DROP VIEW SCI_POOL_DISTRIBUTION;
CREATE VIEW SCI_POOL_DISTRIBUTION AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `P_ID`                         AS `P_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `POOL_DISTRIBUTION`;
DROP VIEW SCI_POOLED_RESOURCE;
CREATE VIEW SCI_POOLED_RESOURCE AS
SELECT
    ID
    , `P_ID`                         AS `P_ID`
    , `R_ID`                         AS `R_ID`
    , CASE `IS_POOL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_POOL`
    , CASE `IS_MANAGED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_MANAGED`
    , `NOM_PCT`                      AS `NOM_PCT`
    , `FREE_PCT`                     AS `FREE_PCT`
    , `MIN_PCT`                      AS `MIN_PCT`
    , `MAX_PCT`                      AS `MAX_PCT`
    , CASE `ACT_IS_MANAGED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `ACT_IS_MANAGED`
    , `ACT_NOM_PCT`                  AS `ACT_NOM_PCT`
    , `ACT_FREE_PCT`                 AS `ACT_FREE_PCT`
    , `ACT_MIN_PCT`                  AS `ACT_MIN_PCT`
    , `ACT_MAX_PCT`                  AS `ACT_MAX_PCT`
    , `TARGET_AMOUNT`                AS `TARGET_AMOUNT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `POOLED_RESOURCE`;
DROP VIEW SCI_RESSOURCE;
CREATE VIEW SCI_RESSOURCE AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , `MASTER_ID`                    AS `MASTER_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `LINK_ID`                      AS `LINK_ID`
    , `MANAGER_ID`                   AS `MANAGER_ID`
    , `TAG`                          AS `TAG`
    , `RSD_ID`                       AS `RSD_ID`
    , from_unixtime((`RSD_TIME` & ~1125899906842624)/1000) AS `RSD_TIME`
    , `DEFINED_AMOUNT`               AS `DEFINED_AMOUNT`
    , `REQUESTABLE_AMOUNT`           AS `REQUESTABLE_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , `FREE_AMOUNT`                  AS `FREE_AMOUNT`
    , CASE `IS_ONLINE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ONLINE`
    , `FACTOR`                       AS `FACTOR`
    , `TRACE_INTERVAL`               AS `TRACE_INTERVAL`
    , `TRACE_BASE`                   AS `TRACE_BASE`
    , `TRACE_BASE_MULTIPLIER`        AS `TRACE_BASE_MULTIPLIER`
    , `TD0_AVG`                      AS `TD0_AVG`
    , `TD1_AVG`                      AS `TD1_AVG`
    , `TD2_AVG`                      AS `TD2_AVG`
    , `LW_AVG`                       AS `LW_AVG`
    , from_unixtime((`LAST_EVAL` & ~1125899906842624)/1000) AS `LAST_EVAL`
    , from_unixtime((`LAST_WRITE` & ~1125899906842624)/1000) AS `LAST_WRITE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESSOURCE`;
DROP VIEW SCI_RESOURCE_ALLOCATION;
CREATE VIEW SCI_RESOURCE_ALLOCATION AS
SELECT
    ID
    , `R_ID`                         AS `R_ID`
    , `SME_ID`                       AS `SME_ID`
    , `NR_ID`                        AS `NR_ID`
    , `AMOUNT`                       AS `AMOUNT`
    , `ORIG_AMOUNT`                  AS `ORIG_AMOUNT`
    , CASE `KEEP_MODE` WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS `KEEP_MODE`
    , CASE `IS_STICKY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STICKY`
    , `STICKY_NAME`                  AS `STICKY_NAME`
    , `STICKY_PARENT`                AS `STICKY_PARENT`
    , CASE `ALLOCATION_TYPE` WHEN 1 THEN 'REQUEST' WHEN 6 THEN 'MASTER_REQUEST' WHEN 2 THEN 'RESERVATION' WHEN 3 THEN 'MASTER_RESERVATION' WHEN 4 THEN 'ALLOCATION' WHEN 5 THEN 'IGNORE' END AS `ALLOCATION_TYPE`
    , `RSMP_ID`                      AS `RSMP_ID`
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`
    , `REFCOUNT`                     AS `REFCOUNT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_ALLOCATION`;
DROP VIEW SCI_C_RESOURCE_REQ_STATES;
DROP VIEW SCI_V_RESOURCE_REQ_STATES;
CREATE VIEW SCI_C_RESOURCE_REQ_STATES AS
SELECT
    ID
    , `RR_ID`                        AS `RR_ID`
    , `RSD_ID`                       AS `RSD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_REQ_STATES`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_REQ_STATES AS
SELECT
    ID
    , `RR_ID`                        AS `RR_ID`
    , `RSD_ID`                       AS `RSD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_REQ_STATES`;
DROP VIEW SCI_C_RESOURCE_REQUIREMENT;
DROP VIEW SCI_V_RESOURCE_REQUIREMENT;
CREATE VIEW SCI_C_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `KEEP_MODE` WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS `KEEP_MODE`
    , CASE `IS_STICKY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STICKY`
    , `STICKY_NAME`                  AS `STICKY_NAME`
    , `STICKY_PARENT`                AS `STICKY_PARENT`
    , `RSMP_ID`                      AS `RSMP_ID`
    , `EXPIRED_AMOUNT`               AS `EXPIRED_AMOUNT`
    , CASE `EXPIRED_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `EXPIRED_BASE`
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_REQUIREMENT`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `KEEP_MODE` WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS `KEEP_MODE`
    , CASE `IS_STICKY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STICKY`
    , `STICKY_NAME`                  AS `STICKY_NAME`
    , `STICKY_PARENT`                AS `STICKY_PARENT`
    , `RSMP_ID`                      AS `RSMP_ID`
    , `EXPIRED_AMOUNT`               AS `EXPIRED_AMOUNT`
    , CASE `EXPIRED_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `EXPIRED_BASE`
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_REQUIREMENT`;
DROP VIEW SCI_RESOURCE_STATE;
CREATE VIEW SCI_RESOURCE_STATE AS
SELECT
    ID
    , `RSD_ID`                       AS `RSD_ID`
    , `RSP_ID`                       AS `RSP_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_STATE`;
DROP VIEW SCI_RESOURCE_STATE_DEFINITION;
CREATE VIEW SCI_RESOURCE_STATE_DEFINITION AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_STATE_DEFINITION`;
DROP VIEW SCI_C_RESOURCE_STATE_MAPPING;
DROP VIEW SCI_V_RESOURCE_STATE_MAPPING;
CREATE VIEW SCI_C_RESOURCE_STATE_MAPPING AS
SELECT
    ID
    , `RSMP_ID`                      AS `RSMP_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `FROM_RSD_ID`                  AS `FROM_RSD_ID`
    , `TO_RSD_ID`                    AS `TO_RSD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_STATE_MAPPING`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_STATE_MAPPING AS
SELECT
    ID
    , `RSMP_ID`                      AS `RSMP_ID`
    , `ESD_ID`                       AS `ESD_ID`
    , `FROM_RSD_ID`                  AS `FROM_RSD_ID`
    , `TO_RSD_ID`                    AS `TO_RSD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_STATE_MAPPING`;
DROP VIEW SCI_C_RESOURCE_STATE_MAP_PROF;
DROP VIEW SCI_V_RESOURCE_STATE_MAP_PROF;
CREATE VIEW SCI_C_RESOURCE_STATE_MAP_PROF AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_STATE_MAP_PROF`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_STATE_MAP_PROF AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_STATE_MAP_PROF`;
DROP VIEW SCI_RESOURCE_STATE_PROFILE;
CREATE VIEW SCI_RESOURCE_STATE_PROFILE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `INITIAL_RSD_ID`               AS `INITIAL_RSD_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_STATE_PROFILE`;
DROP VIEW SCI_C_RESOURCE_TEMPLATE;
DROP VIEW SCI_V_RESOURCE_TEMPLATE;
CREATE VIEW SCI_C_RESOURCE_TEMPLATE AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `RSD_ID`                       AS `RSD_ID`
    , `REQUESTABLE_AMOUNT`           AS `REQUESTABLE_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `IS_ONLINE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ONLINE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_TEMPLATE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_TEMPLATE AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `RSD_ID`                       AS `RSD_ID`
    , `REQUESTABLE_AMOUNT`           AS `REQUESTABLE_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `IS_ONLINE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ONLINE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_TEMPLATE`;
DROP VIEW SCI_RESOURCE_VARIABLE;
CREATE VIEW SCI_RESOURCE_VARIABLE AS
SELECT
    ID
    , `PD_ID`                        AS `PD_ID`
    , `R_ID`                         AS `R_ID`
    , `VALUE`                        AS `VALUE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_VARIABLE`;
DROP VIEW SCI_RUNNABLE_QUEUE;
CREATE VIEW SCI_RUNNABLE_QUEUE AS
SELECT
    ID
    , `SME_ID`                       AS `SME_ID`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , CASE `STATE` WHEN 1 THEN 'DEPENDENCY_WAIT' WHEN 2 THEN 'SYNCHRONIZE_WAIT' WHEN 3 THEN 'RESOURCE_WAIT' WHEN 4 THEN 'RUNNABLE' WHEN 5 THEN 'STARTING' END AS `STATE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RUNNABLE_QUEUE`;
DROP VIEW SCI_SCHEDULE;
CREATE VIEW SCI_SCHEDULE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `INT_ID`                       AS `INT_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , `TIME_ZONE`                    AS `TIME_ZONE`
    , `SE_ID`                        AS `SE_ID`
    , CASE `ACTIVE` WHEN 1 THEN 'ACTIVE' WHEN 0 THEN 'INACTIVE' END AS `ACTIVE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
  FROM `SCHEDULE`;
DROP VIEW SCI_SCHEDULED_EVENT;
CREATE VIEW SCI_SCHEDULED_EVENT AS
SELECT
    ID
    , `OWNER_ID`                     AS `OWNER_ID`
    , `SCE_ID`                       AS `SCE_ID`
    , `EVT_ID`                       AS `EVT_ID`
    , CASE `ACTIVE` WHEN 1 THEN 'ACTIVE' WHEN 0 THEN 'INACTIVE' END AS `ACTIVE`
    , CASE `BROKEN` WHEN 1 THEN 'BROKEN' WHEN 0 THEN 'NOBROKEN' END AS `BROKEN`
    , `ERROR_CODE`                   AS `ERROR_CODE`
    , `ERROR_MSG`                    AS `ERROR_MSG`
    , from_unixtime((`LAST_START_TIME` & ~1125899906842624)/1000) AS `LAST_START_TIME`
    , from_unixtime((`NEXT_START_TIME` & ~1125899906842624)/1000) AS `NEXT_START_TIME`
    , CASE `NEXT_IS_TRIGGER` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `NEXT_IS_TRIGGER`
    , CASE `BACKLOG_HANDLING` WHEN 0 THEN 'NONE' WHEN 1 THEN 'LAST' WHEN 2 THEN 'ALL' END AS `BACKLOG_HANDLING`
    , CASE `SUSPEND_LIMIT` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `SUSPEND_LIMIT`
    , `SUSPEND_LIMIT_MULTIPLIER`     AS `SUSPEND_LIMIT_MULTIPLIER`
    , CASE `IS_CALENDAR` WHEN 1 THEN 'ACTIVE' WHEN 0 THEN 'INACTIVE' END AS `IS_CALENDAR`
    , `CALENDAR_HORIZON`             AS `CALENDAR_HORIZON`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SCHEDULED_EVENT`;
DROP VIEW SCI_C_SCHEDULING_ENTITY;
DROP VIEW SCI_V_SCHEDULING_ENTITY;
CREATE VIEW SCI_C_SCHEDULING_ENTITY AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `FOLDER_ID`                    AS `FOLDER_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , CASE `TYPE` WHEN 1 THEN 'JOB' WHEN 2 THEN 'BATCH' WHEN 3 THEN 'MILESTONE' END AS `TYPE`
    , `RUN_PROGRAM`                  AS `RUN_PROGRAM`
    , `RERUN_PROGRAM`                AS `RERUN_PROGRAM`
    , `KILL_PROGRAM`                 AS `KILL_PROGRAM`
    , `WORKDIR`                      AS `WORKDIR`
    , `LOGFILE`                      AS `LOGFILE`
    , CASE `TRUNC_LOG` WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS `TRUNC_LOG`
    , `ERRLOGFILE`                   AS `ERRLOGFILE`
    , CASE `TRUNC_ERRLOG` WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS `TRUNC_ERRLOG`
    , `EXPECTED_RUNTIME`             AS `EXPECTED_RUNTIME`
    , `EXPECTED_FINALTIME`           AS `EXPECTED_FINALTIME`
    , `GET_EXPECTED_RUNTIME`         AS `GET_EXPECTED_RUNTIME`
    , `PRIORITY`                     AS `PRIORITY`
    , `MIN_PRIORITY`                 AS `MIN_PRIORITY`
    , `AGING_AMOUNT`                 AS `AGING_AMOUNT`
    , CASE `AGING_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `AGING_BASE`
    , CASE `SUBMIT_SUSPENDED` WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS `SUBMIT_SUSPENDED`
    , `RESUME_AT`                    AS `RESUME_AT`
    , `RESUME_IN`                    AS `RESUME_IN`
    , CASE `RESUME_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `RESUME_BASE`
    , CASE `MASTER_SUBMITTABLE` WHEN 1 THEN 'MASTER' WHEN 0 THEN 'NOMASTER' END AS `MASTER_SUBMITTABLE`
    , `TIMEOUT_AMOUNT`               AS `TIMEOUT_AMOUNT`
    , CASE `TIMEOUT_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `TIMEOUT_BASE`
    , `TIMEOUT_STATE_ID`             AS `TIMEOUT_STATE_ID`
    , CASE `DEPENDENCY_OPERATION` WHEN 1 THEN 'AND' WHEN 2 THEN 'OR' END AS `DEPENDENCY_OPERATION`
    , `ESMP_ID`                      AS `ESMP_ID`
    , `ESP_ID`                       AS `ESP_ID`
    , `NE_ID`                        AS `NE_ID`
    , `FP_ID`                        AS `FP_ID`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SCHEDULING_ENTITY`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_SCHEDULING_ENTITY AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `FOLDER_ID`                    AS `FOLDER_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , CASE `TYPE` WHEN 1 THEN 'JOB' WHEN 2 THEN 'BATCH' WHEN 3 THEN 'MILESTONE' END AS `TYPE`
    , `RUN_PROGRAM`                  AS `RUN_PROGRAM`
    , `RERUN_PROGRAM`                AS `RERUN_PROGRAM`
    , `KILL_PROGRAM`                 AS `KILL_PROGRAM`
    , `WORKDIR`                      AS `WORKDIR`
    , `LOGFILE`                      AS `LOGFILE`
    , CASE `TRUNC_LOG` WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS `TRUNC_LOG`
    , `ERRLOGFILE`                   AS `ERRLOGFILE`
    , CASE `TRUNC_ERRLOG` WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS `TRUNC_ERRLOG`
    , `EXPECTED_RUNTIME`             AS `EXPECTED_RUNTIME`
    , `EXPECTED_FINALTIME`           AS `EXPECTED_FINALTIME`
    , `GET_EXPECTED_RUNTIME`         AS `GET_EXPECTED_RUNTIME`
    , `PRIORITY`                     AS `PRIORITY`
    , `MIN_PRIORITY`                 AS `MIN_PRIORITY`
    , `AGING_AMOUNT`                 AS `AGING_AMOUNT`
    , CASE `AGING_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `AGING_BASE`
    , CASE `SUBMIT_SUSPENDED` WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS `SUBMIT_SUSPENDED`
    , `RESUME_AT`                    AS `RESUME_AT`
    , `RESUME_IN`                    AS `RESUME_IN`
    , CASE `RESUME_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `RESUME_BASE`
    , CASE `MASTER_SUBMITTABLE` WHEN 1 THEN 'MASTER' WHEN 0 THEN 'NOMASTER' END AS `MASTER_SUBMITTABLE`
    , `TIMEOUT_AMOUNT`               AS `TIMEOUT_AMOUNT`
    , CASE `TIMEOUT_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `TIMEOUT_BASE`
    , `TIMEOUT_STATE_ID`             AS `TIMEOUT_STATE_ID`
    , CASE `DEPENDENCY_OPERATION` WHEN 1 THEN 'AND' WHEN 2 THEN 'OR' END AS `DEPENDENCY_OPERATION`
    , `ESMP_ID`                      AS `ESMP_ID`
    , `ESP_ID`                       AS `ESP_ID`
    , `NE_ID`                        AS `NE_ID`
    , `FP_ID`                        AS `FP_ID`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `SCHEDULING_ENTITY`;
DROP VIEW SCI_C_SCHEDULING_HIERARCHY;
DROP VIEW SCI_V_SCHEDULING_HIERARCHY;
CREATE VIEW SCI_C_SCHEDULING_HIERARCHY AS
SELECT
    ID
    , `SE_PARENT_ID`                 AS `SE_PARENT_ID`
    , `SE_CHILD_ID`                  AS `SE_CHILD_ID`
    , `ALIAS_NAME`                   AS `ALIAS_NAME`
    , CASE `IS_STATIC` WHEN 1 THEN 'STATIC' WHEN 0 THEN 'DYNAMIC' END AS `IS_STATIC`
    , `PRIORITY`                     AS `PRIORITY`
    , CASE `SUSPEND` WHEN 1 THEN 'CHILDSUSPEND' WHEN 2 THEN 'NOSUSPEND' WHEN 3 THEN 'SUSPEND' END AS `SUSPEND`
    , `RESUME_AT`                    AS `RESUME_AT`
    , `RESUME_IN`                    AS `RESUME_IN`
    , CASE `RESUME_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `RESUME_BASE`
    , CASE `MERGE_MODE` WHEN 1 THEN 'MERGE_LOCAL' WHEN 2 THEN 'MERGE_GLOBAL' WHEN 3 THEN 'NOMERGE' WHEN 4 THEN 'FAILURE' END AS `MERGE_MODE`
    , `ESTP_ID`                      AS `ESTP_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SCHEDULING_HIERARCHY`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_SCHEDULING_HIERARCHY AS
SELECT
    ID
    , `SE_PARENT_ID`                 AS `SE_PARENT_ID`
    , `SE_CHILD_ID`                  AS `SE_CHILD_ID`
    , `ALIAS_NAME`                   AS `ALIAS_NAME`
    , CASE `IS_STATIC` WHEN 1 THEN 'STATIC' WHEN 0 THEN 'DYNAMIC' END AS `IS_STATIC`
    , `PRIORITY`                     AS `PRIORITY`
    , CASE `SUSPEND` WHEN 1 THEN 'CHILDSUSPEND' WHEN 2 THEN 'NOSUSPEND' WHEN 3 THEN 'SUSPEND' END AS `SUSPEND`
    , `RESUME_AT`                    AS `RESUME_AT`
    , `RESUME_IN`                    AS `RESUME_IN`
    , CASE `RESUME_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `RESUME_BASE`
    , CASE `MERGE_MODE` WHEN 1 THEN 'MERGE_LOCAL' WHEN 2 THEN 'MERGE_GLOBAL' WHEN 3 THEN 'NOMERGE' WHEN 4 THEN 'FAILURE' END AS `MERGE_MODE`
    , `ESTP_ID`                      AS `ESTP_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `SCHEDULING_HIERARCHY`;
DROP VIEW SCI_SCOPE;
CREATE VIEW SCI_SCOPE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , CASE `TYPE` WHEN 1 THEN 'SCOPE' WHEN 2 THEN 'SERVER' END AS `TYPE`
    , CASE `IS_TERMINATE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_TERMINATE`
    , CASE `HAS_ALTEREDCONFIG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `HAS_ALTEREDCONFIG`
    , CASE `IS_SUSPENDED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_SUSPENDED`
    , CASE `IS_ENABLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ENABLED`
    , CASE `IS_REGISTERED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_REGISTERED`
    , CASE `STATE` WHEN 1 THEN 'NOMINAL' WHEN 2 THEN 'NONFATAL' WHEN 3 THEN 'FATAL' END AS `STATE`
    , `PID`                          AS `PID`
    , `NODE`                         AS `NODE`
    , `ERRMSG`                       AS `ERRMSG`
    , `LAST_ACTIVE`                  AS `LAST_ACTIVE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
  FROM `SCOPE`;
DROP VIEW SCI_SCOPE_CONFIG;
CREATE VIEW SCI_SCOPE_CONFIG AS
SELECT
    ID
    , `KEY`                          AS `KEY`
    , `VALUE`                        AS `VALUE`
    , `S_ID`                         AS `S_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SCOPE_CONFIG`;
DROP VIEW SCI_SCOPE_CONFIG_ENVMAPPING;
CREATE VIEW SCI_SCOPE_CONFIG_ENVMAPPING AS
SELECT
    ID
    , `KEY`                          AS `KEY`
    , `VALUE`                        AS `VALUE`
    , `S_ID`                         AS `S_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SCOPE_CONFIG_ENVMAPPING`;
DROP VIEW SCI_SME_COUNTER;
CREATE VIEW SCI_SME_COUNTER AS
SELECT
    ID
    , `JAHR`                         AS `JAHR`
    , `MONAT`                        AS `MONAT`
    , `TAG`                          AS `TAG`
    , `ANZAHL`                       AS `ANZAHL`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SME_COUNTER`;
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
DROP VIEW SCI_C_TEMPLATE_VARIABLE;
DROP VIEW SCI_V_TEMPLATE_VARIABLE;
CREATE VIEW SCI_C_TEMPLATE_VARIABLE AS
SELECT
    ID
    , `PD_ID`                        AS `PD_ID`
    , `RT_ID`                        AS `RT_ID`
    , `VALUE`                        AS `VALUE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `TEMPLATE_VARIABLE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_TEMPLATE_VARIABLE AS
SELECT
    ID
    , `PD_ID`                        AS `PD_ID`
    , `RT_ID`                        AS `RT_ID`
    , `VALUE`                        AS `VALUE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `TEMPLATE_VARIABLE`;
DROP VIEW SCI_C_TRIGGER_DEFINITION;
DROP VIEW SCI_V_TRIGGER_DEFINITION;
CREATE VIEW SCI_C_TRIGGER_DEFINITION AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `FIRE_ID`                      AS `FIRE_ID`
    , CASE `OBJECT_TYPE` WHEN 0 THEN 'JOB_DEFINITION' WHEN 1 THEN 'RESOURCE' WHEN 2 THEN 'NAMED_RESOURCE' WHEN 3 THEN 'OBJECT_MONITOR' END AS `OBJECT_TYPE`
    , `SE_ID`                        AS `SE_ID`
    , `MAIN_SE_ID`                   AS `MAIN_SE_ID`
    , `PARENT_SE_ID`                 AS `PARENT_SE_ID`
    , CASE `IS_ACTIVE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ACTIVE`
    , CASE `ACTION` WHEN 0 THEN 'SUBMIT' WHEN 1 THEN 'RERUN' END AS `ACTION`
    , CASE `TYPE` WHEN 0 THEN 'IMMEDIATE_LOCAL' WHEN 2 THEN 'BEFORE_FINAL' WHEN 3 THEN 'AFTER_FINAL' WHEN 1 THEN 'IMMEDIATE_MERGE' WHEN 4 THEN 'FINISH_CHILD' WHEN 5 THEN 'UNTIL_FINISHED' WHEN 6 THEN 'UNTIL_FINAL' WHEN 7 THEN 'WARNING' END AS `TYPE`
    , CASE `IS_MASTER` WHEN 1 THEN 'MASTER' WHEN 0 THEN 'NOMASTER' END AS `IS_MASTER`
    , CASE `IS_SUSPEND` WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS `IS_SUSPEND`
    , CASE `IS_CREATE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_CREATE`
    , CASE `IS_CHANGE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_CHANGE`
    , CASE `IS_DELETE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_DELETE`
    , CASE `IS_GROUP` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_GROUP`
    , `RESUME_AT`                    AS `RESUME_AT`
    , `RESUME_IN`                    AS `RESUME_IN`
    , CASE `RESUME_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `RESUME_BASE`
    , CASE `IS_WARN_ON_LIMIT` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_WARN_ON_LIMIT`
    , `MAX_RETRY`                    AS `MAX_RETRY`
    , `SUBMIT_OWNER_ID`              AS `SUBMIT_OWNER_ID`
    , `CONDITION`                    AS `CONDITION`
    , `CHECK_AMOUNT`                 AS `CHECK_AMOUNT`
    , CASE `CHECK_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `CHECK_BASE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `TRIGGER_DEFINITION`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_TRIGGER_DEFINITION AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `FIRE_ID`                      AS `FIRE_ID`
    , CASE `OBJECT_TYPE` WHEN 0 THEN 'JOB_DEFINITION' WHEN 1 THEN 'RESOURCE' WHEN 2 THEN 'NAMED_RESOURCE' WHEN 3 THEN 'OBJECT_MONITOR' END AS `OBJECT_TYPE`
    , `SE_ID`                        AS `SE_ID`
    , `MAIN_SE_ID`                   AS `MAIN_SE_ID`
    , `PARENT_SE_ID`                 AS `PARENT_SE_ID`
    , CASE `IS_ACTIVE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ACTIVE`
    , CASE `ACTION` WHEN 0 THEN 'SUBMIT' WHEN 1 THEN 'RERUN' END AS `ACTION`
    , CASE `TYPE` WHEN 0 THEN 'IMMEDIATE_LOCAL' WHEN 2 THEN 'BEFORE_FINAL' WHEN 3 THEN 'AFTER_FINAL' WHEN 1 THEN 'IMMEDIATE_MERGE' WHEN 4 THEN 'FINISH_CHILD' WHEN 5 THEN 'UNTIL_FINISHED' WHEN 6 THEN 'UNTIL_FINAL' WHEN 7 THEN 'WARNING' END AS `TYPE`
    , CASE `IS_MASTER` WHEN 1 THEN 'MASTER' WHEN 0 THEN 'NOMASTER' END AS `IS_MASTER`
    , CASE `IS_SUSPEND` WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS `IS_SUSPEND`
    , CASE `IS_CREATE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_CREATE`
    , CASE `IS_CHANGE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_CHANGE`
    , CASE `IS_DELETE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_DELETE`
    , CASE `IS_GROUP` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_GROUP`
    , `RESUME_AT`                    AS `RESUME_AT`
    , `RESUME_IN`                    AS `RESUME_IN`
    , CASE `RESUME_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `RESUME_BASE`
    , CASE `IS_WARN_ON_LIMIT` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_WARN_ON_LIMIT`
    , `MAX_RETRY`                    AS `MAX_RETRY`
    , `SUBMIT_OWNER_ID`              AS `SUBMIT_OWNER_ID`
    , `CONDITION`                    AS `CONDITION`
    , `CHECK_AMOUNT`                 AS `CHECK_AMOUNT`
    , CASE `CHECK_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `CHECK_BASE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `TRIGGER_DEFINITION`;
DROP VIEW SCI_TRIGGER_QUEUE;
CREATE VIEW SCI_TRIGGER_QUEUE AS
SELECT
    ID
    , `SME_ID`                       AS `SME_ID`
    , `TR_ID`                        AS `TR_ID`
    , `NEXT_TRIGGER_TIME`            AS `NEXT_TRIGGER_TIME`
    , `TIMES_CHECKED`                AS `TIMES_CHECKED`
    , `TIMES_TRIGGERED`              AS `TIMES_TRIGGERED`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `TRIGGER_QUEUE`;
DROP VIEW SCI_C_TRIGGER_STATE;
DROP VIEW SCI_V_TRIGGER_STATE;
CREATE VIEW SCI_C_TRIGGER_STATE AS
SELECT
    ID
    , `TRIGGER_ID`                   AS `TRIGGER_ID`
    , `FROM_STATE_ID`                AS `FROM_STATE_ID`
    , `TO_STATE_ID`                  AS `TO_STATE_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `TRIGGER_STATE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_TRIGGER_STATE AS
SELECT
    ID
    , `TRIGGER_ID`                   AS `TRIGGER_ID`
    , `FROM_STATE_ID`                AS `FROM_STATE_ID`
    , `TO_STATE_ID`                  AS `TO_STATE_ID`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `TRIGGER_STATE`;
DROP VIEW SCI_USERS;
CREATE VIEW SCI_USERS AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , CASE `IS_ENABLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ENABLED`
    , `DEFAULT_G_ID`                 AS `DEFAULT_G_ID`
    , `DELETE_VERSION`               AS `DELETE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `USERS`;
DROP VIEW SCI_C_WATCH_TYPE;
DROP VIEW SCI_V_WATCH_TYPE;
CREATE VIEW SCI_C_WATCH_TYPE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `WATCH_TYPE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_WATCH_TYPE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `WATCH_TYPE`;
DROP VIEW SCI_WATCH_TYPE_PARAMETER;
CREATE VIEW SCI_WATCH_TYPE_PARAMETER AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `DEFAULTVALUE`                 AS `DEFAULTVALUE`
    , `WT_ID`                        AS `WT_ID`
    , CASE `IS_SUBMIT_PAR` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_SUBMIT_PAR`
    , CASE `TYPE` WHEN 1 THEN 'CONFIG' WHEN 2 THEN 'VALUE' WHEN 3 THEN 'INFO' END AS `TYPE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `WATCH_TYPE_PARAMETER`;
DROP VIEW SCI_RESOURCE_TRACE;
CREATE VIEW SCI_RESOURCE_TRACE AS
SELECT
    `R_ID`                         AS `R_ID`
    , `TAG`                          AS `TAG`
    , `TRACE_INTERVAL`               AS `TRACE_INTERVAL`
    , `TRACE_BASE`                   AS `TRACE_BASE`
    , `TRACE_BASE_MULTIPLIER`        AS `TRACE_BASE_MULTIPLIER`
    , `TD0_AVG`                      AS `TD0_AVG`
    , `TD1_AVG`                      AS `TD1_AVG`
    , `TD2_AVG`                      AS `TD2_AVG`
    , `LW_AVG`                       AS `LW_AVG`
    , from_unixtime((`WRITE_TIME` & ~1125899906842624)/1000) AS `WRITE_TIME`
    , from_unixtime((`LAST_WRITE` & ~1125899906842624)/1000) AS `LAST_WRITE`
  FROM `RESOURCE_TRACE`;
UPDATE SUBMITTED_ENTITY SET `RAW_PRIORITY` = `PRIORITY`;
commit;
