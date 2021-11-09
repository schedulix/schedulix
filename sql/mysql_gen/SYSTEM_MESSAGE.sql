-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE `SYSTEM_MESSAGE` (
    `ID`                           decimal(20) NOT NULL
    , `MSG_TYPE`                     integer         NOT NULL
    , `SME_ID`                       decimal(20)     NOT NULL
    , `MASTER_ID`                    decimal(20)     NOT NULL
    , `OPERATION`                    integer         NOT NULL
    , `IS_MANDATORY`                 integer         NOT NULL
    , `REQUEST_U_ID`                 decimal(20)     NOT NULL
    , `REQUEST_TS`                   decimal(20)     NOT NULL
    , `REQUEST_MSG`                  varchar(512)        NULL
    , `ADDITIONAL_LONG`              decimal(20)         NULL
    , `ADDITIONAL_BOOL`              integer             NULL
    , `SECOND_LONG`                  decimal(20)         NULL
    , `COMMENT`                      varchar(1024)       NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) ENGINE = INNODB;
CREATE UNIQUE INDEX PK_SYSTEM_MESSAGE
ON `SYSTEM_MESSAGE`(`ID`);
CREATE VIEW SCI_SYSTEM_MESSAGE AS
SELECT 
    ID 
    , CASE `MSG_TYPE` WHEN 1 THEN 'APPROVAL' END AS `MSG_TYPE`
    , `SME_ID`                       AS `SME_ID`
    , `MASTER_ID`                    AS `MASTER_ID`
    , CASE `OPERATION` WHEN 1 THEN 'CANCEL' WHEN 2 THEN 'RERUN' WHEN 3 THEN 'ENABLE' WHEN 4 THEN 'SET_STATE' WHEN 5 THEN 'IGN_DEPENDENCY' WHEN 6 THEN 'IGN_RESOURCE' WHEN 7 THEN 'CLONE' WHEN 8 THEN 'SUSPEND' WHEN 9 THEN 'CLEAR_WARNING' WHEN 10 THEN 'PRIORITY' WHEN 11 THEN 'MODIFY_PARAMETER' WHEN 12 THEN 'KILL' WHEN 23 THEN 'DISABLE' WHEN 28 THEN 'RESUME' WHEN 29 THEN 'SET_WARNING' WHEN 30 THEN 'RENICE' WHEN 50 THEN 'NICEVALUE' WHEN 13 THEN 'SET_JOB_STATE' END AS `OPERATION`
    , CASE `IS_MANDATORY` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `IS_MANDATORY`
    , `REQUEST_U_ID`                 AS `REQUEST_U_ID`
    , from_unixtime((`REQUEST_TS` & ~1125899906842624)/1000) AS `REQUEST_TS`
    , `REQUEST_MSG`                  AS `REQUEST_MSG`
    , `ADDITIONAL_LONG`              AS `ADDITIONAL_LONG`
    , CASE `ADDITIONAL_BOOL` WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS `ADDITIONAL_BOOL`
    , `SECOND_LONG`                  AS `SECOND_LONG`
    , `COMMENT`                      AS `COMMENT`
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`
  FROM `SYSTEM_MESSAGE`;
