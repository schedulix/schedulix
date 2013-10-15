-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2013 independIT Integrative Technologies GmbH

CREATE TABLE MASTER_ALLOCATION (
    ID                             DECIMAL(20) NOT NULL
    , RA_ID                          decimal(20)     NOT NULL
    , SME_ID                         decimal(20)     NOT NULL
    , AMOUNT                         integer         WITH NULL
    , STICKY_NAME                    varchar(64)     WITH NULL
    , STICKY_PARENT                  decimal(20)     WITH NULL
    , LOCKMODE                       integer         WITH NULL
    , CREATOR_U_ID                   decimal(20)     NOT NULL
    , CREATE_TS                      decimal(20)     NOT NULL
    , CHANGER_U_ID                   decimal(20)     NOT NULL
    , CHANGE_TS                      decimal(20)     NOT NULL
);\g
CREATE UNIQUE INDEX PK_MASTER_ALLOCATION
ON MASTER_ALLOCATION(ID) WITH STRUCTURE = BTREE;\g
CREATE VIEW SCI_MASTER_ALLOCATION AS
SELECT 
    ID 
    , RA_ID                          AS RA_ID                         
    , SME_ID                         AS SME_ID                        
    , AMOUNT                         AS AMOUNT                        
    , STICKY_NAME                    AS STICKY_NAME                   
    , STICKY_PARENT                  AS STICKY_PARENT                 
    , CASE LOCKMODE WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS LOCKMODE                      
    , CREATOR_U_ID                   AS CREATOR_U_ID                  
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CREATE_TS- decimal(CREATE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CREATE_TS                     
    , CHANGER_U_ID                   AS CHANGER_U_ID                  
    , '01-JAN-1970 00:00:00 GMT' + date(char(decimal((CHANGE_TS- decimal(CHANGE_TS/1125899906842624, 18, 0)*1125899906842624)/1000, 18, 0)) + ' secs') AS CHANGE_TS                     
  FROM MASTER_ALLOCATION;\g
