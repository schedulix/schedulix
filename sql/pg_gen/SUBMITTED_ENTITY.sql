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
-- Copyright (C) 2003-2006 independIT Integrative Technologies GmbH

CREATE TABLE SUBMITTED_ENTITY (
    ID                             DECIMAL(20) NOT NULL
    , ACCESS_KEY                     decimal(20)     NOT NULL
    , MASTER_ID                      decimal(20)     NOT NULL
    , SUBMIT_TAG                     varchar(32)         NULL
    , UNRESOLVED_HANDLING            integer             NULL
    , SE_ID                          decimal(20)     NOT NULL
    , CHILD_TAG                      varchar(70)         NULL
    , SE_VERSION                     decimal(20)     NOT NULL
    , OWNER_ID                       decimal(20)     NOT NULL
    , PARENT_ID                      decimal(20)         NULL
    , SCOPE_ID                       decimal(20)         NULL
    , IS_STATIC                      integer         NOT NULL
    , MERGE_MODE                     integer         NOT NULL
    , STATE                          integer         NOT NULL
    , JOB_ESD_ID                     decimal(20)         NULL
    , JOB_ESD_PREF                   integer             NULL
    , JOB_IS_FINAL                   integer         NOT NULL
    , JOB_IS_RESTARTABLE             integer         NOT NULL
    , FINAL_ESD_ID                   decimal(20)         NULL
    , EXIT_CODE                      integer             NULL
    , COMMANDLINE                    varchar(512)        NULL
    , RR_COMMANDLINE                 varchar(512)        NULL
    , RERUN_SEQ                      integer         NOT NULL
    , IS_REPLACED                    integer         NOT NULL
    , IS_CANCELLED                   integer             NULL
    , BASE_SME_ID                    decimal(20)         NULL
    , REASON_SME_ID                  decimal(20)         NULL
    , FIRE_SME_ID                    decimal(20)         NULL
    , FIRE_SE_ID                     decimal(20)         NULL
    , TR_ID                          decimal(20)         NULL
    , TR_SD_ID_OLD                   decimal(20)         NULL
    , TR_SD_ID_NEW                   decimal(20)         NULL
    , TR_SEQ                         integer         NOT NULL
    , WORKDIR                        varchar(512)        NULL
    , LOGFILE                        varchar(512)        NULL
    , ERRLOGFILE                     varchar(512)        NULL
    , PID                            varchar(32)         NULL
    , EXTPID                         varchar(32)         NULL
    , ERROR_MSG                      varchar(256)        NULL
    , KILL_ID                        decimal(20)         NULL
    , KILL_EXIT_CODE                 integer             NULL
    , IS_SUSPENDED                   integer         NOT NULL
    , IS_SUSPENDED_LOCAL             integer             NULL
    , PRIORITY                       integer         NOT NULL
    , NICE                           integer         NOT NULL
    , MIN_PRIORITY                   integer         NOT NULL
    , AGING_AMOUNT                   integer         NOT NULL
    , PARENT_SUSPENDED               integer         NOT NULL
    , CHILD_SUSPENDED                integer         NOT NULL
    , WARN_COUNT                     integer         NOT NULL
    , WARN_LINK                      decimal(20)         NULL
    , SUBMIT_TS                      decimal(20)     NOT NULL
    , RESUME_TS                      decimal(20)         NULL
    , SYNC_TS                        decimal(20)         NULL
    , RESOURCE_TS                    decimal(20)         NULL
    , RUNNABLE_TS                    decimal(20)         NULL
    , START_TS                       decimal(20)         NULL
    , FINSH_TS                       decimal(20)         NULL
    , FINAL_TS                       decimal(20)         NULL
    , CNT_SUBMITTED                  integer         NOT NULL
    , CNT_DEPENDENCY_WAIT            integer         NOT NULL
    , CNT_SYNCHRONIZE_WAIT           integer         NOT NULL
    , CNT_RESOURCE_WAIT              integer         NOT NULL
    , CNT_RUNNABLE                   integer         NOT NULL
    , CNT_STARTING                   integer         NOT NULL
    , CNT_STARTED                    integer         NOT NULL
    , CNT_RUNNING                    integer         NOT NULL
    , CNT_TO_KILL                    integer         NOT NULL
    , CNT_KILLED                     integer         NOT NULL
    , CNT_CANCELLED                  integer         NOT NULL
    , CNT_FINISHED                   integer         NOT NULL
    , CNT_FINAL                      integer         NOT NULL
    , CNT_BROKEN_ACTIVE              integer         NOT NULL
    , CNT_BROKEN_FINISHED            integer         NOT NULL
    , CNT_ERROR                      integer         NOT NULL
    , CNT_UNREACHABLE                integer         NOT NULL
    , CNT_RESTARTABLE                integer         NOT NULL
    , CNT_WARN                       integer         NOT NULL
    , CNT_PENDING                    integer         NOT NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_SUBMITTED_ENTITY
ON SUBMITTED_ENTITY(ID);
CREATE VIEW SCI_SUBMITTED_ENTITY AS
SELECT
    ID
    , MASTER_ID                      AS MASTER_ID
    , SUBMIT_TAG                     AS SUBMIT_TAG
    , CASE UNRESOLVED_HANDLING WHEN 1 THEN 'UH_IGNORE' WHEN 3 THEN 'UH_SUSPEND' WHEN 2 THEN 'UH_ERROR' END AS UNRESOLVED_HANDLING
    , SE_ID                          AS SE_ID
    , CHILD_TAG                      AS CHILD_TAG
    , SE_VERSION                     AS SE_VERSION
    , OWNER_ID                       AS OWNER_ID
    , PARENT_ID                      AS PARENT_ID
    , SCOPE_ID                       AS SCOPE_ID
    , CASE IS_STATIC WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_STATIC
    , CASE MERGE_MODE WHEN 1 THEN 'MERGE_LOCAL' WHEN 2 THEN 'MERGE_GLOBAL' WHEN 3 THEN 'NOMERGE' WHEN 4 THEN 'FAILURE' END AS MERGE_MODE
    , CASE STATE WHEN 0 THEN 'SUBMITTED' WHEN 1 THEN 'DEPENDENCY_WAIT' WHEN 2 THEN 'SYNCHRONIZE_WAIT' WHEN 3 THEN 'RESOURCE_WAIT' WHEN 4 THEN 'RUNNABLE' WHEN 5 THEN 'STARTING' WHEN 6 THEN 'STARTED' WHEN 7 THEN 'RUNNING' WHEN 8 THEN 'TO_KILL' WHEN 9 THEN 'KILLED' WHEN 10 THEN 'CANCELLED' WHEN 11 THEN 'FINISHED' WHEN 12 THEN 'FINAL' WHEN 13 THEN 'BROKEN_ACTIVE' WHEN 14 THEN 'BROKEN_FINISHED' WHEN 15 THEN 'ERROR' WHEN 16 THEN 'UNREACHABLE' END AS STATE
    , JOB_ESD_ID                     AS JOB_ESD_ID
    , JOB_ESD_PREF                   AS JOB_ESD_PREF
    , CASE JOB_IS_FINAL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS JOB_IS_FINAL
    , CASE JOB_IS_RESTARTABLE WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS JOB_IS_RESTARTABLE
    , FINAL_ESD_ID                   AS FINAL_ESD_ID
    , EXIT_CODE                      AS EXIT_CODE
    , COMMANDLINE                    AS COMMANDLINE
    , RR_COMMANDLINE                 AS RR_COMMANDLINE
    , RERUN_SEQ                      AS RERUN_SEQ
    , CASE IS_REPLACED WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_REPLACED
    , CASE IS_CANCELLED WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_CANCELLED
    , BASE_SME_ID                    AS BASE_SME_ID
    , REASON_SME_ID                  AS REASON_SME_ID
    , FIRE_SME_ID                    AS FIRE_SME_ID
    , FIRE_SE_ID                     AS FIRE_SE_ID
    , TR_ID                          AS TR_ID
    , TR_SD_ID_OLD                   AS TR_SD_ID_OLD
    , TR_SD_ID_NEW                   AS TR_SD_ID_NEW
    , TR_SEQ                         AS TR_SEQ
    , WORKDIR                        AS WORKDIR
    , LOGFILE                        AS LOGFILE
    , ERRLOGFILE                     AS ERRLOGFILE
    , PID                            AS PID
    , EXTPID                         AS EXTPID
    , ERROR_MSG                      AS ERROR_MSG
    , KILL_ID                        AS KILL_ID
    , KILL_EXIT_CODE                 AS KILL_EXIT_CODE
    , CASE IS_SUSPENDED WHEN 1 THEN 'SUSPEND' WHEN 0 THEN 'NOSUSPEND' END AS IS_SUSPENDED
    , CASE IS_SUSPENDED_LOCAL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_SUSPENDED_LOCAL
    , PRIORITY                       AS PRIORITY
    , NICE                           AS NICE
    , MIN_PRIORITY                   AS MIN_PRIORITY
    , AGING_AMOUNT                   AS AGING_AMOUNT
    , PARENT_SUSPENDED               AS PARENT_SUSPENDED
    , CHILD_SUSPENDED                AS CHILD_SUSPENDED
    , WARN_COUNT                     AS WARN_COUNT
    , WARN_LINK                      AS WARN_LINK
    , timestamp 'epoch' + cast(to_char(mod(SUBMIT_TS, 1125899906842624)/1000, '999999999999') as interval) AS SUBMIT_TS
    , timestamp 'epoch' + cast(to_char(mod(RESUME_TS, 1125899906842624)/1000, '999999999999') as interval) AS RESUME_TS
    , timestamp 'epoch' + cast(to_char(mod(SYNC_TS, 1125899906842624)/1000, '999999999999') as interval) AS SYNC_TS
    , timestamp 'epoch' + cast(to_char(mod(RESOURCE_TS, 1125899906842624)/1000, '999999999999') as interval) AS RESOURCE_TS
    , timestamp 'epoch' + cast(to_char(mod(RUNNABLE_TS, 1125899906842624)/1000, '999999999999') as interval) AS RUNNABLE_TS
    , timestamp 'epoch' + cast(to_char(mod(START_TS, 1125899906842624)/1000, '999999999999') as interval) AS START_TS
    , timestamp 'epoch' + cast(to_char(mod(FINSH_TS, 1125899906842624)/1000, '999999999999') as interval) AS FINSH_TS
    , timestamp 'epoch' + cast(to_char(mod(FINAL_TS, 1125899906842624)/1000, '999999999999') as interval) AS FINAL_TS
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM SUBMITTED_ENTITY;
