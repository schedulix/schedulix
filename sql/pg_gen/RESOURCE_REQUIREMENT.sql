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

CREATE TABLE RESOURCE_REQUIREMENT (
    ID                             decimal(20) NOT NULL
    , NR_ID                          decimal(20)     NOT NULL
    , SE_ID                          decimal(20)     NOT NULL
    , AMOUNT                         integer             NULL
    , KEEP_MODE                      integer         NOT NULL
    , IS_STICKY                      integer         NOT NULL
    , STICKY_NAME                    varchar(64)         NULL
    , STICKY_PARENT                  decimal(20)         NULL
    , RSMP_ID                        decimal(20)         NULL
    , EXPIRED_AMOUNT                 integer             NULL
    , EXPIRED_BASE                   integer             NULL
    , IGNORE_ON_RERUN                integer         NOT NULL
    , LOCKMODE                       integer             NULL
    , CONDITION                      varchar(1024)       NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   decimal(20) NOT NULL
    , VALID_TO                     decimal(20) NOT NULL
);
CREATE INDEX PK_RESOURCE_REQUIREMENT
ON RESOURCE_REQUIREMENT(ID);
CREATE VIEW SCI_C_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , NR_ID                          AS NR_ID
    , SE_ID                          AS SE_ID
    , AMOUNT                         AS AMOUNT
    , CASE KEEP_MODE WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS KEEP_MODE
    , CASE IS_STICKY WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_STICKY
    , STICKY_NAME                    AS STICKY_NAME
    , STICKY_PARENT                  AS STICKY_PARENT
    , RSMP_ID                        AS RSMP_ID
    , EXPIRED_AMOUNT                 AS EXPIRED_AMOUNT
    , CASE EXPIRED_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS EXPIRED_BASE
    , CASE IGNORE_ON_RERUN WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IGNORE_ON_RERUN
    , CASE LOCKMODE WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS LOCKMODE
    , CONDITION                      AS CONDITION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamptz 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamptz 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM RESOURCE_REQUIREMENT
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , NR_ID                          AS NR_ID
    , SE_ID                          AS SE_ID
    , AMOUNT                         AS AMOUNT
    , CASE KEEP_MODE WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS KEEP_MODE
    , CASE IS_STICKY WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_STICKY
    , STICKY_NAME                    AS STICKY_NAME
    , STICKY_PARENT                  AS STICKY_PARENT
    , RSMP_ID                        AS RSMP_ID
    , EXPIRED_AMOUNT                 AS EXPIRED_AMOUNT
    , CASE EXPIRED_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS EXPIRED_BASE
    , CASE IGNORE_ON_RERUN WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IGNORE_ON_RERUN
    , CASE LOCKMODE WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS LOCKMODE
    , CONDITION                      AS CONDITION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamptz 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamptz 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM RESOURCE_REQUIREMENT;
