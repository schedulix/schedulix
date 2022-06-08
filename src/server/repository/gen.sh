#!/bin/sh
#
# $Id: gen.sh,v 2.3 2012/02/17 07:32:52 ronald Exp $
#
# Copyright independIT Technologies GmbH
#
TABLE=$1

echo "
false = 0
true = 1
import $TABLE
table = $TABLE.table
import standard_columns
if ('sql_only' not in table) or (table['sql_only'] == 0):
	table['columns'] = table['columns'] + standard_columns.columns

if 'hierarchy' not in table:
	table['hierarchy'] = 0

if table['hierarchy'] == 1:
	table['columns'] = table['columns'] + [ {
		'column_name' : 'inherit_privs' ,
		'attribute_name' : 'inheritPrivs' ,
		'datatype'    : { 'type' : 'long' },
		'nullable'    : false,
		'privCheckForEdit' : 'SDMSPrivilege.EDIT|SDMSPrivilege.GRANT',
		'scidesc'     : 'Die Bin\\"are Repr\\"asentation der geerbten Privilegien'
	}
]

import generate
generate.generate(table)
" | python
