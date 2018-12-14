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

CREATE TABLE SCOPE (
    `ID`                           decimal(20) NOT NULL
    , `NAME`                         varchar(64)     NOT NULL
    , `OWNER_ID`                     decimal(20)     NOT NULL
    , `PARENT_ID`                    decimal(20)         NULL
    , `TYPE`                         integer         NOT NULL
    , `IS_TERMINATE`                 integer             NULL
    , `HAS_ALTEREDCONFIG`            integer             NULL
    , `IS_SUSPENDED`                 integer             NULL
    , `IS_ENABLED`                   integer             NULL
    , `IS_REGISTERED`                integer             NULL
    , `STATE`                        integer             NULL
    , `PASSWD`                       varchar(64)         NULL
    , `SALT`                         varchar(64)         NULL
    , `METHOD`                       integer         NOT NULL
    , `PID`                          varchar(32)         NULL
    , `NODE`                         varchar(64)         NULL
    , `ERRMSG`                       varchar(256)        NULL
    , `LAST_ACTIVE`                  decimal(20)         NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
    , `INHERIT_PRIVS`                decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_SCOPE
ON SCOPE(`ID`);
CREATE VIEW SCI_SCOPE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , CASE `TYPE` WHEN 1 THEN 'SCOPE' WHEN 2 THEN 'SERVER' END AS `TYPE`
    , CASE `IS_TERMINATE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_TERMINATE`
    , CASE `HAS_ALTEREDCONFIG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `HAS_ALTEREDCONFIG`
    , CASE `IS_SUSPENDED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_SUSPENDED`
    , CASE `IS_ENABLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ENABLED`
    , CASE `IS_REGISTERED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_REGISTERED`
    , CASE `STATE` WHEN 1 THEN 'NOMINAL' WHEN 2 THEN 'NONFATAL' WHEN 3 THEN 'FATAL' END AS `STATE`
    , `PID`                          AS `PID`
    , `NODE`                         AS `NODE`
    , `ERRMSG`                       AS `ERRMSG`
    , `LAST_ACTIVE`                  AS `LAST_ACTIVE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
  FROM SCOPE;
