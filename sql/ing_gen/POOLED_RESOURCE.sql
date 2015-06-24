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

CREATE TABLE POOLED_RESOURCE (
    ID                             decimal(20) NOT NULL
    , P_ID                           decimal(20)     NOT NULL
    , R_ID                           decimal(20)     NOT NULL
    , IS_POOL                        integer         NOT NULL
    , IS_MANAGED                     integer         NOT NULL
    , NOM_PCT                        integer         NOT NULL
    , FREE_PCT                       integer         NOT NULL
    , MIN_PCT                        integer         NOT NULL
    , MAX_PCT                        integer         NOT NULL
    , ACT_IS_MANAGED                 integer         NOT NULL
    , ACT_NOM_PCT                    integer         NOT NULL
    , ACT_FREE_PCT                   integer         NOT NULL
    , ACT_MIN_PCT                    integer         NOT NULL
    , ACT_MAX_PCT                    integer         NOT NULL
    , TARGET_AMOUNT                  integer         NOT NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_POOLED_RESOURCE
ON POOLED_RESOURCE(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_POOLED_RESOURCE AS
SELECT
    ID
    , P_ID                           AS P_ID
    , R_ID                           AS R_ID
    , CASE IS_POOL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_POOL
    , CASE IS_MANAGED WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_MANAGED
    , NOM_PCT                        AS NOM_PCT
    , FREE_PCT                       AS FREE_PCT
    , MIN_PCT                        AS MIN_PCT
    , MAX_PCT                        AS MAX_PCT
    , CASE ACT_IS_MANAGED WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS ACT_IS_MANAGED
    , ACT_NOM_PCT                    AS ACT_NOM_PCT
    , ACT_FREE_PCT                   AS ACT_FREE_PCT
    , ACT_MIN_PCT                    AS ACT_MIN_PCT
    , ACT_MAX_PCT                    AS ACT_MAX_PCT
    , TARGET_AMOUNT                  AS TARGET_AMOUNT
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM POOLED_RESOURCE;\g
