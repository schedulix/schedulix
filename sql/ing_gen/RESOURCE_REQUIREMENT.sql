/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software: 
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

CREATE TABLE RESOURCE_REQUIREMENT (
    ID                             DECIMAL(20) NOT NULL
    , NR_ID                          decimal(20)     NOT NULL
    , SE_ID                          decimal(20)     NOT NULL
    , AMOUNT                         integer         WITH NULL
    , KEEP_MODE                      integer         NOT NULL
    , IS_STICKY                      integer         NOT NULL
    , RSMP_ID                        decimal(20)     WITH NULL
    , EXPIRED_AMOUNT                 integer         WITH NULL
    , EXPIRED_BASE                   integer         WITH NULL
    , LOCKMODE                       integer         WITH NULL
    , CONDITION                      varchar(1024)   WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   DECIMAL(20) NOT NULL
    , VALID_TO                     DECIMAL(20) NOT NULL
);\g
CREATE INDEX PK_RESOURCE_REQUIREMENT
ON RESOURCE_REQUIREMENT(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_C_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , NR_ID                          AS NR_ID
    , SE_ID                          AS SE_ID
    , AMOUNT                         AS AMOUNT
    , CASE KEEP_MODE WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS KEEP_MODE
    , CASE IS_STICKY WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_STICKY
    , RSMP_ID                        AS RSMP_ID
    , EXPIRED_AMOUNT                 AS EXPIRED_AMOUNT
    , CASE EXPIRED_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS EXPIRED_BASE
    , CASE LOCKMODE WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS LOCKMODE
    , CONDITION                      AS CONDITION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM RESOURCE_REQUIREMENT
 WHERE VALID_TO = 9223372036854775807;\g
CREATE VIEW SCI_V_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , NR_ID                          AS NR_ID
    , SE_ID                          AS SE_ID
    , AMOUNT                         AS AMOUNT
    , CASE KEEP_MODE WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS KEEP_MODE
    , CASE IS_STICKY WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_STICKY
    , RSMP_ID                        AS RSMP_ID
    , EXPIRED_AMOUNT                 AS EXPIRED_AMOUNT
    , CASE EXPIRED_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS EXPIRED_BASE
    , CASE LOCKMODE WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS LOCKMODE
    , CONDITION                      AS CONDITION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM RESOURCE_REQUIREMENT;\g
