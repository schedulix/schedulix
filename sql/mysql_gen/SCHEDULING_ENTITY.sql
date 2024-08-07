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
-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE `SCHEDULING_ENTITY` (
    `ID`                           decimal(20) NOT NULL
    , `NAME`                         varchar(64)     NOT NULL
    , `FOLDER_ID`                    decimal(20)     NOT NULL
    , `OWNER_ID`                     decimal(20)     NOT NULL
    , `TYPE`                         integer         NOT NULL
    , `RUN_PROGRAM`                  varchar(512)        NULL
    , `RERUN_PROGRAM`                varchar(512)        NULL
    , `KILL_PROGRAM`                 varchar(512)        NULL
    , `WORKDIR`                      varchar(512)        NULL
    , `LOGFILE`                      varchar(512)        NULL
    , `TRUNC_LOG`                    integer             NULL
    , `ERRLOGFILE`                   varchar(512)        NULL
    , `TRUNC_ERRLOG`                 integer             NULL
    , `EXPECTED_RUNTIME`             integer             NULL
    , `EXPECTED_FINALTIME`           integer             NULL
    , `GET_EXPECTED_RUNTIME`         varchar(32)         NULL
    , `PRIORITY`                     integer         NOT NULL
    , `MIN_PRIORITY`                 integer             NULL
    , `AGING_AMOUNT`                 integer             NULL
    , `AGING_BASE`                   integer             NULL
    , `SUBMIT_SUSPENDED`             integer         NOT NULL
    , `RESUME_AT`                    varchar(20)         NULL
    , `RESUME_IN`                    integer             NULL
    , `RESUME_BASE`                  integer             NULL
    , `MASTER_SUBMITTABLE`           integer         NOT NULL
    , `TIMEOUT_AMOUNT`               integer             NULL
    , `TIMEOUT_BASE`                 integer             NULL
    , `TIMEOUT_STATE_ID`             decimal(20)         NULL
    , `SAME_NODE`                    integer             NULL
    , `GANG_SCHEDULE`                integer             NULL
    , `DEPENDENCY_OPERATION`         integer         NOT NULL
    , `ESMP_ID`                      decimal(20)         NULL
    , `ESP_ID`                       decimal(20)         NULL
    , `QA_ID`                        decimal(20)         NULL
    , `NE_ID`                        decimal(20)         NULL
    , `FP_ID`                        decimal(20)         NULL
    , `CANCEL_LEAD_FLAG`             integer         NOT NULL
    , `CANCEL_APPROVAL`              integer         NOT NULL
    , `RERUN_LEAD_FLAG`              integer         NOT NULL
    , `RERUN_APPROVAL`               integer         NOT NULL
    , `ENABLE_LEAD_FLAG`             integer         NOT NULL
    , `ENABLE_APPROVAL`              integer         NOT NULL
    , `SET_STATE_LEAD_FLAG`          integer         NOT NULL
    , `SET_STATE_APPROVAL`           integer         NOT NULL
    , `IGN_DEP_LEAD_FLAG`            integer         NOT NULL
    , `IGN_DEP_APPROVAL`             integer         NOT NULL
    , `IGN_RSS_LEAD_FLAG`            integer         NOT NULL
    , `IGN_RSS_APPROVAL`             integer         NOT NULL
    , `CLONE_LEAD_FLAG`              integer         NOT NULL
    , `CLONE_APPROVAL`               integer         NOT NULL
    , `EDIT_PARM_LEAD_FLAG`          integer         NOT NULL
    , `EDIT_PARM_APPROVAL`           integer         NOT NULL
    , `KILL_LEAD_FLAG`               integer         NOT NULL
    , `KILL_APPROVAL`                integer         NOT NULL
    , `SET_JOB_STATE_LEAD_FLAG`      integer         NOT NULL
    , `SET_JOB_STATE_APPROVAL`       integer         NOT NULL
    , `INHERIT_PRIVS`                decimal(20)     NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
    , `VALID_FROM`                 decimal(20) NOT NULL
    , `VALID_TO`                   decimal(20) NOT NULL
) ENGINE = INNODB;
CREATE INDEX PK_SCHEDULING_ENTITY
ON `SCHEDULING_ENTITY`(`ID`);
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
    , CASE `CANCEL_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `CANCEL_LEAD_FLAG`
    , CASE `CANCEL_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `CANCEL_APPROVAL`
    , CASE `RERUN_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `RERUN_LEAD_FLAG`
    , CASE `RERUN_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `RERUN_APPROVAL`
    , CASE `ENABLE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `ENABLE_LEAD_FLAG`
    , CASE `ENABLE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `ENABLE_APPROVAL`
    , CASE `SET_STATE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `SET_STATE_LEAD_FLAG`
    , CASE `SET_STATE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `SET_STATE_APPROVAL`
    , CASE `IGN_DEP_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IGN_DEP_LEAD_FLAG`
    , CASE `IGN_DEP_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `IGN_DEP_APPROVAL`
    , CASE `IGN_RSS_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IGN_RSS_LEAD_FLAG`
    , CASE `IGN_RSS_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `IGN_RSS_APPROVAL`
    , CASE `CLONE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `CLONE_LEAD_FLAG`
    , CASE `CLONE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `CLONE_APPROVAL`
    , CASE `EDIT_PARM_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `EDIT_PARM_LEAD_FLAG`
    , CASE `EDIT_PARM_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `EDIT_PARM_APPROVAL`
    , CASE `KILL_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `KILL_LEAD_FLAG`
    , CASE `KILL_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `KILL_APPROVAL`
    , CASE `SET_JOB_STATE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `SET_JOB_STATE_LEAD_FLAG`
    , CASE `SET_JOB_STATE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `SET_JOB_STATE_APPROVAL`
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
    , CASE `CANCEL_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `CANCEL_LEAD_FLAG`
    , CASE `CANCEL_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `CANCEL_APPROVAL`
    , CASE `RERUN_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `RERUN_LEAD_FLAG`
    , CASE `RERUN_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `RERUN_APPROVAL`
    , CASE `ENABLE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `ENABLE_LEAD_FLAG`
    , CASE `ENABLE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `ENABLE_APPROVAL`
    , CASE `SET_STATE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `SET_STATE_LEAD_FLAG`
    , CASE `SET_STATE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `SET_STATE_APPROVAL`
    , CASE `IGN_DEP_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IGN_DEP_LEAD_FLAG`
    , CASE `IGN_DEP_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `IGN_DEP_APPROVAL`
    , CASE `IGN_RSS_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IGN_RSS_LEAD_FLAG`
    , CASE `IGN_RSS_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `IGN_RSS_APPROVAL`
    , CASE `CLONE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `CLONE_LEAD_FLAG`
    , CASE `CLONE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `CLONE_APPROVAL`
    , CASE `EDIT_PARM_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `EDIT_PARM_LEAD_FLAG`
    , CASE `EDIT_PARM_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `EDIT_PARM_APPROVAL`
    , CASE `KILL_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `KILL_LEAD_FLAG`
    , CASE `KILL_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `KILL_APPROVAL`
    , CASE `SET_JOB_STATE_LEAD_FLAG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `SET_JOB_STATE_LEAD_FLAG`
    , CASE `SET_JOB_STATE_APPROVAL` WHEN 0 THEN 'DEFAULT' WHEN 1 THEN 'PARENT' WHEN 2 THEN 'NO' WHEN 5 THEN 'APPROVE' WHEN 4 THEN 'REVIEW' END AS `SET_JOB_STATE_APPROVAL`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `SCHEDULING_ENTITY`;
