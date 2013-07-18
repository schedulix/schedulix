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

CREATE TABLE HIERARCHY_INSTANCE (
    ID                             DECIMAL(20) NOT NULL
    , `PARENT_ID`                    decimal(20)     NOT NULL
    , `CHILD_ID`                     decimal(20)     NOT NULL
    , `SH_ID`                        decimal(20)     NOT NULL
    , `NICE`                         integer         NOT NULL
    , `CHILD_ESD_ID`                 decimal(20)         NULL
    , `CHILD_ES_PREFERENCE`          integer             NULL
    , `SE_VERSION`                   decimal(20)     NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) engine = innodb;
CREATE UNIQUE INDEX PK_HIERARCHY_INSTANCE
ON HIERARCHY_INSTANCE(id);
CREATE VIEW SCI_HIERARCHY_INSTANCE AS
SELECT
    ID
    , `PARENT_ID`                    AS `PARENT_ID`
    , `CHILD_ID`                     AS `CHILD_ID`
    , `SH_ID`                        AS `SH_ID`
    , `NICE`                         AS `NICE`
    , `CHILD_ESD_ID`                 AS `CHILD_ESD_ID`
    , `CHILD_ES_PREFERENCE`          AS `CHILD_ES_PREFERENCE`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM HIERARCHY_INSTANCE;
