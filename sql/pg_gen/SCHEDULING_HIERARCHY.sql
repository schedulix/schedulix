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

CREATE TABLE SCHEDULING_HIERARCHY (
    ID                             DECIMAL(20) NOT NULL
    , SE_PARENT_ID                   decimal(20)         NULL
    , SE_CHILD_ID                    decimal(20)         NULL
    , ALIAS_NAME                     varchar(64)         NULL
    , IS_STATIC                      integer         NOT NULL
    , PRIORITY                       integer         NOT NULL
    , SUSPEND                        integer         NOT NULL
    , RESUME_AT                      varchar(20)         NULL
    , RESUME_IN                      integer             NULL
    , RESUME_BASE                    integer             NULL
    , MERGE_MODE                     integer         NOT NULL
    , ESTP_ID                        decimal(20)         NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   DECIMAL(20) NOT NULL
    , VALID_TO                     DECIMAL(20) NOT NULL
);
CREATE INDEX PK_SCHEDULING_HIERARCHY
ON SCHEDULING_HIERARCHY(ID);
CREATE VIEW SCI_C_SCHEDULING_HIERARCHY AS
SELECT
    ID
    , SE_PARENT_ID                   AS SE_PARENT_ID
    , SE_CHILD_ID                    AS SE_CHILD_ID
    , ALIAS_NAME                     AS ALIAS_NAME
    , CASE IS_STATIC WHEN 1 THEN 'STATIC' WHEN 0 THEN 'DYNAMIC' END AS IS_STATIC
    , PRIORITY                       AS PRIORITY
    , CASE SUSPEND WHEN 1 THEN 'CHILDSUSPEND' WHEN 2 THEN 'NOSUSPEND' WHEN 3 THEN 'SUSPEND' END AS SUSPEND
    , RESUME_AT                      AS RESUME_AT
    , RESUME_IN                      AS RESUME_IN
    , CASE RESUME_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS RESUME_BASE
    , CASE MERGE_MODE WHEN 1 THEN 'MERGE_LOCAL' WHEN 2 THEN 'MERGE_GLOBAL' WHEN 3 THEN 'NOMERGE' WHEN 4 THEN 'FAILURE' END AS MERGE_MODE
    , ESTP_ID                        AS ESTP_ID
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM SCHEDULING_HIERARCHY
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_SCHEDULING_HIERARCHY AS
SELECT
    ID
    , SE_PARENT_ID                   AS SE_PARENT_ID
    , SE_CHILD_ID                    AS SE_CHILD_ID
    , ALIAS_NAME                     AS ALIAS_NAME
    , CASE IS_STATIC WHEN 1 THEN 'STATIC' WHEN 0 THEN 'DYNAMIC' END AS IS_STATIC
    , PRIORITY                       AS PRIORITY
    , CASE SUSPEND WHEN 1 THEN 'CHILDSUSPEND' WHEN 2 THEN 'NOSUSPEND' WHEN 3 THEN 'SUSPEND' END AS SUSPEND
    , RESUME_AT                      AS RESUME_AT
    , RESUME_IN                      AS RESUME_IN
    , CASE RESUME_BASE WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS RESUME_BASE
    , CASE MERGE_MODE WHEN 1 THEN 'MERGE_LOCAL' WHEN 2 THEN 'MERGE_GLOBAL' WHEN 3 THEN 'NOMERGE' WHEN 4 THEN 'FAILURE' END AS MERGE_MODE
    , ESTP_ID                        AS ESTP_ID
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM SCHEDULING_HIERARCHY;
