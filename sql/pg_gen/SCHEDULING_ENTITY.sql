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

CREATE TABLE SCHEDULING_ENTITY (
    ID                             DECIMAL(20) NOT NULL
    , NAME                           varchar(64)     NOT NULL
    , FOLDER_ID                      decimal(20)     NOT NULL
    , OWNER_ID                       decimal(20)     NOT NULL
    , TYPE                           integer         NOT NULL
    , RUN_PROGRAM                    varchar(512)        NULL
    , RERUN_PROGRAM                  varchar(512)        NULL
    , KILL_PROGRAM                   varchar(512)        NULL
    , WORKDIR                        varchar(512)        NULL
    , LOGFILE                        varchar(512)        NULL
    , TRUNC_LOG                      integer             NULL
    , ERRLOGFILE                     varchar(512)        NULL
    , TRUNC_ERRLOG                   integer             NULL
    , EXPECTED_RUNTIME               integer             NULL
    , EXPECTED_FINALTIME             integer             NULL
    , GET_EXPECTED_RUNTIME           varchar(32)         NULL
    , PRIORITY                       integer         NOT NULL
    , MIN_PRIORITY                   integer             NULL
    , AGING_AMOUNT                   integer             NULL
    , AGING_BASE                     integer             NULL
    , SUBMIT_SUSPENDED               integer         NOT NULL
    , RESUME_AT                      varchar(20)         NULL
    , RESUME_IN                      integer             NULL
    , RESUME_BASE                    integer             NULL
    , MASTER_SUBMITTABLE             integer         NOT NULL
    , TIMEOUT_AMOUNT                 integer             NULL
    , TIMEOUT_BASE                   integer             NULL
    , TIMEOUT_STATE_ID               decimal(20)         NULL
    , SAME_NODE                      integer             NULL
    , GANG_SCHEDULE                  integer             NULL
    , DEPENDENCY_OPERATION           integer         NOT NULL
    , ESMP_ID                        decimal(20)         NULL
    , ESP_ID                         decimal(20)         NULL
    , QA_ID                          decimal(20)         NULL
    , NE_ID                          decimal(20)         NULL
    , FP_ID                          decimal(20)         NULL
    , INHERIT_PRIVS                  decimal(20)     NOT NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   DECIMAL(20) NOT NULL
    , VALID_TO                     DECIMAL(20) NOT NULL
);
CREATE INDEX PK_SCHEDULING_ENTITY
ON SCHEDULING_ENTITY(ID);
CREATE VIEW SCI_C_SCHEDULING_ENTITY AS
SELECT
    ID
    , NAME                           AS NAME
    , FOLDER_ID                      AS FOLDER_ID
    , OWNER_ID                       AS OWNER_ID
    , CASE TYPE WHEN 1 THEN 'JOB' WHEN 2 THEN 'BATCH' WHEN 3 THEN 'MILESTONE' END AS TYPE
    , RUN_PROGRAM                    AS RUN_PROGRAM
    , RERUN_PROGRAM                  AS RERUN_PROGRAM
    , KILL_PROGRAM                   AS KILL_PROGRAM
    , WORKDIR                        AS WORKDIR
    , LOGFILE                        AS LOGFILE
    , CASE TRUNC_LOG WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS TRUNC_LOG
    , ERRLOGFILE                     AS ERRLOGFILE
    , CASE TRUNC_ERRLOG WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS TRUNC_ERRLOG
    , EXPECTED_RUNTIME               AS EXPECTED_RUNTIME
    , EXPECTED_FINALTIME             AS EXPECTED_FINALTIME
    , GET_EXPECTED_RUNTIME           AS GET_EXPECTED_RUNTIME
    , PRIORITY                       AS PRIORITY
    , MIN_PRIORITY                   AS MIN_PRIORITY
    , AGING_AMOUNT                   AS AGING_AMOUNT
    , CASE AGING_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS AGING_BASE
    , CASE SUBMIT_SUSPENDED WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS SUBMIT_SUSPENDED
    , RESUME_AT                      AS RESUME_AT
    , RESUME_IN                      AS RESUME_IN
    , CASE RESUME_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS RESUME_BASE
    , CASE MASTER_SUBMITTABLE WHEN 1 THEN 'MASTER' WHEN 0 THEN 'NOMASTER' END AS MASTER_SUBMITTABLE
    , TIMEOUT_AMOUNT                 AS TIMEOUT_AMOUNT
    , CASE TIMEOUT_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS TIMEOUT_BASE
    , TIMEOUT_STATE_ID               AS TIMEOUT_STATE_ID
    , CASE DEPENDENCY_OPERATION WHEN 1 THEN 'AND' WHEN 2 THEN 'OR' END AS DEPENDENCY_OPERATION
    , ESMP_ID                        AS ESMP_ID
    , ESP_ID                         AS ESP_ID
    , NE_ID                          AS NE_ID
    , FP_ID                          AS FP_ID
    , INHERIT_PRIVS                  AS INHERIT_PRIVS
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM SCHEDULING_ENTITY
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_SCHEDULING_ENTITY AS
SELECT
    ID
    , NAME                           AS NAME
    , FOLDER_ID                      AS FOLDER_ID
    , OWNER_ID                       AS OWNER_ID
    , CASE TYPE WHEN 1 THEN 'JOB' WHEN 2 THEN 'BATCH' WHEN 3 THEN 'MILESTONE' END AS TYPE
    , RUN_PROGRAM                    AS RUN_PROGRAM
    , RERUN_PROGRAM                  AS RERUN_PROGRAM
    , KILL_PROGRAM                   AS KILL_PROGRAM
    , WORKDIR                        AS WORKDIR
    , LOGFILE                        AS LOGFILE
    , CASE TRUNC_LOG WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS TRUNC_LOG
    , ERRLOGFILE                     AS ERRLOGFILE
    , CASE TRUNC_ERRLOG WHEN 0 THEN 'NOTRUNC' WHEN 1 THEN 'TRUNC' END AS TRUNC_ERRLOG
    , EXPECTED_RUNTIME               AS EXPECTED_RUNTIME
    , EXPECTED_FINALTIME             AS EXPECTED_FINALTIME
    , GET_EXPECTED_RUNTIME           AS GET_EXPECTED_RUNTIME
    , PRIORITY                       AS PRIORITY
    , MIN_PRIORITY                   AS MIN_PRIORITY
    , AGING_AMOUNT                   AS AGING_AMOUNT
    , CASE AGING_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS AGING_BASE
    , CASE SUBMIT_SUSPENDED WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS SUBMIT_SUSPENDED
    , RESUME_AT                      AS RESUME_AT
    , RESUME_IN                      AS RESUME_IN
    , CASE RESUME_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS RESUME_BASE
    , CASE MASTER_SUBMITTABLE WHEN 1 THEN 'MASTER' WHEN 0 THEN 'NOMASTER' END AS MASTER_SUBMITTABLE
    , TIMEOUT_AMOUNT                 AS TIMEOUT_AMOUNT
    , CASE TIMEOUT_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS TIMEOUT_BASE
    , TIMEOUT_STATE_ID               AS TIMEOUT_STATE_ID
    , CASE DEPENDENCY_OPERATION WHEN 1 THEN 'AND' WHEN 2 THEN 'OR' END AS DEPENDENCY_OPERATION
    , ESMP_ID                        AS ESMP_ID
    , ESP_ID                         AS ESP_ID
    , NE_ID                          AS NE_ID
    , FP_ID                          AS FP_ID
    , INHERIT_PRIVS                  AS INHERIT_PRIVS
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM SCHEDULING_ENTITY;
