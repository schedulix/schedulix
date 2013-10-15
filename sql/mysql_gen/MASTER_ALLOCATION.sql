-- Copyright (C) 2001,2002 topIT Informationstechnologie GmbH
-- Copyright (C) 2003-2013 independIT Integrative Technologies GmbH

CREATE TABLE MASTER_ALLOCATION (
    ID                             DECIMAL(20) NOT NULL
    , `RA_ID`                        decimal(20)     NOT NULL
    , `SME_ID`                       decimal(20)     NOT NULL
    , `AMOUNT`                       integer             NULL
    , `STICKY_NAME`                  varchar(64)         NULL
    , `STICKY_PARENT`                decimal(20)         NULL
    , `LOCKMODE`                     integer             NULL
    , `CREATOR_U_ID`                 decimal(20)     NOT NULL
    , `CREATE_TS`                    decimal(20)     NOT NULL
    , `CHANGER_U_ID`                 decimal(20)     NOT NULL
    , `CHANGE_TS`                    decimal(20)     NOT NULL
) engine = innodb;
CREATE UNIQUE INDEX PK_MASTER_ALLOCATION
ON MASTER_ALLOCATION(id);
CREATE VIEW SCI_MASTER_ALLOCATION AS
SELECT 
    ID 
    , `RA_ID`                        AS `RA_ID`                       
    , `SME_ID`                       AS `SME_ID`                      
    , `AMOUNT`                       AS `AMOUNT`                      
    , `STICKY_NAME`                  AS `STICKY_NAME`                 
    , `STICKY_PARENT`                AS `STICKY_PARENT`               
    , CASE `LOCKMODE` WHEN 255 THEN 'N' WHEN 0 THEN 'X' WHEN 2 THEN 'SX' WHEN 4 THEN 'S' WHEN 6 THEN 'SC' END AS `LOCKMODE`                    
    , `CREATOR_U_ID`                 AS `CREATOR_U_ID`                
    , from_unixtime((`CREATE_TS` & ~1125899906842624)/1000) AS `CREATE_TS`                   
    , `CHANGER_U_ID`                 AS `CHANGER_U_ID`                
    , from_unixtime((`CHANGE_TS` & ~1125899906842624)/1000) AS `CHANGE_TS`                   
  FROM MASTER_ALLOCATION;
