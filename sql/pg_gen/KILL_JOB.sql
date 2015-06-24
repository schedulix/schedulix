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

CREATE TABLE KILL_JOB (
    ID                             decimal(20) NOT NULL
    , SE_ID                          decimal(20)     NOT NULL
    , SE_VERSION                     decimal(20)     NOT NULL
    , SME_ID                         decimal(20)     NOT NULL
    , SCOPE_ID                       decimal(20)     NOT NULL
    , STATE                          integer         NOT NULL
    , EXIT_CODE                      integer             NULL
    , COMMANDLINE                    varchar(512)        NULL
    , LOGFILE                        varchar(512)        NULL
    , ERRLOGFILE                     varchar(512)        NULL
    , PID                            varchar(32)         NULL
    , EXTPID                         varchar(32)         NULL
    , ERROR_MSG                      varchar(256)        NULL
    , RUNNABLE_TS                    decimal(20)         NULL
    , START_TS                       decimal(20)         NULL
    , FINSH_TS                       decimal(20)         NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_KILL_JOB
ON KILL_JOB(ID);
CREATE TABLE ARC_KILL_JOB (
    ID                             decimal(20) NOT NULL
    , SE_ID                          decimal(20)      NULL
    , SE_VERSION                     decimal(20)      NULL
    , SME_ID                         decimal(20)      NULL
    , SCOPE_ID                       decimal(20)      NULL
    , STATE                          integer          NULL
    , EXIT_CODE                      integer          NULL
    , COMMANDLINE                    varchar(512)     NULL
    , LOGFILE                        varchar(512)     NULL
    , ERRLOGFILE                     varchar(512)     NULL
    , PID                            varchar(32)      NULL
    , EXTPID                         varchar(32)      NULL
    , ERROR_MSG                      varchar(256)     NULL
    , RUNNABLE_TS                    decimal(20)      NULL
    , START_TS                       decimal(20)      NULL
    , FINSH_TS                       decimal(20)      NULL
    , CREATOR_U_ID                   decimal(20)      NULL
    , CREATE_TS                      decimal(20)      NULL
    , CHANGER_U_ID                   decimal(20)      NULL
    , CHANGE_TS                      decimal(20)      NULL
);
CREATE VIEW SCI_KILL_JOB AS
SELECT
    ID
    , SE_ID                          AS SE_ID
    , SE_VERSION                     AS SE_VERSION
    , SME_ID                         AS SME_ID
    , SCOPE_ID                       AS SCOPE_ID
    , CASE STATE WHEN 4 THEN 'RUNNABLE' WHEN 5 THEN 'STARTING' WHEN 6 THEN 'STARTED' WHEN 7 THEN 'RUNNING' WHEN 11 THEN 'FINISHED' WHEN 13 THEN 'BROKEN_ACTIVE' WHEN 14 THEN 'BROKEN_FINISHED' WHEN 15 THEN 'ERROR' END AS STATE
    , EXIT_CODE                      AS EXIT_CODE
    , COMMANDLINE                    AS COMMANDLINE
    , LOGFILE                        AS LOGFILE
    , ERRLOGFILE                     AS ERRLOGFILE
    , PID                            AS PID
    , EXTPID                         AS EXTPID
    , ERROR_MSG                      AS ERROR_MSG
    , timestamp 'epoch' + cast(to_char(mod(RUNNABLE_TS, 1125899906842624)/1000, '999999999999') as interval) AS RUNNABLE_TS
    , timestamp 'epoch' + cast(to_char(mod(START_TS, 1125899906842624)/1000, '999999999999') as interval) AS START_TS
    , timestamp 'epoch' + cast(to_char(mod(FINSH_TS, 1125899906842624)/1000, '999999999999') as interval) AS FINSH_TS
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM KILL_JOB;
