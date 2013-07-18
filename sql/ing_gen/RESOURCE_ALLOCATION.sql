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
-- Copyright (C) 2003-2006 independIT Integrative Technologies GmbH

CREATE TABLE RESOURCE_ALLOCATION (
    ID                             DECIMAL(20) NOT NULL
    , R_ID                           decimal(20)     NOT NULL
    , SME_ID                         decimal(20)     NOT NULL
    , NR_ID                          decimal(20)     NOT NULL
    , AMOUNT                         integer         WITH NULL
    , ORIG_AMOUNT                    integer         WITH NULL
    , KEEP_MODE                      integer         NOT NULL
    , IS_STICKY                      integer         NOT NULL
    , ALLOCATION_TYPE                integer         NOT NULL
    , RSMP_ID                        decimal(20)     WITH NULL
    , LOCKMODE                       integer         WITH NULL
    , REFCOUNT                       integer         NOT NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_RESOURCE_ALLOCATION
ON RESOURCE_ALLOCATION(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_RESOURCE_ALLOCATION AS
SELECT
    ID
    , R_ID                           AS R_ID
    , SME_ID                         AS SME_ID
    , NR_ID                          AS NR_ID
    , AMOUNT                         AS AMOUNT
    , ORIG_AMOUNT                    AS ORIG_AMOUNT
    , CASE KEEP_MODE WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS KEEP_MODE
    , CASE IS_STICKY WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_STICKY
    , CASE ALLOCATION_TYPE WHEN 1 THEN 'REQUEST' WHEN 2 THEN 'RESERVATION' WHEN 3 THEN 'MASTER_RESERVATION' WHEN 4 THEN 'ALLOCATION' WHEN 5 THEN 'IGNORE' END AS ALLOCATION_TYPE
    , RSMP_ID                        AS RSMP_ID
    , CASE LOCKMODE WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS LOCKMODE
    , REFCOUNT                       AS REFCOUNT
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM RESOURCE_ALLOCATION;\g
