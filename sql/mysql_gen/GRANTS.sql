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

CREATE TABLE GRANTS (
    ID                             DECIMAL(20) NOT NULL
    , `OBJECT_ID`                    decimal(20)     NOT NULL
    , `G_ID`                         decimal(20)     NOT NULL
    , `OBJECT_TYPE`                  integer         NOT NULL
    , `PRIVS`                        decimal(20)     NOT NULL
    , `DELETE_VERSION`               decimal(20)         NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) engine = innodb;
CREATE UNIQUE INDEX PK_GRANTS
ON GRANTS(id);
CREATE VIEW SCI_GRANTS AS
SELECT
    ID
    , `OBJECT_ID`                    AS `OBJECT_ID`
    , `G_ID`                         AS `G_ID`
    , CASE `OBJECT_TYPE` WHEN 1 THEN 'ENVIRONMENT' WHEN 18 THEN 'EVENT' WHEN 6 THEN 'FOLDER' WHEN 19 THEN 'INTERVAL' WHEN 17 THEN 'JOB' WHEN 9 THEN 'JOB_DEFINITION' WHEN 10 THEN 'NAMED_RESOURCE' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 15 THEN 'SCOPE' WHEN 21 THEN 'GROUP' WHEN 11 THEN 'RESOURCE' WHEN 2 THEN 'EXIT_STATE_DEFINITION' WHEN 3 THEN 'EXIT_STATE_PROFILE' WHEN 4 THEN 'EXIT_STATE_MAPPING' WHEN 5 THEN 'EXIT_STATE_TRANSLATION' WHEN 13 THEN 'RESOURCE_STATE_DEFINITION' WHEN 14 THEN 'RESOURCE_STATE_PROFILE' WHEN 12 THEN 'RESOURCE_STATE_MAPPING' WHEN 7 THEN 'FOOTPRINT' WHEN 8 THEN 'USER' WHEN 30 THEN 'OBJECT_MONITOR' WHEN 0 THEN 'SYSTEM' END AS `OBJECT_TYPE`
    , `PRIVS`                        AS `PRIVS`
    , `DELETE_VERSION`               AS `DELETE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM GRANTS;
