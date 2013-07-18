#!/bin/bash
for FILE in component.xml datatypes.py __init__.py
do
        if ! diff $BICSUITEHOME/zope/https/chk/$FILE $HOME/software/Zope/lib/python2.7/site-packages/Zope2-2.13.6-py2.7.egg/ZServer/$FILE >/dev/null
        then
                if ! diff $BICSUITEHOME/zope/https/patch/$FILE $HOME/software/Zope/lib/python2.7/site-packages/Zope2-2.13.6-py2.7.egg/ZServer/$FILE >/dev/null
                then
                        echo "$BICSUITEHOME/zope/https/chk/$FILE does not match $HOME/software/Zope/lib/python2.7/site-packages/Zope2-2.13.6-py2.7.egg/ZServer/$FILE !"
                        echo "cannot patch"
                        exit 1
                else
                        echo "$BICSUITEHOME/zope/https/patch/$FILE already matches $HOME/software/Zope/lib/python2.7/site-packages/Zope2-2.13.6-py2.7.egg/ZServer/$FILE !"
                fi
        fi
done
for FILE in component.xml datatypes.py __init__.py HTTPS_Server.py medusa/https_server.py
do
        cp $BICSUITEHOME/zope/https/patch/$FILE $HOME/software/Zope/lib/python2.7/site-packages/Zope2-2.13.6-py2.7.egg/ZServer/$FILE
done
