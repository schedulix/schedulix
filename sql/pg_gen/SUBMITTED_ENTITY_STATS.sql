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

CREATE TABLE SUBMITTED_ENTITY_STATS (
    ID                             decimal(20) NOT NULL
    , SME_ID                         decimal(20)     NOT NULL
    , RERUN_SEQ                      integer         NOT NULL
    , SCOPE_ID                       decimal(20)         NULL
    , JOB_ESD_ID                     decimal(20)         NULL
    , EXIT_CODE                      integer             NULL
    , COMMANDLINE                    varchar(512)        NULL
    , WORKDIR                        varchar(512)        NULL
    , LOGFILE                        varchar(512)        NULL
    , ERRLOGFILE                     varchar(512)        NULL
    , EXTPID                         varchar(32)         NULL
    , SYNC_TS                        decimal(20)         NULL
    , RESOURCE_TS                    decimal(20)         NULL
    , RUNNABLE_TS                    decimal(20)         NULL
    , START_TS                       decimal(20)         NULL
    , FINISH_TS                      decimal(20)         NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_SUBMITTED_ENTITY_STATS
ON SUBMITTED_ENTITY_STATS(ID);
CREATE VIEW SCI_SUBMITTED_ENTITY_STATS AS
SELECT
    ID
    , SME_ID                         AS SME_ID
    , RERUN_SEQ                      AS RERUN_SEQ
    , SCOPE_ID                       AS SCOPE_ID
    , JOB_ESD_ID                     AS JOB_ESD_ID
    , EXIT_CODE                      AS EXIT_CODE
    , COMMANDLINE                    AS COMMANDLINE
    , WORKDIR                        AS WORKDIR
    , LOGFILE                        AS LOGFILE
    , ERRLOGFILE                     AS ERRLOGFILE
    , EXTPID                         AS EXTPID
    , timestamp 'epoch' + cast(to_char(mod(SYNC_TS, 1125899906842624)/1000, '999999999999') as interval) AS SYNC_TS
    , timestamp 'epoch' + cast(to_char(mod(RESOURCE_TS, 1125899906842624)/1000, '999999999999') as interval) AS RESOURCE_TS
    , timestamp 'epoch' + cast(to_char(mod(RUNNABLE_TS, 1125899906842624)/1000, '999999999999') as interval) AS RUNNABLE_TS
    , timestamp 'epoch' + cast(to_char(mod(START_TS, 1125899906842624)/1000, '999999999999') as interval) AS START_TS
    , timestamp 'epoch' + cast(to_char(mod(FINISH_TS, 1125899906842624)/1000, '999999999999') as interval) AS FINISH_TS
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM SUBMITTED_ENTITY_STATS;
CREATE TABLE ARC_SUBMITTED_ENTITY_STATS (
    ID                             decimal(20) NOT NULL
    , SME_ID                         decimal(20)      NULL
    , RERUN_SEQ                      integer          NULL
    , SCOPE_ID                       decimal(20)      NULL
    , JOB_ESD_ID                     decimal(20)      NULL
    , EXIT_CODE                      integer          NULL
    , COMMANDLINE                    varchar(512)     NULL
    , WORKDIR                        varchar(512)     NULL
    , LOGFILE                        varchar(512)     NULL
    , ERRLOGFILE                     varchar(512)     NULL
    , EXTPID                         varchar(32)      NULL
    , SYNC_TS                        decimal(20)      NULL
    , RESOURCE_TS                    decimal(20)      NULL
    , RUNNABLE_TS                    decimal(20)      NULL
    , START_TS                       decimal(20)      NULL
    , FINISH_TS                      decimal(20)      NULL
    , CREATOR_U_ID                   decimal(20)      NULL
    , CREATE_TS                      decimal(20)      NULL
    , CHANGER_U_ID                   decimal(20)      NULL
    , CHANGE_TS                      decimal(20)      NULL
);
