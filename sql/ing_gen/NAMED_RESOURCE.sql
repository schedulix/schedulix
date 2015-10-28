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

CREATE TABLE NAMED_RESOURCE (
    ID                             decimal(20) NOT NULL
    , NAME                           varchar(64)     NOT NULL
    , OWNER_ID                       decimal(20)     NOT NULL
    , PARENT_ID                      decimal(20)     WITH NULL
    , USAGE                          integer         NOT NULL
    , RSP_ID                         decimal(20)     WITH NULL
    , FACTOR                         float           WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , INHERIT_PRIVS                  decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_NAMED_RESOURCE
ON NAMED_RESOURCE(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_NAMED_RESOURCE AS
SELECT
    ID
    , NAME                           AS NAME
    , OWNER_ID                       AS OWNER_ID
    , PARENT_ID                      AS PARENT_ID
    , CASE USAGE WHEN 1 THEN 'STATIC' WHEN 2 THEN 'SYSTEM' WHEN 4 THEN 'SYNCHRONIZING' WHEN 8 THEN 'CATEGORY' WHEN 3 THEN 'POOL' END AS USAGE
    , RSP_ID                         AS RSP_ID
    , FACTOR                         AS FACTOR
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
    , INHERIT_PRIVS                  AS INHERIT_PRIVS
  FROM NAMED_RESOURCE;\g
