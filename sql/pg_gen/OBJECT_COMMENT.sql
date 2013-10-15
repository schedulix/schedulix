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

CREATE TABLE OBJECT_COMMENT (
    ID                             DECIMAL(20) NOT NULL
    , OBJECT_ID                      decimal(20)     NOT NULL
    , OBJECT_TYPE                    integer         NOT NULL
    , INFO_TYPE                      integer         NOT NULL
    , SEQUENCE_NUMBER                integer         NOT NULL
    , DESCRIPTION                    varchar(1900)   NOT NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
    , VALID_FROM                   DECIMAL(20) NOT NULL
    , VALID_TO                     DECIMAL(20) NOT NULL
);
CREATE INDEX PK_OBJECT_COMMENT
ON OBJECT_COMMENT(ID);
CREATE VIEW SCI_C_OBJECT_COMMENT AS
SELECT
    ID
    , OBJECT_ID                      AS OBJECT_ID
    , CASE OBJECT_TYPE WHEN 9 THEN 'JOB_DEFINITION' WHEN 2 THEN 'EXIT_STATE_DEFINITION' WHEN 3 THEN 'EXIT_STATE_PROFILE' WHEN 4 THEN 'EXIT_STATE_MAPPING' WHEN 5 THEN 'EXIT_STATE_TRANSLATION' WHEN 6 THEN 'FOLDER' WHEN 15 THEN 'SCOPE' WHEN 10 THEN 'NAMED_RESOURCE' WHEN 11 THEN 'RESOURCE' WHEN 1 THEN 'ENVIRONMENT' WHEN 7 THEN 'FOOTPRINT' WHEN 13 THEN 'RESOURCE_STATE_DEFINITION' WHEN 14 THEN 'RESOURCE_STATE_PROFILE' WHEN 12 THEN 'RESOURCE_STATE_MAPPING' WHEN 8 THEN 'USER' WHEN 16 THEN 'TRIGGER' WHEN 17 THEN 'JOB' WHEN 18 THEN 'EVENT' WHEN 19 THEN 'INTERVAL' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 21 THEN 'GROUP' WHEN 23 THEN 'PARAMETER' WHEN 24 THEN 'POOL' WHEN 25 THEN 'DISTRIBUTION' WHEN 29 THEN 'WATCH_TYPE' WHEN 30 THEN 'OBJECT_MONITOR' END AS OBJECT_TYPE
    , CASE INFO_TYPE WHEN 0 THEN 'TEXT' WHEN 1 THEN 'URL' END AS INFO_TYPE
    , SEQUENCE_NUMBER                AS SEQUENCE_NUMBER
    , DESCRIPTION                    AS DESCRIPTION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM OBJECT_COMMENT
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_OBJECT_COMMENT AS
SELECT
    ID
    , OBJECT_ID                      AS OBJECT_ID
    , CASE OBJECT_TYPE WHEN 9 THEN 'JOB_DEFINITION' WHEN 2 THEN 'EXIT_STATE_DEFINITION' WHEN 3 THEN 'EXIT_STATE_PROFILE' WHEN 4 THEN 'EXIT_STATE_MAPPING' WHEN 5 THEN 'EXIT_STATE_TRANSLATION' WHEN 6 THEN 'FOLDER' WHEN 15 THEN 'SCOPE' WHEN 10 THEN 'NAMED_RESOURCE' WHEN 11 THEN 'RESOURCE' WHEN 1 THEN 'ENVIRONMENT' WHEN 7 THEN 'FOOTPRINT' WHEN 13 THEN 'RESOURCE_STATE_DEFINITION' WHEN 14 THEN 'RESOURCE_STATE_PROFILE' WHEN 12 THEN 'RESOURCE_STATE_MAPPING' WHEN 8 THEN 'USER' WHEN 16 THEN 'TRIGGER' WHEN 17 THEN 'JOB' WHEN 18 THEN 'EVENT' WHEN 19 THEN 'INTERVAL' WHEN 20 THEN 'SCHEDULE' WHEN 22 THEN 'SCHEDULED_EVENT' WHEN 21 THEN 'GROUP' WHEN 23 THEN 'PARAMETER' WHEN 24 THEN 'POOL' WHEN 25 THEN 'DISTRIBUTION' WHEN 29 THEN 'WATCH_TYPE' WHEN 30 THEN 'OBJECT_MONITOR' END AS OBJECT_TYPE
    , CASE INFO_TYPE WHEN 0 THEN 'TEXT' WHEN 1 THEN 'URL' END AS INFO_TYPE
    , SEQUENCE_NUMBER                AS SEQUENCE_NUMBER
    , DESCRIPTION                    AS DESCRIPTION
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamp 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
    , VALID_FROM
    , VALID_TO
  FROM OBJECT_COMMENT;
