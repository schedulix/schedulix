#!/bin/sh

cd $BICSUITEHOME/..
HOMEDIR=`pwd`

if ! test -d $HOMEDIR/tmp
then
	mkdir $HOMEDIR/tmp
fi

if ! test -d $HOMEDIR/taskfiles
then
	mkdir $HOMEDIR/taskfiles
	chmod og-r $HOMEDIR/taskfiles
fi

HOSTNAME=localhost

echo "
begin multicommand

create or alter scope GLOBAL.'EXAMPLES'
with
	group = 'PUBLIC',
	config = (
		'REPOHOST' = '$HOSTNAME',
		'REPOPORT' = '2506',
		'BOOTTIME' = 'NONE',
		'USEPATH' = 'true',
		'JOBEXECUTOR' = '$BICSUITEHOME/bin/jobexecutor',
		'DEFAULTWORKDIR' = '$HOMEDIR/tmp',
		'VERBOSELOGS' = 'true',
		'NAME_PATTERN_LOGFILES' = '.*\\.log',
		'ENV' = (
			'SDMSHOST' = 'SDMSHOST',
			'JOBID' = 'JOBID',
			'SDMSPORT' = 'SDMSPORT',
			'KEY' = 'KEY'
		)
	);

create or alter named resource RESOURCE.'EXAMPLES'
with
	group = 'PUBLIC',
	usage = CATEGORY;

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'
with
	group = 'PUBLIC',
	usage = CATEGORY;

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'
with
	group = 'PUBLIC',
	usage = CATEGORY;

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'.'USER'
with
	group = 'PUBLIC',
	usage = CATEGORY;

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER'
with
	group = 'PUBLIC',
	usage = STATIC;

create or alter scope GLOBAL.'EXAMPLES'.'LOCALHOST'
with
	group = 'PUBLIC',
	config = (
		'HTTPHOST' = '$HOSTNAME'
	),
	parameter = (
		'E0015_SCOPE_PARAMETER' = 'SCOPE_PARAMETER_VALUE'
	);

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'LOCALHOST'
with
	group = 'PUBLIC',
	usage = STATIC;

create or alter resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'LOCALHOST' in GLOBAL.'EXAMPLES'.'LOCALHOST'
with
	group = 'PUBLIC',
	online;

create or alter job server GLOBAL.'EXAMPLES'.'LOCALHOST'.'SERVER'
with
	group = 'PUBLIC',
	rawpassword = 'cf1e8c14 e54505f6 0aa10ceb 8d5d8ab3',
	node = '$HOSTNAME',
	config = (
		'JOBFILEPREFIX' = '$HOMEDIR/taskfiles/localhost-',
		'NOTIFYPORT' = '45500',
		'HTTPPORT' = '8900'
	);

create or alter resource RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER' in GLOBAL.'EXAMPLES'.'LOCALHOST'.'SERVER'
with
	group = 'PUBLIC',
	online;

create or alter scope GLOBAL.'EXAMPLES'.'HOST_1'
with
	group = 'PUBLIC',
	config = (
		'HTTPHOST' = '$HOSTNAME'
	);

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'HOST_1'
with
	group = 'PUBLIC',
	usage = STATIC;

create or alter resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'HOST_1' in GLOBAL.'EXAMPLES'.'HOST_1'
with
	group = 'PUBLIC',
	online;

create or alter job server GLOBAL.'EXAMPLES'.'HOST_1'.'SERVER'
with
	group = 'PUBLIC',
	rawpassword = 'cf1e8c14 e54505f6 0aa10ceb 8d5d8ab3',
	node = '$HOSTNAME',
	config = (
		'JOBFILEPREFIX' = '$HOMEDIR/taskfiles/host_1-',
		'NOTIFYPORT' = '45501',
		'HTTPPORT' = '8901'
	);

create or alter resource RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER' in GLOBAL.'EXAMPLES'.'HOST_1'.'SERVER'
with
	group = 'PUBLIC',
	online;

create or alter scope GLOBAL.'EXAMPLES'.'HOST_2'
with
	group = 'PUBLIC',
	config = (
		'HTTPHOST' = '$HOSTNAME'
	);

create or alter named resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'HOST_2'
with
	group = 'PUBLIC',
	usage = STATIC;

create or alter resource RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'HOST_2' in GLOBAL.'EXAMPLES'.'HOST_2'
with
	group = 'PUBLIC',
	online;

create or alter job server GLOBAL.'EXAMPLES'.'HOST_2'.'SERVER'
with
	group = 'PUBLIC',
	rawpassword = 'cf1e8c14 e54505f6 0aa10ceb 8d5d8ab3',
	node = '$HOSTNAME',
	config = (
		'JOBFILEPREFIX' = '$HOMEDIR/taskfiles/host_2-',
		'NOTIFYPORT' = '45502',
		'HTTPPORT' = '8902'
	);

create or alter resource RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER' in GLOBAL.'EXAMPLES'.'HOST_2'.'SERVER'
with
	group = 'PUBLIC',
	online;

create or alter environment 'SERVER@LOCALHOST'
with
	resources = (
		RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'LOCALHOST',
		RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER'
	);

grant USE, VIEW
on environment 'SERVER@LOCALHOST'
to 'PUBLIC';

create or alter environment 'SERVER@HOST_1'
with
	resources = (
		RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'HOST_1',
		RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER'
	);

grant USE, VIEW
on environment 'SERVER@HOST_1'
to 'PUBLIC';

create or alter environment 'SERVER@HOST_2'
with
	resources = (
		RESOURCE.'EXAMPLES'.'STATIC'.'NODE'.'HOST_2',
		RESOURCE.'EXAMPLES'.'STATIC'.'USER'.'SERVER'
	);

grant USE, VIEW
on environment 'SERVER@HOST_2'
to 'PUBLIC';

end multicommand /* rollback */;
" | sdmsh "$@"

