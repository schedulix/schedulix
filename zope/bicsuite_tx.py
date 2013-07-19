#
# Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
# Authors: Ronald Jeninga, Dieter Stubler
# 
# schedulix Enterprise Job Scheduling System
# 
# independIT Integrative Technologies GmbH [http://www.independit.de]
# mailto:contact@independit.de
# 
# This file is part of schedulix
# 
# schedulix is free software: 
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
#
import transaction

#
#       Commit the ZODB Transaction
#
#       if retry == 0 the exceptions will be catched and
#       transaction will not be retried
#       on an exception (typically conflict error)
#       This should be used on any writing Transactions to the
#       SDMS Server.
#
#       if retry == 1 the exceptions will be raised and
#       ZOPE will retry the transaction (typically 3 times)
#       This should be used when changing Data in ZODB
#       like Copy, Cut, Bookmark management, Web User management
#
def commit_transaction(retry):
        try:
                transaction.commit()
        except:
                if retry == 1:
                        raise
                else:
                        try:
                                transaction.abort()
                        except:
                                pass
