#
# $Id: compress.awk,v 2.1.2.1 2013/03/14 10:24:54 ronald Exp $
#
# Copyright (C) 2002 topIT Informationstechnologie GmbH
#
# 
# The last case line will never be merged because the closing
# brace is contained. I know, this is not very beautiful, but
# at least it works.
#
# The sort and print function doesn't really do some sorting
# The complexity is O(n^2), but the overhead is small when
# many actions can be merged
#
function sort_and_print()	{
	print " // maxcase = " maxcase;
	for (i = 0; i <= maxcase; i++) {
		if(caseline[i] != "") {
			print "\t\t\t\t\t\tcase " i ":";
			for(j = i+1; j <= maxcase; j++) {
				if(caseline[i] == caseline[j]) {
					print "\t\t\t\t\t\tcase " j ":";
					caseline[j] = "";
				}
			}
			print caseline[i];
		}
	}
}

#
# The print_table_reader function creates a function to 
# dynamically load the yytable or yycheck table
# The table is loaded from a file named yytable.data or yycheck.data
# found in the server/parser path
#
function print_table_reader(tablename, filename)	{
	print "    public static short " tablename "[];\n";
	print "    public static synchronized void fillTable()";
	print "    {";
	print "        // System.out.println(\"I am in fillTable of " tablename "\");";
	print "        if(" tablename " == null) {";
	print "            " tablename " = new short[" entries "];";
	print "            StreamTokenizer st = new StreamTokenizer(";
	print "                                     new InputStreamReader(";
	print "                                         System.class.getResourceAsStream(\"/de/independit/scheduler/server/parser/" filename ".data\")));";
	print "            int tok;";
	print "            int i = 0;";
	print "            try {";
	print "                while((tok = st.nextToken()) != StreamTokenizer.TT_EOF) {";
	print "                    if(tok == StreamTokenizer.TT_NUMBER) {";
	print "                        " tablename "[i] = (short) st.nval;";
	print "                        i++;";
	print "                    }";
	print "                }";
	print "            } catch(java.io.IOException ioe) {";
	print "                throw new RuntimeException(\"Error reading Parser Tables\");";
	print "            }";
	print "        }";
	print "    }";
}
BEGIN 				{	verbosecopy = 1;
					maxcase = -1;
					incase = 0;
					inyytable = 0;
					inyycheck = 0;
				}

# the start of the switch
/[ 	]*switch \(yyN\)/	{	verbosecopy = 0;
					incase = 1;
					print "\t\t\t\ttry {\n";
					print;
					next;
				}

# the start of the yytableclass
/protected static final class YyTableClass/	{
					inyytable = 1;
					verbosecopy = 0;
					entries = 0;
					print;
					next;
				}

# the start of the yycheckclass
/protected static final class YyCheckClass/	{
					inyycheck = 1;
					verbosecopy = 0;
					entries = 0;
					print;
					next;
				}

# as long as we are not in the switch, we just copy
verbosecopy == 1		{	print;
					next;
				}

# we need the number of the action
incase == 1 && /^case [0-9]+:/	{	casenr = $2 + 0; 
					if(casenr > maxcase) { maxcase = casenr; }
					next;
				}

/[ 	]*\/\/.*/		{ next; } # strip comments

# this is the first statement after the switch
/[ 	]*yyTop -= yyLen\[yyN\];/	{ 
					verbosecopy = 1;
					incase = 0;
					# now sort and print
					sort_and_print();
					print "\t\t\t\t\t} catch (SDMSEscape __e) {";
					print "\t\t\t\t\t\tString __s[] = null;";
					print "\t\t\t\t\t\tyyerror(\"$1\\nSyntax Error: \" + __e.getMessage() + \"$2\", __s);";
					print "\t\t\t\t\t}";
					print;
					next;
				}

# here we collect everything between the case statements
incase == 1			{
					caseline[casenr] = caseline[casenr] "\n" $0 ;
					next;
				}

# here we create the code for the yytable class
inyytable == 1 && /public static final short yyTable/ 	{
					print $NF >"yytable.data";
					entries = 1;
					next;
				}

# last line of the table class table
(inyycheck == 1 || inyytable == 1) && /};/		{
					verbosecopy = 1;
					if(inyycheck == 1) {
						print_table_reader("yyCheck", "yycheck");
					} else {
						print_table_reader("yyTable", "yytable");
					}
					inyytable = 0;
					inyycheck = 0;
					next;
				}

# here we create the code for the yychecktable class
inyycheck == 1 && /public static final short yyCheck/ 	{
					print $NF >"yycheck.data";
					entries = 1;
					next;
				}

# we here copy the table contents to a datafile
inyytable == 1||inyycheck == 1	{
					if(inyycheck == 1) {
						print >>"yycheck.data";
					} else {
						print >>"yytable.data";
					}
					entries += NF;
					next;
				}


#
# end of file
#
