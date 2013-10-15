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

CREATE TABLE OBJECT_EVENT (
    ID                             DECIMAL(20) NOT NULL
    , `TR_ID`                        decimal(20)         NULL
    , `OI_ID`                        decimal(20)     NOT NULL
    , `EVENT_TYPE`                   integer         NOT NULL
    , `SME_ID`                       decimal(20)     NOT NULL
    , `SE_ID`                        decimal(20)     NOT NULL
    , `SUBMIT_TS`                    decimal(20)         NULL
    , `FINAL_TS`                     decimal(20)         NULL
    , `FINAL_ESD_ID`                 decimal(20)         NULL
    , `MAIN_SME_ID`                  decimal(20)         NULL
    , `MAIN_SE_ID`                   decimal(20)         NULL
    , `MAIN_FINAL_TS`                decimal(20)         NULL
    , `MAIN_FINAL_ESD_ID`            decimal(20)         NULL
    , `SE_VERSION`                   decimal(20)     NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) engine = innodb;
CREATE UNIQUE INDEX PK_OBJECT_EVENT
ON OBJECT_EVENT(id);
CREATE VIEW SCI_OBJECT_EVENT AS
SELECT
    ID
    , `TR_ID`                        AS `TR_ID`
    , `OI_ID`                        AS `OI_ID`
    , CASE `EVENT_TYPE` WHEN 1 THEN 'CREATE' WHEN 2 THEN 'CHANGE' WHEN 3 THEN 'DELETE' END AS `EVENT_TYPE`
    , `SME_ID`                       AS `SME_ID`
    , `SE_ID`                        AS `SE_ID`
    , from_unixtime((`SUBMIT_TS` & ~1125899906842624)/1000) AS `SUBMIT_TS`
    , from_unixtime((`FINAL_TS` & ~1125899906842624)/1000) AS `FINAL_TS`
    , `FINAL_ESD_ID`                 AS `FINAL_ESD_ID`
    , `MAIN_SME_ID`                  AS `MAIN_SME_ID`
    , `MAIN_SE_ID`                   AS `MAIN_SE_ID`
    , from_unixtime((`MAIN_FINAL_TS` & ~1125899906842624)/1000) AS `MAIN_FINAL_TS`
    , `MAIN_FINAL_ESD_ID`            AS `MAIN_FINAL_ESD_ID`
    , `SE_VERSION`                   AS `SE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM OBJECT_EVENT;
