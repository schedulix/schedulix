#!/bin/bash

MASTERID=$1
shift
SDMSPOPUPOPTIONS=$*

echo MASTERID=$MASTERID
echo SDMSPOPUPOPTIONS=$SDMSPOPUPOPTIONS

set -x

echo "show job $MASTERID; show resource state mapping dog;" |
sdmsh

echo "show job $MASTERID; show resource state mapping dog;" | 
sdmsh | 
awk '
BEGIN { for (i=0;i <81;++i) w[i]=1; } 
$2 ~ /RESOURCE.EXAMPLES.E0440_DOG.DOG/ {
	o = substr($6, 5, 2) - 1;
	w[((o - o%4)/4 + 1)*18 + 2*(o%4) - 8] = 0
}
$2 ~ /ROOM../ { 
	if ($1 == "NORTH") r = -9;
	if ($1 == "EAST")  r = 1;
	if ($1 == "SOUTH") r = 9;
	if ($1 == "WEST")  r = -1;
	o = substr($2,5,2) - 1;
	w[((o - o%4)/4 + 1)*18 + 2*(o%4) - 8 + r] = 0;
}
END { 
cmd = "SDMSpopup.sh -c 0=OK '"$SDMSPOPUPOPTIONS"' "
for (zeile = 0; zeile < 9; ++zeile) {
    cmd = cmd "\""
    for (spalte = 0; spalte < 9; ++spalte) {
       if (spalte % 2 == 0) {
           if (zeile % 2 == 0) {
               cmd = cmd "+"
           } else {
               if (w[zeile*9 + spalte] == 1) cmd = cmd "|"
               else                          cmd = cmd " "
           }
       } else {
           if (zeile % 2 == 0) {
               if (w[zeile*9 + spalte] == 1) cmd = cmd "---"
               else                          cmd = cmd "   "
           } else {
               if (w[zeile*9 + spalte] == 1) cmd = cmd "   "
               else                          cmd = cmd " D "
           }
       }
    }
    cmd = cmd "\" "
}
system(cmd)
}'

