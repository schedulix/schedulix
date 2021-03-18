
Remote User Folder - Edit: Edit SiteRoot parameters

 Description

    A RemoteUserFolder is used in cases where the Zope installation is behind
    a webserver which already takes care of authentication. In some cases it makes
    sense to let the webserver handle the authentication and Zope to handle permissions
    for those authenticated users. This user folder enables the hand off of all
    authentication to the remote webserver and
    you to control the roles of these users once authenticated.

    If an object requires more than anonymous permissions then this folder will use
    the REMOTE_USER environment variable determine the ID of the user that was
    authenticated. If the ID matches that of a user object contained in the folder
    then this is the user object that will be used. If the ID does not match then a new
    user object will be created with no roles (if "auto add" option
    is turned on). This allows the webserver administrator
    to have complete control over who is allowed authenticated and the Zope administrator
    to control what they have access to.

    An example of how this might be useful is the use of IIS internal windows authentication.
    IIS can be set to handle authentication of users against their current windows domain login,
    thus not requiring any further login to the website.
    With RemoteUserFolder installed, any user with a domain login will be automatically be
    a zope
    authenticated user. In addition with RemoteUserFolder it is possible to set a
    default set of roles for any user of a particular NT domain. 


Properties

 'Simple Usernames' -- means that the name returned by
AUTHENTICATED_USER.getUserName() will be just the username portion of <i>DOMAIN\username</i>,  
whereas AUTHENTICATED_USER.getUserId() will return the full <i>DOMAIN\username</i> style name.


 'Domain Roles' --
 allows for a dummy user to be created with a name of the form
"DOMAIN\*". If such a user exists then all users in that domain, ie with a name of the
form "DOMAIN\username" will inherit the roles from the dummy domain roles user.

 'Case Insensitive' --
means all REMOTE_USER names will be converted to a normalized for before matching to counter 
any  variability introduced by the authentication mechanism (such as as happens with IIS). Names
in NT domain form will be normalized as DOMAIN\username. All other names will become lower case.

 'Auto Add Users' --
means that if a REMOTE_USER is not recongnized as an existing user in this user folder, it will 
be automatically added. This allows you to assign complete responsibility to the remote  authentication mechanism for the list of users that can be authenticated. Using <a  href="#domian_roles">Domain Roles</a> in addition allows these new users to automatically  inherit roles

 'Maximum users listed' --
 presents search dialog when more users than N (-1 is always, 0 is never) users are listed when
doing Role Assignment


