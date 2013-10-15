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

CREATE TABLE RESSOURCE (
    ID                             DECIMAL(20) NOT NULL
    , `NR_ID`                        decimal(20)     NOT NULL
    , `SCOPE_ID`                     decimal(20)         NULL
    , `MASTER_ID`                    decimal(20)         NULL
    , `OWNER_ID`                     decimal(20)     NOT NULL
    , `LINK_ID`                      decimal(20)         NULL
    , `MANAGER_ID`                   decimal(20)         NULL
    , `TAG`                          varchar(64)         NULL
    , `RSD_ID`                       decimal(20)         NULL
    , `RSD_TIME`                     decimal(20)         NULL
    , `DEFINED_AMOUNT`               integer             NULL
    , `REQUESTABLE_AMOUNT`           integer             NULL
    , `AMOUNT`                       integer             NULL
    , `FREE_AMOUNT`                  integer             NULL
    , `IS_ONLINE`                    integer             NULL
    , `FACTOR`                       float               NULL
    , `TRACE_INTERVAL`               integer             NULL
    , `TRACE_BASE`                   integer             NULL
    , `TRACE_BASE_MULTIPLIER`        integer         NOT NULL
    , `TD0_AVG`                      float           NOT NULL
    , `TD1_AVG`                      float           NOT NULL
    , `TD2_AVG`                      float           NOT NULL
    , `LW_AVG`                       float           NOT NULL
    , `LAST_EVAL`                    decimal(20)     NOT NULL
    , `LAST_WRITE`                   decimal(20)     NOT NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) engine = innodb;
CREATE UNIQUE INDEX PK_RESSOURCE
ON RESSOURCE(id);
CREATE VIEW SCI_RESSOURCE AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SCOPE_ID`                     AS `SCOPE_ID`
    , `MASTER_ID`                    AS `MASTER_ID`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `LINK_ID`                      AS `LINK_ID`
    , `MANAGER_ID`                   AS `MANAGER_ID`
    , `TAG`                          AS `TAG`
    , `RSD_ID`                       AS `RSD_ID`
    , from_unixtime((`RSD_TIME` & ~1125899906842624)/1000) AS `RSD_TIME`
    , `DEFINED_AMOUNT`               AS `DEFINED_AMOUNT`
    , `REQUESTABLE_AMOUNT`           AS `REQUESTABLE_AMOUNT`
    , `AMOUNT`                       AS `AMOUNT`
    , `FREE_AMOUNT`                  AS `FREE_AMOUNT`
    , CASE `IS_ONLINE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ONLINE`
    , `FACTOR`                       AS `FACTOR`
    , `TRACE_INTERVAL`               AS `TRACE_INTERVAL`
    , `TRACE_BASE`                   AS `TRACE_BASE`
    , `TRACE_BASE_MULTIPLIER`        AS `TRACE_BASE_MULTIPLIER`
    , `TD0_AVG`                      AS `TD0_AVG`
    , `TD1_AVG`                      AS `TD1_AVG`
    , `TD2_AVG`                      AS `TD2_AVG`
    , `LW_AVG`                       AS `LW_AVG`
    , from_unixtime((`LAST_EVAL` & ~1125899906842624)/1000) AS `LAST_EVAL`
    , from_unixtime((`LAST_WRITE` & ~1125899906842624)/1000) AS `LAST_WRITE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM RESSOURCE;
