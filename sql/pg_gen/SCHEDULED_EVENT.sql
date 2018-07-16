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

CREATE TABLE SCHEDULED_EVENT (
    ID                             decimal(20) NOT NULL
    , OWNER_ID                       decimal(20)     NOT NULL
    , SCE_ID                         decimal(20)     NOT NULL
    , EVT_ID                         decimal(20)     NOT NULL
    , ACTIVE                         integer         NOT NULL
    , BROKEN                         integer         NOT NULL
    , ERROR_CODE                     varchar(32)         NULL
    , ERROR_MSG                      varchar(256)        NULL
    , LAST_START_TIME                decimal(20)         NULL
    , NEXT_START_TIME                decimal(20)         NULL
    , NEXT_IS_TRIGGER                integer             NULL
    , BACKLOG_HANDLING               integer         NOT NULL
    , SUSPEND_LIMIT                  integer             NULL
    , SUSPEND_LIMIT_MULTIPLIER       integer             NULL
    , IS_CALENDAR                    integer         NOT NULL
    , CALENDAR_HORIZON               integer             NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);
CREATE UNIQUE INDEX PK_SCHEDULED_EVENT
ON SCHEDULED_EVENT(ID);
CREATE VIEW SCI_SCHEDULED_EVENT AS
SELECT
    ID
    , OWNER_ID                       AS OWNER_ID
    , SCE_ID                         AS SCE_ID
    , EVT_ID                         AS EVT_ID
    , CASE ACTIVE WHEN 1 THEN 'ACTIVE' WHEN 0 THEN 'INACTIVE' END AS ACTIVE
    , CASE BROKEN WHEN 1 THEN 'BROKEN' WHEN 0 THEN 'NOBROKEN' END AS BROKEN
    , ERROR_CODE                     AS ERROR_CODE
    , ERROR_MSG                      AS ERROR_MSG
    , timestamptz 'epoch' + cast(to_char(mod(LAST_START_TIME, 1125899906842624)/1000, '999999999999') as interval) AS LAST_START_TIME
    , timestamptz 'epoch' + cast(to_char(mod(NEXT_START_TIME, 1125899906842624)/1000, '999999999999') as interval) AS NEXT_START_TIME
    , CASE NEXT_IS_TRIGGER WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS NEXT_IS_TRIGGER
    , CASE BACKLOG_HANDLING WHEN 0 THEN 'NONE' WHEN 1 THEN 'LAST' WHEN 2 THEN 'ALL' END AS BACKLOG_HANDLING
    , CASE SUSPEND_LIMIT WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS SUSPEND_LIMIT
    , SUSPEND_LIMIT_MULTIPLIER       AS SUSPEND_LIMIT_MULTIPLIER
    , CASE IS_CALENDAR WHEN 1 THEN 'ACTIVE' WHEN 0 THEN 'INACTIVE' END AS IS_CALENDAR
    , CALENDAR_HORIZON               AS CALENDAR_HORIZON
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , timestamptz 'epoch' + cast(to_char(mod(CREATE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , timestamptz 'epoch' + cast(to_char(mod(CHANGE_TS, 1125899906842624)/1000, '999999999999') as interval) AS CHANGE_TS
  FROM SCHEDULED_EVENT;
