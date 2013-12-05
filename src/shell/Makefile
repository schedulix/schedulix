#
# Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH"
# 
# schedulix Enterprise Job Scheduling System
# 
# independIT Integrative Technologies GmbH [http://www.independit.de]
# mailto:contact@independit.de
# 
# This file is part of schedulix
# 
# schedulix is is free software: 
# you can redistribute it and/or modify it under the terms of the 
# GNU Affero General Public License as published by the 
# Free Software Foundation, either version 3 of the License, 
# or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.

BASECLASSES = MiniParser.class MiniScanner.class SDMSServerConnection.class

SUBDIRS = .

all: generate subdirs $(BASECLASSES) $(NODECLASSES) $(HELPERCLASSES)

new: clean all

generate: MiniParser.java MiniScanner.java

subdirs: commonsubdirs

clean: commonclean
	rm -f MiniParser.java MiniScanner.java y.output parser.tables

#
# This is just a syntax-definition check
#
syntax:
	$(JAY) MiniParser.jay < /dev/null

MiniScanner.java:       MiniScanner.jlex MiniParser.java
	$(JLEX) -d /tmp -skel skeleton.nested MiniScanner.jlex
	$(SED) -e 's/zzCMapL\[zzInput\]/(((zzInput >= 0) \&\& (zzInput < zzCMapL.length)) ? zzCMapL\[zzInput\] : 0)/' /tmp/$@ >$@
	rm -f /tmp/$@

include $(SDMSHOME)/src/Makefile.CommonRules

