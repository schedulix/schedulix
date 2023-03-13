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
ALTER TABLE `RESOURCE_ALLOCATION`
    ADD `STICKY_NAME` varchar(64),
    ADD `STICKY_PARENT` decimal(20);
DROP VIEW SCI_RESOURCE_ALLOCATION;
CREATE VIEW SCI_RESOURCE_ALLOCATION AS
SELECT
    ID
    , `R_ID`                         AS `R_ID`
    , `SME_ID`                       AS `SME_ID`
    , `NR_ID`                        AS `NR_ID`
    , `AMOUNT`                       AS `AMOUNT`
    , `ORIG_AMOUNT`                  AS `ORIG_AMOUNT`
    , CASE `KEEP_MODE` WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS `KEEP_MODE`
    , CASE `IS_STICKY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STICKY`
    , `STICKY_NAME`                  AS `STICKY_NAME`
    , `STICKY_PARENT`                AS `STICKY_PARENT`
    , CASE `ALLOCATION_TYPE` WHEN 1 THEN 'REQUEST' WHEN 6 THEN 'MASTER_REQUEST' WHEN 2 THEN 'RESERVATION' WHEN 3 THEN 'MASTER_RESERVATION' WHEN 4 THEN 'ALLOCATION' WHEN 5 THEN 'IGNORE' END AS `ALLOCATION_TYPE`
    , `RSMP_ID`                      AS `RSMP_ID`
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`
    , `REFCOUNT`                     AS `REFCOUNT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_ALLOCATION`;
ALTER TABLE `RESOURCE_REQUIREMENT`
    ADD `STICKY_NAME` varchar(64),
    ADD `STICKY_PARENT` decimal(20);
DROP VIEW SCI_C_RESOURCE_REQUIREMENT;
DROP VIEW SCI_V_RESOURCE_REQUIREMENT;
CREATE VIEW SCI_C_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `KEEP_MODE` WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS `KEEP_MODE`
    , CASE `IS_STICKY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STICKY`
    , `STICKY_NAME`                  AS `STICKY_NAME`
    , `STICKY_PARENT`                AS `STICKY_PARENT`
    , `RSMP_ID`                      AS `RSMP_ID`
    , `EXPIRED_AMOUNT`               AS `EXPIRED_AMOUNT`
    , CASE `EXPIRED_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `EXPIRED_BASE`
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `RESOURCE_REQUIREMENT`
 WHERE VALID_TO = 9223372036854775807;
CREATE VIEW SCI_V_RESOURCE_REQUIREMENT AS
SELECT
    ID
    , `NR_ID`                        AS `NR_ID`
    , `SE_ID`                        AS `SE_ID`
    , `AMOUNT`                       AS `AMOUNT`
    , CASE `KEEP_MODE` WHEN 0 THEN 'NOKEEP' WHEN 1 THEN 'KEEP' WHEN 2 THEN 'KEEP_FINAL' END AS `KEEP_MODE`
    , CASE `IS_STICKY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_STICKY`
    , `STICKY_NAME`                  AS `STICKY_NAME`
    , `STICKY_PARENT`                AS `STICKY_PARENT`
    , `RSMP_ID`                      AS `RSMP_ID`
    , `EXPIRED_AMOUNT`               AS `EXPIRED_AMOUNT`
    , CASE `EXPIRED_BASE` WHEN 0 THEN 'MINUTE' WHEN 1 THEN 'HOUR' WHEN 2 THEN 'DAY' WHEN 3 THEN 'WEEK' WHEN 4 THEN 'MONTH' WHEN 5 THEN 'YEAR' END AS `EXPIRED_BASE`
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`
    , `CONDITION`                    AS `CONDITION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , VALID_FROM
    , VALID_TO
  FROM `RESOURCE_REQUIREMENT`;
ALTER TABLE `SCOPE`
    ADD `SALT` varchar(64),
    ADD `METHOD` integer NOT NULL DEFAULT 0;
DROP VIEW SCI_SCOPE;
CREATE VIEW SCI_SCOPE AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , `OWNER_ID`                     AS `OWNER_ID`
    , `PARENT_ID`                    AS `PARENT_ID`
    , CASE `TYPE` WHEN 1 THEN 'SCOPE' WHEN 2 THEN 'SERVER' END AS `TYPE`
    , CASE `IS_TERMINATE` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_TERMINATE`
    , CASE `HAS_ALTEREDCONFIG` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `HAS_ALTEREDCONFIG`
    , CASE `IS_SUSPENDED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_SUSPENDED`
    , CASE `IS_ENABLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ENABLED`
    , CASE `IS_REGISTERED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_REGISTERED`
    , CASE `STATE` WHEN 1 THEN 'NOMINAL' WHEN 2 THEN 'NONFATAL' WHEN 3 THEN 'FATAL' END AS `STATE`
    , `PID`                          AS `PID`
    , `NODE`                         AS `NODE`
    , `ERRMSG`                       AS `ERRMSG`
    , `LAST_ACTIVE`                  AS `LAST_ACTIVE`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
    , `INHERIT_PRIVS`                AS `INHERIT_PRIVS`
  FROM `SCOPE`;
ALTER TABLE `USERS`
    ADD `SALT` varchar(64),
    ADD `METHOD` integer NOT NULL DEFAULT 0;
DROP VIEW SCI_USERS;
CREATE VIEW SCI_USERS AS
SELECT
    ID
    , `NAME`                         AS `NAME`
    , CASE `IS_ENABLED` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_ENABLED`
    , `DEFAULT_G_ID`                 AS `DEFAULT_G_ID`
    , `DELETE_VERSION`               AS `DELETE_VERSION`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `USERS`;
CREATE TABLE SME2LOAD (ID DECIMAL(20) NOT NULL) ENGINE = INNODB;
CREATE INDEX SME2LOAD_IDX ON SME2LOAD (`ID`);
CREATE INDEX SME_IDX ON SUBMITTED_ENTITY (`MASTER_ID`, `ID`, `STATE`, `FINAL_TS`);
CREATE INDEX KILL_JOB_IDX ON KILL_JOB (`SME_ID`);
CREATE INDEX AUDIT_TRAIL_IDX ON AUDIT_TRAIL (`OBJECT_ID`);
CREATE INDEX ENTITY_VARIABLE_IDX ON ENTITY_VARIABLE (`SME_ID`);
CREATE INDEX DEPENDENCY_INSTANCE_IDX ON DEPENDENCY_INSTANCE (`DEPENDENT_ID`);
CREATE INDEX HIERARCHY_INSTANCE_IDX ON HIERARCHY_INSTANCE (`CHILD_ID`);
UPDATE `RESOURCE_ALLOCATION`
   SET `STICKY_PARENT` = (
       SELECT `MASTER_ID`
         FROM SUBMITTED_ENTITY
        WHERE `ID` = `SME_ID`
       )
 WHERE `IS_STICKY` = 1
   AND `SME_ID` > 0;
UPDATE `RESOURCE_ALLOCATION`
   SET `STICKY_PARENT` = -`SME_ID`
 WHERE `IS_STICKY` = 1
   AND `SME_ID` < 0;
ALTER TABLE `SCOPE` MODIFY PASSWD VARCHAR(64);
