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

CREATE TABLE `DEPENDENCY_INSTANCE` (
    `ID`                           decimal(20) NOT NULL
    , `DD_ID`                        decimal(20)     NOT NULL
    , `DEPENDENT_ID`                 decimal(20)     NOT NULL
    , `DEPENDENT_ID_ORIG`            decimal(20)     NOT NULL
    , `DEPENDENCY_OPERATION`         integer         NOT NULL
    , `REQUIRED_ID`                  decimal(20)     NOT NULL
    , `REQUIRED_SE_ID`               decimal(20)         NULL
    , `STATE`                        integer         NOT NULL
    , `IGNORE`                       integer         NOT NULL
    , `DI_ID_ORIG`                   decimal(20)     NOT NULL
    , `SE_VERSION`                   decimal(20)     NOT NULL
    , `IGNORE_TS`                    decimal(20)         NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_DEPENDENCY_INSTANCE
ON `DEPENDENCY_INSTANCE`(`ID`);
CREATE VIEW SCI_DEPENDENCY_INSTANCE AS
SELECT
    ID
    , `DD_ID`                        AS `DD_ID`
    , `DEPENDENT_ID`                 AS `DEPENDENT_ID`
    , `DEPENDENT_ID_ORIG`            AS `DEPENDENT_ID_ORIG`
    , CASE `DEPENDENCY_OPERATION` WHEN 1 THEN 'AND' WHEN 2 THEN 'OR' END AS `DEPENDENCY_OPERATION`
    , `REQUIRED_ID`                  AS `REQUIRED_ID`
    , `REQUIRED_SE_ID`               AS `REQUIRED_SE_ID`
    , CASE `STATE` WHEN 0 THEN 'OPEN' WHEN 1 THEN 'FULFILLED' WHEN 2 THEN 'FAILED' WHEN 3 THEN 'BROKEN' WHEN 4 THEN 'DEFERRED' WHEN 8 THEN 'CANCELLED' END AS `STATE`
    , CASE `IGNORE` WHEN 0 THEN 'NO' WHEN 1 THEN 'YES' WHEN 2 THEN 'RECURSIVE' END AS `IGNORE`
    , `DI_ID_ORIG`                   AS `DI_ID_ORIG`
    , `SE_VERSION`                   AS `SE_VERSION`
    , from_unixtime((`IGNORE_TS` & ~1125899906842624)/1000) AS `IGNORE_TS`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `DEPENDENCY_INSTANCE`;
CREATE TABLE ARC_DEPENDENCY_INSTANCE (
    ID                             decimal(20) NOT NULL
    , `DD_ID`                        decimal(20)      NULL
    , `DEPENDENT_ID`                 decimal(20)      NULL
    , `DEPENDENT_ID_ORIG`            decimal(20)      NULL
    , `DEPENDENCY_OPERATION`         integer          NULL
    , `REQUIRED_ID`                  decimal(20)      NULL
    , `REQUIRED_SE_ID`               decimal(20)      NULL
    , `STATE`                        integer          NULL
    , `IGNORE`                       integer          NULL
    , `DI_ID_ORIG`                   decimal(20)      NULL
    , `SE_VERSION`                   decimal(20)      NULL
    , `IGNORE_TS`                    decimal(20)      NULL
    , `CREATOR_U_ID`                 decimal(20)      NULL
    , `CREATE_TS`                    decimal(20)      NULL
    , `CHANGER_U_ID`                 decimal(20)      NULL
    , `CHANGE_TS`                    decimal(20)      NULL
) ENGINE = INNODB;
