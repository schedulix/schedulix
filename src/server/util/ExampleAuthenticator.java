//
// ExampleAuthenticator.java
//
// Copyright (C) 2016 independIT Integrative Technologies GmbH
//
package de.independit.scheduler.server.util;

/*
 * NOTE: This is just an example implementation of an external authentication class
 * This is NOT secure and NOT intended for production use
 */

public class ExampleAuthenticator extends Authenticator
{

	/*
	 * everyone who asks politely is granted access
	 */
	public int checkCredentials (String userName, String passwd)
	{
		if (passwd != null && passwd.equals("PleaseLetMeIn"))
			return SUCCESS;
		return ABORT;
	}

	/*
         * everyone except user SYSTEM is checked externally
         */
	public boolean checkExternally (String userName)
	{
		return true;
	}

	/*
         * Users are member of PUBLIC only
         * (returning a null would at least preserve later changes;
         * this way the group list is reset to PUBLIC-only on connect)
         */
	public String[] getGroupNames (String userName)
	{
		String groups[] = new String[1];
		groups[0] = "PUBLIC";

		return groups;
	}

	/*
	 * if a user is allowed to validate his credentials against
	 * the internal administration, an external password change
	 * and a subsequent login will change the internally stored
	 * password (hash) as well.
	 */
	public boolean syncCredentials(String userName)
	{
		return true;
	}

	/*
	 * if checkCredentials returns ABORT for a user that is said
	 * to be checked externally, this function is called to determine
	 * if an internal check should be performed.
	 */
	public boolean checkInternally(String userName)
	{
		return true;
	}
}

