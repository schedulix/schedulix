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

CREATE TABLE `RESOURCE_TEMPLATE` (
    `ID`                           decimal(20) NOT NULL
    , `NR_ID`                        decimal(20)     NOT NULL
    , `SE_ID`                        decimal(20)     NOT NULL
    , `OWNER_ID`                     decimal(20)     NOT NULL
    , `RSD_ID`                       decimal(20)         NULL
    , `REQUESTABLE_AMOUNT`           integer             NULL
    , `AMOUNT`                       integer             NULL
    , `IS_ONLINE`                    integer         NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
    , `VALID_FROM`                 decimal(20) NOT NULL
    , `VALID_TO`                   decimal(20) NOT NULL
) ENGINE = INNODB;
CREATE INDEX PK_RESOURCE_TEMPLATE
ON `RESOURCE_TEMPLATE`(`ID`);
CREATE VIEW SCI_C_RESOURCE_TEMPLATE AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `RSD_ID`                       AS `RSD_ID`
    , `REQUESTABLE_AMOUNT`           AS `REQUESTABLE_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `IS_ONLINE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ONLINE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_TEMPLATE`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_TEMPLATE AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `RSD_ID`                       AS `RSD_ID`
    , `REQUESTABLE_AMOUNT`           AS `REQUESTABLE_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `IS_ONLINE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ONLINE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_TEMPLATE`;
