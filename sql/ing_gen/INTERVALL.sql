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

CREATE TABLE INTERVALL (
    ID                             decimal(20) NOT NULL
    , NAME                           varchar(64)     NOT NULL
    , OWNER_ID                       decimal(20)     NOT NULL
    , START_TIME                     decimal(20)     WITH NULL
    , END_TIME                       decimal(20)     WITH NULL
    , DELAY                          decimal(20)     WITH NULL
    , BASE_INTERVAL                  integer         WITH NULL
    , BASE_INTERVAL_MULTIPLIER       integer         WITH NULL
    , DURATION                       integer         WITH NULL
    , DURATION_MULTIPLIER            integer         WITH NULL
    , SYNC_TIME                      decimal(20)     NOT NULL
    , IS_INVERSE                     integer         NOT NULL
    , IS_MERGE                       integer         NOT NULL
    , EMBEDDED_INT_ID                decimal(20)     WITH NULL
    , SE_ID                          decimal(20)     WITH NULL
    , OBJ_ID                         decimal(20)     WITH NULL
    , OBJ_TYPE                       integer         WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_INTERVALL
ON INTERVALL(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_INTERVALL AS
SELECT
    ID
    , NAME                           AS NAME
    , OWNER_ID                       AS OWNER_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((START_TIME- decimal(START_TIME/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS START_TIME
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((END_TIME- decimal(END_TIME/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS END_TIME
    , DELAY                          AS DELAY
    , CASE BASE_INTERVAL WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS BASE_INTERVAL
    , BASE_INTERVAL_MULTIPLIER       AS BASE_INTERVAL_MULTIPLIER
    , CASE DURATION WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS DURATION
    , DURATION_MULTIPLIER            AS DURATION_MULTIPLIER
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((SYNC_TIME- decimal(SYNC_TIME/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS SYNC_TIME
    , CASE IS_INVERSE WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_INVERSE
    , CASE IS_MERGE WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_MERGE
    , EMBEDDED_INT_ID                AS EMBEDDED_INT_ID
    , SE_ID                          AS SE_ID
    , OBJ_ID                         AS OBJ_ID
    , CASE OBJ_TYPE WHEN 25 THEN 'DISTRIBUTION' WHEN 8 THEN 'USER' WHEN 9 THEN 'JOB_DEFINITION' WHEN 11 THEN 'RESOURCE' WHEN 15 THEN 'SCOPE' WHEN 16 THEN 'TRIGGER' WHEN 18 THEN 'EVENT' WHEN 19 THEN 'INTERVAL' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 28 THEN 'RESOURCE_TEMPLATE' WHEN 88 THEN 'DISPATCHER_DISPATCH' WHEN 89 THEN 'DISPATCHER_USE' END AS OBJ_TYPE
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM INTERVALL;\g
