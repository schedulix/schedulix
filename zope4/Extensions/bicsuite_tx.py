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
