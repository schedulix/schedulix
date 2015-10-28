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

CREATE TABLE DEPENDENCY_DEFINITION (
    ID                             decimal(20) NOT NULL
    , SE_DEPENDENT_ID                decimal(20)     NOT NULL
    , SE_REQUIRED_ID                 decimal(20)     NOT NULL
    , NAME                           varchar(64)     WITH NULL
    , UNRESOLVED_HANDLING            integer         NOT NULL
    , DMODE                          integer         NOT NULL
    , STATE_SELECTION                integer         NOT NULL
    , CONDITION                      varchar(1024)   WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   decimal(20) NOT NULL
    , VALID_TO                     decimal(20) NOT NULL
);\g
CREATE INDEX PK_DEPENDENCY_DEFINITION
ON DEPENDENCY_DEFINITION(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_C_DEPENDENCY_DEFINITION AS
SELECT
    ID
    , SE_DEPENDENT_ID                AS SE_DEPENDENT_ID
    , SE_REQUIRED_ID                 AS SE_REQUIRED_ID
    , NAME                           AS NAME
    , CASE UNRESOLVED_HANDLING WHEN 1 THEN 'IGNORE' WHEN 2 THEN 'ERROR' WHEN 3 THEN 'SUSPEND' WHEN 4 THEN 'DEFER' END AS UNRESOLVED_HANDLING
    , CASE DMODE WHEN 1 THEN 'ALL_FINAL' WHEN 2 THEN 'JOB_FINAL' END AS DMODE
    , CASE STATE_SELECTION WHEN 0 THEN 'FINAL' WHEN 1 THEN 'ALL_REACHABLE' WHEN 2 THEN 'UNREACHABLE' WHEN 3 THEN 'DEFAULT' END AS STATE_SELECTION
    , CONDITION                      AS CONDITION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM DEPENDENCY_DEFINITION
 WHERE VALID_TO = 9223372036854775807;\g
CREATE VIEW SCI_V_DEPENDENCY_DEFINITION AS
SELECT
    ID
    , SE_DEPENDENT_ID                AS SE_DEPENDENT_ID
    , SE_REQUIRED_ID                 AS SE_REQUIRED_ID
    , NAME                           AS NAME
    , CASE UNRESOLVED_HANDLING WHEN 1 THEN 'IGNORE' WHEN 2 THEN 'ERROR' WHEN 3 THEN 'SUSPEND' WHEN 4 THEN 'DEFER' END AS UNRESOLVED_HANDLING
    , CASE DMODE WHEN 1 THEN 'ALL_FINAL' WHEN 2 THEN 'JOB_FINAL' END AS DMODE
    , CASE STATE_SELECTION WHEN 0 THEN 'FINAL' WHEN 1 THEN 'ALL_REACHABLE' WHEN 2 THEN 'UNREACHABLE' WHEN 3 THEN 'DEFAULT' END AS STATE_SELECTION
    , CONDITION                      AS CONDITION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM DEPENDENCY_DEFINITION;\g
