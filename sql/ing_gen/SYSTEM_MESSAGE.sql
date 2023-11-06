-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2014 independIT Integrative Technologies GmbH

CREATE TABLE SYSTEM_MESSAGE (
    ID                             decimal(20) NOT NULL
    , MSG_TYPE                       integer         NOT NULL
    , SME_ID                         decimal(20)     NOT NULL
    , MASTER_ID                      decimal(20)     NOT NULL
    , OPERATION                      integer         NOT NULL
    , IS_MANDATORY                   integer         NOT NULL
    , REQUEST_U_ID                   decimal(20)     NOT NULL
    , REQUEST_TS                     decimal(20)     NOT NULL
    , REQUEST_MSG                    varchar(512)    WITH NULL
    , ADDITIONAL_LONG                decimal(20)     WITH NULL
    , ADDITIONAL_BOOL                integer         WITH NULL
    , SECOND_LONG                    decimal(20)     WITH NULL
    , COMMENT                        varchar(1024)   WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_SYSTEM_MESSAGE
ON SYSTEM_MESSAGE(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_SYSTEM_MESSAGE AS
SELECT 
    ID 
    , CASE MSG_TYPE WHEN 1 THEN 'APPROVAL' END AS MSG_TYPE
    , SME_ID                         AS SME_ID
    , MASTER_ID                      AS MASTER_ID
    , CASE OPERATION WHEN 1 THEN 'CANCEL' WHEN 2 THEN 'RERUN' WHEN 3 THEN 'ENABLE' WHEN 4 THEN 'SET_STATE' WHEN 5 THEN 'IGN_DEPENDENCY' WHEN 6 THEN 'IGN_RESOURCE' WHEN 7 THEN 'CLONE' WHEN 11 THEN 'MODIFY_PARAMETER' WHEN 12 THEN 'KILL' WHEN 23 THEN 'DISABLE' WHEN 13 THEN 'SET_JOB_STATE' END AS OPERATION
    , CASE IS_MANDATORY WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS IS_MANDATORY
    , REQUEST_U_ID                   AS REQUEST_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((REQUEST_TS- decimal(REQUEST_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS REQUEST_TS
    , REQUEST_MSG                    AS REQUEST_MSG
    , ADDITIONAL_LONG                AS ADDITIONAL_LONG
    , CASE ADDITIONAL_BOOL WHEN 1 THEN 'TRUE' WHEN 0 THEN 'FALSE' END AS ADDITIONAL_BOOL
    , SECOND_LONG                    AS SECOND_LONG
    , COMMENT                        AS COMMENT
    , CREATOR_U_ID                   AS CREATOR_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS
    , CHANGER_U_ID                   AS CHANGER_U_ID
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS
  FROM SYSTEM_MESSAGE;\g
