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

CREATE TABLE ENTITY_VARIABLE (
    ID                             decimal(20) NOT NULL
    , SME_ID                         decimal(20)     NOT NULL
    , NAME                           varchar(64)     NOT NULL
    , VALUE                          varchar(256)        NULL
    , IS_LOCAL                       integer         NOT NULL
    , EV_LINK                        decimal(20)         NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_ENTITY_VARIABLE
ON ENTITY_VARIABLE(ID);
CREATE VIEW SCI_ENTITY_VARIABLE AS
SELECT
    ID
    , SME_ID                         AS SME_ID
    , NAME                           AS NAME
    , VALUE                          AS VALUE
    , CASE IS_LOCAL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_LOCAL
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM ENTITY_VARIABLE;
CREATE TABLE ARC_ENTITY_VARIABLE (
    ID                             decimal(20) NOT NULL
    , SME_ID                         decimal(20)      NULL
    , NAME                           varchar(64)      NULL
    , VALUE                          varchar(256)     NULL
    , IS_LOCAL                       integer          NULL
    , EV_LINK                        decimal(20)      NULL
    , CREATOR_U_ID                   decimal(20)      NULL
    , CREATE_TS                      decimal(20)      NULL
    , CHANGER_U_ID                   decimal(20)      NULL
    , CHANGE_TS                      decimal(20)      NULL
);
