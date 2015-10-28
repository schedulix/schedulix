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

CREATE TABLE OBJECT_MONITOR (
    `ID`                           decimal(20) NOT NULL
    , `NAME`                         varchar(64)     NOT NULL
    , `OWNER_ID`                     decimal(20)     NOT NULL
    , `WT_ID`                        decimal(20)     NOT NULL
    , `RECREATE_HANDLING`            integer         NOT NULL
    , `WATCH_SE_ID`                  decimal(20)         NULL
    , `DELETE_AMOUNT`                integer             NULL
    , `DELETE_BASE`                  integer             NULL
    , `EVENT_DELETE_AMOUNT`          integer             NULL
    , `EVENT_DELETE_BASE`            integer             NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_OBJECT_MONITOR
ON OBJECT_MONITOR(`ID`);
CREATE VIEW SCI_OBJECT_MONITOR AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `WT_ID`                        AS `WT_ID`
    , CASE `RECREATE_HANDLING` WHEN 0 THEN 'NONE' WHEN 1 THEN 'CREATE' WHEN 2 THEN 'CHANGE' END AS `RECREATE_HANDLING`
    , `WATCH_SE_ID`                  AS `WATCH_SE_ID`
    , `DELETE_AMOUNT`                AS `DELETE_AMOUNT`
    , CASE `DELETE_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `DELETE_BASE`
    , `EVENT_DELETE_AMOUNT`          AS `EVENT_DELETE_AMOUNT`
    , CASE `EVENT_DELETE_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `EVENT_DELETE_BASE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM OBJECT_MONITOR;
