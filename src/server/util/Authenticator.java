//
// Authenticator.java
//
// Copyright (C) 2016 independIT Integrative Technologies GmbH
//
package de.independit.scheduler.server.util;

public abstract class Authenticator
{
	public static final int SUCCESS = 0;
	public static final int FAILURE = 1;
	public static final int ABORT = 2;

	/**
	 ** The function checkCredentials() checks the provided credentials
	 ** against some external database/system like an LDAP or AD repository.
	 ** If the provided combination of user name and password is valid
	 ** the function should return SUCCESS. If the combination is invalid
	 ** the function should return FAILURE. If for some reason the external
	 ** system isn't available, it should return ABORT.
	 ** If an internal check has to be done if the external check fails or
	 ** aborts, it is valid to return ABORT on a invalid combination of
	 ** user name and password.
	 **
	 ** If a validated user doesn't exist yet, he'll be created.
	 **
	 ** Note: at this point Strings are case sensitive.
	 ** If a connect command doesn't quote the user name, the name will
	 ** be converted to upper case within the parser. But if quoted,
	 ** the names aren't changed.
	 ** This means, if the authentication here is done case insensitively,
	 ** one might end up with loads of users, e.g. 'aaa', 'aaA', 'aAa',
	 ** 'aAA', 'Aaa', 'AaA', 'AAa' and 'AAA'. These are regarded different
         ** users.
	 */
	public abstract int checkCredentials (String userName, String passwd);


	/**
         ** The function checkExternally() returns the information if
	 ** some user should be checked against the scheduler's internal
	 ** administration (return value "false") or not (return value "true").
	 **
	 ** The user SYSTEM is checked internally only.
	 */
	public abstract boolean checkExternally (String userName);


	/**
	 ** The function getGroupNames() returns the list of group names
	 ** for the specified user.
	 ** Returning "PUBLIC" as a member of this list is optional.
	 ** If a null is returned, the group list from the scheduler's 
	 ** internal administration is used.
	 ** The returned list of group names is validated against the
	 ** scheduler's database. If non existing groups are specified, 
	 ** they will be created. New groups won't have any privileges.
	 ** If the user used to be in some group which is not part of
	 ** the list (except PUBLIC), the user will be removed from that
	 ** group.
	 **
	 ** As above, names are case sensitive here. Returning 'public'
	 ** instead of 'PUBLIC' would be a mistake.
	 **
	 ** The first group specified will be the user's default group
	 ** in case the user didn't exist. If no groups are returned,
	 ** i.e. an empty list, the default group will be PUBLIC.
         */
	public abstract String[] getGroupNames (String userName);


	/**
	 ** The function syncCredentials() tells the server whether or
	 ** not the credentials should be stored locally after a successful
	 ** login.
	 */
	public abstract boolean syncCredentials(String userName);


	/**
	 ** The function checkInternally() defines whether to check
	 ** the user credentials internally IF THE EXTERNAL CHECK FAILS/ABORTS.
	 **
	 ** This function is only called if
	 ** a. checkExternally() returns true
	 ** b. checkCredentials() returns ABORT
	 */
	public abstract boolean checkInternally(String userName);
}

