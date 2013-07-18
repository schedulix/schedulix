#!/bin/sh 
if [ $# -lt 1 ]; then
	SERVERS="localhost host_1 host_2"
else
	SERVERS="$*"
fi

cd $BICSUITEHOME/..
HOMEDIR=`pwd`

mkdir -p $HOMEDIR/log

if [ -z "$BICSUITECONFIG" ]
then
	BICSUITECONFIG=$BICSUITEHOME/etc
fi

for JS in $SERVERS
do
	echo "Starting Jobserver $JS"

	if ! test -f $BICSUITECONFIG/$JS.conf
	then
		cp $BICSUITEHOME/etc/$JS.conf.template $BICSUITECONFIG/$JS.conf
	fi

	# without scrolllog
	# $BICSUITEHOME/bin/jobserver-run $BICSUITECONFIG/$JS.conf > $HOMEDIR/log/$JS.out 2>&1 &

	# with scrolllog
	$BICSUITEHOME/bin/jobserver-run $BICSUITECONFIG/$JS.conf $HOMEDIR/log/$JS.out
done
