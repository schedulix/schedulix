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

CREATE TABLE `TRIGGER_QUEUE` (
    `ID`                           decimal(20) NOT NULL
    , `SME_ID`                       decimal(20)     NOT NULL
    , `TR_ID`                        decimal(20)     NOT NULL
    , `NEXT_TRIGGER_TIME`            decimal(20)     NOT NULL
    , `TIMES_CHECKED`                integer         NOT NULL
    , `TIMES_TRIGGERED`              integer         NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_TRIGGER_QUEUE
ON `TRIGGER_QUEUE`(`ID`);
CREATE VIEW SCI_TRIGGER_QUEUE AS
SELECT
    ID
    , `SME_ID`                       AS `SME_ID`
    , `TR_ID`                        AS `TR_ID`
    , `NEXT_TRIGGER_TIME`            AS `NEXT_TRIGGER_TIME`
    , `TIMES_CHECKED`                AS `TIMES_CHECKED`
    , `TIMES_TRIGGERED`              AS `TIMES_TRIGGERED`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `TRIGGER_QUEUE`;
