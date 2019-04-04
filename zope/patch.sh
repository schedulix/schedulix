#!/bin/bash
if [ "$BICSUITEHOME" = "" ]
then
	PATCHHOME=$SDMSHOME/src/zope/https
else
	PATCHHOME=$BICSUITEHOME/zope/https
fi
cd $MYZOPE/lib/python2.7/site-packages
cd `ls -d Zope2-*-py2.7.egg`
PATCHDIR=`pwd`/ZServer
LIST=""
for FILE in component.xml datatypes.py __init__.py
do
        if ! diff $PATCHHOME/chk/$FILE $PATCHDIR/$FILE >/dev/null
        then
                if ! diff $PATCHHOME/patch/$FILE $PATCHDIR/$FILE >/dev/null
                then
                        echo "$PATCHHOME/chk/$FILE does not match $PATCHDIR/$FILE !"
                        echo "cannot patch"
                        exit 1
                else
                        echo "$PATCHHOME/patch/$FILE already matches $PATCHDIR/$FILE !"
                fi
	else
		LIST="$LIST $FILE"
        fi
done
for FILE in $LIST HTTPS_Server.py medusa/https_server.py
do
        echo cp $PATCHHOME/patch/$FILE $PATCHDIR/$FILE
        cp $PATCHHOME/patch/$FILE $PATCHDIR/$FILE
done
