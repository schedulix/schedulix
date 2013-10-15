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

CREATE TABLE PARAMETER_DEFINITION (
    ID                             DECIMAL(20) NOT NULL
    , SE_ID                          decimal(20)     NOT NULL
    , NAME                           varchar(64)     NOT NULL
    , TYPE                           integer         NOT NULL
    , AGG_FUNCTION                   integer         NOT NULL
    , DEFAULTVALUE                   varchar(256)    WITH NULL
    , IS_LOCAL                       integer         NOT NULL
    , LINK_PD_ID                     decimal(20)     WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   DECIMAL(20) NOT NULL
    , VALID_TO                     DECIMAL(20) NOT NULL
);\g
CREATE INDEX PK_PARAMETER_DEFINITION
ON PARAMETER_DEFINITION(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_C_PARAMETER_DEFINITION AS
SELECT
    ID
    , SE_ID                          AS SE_ID
    , NAME                           AS NAME
    , CASE TYPE WHEN 10 THEN 'REFERENCE' WHEN 20 THEN 'CHILDREFERENCE' WHEN 30 THEN 'CONSTANT' WHEN 40 THEN 'RESULT' WHEN 50 THEN 'PARAMETER' WHEN 60 THEN 'EXPRESSION' WHEN 70 THEN 'IMPORT' WHEN 80 THEN 'DYNAMIC' WHEN 81 THEN 'DYNAMICVALUE' WHEN 90 THEN 'LOCAL_CONSTANT' WHEN 91 THEN 'RESOURCEREFERENCE' END AS TYPE
    , CASE AGG_FUNCTION WHEN 0 THEN 'NONE' WHEN 61 THEN 'AVG' WHEN 62 THEN 'COUNT' WHEN 63 THEN 'MIN' WHEN 64 THEN 'MAX' WHEN 65 THEN 'SUM' END AS AGG_FUNCTION
    , DEFAULTVALUE                   AS DEFAULTVALUE
    , CASE IS_LOCAL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_LOCAL
    , LINK_PD_ID                     AS LINK_PD_ID
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM PARAMETER_DEFINITION
 WHERE VALID_TO = 9223372036854775807;\g
CREATE VIEW SCI_V_PARAMETER_DEFINITION AS
SELECT
    ID
    , SE_ID                          AS SE_ID
    , NAME                           AS NAME
    , CASE TYPE WHEN 10 THEN 'REFERENCE' WHEN 20 THEN 'CHILDREFERENCE' WHEN 30 THEN 'CONSTANT' WHEN 40 THEN 'RESULT' WHEN 50 THEN 'PARAMETER' WHEN 60 THEN 'EXPRESSION' WHEN 70 THEN 'IMPORT' WHEN 80 THEN 'DYNAMIC' WHEN 81 THEN 'DYNAMICVALUE' WHEN 90 THEN 'LOCAL_CONSTANT' WHEN 91 THEN 'RESOURCEREFERENCE' END AS TYPE
    , CASE AGG_FUNCTION WHEN 0 THEN 'NONE' WHEN 61 THEN 'AVG' WHEN 62 THEN 'COUNT' WHEN 63 THEN 'MIN' WHEN 64 THEN 'MAX' WHEN 65 THEN 'SUM' END AS AGG_FUNCTION
    , DEFAULTVALUE                   AS DEFAULTVALUE
    , CASE IS_LOCAL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_LOCAL
    , LINK_PD_ID                     AS LINK_PD_ID
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM PARAMETER_DEFINITION;\g
