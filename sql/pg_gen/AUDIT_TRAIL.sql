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
-- Copyright (C) 2003-2013 independIT Integrative Technologies GmbH

CREATE TABLE AUDIT_TRAIL (
    ID                             DECIMAL(20) NOT NULL
    , USER_ID                        decimal(20)     NOT NULL
    , TS                             decimal(20)     NOT NULL
    , TXID                           decimal(20)     NOT NULL
    , ACTION                         integer         NOT NULL
    , OBJECT_TYPE                    integer         NOT NULL
    , OBJECT_ID                      decimal(20)     NOT NULL
    , ORIGIN_ID                      decimal(20)     NOT NULL
    , IS_SET_WARNING                 integer         NOT NULL
    , ACTION_INFO                    varchar(1024)       NULL
    , ACTION_COMMENT                 varchar(1024)       NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_AUDIT_TRAIL
ON AUDIT_TRAIL(ID);
CREATE VIEW SCI_AUDIT_TRAIL AS
SELECT
    ID
    , USER_ID                        AS USER_ID
    , timestamp 'epoch' + cast(to_char(mod(TS, 1125899906842624)/1000, '999999999999') as interval) AS TS
    , TXID                           AS TXID
    , CASE ACTION WHEN 1 THEN 'RERUN' WHEN 2 THEN 'RERUN_RECURSIVE' WHEN 3 THEN 'CANCEL' WHEN 4 THEN 'SUSPEND' WHEN 5 THEN 'RESUME' WHEN 6 THEN 'SET_STATE' WHEN 7 THEN 'SET_EXIT_STATE' WHEN 8 THEN 'IGNORE_DEPENDENCY' WHEN 9 THEN 'IGNORE_DEP_RECURSIVE' WHEN 10 THEN 'IGNORE_RESOURCE' WHEN 11 THEN 'KILL' WHEN 12 THEN 'ALTER_RUN_PROGRAM' WHEN 13 THEN 'ALTER_RERUN_PROGRAM' WHEN 14 THEN 'COMMENT_JOB' WHEN 15 THEN 'SUBMITTED' WHEN 16 THEN 'TRIGGER_FAILED' WHEN 17 THEN 'TRIGGER_SUBMIT' WHEN 18 THEN 'JOB_RESTARTABLE' WHEN 19 THEN 'CHANGE_PRIORITY' WHEN 20 THEN 'RENICE' WHEN 21 THEN 'SUBMIT_SUSPENDED' WHEN 22 THEN 'IGNORE_NAMED_RESOURCE' WHEN 23 THEN 'TIMEOUT' WHEN 24 THEN 'SET_RESOURCE_STATE' WHEN 25 THEN 'JOB_IN_ERROR' WHEN 26 THEN 'CLEAR_WARNING' WHEN 27 THEN 'SET_WARNING' WHEN 28 THEN 'JOB_UNREACHABLE' END AS ACTION
    , CASE OBJECT_TYPE WHEN 17 THEN 'JOB' END AS OBJECT_TYPE
    , OBJECT_ID                      AS OBJECT_ID
    , ORIGIN_ID                      AS ORIGIN_ID
    , CASE IS_SET_WARNING WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_SET_WARNING
    , ACTION_INFO                    AS ACTION_INFO
    , ACTION_COMMENT                 AS ACTION_COMMENT
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM AUDIT_TRAIL;
