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

CREATE TABLE INTERVALL (
    ID                             DECIMAL(20) NOT NULL
    , NAME                           varchar(64)     NOT NULL
    , OWNER_ID                       decimal(20)     NOT NULL
    , START_TIME                     decimal(20)         NULL
    , END_TIME                       decimal(20)         NULL
    , DELAY                          decimal(20)         NULL
    , BASE_INTERVAL                  integer             NULL
    , BASE_INTERVAL_MULTIPLIER       integer             NULL
    , DURATION                       integer             NULL
    , DURATION_MULTIPLIER            integer             NULL
    , SYNC_TIME                      decimal(20)     NOT NULL
    , IS_INVERSE                     integer         NOT NULL
    , IS_MERGE                       integer         NOT NULL
    , EMBEDDED_INT_ID                decimal(20)         NULL
    , SE_ID                          decimal(20)         NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_INTERVALL
ON INTERVALL(ID);
CREATE VIEW SCI_INTERVALL AS
SELECT
    ID
    , NAME                           AS NAME
    , OWNER_ID                       AS OWNER_ID
    , timestamp 'epoch' + cast(to_char(mod(START_TIME, 1125899906842624)/1000, '999999999999') as interval) AS START_TIME
    , timestamp 'epoch' + cast(to_char(mod(END_TIME, 1125899906842624)/1000, '999999999999') as interval) AS END_TIME
    , DELAY                          AS DELAY
    , CASE BASE_INTERVAL WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS BASE_INTERVAL
    , BASE_INTERVAL_MULTIPLIER       AS BASE_INTERVAL_MULTIPLIER
    , CASE DURATION WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS DURATION
    , DURATION_MULTIPLIER            AS DURATION_MULTIPLIER
    , timestamp 'epoch' + cast(to_char(mod(SYNC_TIME, 1125899906842624)/1000, '999999999999') as interval) AS SYNC_TIME
    , CASE IS_INVERSE WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_INVERSE
    , CASE IS_MERGE WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_MERGE
    , EMBEDDED_INT_ID                AS EMBEDDED_INT_ID
    , SE_ID                          AS SE_ID
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM INTERVALL;
