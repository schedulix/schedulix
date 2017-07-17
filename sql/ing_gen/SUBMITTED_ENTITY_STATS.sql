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
    , SCOPE_ID                       decimal(20)     WITH NULL
    , JOB_ESD_ID                     decimal(20)     WITH NULL
    , EXIT_CODE                      integer         WITH NULL
    , COMMANDLINE                    varchar(512)    WITH NULL
    , WORKDIR                        varchar(512)    WITH NULL
    , LOGFILE                        varchar(512)    WITH NULL
    , ERRLOGFILE                     varchar(512)    WITH NULL
    , EXTPID                         varchar(32)     WITH NULL
    , SYNC_TS                        decimal(20)     WITH NULL
    , RESOURCE_TS                    decimal(20)     WITH NULL
    , RUNNABLE_TS                    decimal(20)     WITH NULL
    , START_TS                       decimal(20)     WITH NULL
    , FINISH_TS                      decimal(20)     WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_SUBMITTED_ENTITY_STATS
ON SUBMITTED_ENTITY_STATS(ID) WITH STRUCTURE = BTREE;\g
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
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((SYNC_TS- decimal(SYNC_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS SYNC_TS
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((RESOURCE_TS- decimal(RESOURCE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS RESOURCE_TS
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((RUNNABLE_TS- decimal(RUNNABLE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS RUNNABLE_TS
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((START_TS- decimal(START_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS START_TS
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((FINISH_TS- decimal(FINISH_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS FINISH_TS
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM SUBMITTED_ENTITY_STATS;\g
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
);\g
