#!/bin/sh
set -x

# format : %A+ %C+ %D %f %G %h %i %M %s %T+ %U

# %A+ = last access time                  (1)
# %C+ = last file status change time      (2)
# %D  = Device Number                     (3)
# %f  = filename                          (4)
# %G  = numeric group id                  (5)
# %h  = leading directories of filename   (6)
# %i  = inode number                      (7)
# %M  = Permissions                       (8)
# %s  = file size                         (9)
# %T+ = last modification date           (10)
# %U  = numeric user id                  (11)

EXITCODE=0
export EXITCODE

# first we retrieve all object types we are responsible for

echo 'list object monitor;' | sdmsh -j $JOBID -k $KEY -h $SDMSHOST -p $SDMSPORT | awk '
BEGIN    { start = 0; }
/----/   { start = 1; next; }
/^$/     { start = 0; next; }
start==1 { print $2; next; }
' | (while read watcher; do

	DEPTH=1

	# No we fetch the configuration for the object type
	echo "show object monitor $watcher;" | sdmsh -j $JOBID -k $KEY -h $SDMSHOST -p $SDMSPORT | awk '
	BEGIN { start = 0;
		sq = "'"'"'";
	}
	/PARAMETERS : *$/ { start = 1; next; }
	/INSTANCES : *$/  { start = 0; next; }
	start == 1 && /^ *[0-9][0-9]*/     { print $2 "=" sq $3 sq; }
	' > /tmp/x.$$

	. /tmp/x.$$
	rm -f /tmp/x.$$

	cd $DIRECTORY
	find $DIRECTORY -maxdepth $DEPTH -type f -a -name "$PATTERN" -printf '%A+\t%C+\t%D\t%f\t%G\t%h\t%i\t%M\t%s\t%T+\t%U\n' | awk '
	BEGIN	{
		FS="\t"
		print "whenever error disconnect 1;"
		print "alter object monitor '$watcher'"
		print "instances = ("
		sep = ""
		sq = "'"'"'"
	}
	{
		mtime = substr($10, 0, 19)
		atime = substr($1,  0, 19)
		ctime = substr($2,  0, 19)
		
		print sep
		print "\t" sq $6 "/" $4 sq " ("
		print "\t\tmtime = " sq mtime sq ","
		print "\t\tfname = " sq $4 sq ","
		print "\t\tdir = " sq $6 sq ","
		print "\t\tgid = " sq $5 sq ","
		print "\t\tdevid = " sq $3 sq ","
		print "\t\tuid = " sq $11 sq ","
		print "\t\tatime = " sq atime sq ","
		print "\t\tmode = " sq $8 sq ","
		print "\t\tctime = " sq ctime sq ","
		print "\t\tsize = " sq $9 sq ","
		print "\t\tinode = " sq $7 sq

		printf "\t)"
		sep = ","
	}

	END	{
		print "\n);"
	}
	' | sdmsh -j $JOBID -k $KEY -h $SDMSHOST -p $SDMSPORT

	RC=$?

	if [ $RC -ne 0 ]; then
		echo "Error occurred while processing object monitor $watcher"
		EXITCODE=1
	fi
done
exit $EXITCODE)

EXITCODE=$?

exit $EXITCODE
