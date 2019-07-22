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

CREATE TABLE `RESOURCE_TRACE` (
    `R_ID`                         decimal(20)     NOT NULL
    , `TAG`                          varchar(64)         NULL
    , `TRACE_INTERVAL`               integer             NULL
    , `TRACE_BASE`                   integer             NULL
    , `TRACE_BASE_MULTIPLIER`        integer         NOT NULL
    , `TD0_AVG`                      float           NOT NULL
    , `TD1_AVG`                      float           NOT NULL
    , `TD2_AVG`                      float           NOT NULL
    , `LW_AVG`                       float           NOT NULL
    , `WRITE_TIME`                   decimal(20)     NOT NULL
    , `LAST_WRITE`                   decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE VIEW SCI_RESOURCE_TRACE AS
SELECT
    `R_ID`                         AS `R_ID`
    , `TAG`                          AS `TAG`
    , `TRACE_INTERVAL`               AS `TRACE_INTERVAL`
    , `TRACE_BASE`                   AS `TRACE_BASE`
    , `TRACE_BASE_MULTIPLIER`        AS `TRACE_BASE_MULTIPLIER`
    , `TD0_AVG`                      AS `TD0_AVG`
    , `TD1_AVG`                      AS `TD1_AVG`
    , `TD2_AVG`                      AS `TD2_AVG`
    , `LW_AVG`                       AS `LW_AVG`
    , from_unixtime((`WRITE_TIME` & ~1125899906842624)/1000) AS `WRITE_TIME`
    , from_unixtime((`LAST_WRITE` & ~1125899906842624)/1000) AS `LAST_WRITE`
  FROM `RESOURCE_TRACE`;
