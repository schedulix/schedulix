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

CREATE TABLE `NICE_PROFILE_ENTRY` (
    `ID`                           decimal(20) NOT NULL
    , `NP_ID`                        decimal(20)     NOT NULL
    , `PREFERENCE`                   integer         NOT NULL
    , `FOLDER_ID`                    decimal(20)         NULL
    , `IS_SUSPENDED`                 integer         NOT NULL
    , `RENICE`                       integer         NOT NULL
    , `IS_ACTIVE`                    integer         NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_NICE_PROFILE_ENTRY
ON `NICE_PROFILE_ENTRY`(`ID`);
CREATE VIEW SCI_NICE_PROFILE_ENTRY AS
SELECT
    ID
    , `NP_ID`                        AS `NP_ID`
    , `PREFERENCE`                   AS `PREFERENCE`
    , `FOLDER_ID`                    AS `FOLDER_ID`
    , CASE `IS_SUSPENDED` WHEN 0 THEN 'NOSUSPEND' WHEN 1 THEN 'SUSPEND' WHEN 2 THEN 'ADMINSUSPEND' END AS `IS_SUSPENDED`
    , `RENICE`                       AS `RENICE`
    , CASE `IS_ACTIVE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ACTIVE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `NICE_PROFILE_ENTRY`;
