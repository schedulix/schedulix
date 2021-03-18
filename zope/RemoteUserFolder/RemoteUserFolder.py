#
# Remote User Folder
#
#
# Author: Dieter Stubler (independIT Integrative Technologies GmbH)

##############################################################################
#
# Zope Public License (ZPL) Version 0.9.4
# ---------------------------------------
# 
# Copyright (c) Digital Creations.  All rights reserved.
# 
# Redistribution and use in source and binary forms, with or
# without modification, are permitted provided that the following
# conditions are met:
# 
# 1. Redistributions in source code must retain the above
#    copyright notice, this list of conditions, and the following
#    disclaimer.
# 
# 6. Redistributions of any form whatsoever must retain the
#    following acknowledgment:
# 
#      "This product includes software developed by Digital
#      Creations for use in the Z Object Publishing Environment
#      (http://www.zope.org/)."
# 
# Disclaimer
# 
#   THIS SOFTWARE IS PROVIDED BY DIGITAL CREATIONS ``AS IS'' AND
#   ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
#   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
#   FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT
#   SHALL DIGITAL CREATIONS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
#   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
#   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
#   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
#   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
#   IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
#   THE POSSIBILITY OF SUCH DAMAGE.
#
##############################################################################

# import Globals, App.Undo, socket, os, string, sha, whrandom, sys, zLOG
import Globals, App.Undo, socket, os, string, sha, sys, zLOG

from Globals import DTMLFile, MessageDialog
from string import join,strip,split,lower,upper,find

from OFS.Folder import Folder
from OFS.CopySupport import CopyContainer

from urllib import quote, unquote

from AccessControl import ClassSecurityInfo
from AccessControl.Role import RoleManager, DEFAULTMAXLISTUSERS
#from AccessControl.User import User, BasicUserFolder, readUserAccessFile, UserFolder, _noroles
from AccessControl.User import User, BasicUserFolder, readUserAccessFile, UserFolder
from AccessControl.PermissionRole import PermissionRole
from OFS.DTMLMethod import DTMLMethod
from time import time
import ldap

from zLOG import format_exception, LOG, ERROR, INFO

import traceback
import time

class NameIsIdUser(User):
    def __init__(self, user):
        self.user = user
    def getId(self):
        return self.user.getId()
    def getUserName(self):
        return self.user.getId()
    def getRoles(self):
        return self.user.getRoles()
    def _shared_roles(self, object):
        return self.user._shared_roles(object)
    def _check_context(self, object):
        return self.user._check_context(object)

class RemoteUser(User):

    def __init__(self, name, roles=[], domains=[]):
        User.__init__(self, name, "", roles, domains)
        
    def getUserName(self):
        """Return the username of a user"""
        id = self.getId()
        if self.simple_usernames and id.count("\\"):
            return string.split(id, "\\", 1)[1]
        else:
            return id

    def getId(self):
        """Get the ID of the user. The ID can be used, at least from
        Python, to get the user from the user's
        UserDatabase"""
        return self.name        
        
    def getRolesInContext(self, object):
        """This override is to hack a problem where the normal user object
           uses the user name rather than the id to detect local roles
        """
        return User.getRolesInContext(NameIsIdUser(self), object)

    def allowed(self, object, object_roles=None):
        """Check whether the user has access to object. The user must
           have one of the roles in object_roles to allow access."""
        user = NameIsIdUser(user=self) 
        return User.allowed(user, object, object_roles)

    def getDomains(self):
        return self.domains

manage_addRemoteUserFolderForm=DTMLFile('dtml/manage_addRemoteUserFolder', globals())

ssoConf = None
checkRolesTs = {}

class RemoteUserFolder(UserFolder):
    """RemoteUserFolder object

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
    """

    meta_type='Remote User Folder'
    id       ='acl_users'
    title    ='Remote User Folder'
    icon     ='RemoteUserFolder.gif'

    _userFolderProperties = DTMLFile('dtml/userFolderProps', globals())

    _editUser=DTMLFile('dtml/editUser', globals(),
                       remote_user_mode__=1)

    _add_User=DTMLFile('dtml/addUser', globals(),
                       remote_user_mode__=1)



    def __init__(self):
		#self.data=PersistentMapping()
		self.anon_prefix = ''
		self.auth_prefix = ''
		self.case_insensitive = 1
		self.simple_usernames = 0
		self.domain_roles = 1
		UserFolder.__init__(self)

    def _doAddUser(self, name, password, roles, domains, **kw):
        """Create a new user. Override so we use RemoteUser objects"""
        self.data[name]=RemoteUser(name,roles,domains)

    def initSsoConf (self):
        global ssoConf
	if ssoConf != None:
            return True
	ssoConfFileDir = os.getenv('BICSUITECONFIG', None)
	if ssoConfFileDir == None:
		return False
	ssoConfFileName = ssoConfFileDir + '/ZopeSSO.conf'
	try:
		ssoConfFile = open(ssoConfFileName, "r")
	except Exception,e:
                print 'Exception (" + str(e) + ") reading sso config from "' + ssoConfFileName
		return False
	try:
		ssoConf= eval(ssoConfFile.read())
	except Exception,e:
                print 'Exception (" + str(e) + ")"' + ssoConfFileName + ' is malformed!'
		return False

	return True

    def validate(self, request, auth='', roles=None):
        global checkRolesTs
        """
        this method performs identification, authentication, and
        authorization
        v is the object (value) we're validating access to
        n is the name used to access the object
        a is the object the object was accessed through
        c is the physical container of the object

        We allow the publishing machinery to defer to higher-level user
        folders or to raise an unauthorized by returning None from this
        method.
        """

        # if we cannot initialize the SSO Configuration, we will deny access
        if not self.initSsoConf():
            print 'Access denied because initialization of SSO Configuration failed'
            return None
                
        v = request['PUBLISHED'] # the published object
        a, c, n, v = self._getobcontext(v, request)

        name = request.environ.get('REMOTE_USER', None)

        # if name is None we weren't called through fastcgi with a remote username
        if name == None:
            # print ('Cannot acces REMOTE_USER')
            return None
        # print 'name = ' + name
            
        nl = name.split('\\')
        domainPart = nl[0]
        namePart = nl[1]
        domainConf = ssoConf.get('DOMAINS', None)
        if domainConf == None:
            print 'Access denied because of no domain configuration found in ZopeSSO.conf for domain ' + domainPart + ' !'
            return None        
        domainConf = domainConf.get(domainPart, None)
        if domainConf == None:
            print 'Access denied because domain config for domain ' + domainPart + ' notfound!'
            return None

        WebIncludeDomainNames = domainConf.get('WebIncludeDomainNames', ssoConf.get('WebIncludeDomainNames', False))
        if not WebIncludeDomainNames:
            name = namePart
        else:
            name = name.replace('\\','_')

        WebNameCase = domainConf.get('WebNameCase', ssoConf.get('WebNameCase', 'MIXED'))
        if WebNameCase == 'UPPER':
            name = name.upper()
        elif WebNameCase == 'LOWER':
            name = name.lower()
        elif WebNameCase == 'MIXED':
            pass
        else:
            print 'SSOnameCase must be MIXED, UPPER or LOWER'
            return None

        user = self.getUser(name)
        WebAutoCreateUsers = domainConf.get('WebAutoCreateUsers', ssoConf.get('WebAutoCreateUsers', False))
        if user is None and not WebAutoCreateUsers:
            print 'User ' + name + ' does not exist !'
            return None
        
        isUser = True
        isManager = False
        WebUseLdapGroups = domainConf.get('WebUseLdapGroups', ssoConf.get('WebUseLdapGroups', False))
        checkRoles = False
        if WebUseLdapGroups:
            WebGroupCheckIntervall = domainConf.get('WebGroupCheckIntervall', ssoConf.get('WebGroupCheckIntervall', 60))
            ts = checkRolesTs.get(name, 0)
            cur = time.time()
            if cur - ts > WebGroupCheckIntervall * 60:
                checkRolesTs.update ( { name : cur } )
                checkRoles = True
        if WebUseLdapGroups and checkRoles:
            isUser = False
            LdapServer = domainConf.get('LdapServer', None)
            LdapUsername = domainConf.get('LdapUsername', None)
            LdapPassword = domainConf.get('LdapPassword', None)
            LdapBaseDn = domainConf.get('LdapBaseDn', None)
            if LdapServer == None or LdapUsername == None or LdapPassword == None or LdapBaseDn == None:
                print 'Missing or uncomplete LDAP settings!'
            else:
                isUser = False
                isManager = False
                WebUserGroup = domainConf.get('WebUserGroup', ssoConf.get('WebUserGroup', 'BICSUITE_WEB_USER'))
                WebManagerGroup = domainConf.get('WebManagerGroup', ssoConf.get('WebManagerGroup', 'BICSUITE_WEB_MANAGER'))
                ldap_filter='(&(objectClass=user)(sAMAccountName=' + namePart + '))'
                attrs = ['memberOf']
                ldap_client = ldap.initialize(LdapServer)
                ldap_client.protocol_version = ldap.VERSION3
                ldap_client.set_option(ldap.OPT_REFERRALS, 0)
                ldap_client.simple_bind_s(LdapUsername, LdapPassword)
                result = ldap_client.search_s(LdapBaseDn, ldap.SCOPE_SUBTREE, ldap_filter,['memberOf'])
                for elem in result:
                    if elem[0] != None:
                        for grp in elem[1]['memberOf']:
                            grp = grp.replace('CN=','')
                            grp = grp[:grp.index(',')]
                            if grp == WebManagerGroup:
                                isManager = True
                                isUser = True
                            elif grp == WebUserGroup:
                                isUser = True
                ldap_client.unbind()
        else:
            isUser = True
            
        if not isUser:
            print 'User ' + name + ' is not in groups ' + WebManagerGroup + ' or ' + WebUserGroup
            return None

        user = self.getUser(name)
        if user is None:
            self._doAddUser(name, None, [], [domainPart])
            user = self.getUser(name)
        else:
            domains = user.getDomains()
            if domains == None:
                domains = []
            else:
                domains = list(domains)
            if not domainPart in domains:
                domains.append(domainPart)
                domains.sort()
                roles = user.getRoles()
                self.userFolderEditUser(name, None, roles, domains)

        if WebUseLdapGroups and checkRoles:
            isAlreadyManager = 'Manager' in user.getRoles()
            newRoles = []
            if isManager:
                newRoles = ['Manager']
            if (isAlreadyManager and not isManager) or (not isAlreadyManager and isManager):
                domains = user.getDomains()
                if domains == None:
                    domains = []
                self.userFolderEditUser(name, None, newRoles, domains)
              
        # print 'returning a user'
        return self.getUser(name).__of__(self)

    def manage_setUserFolderProperties(self, encrypt_passwords=0,
                                       update_passwords=0,
				       anon_prefix='',
				       auth_prefix='',
				       simple_usernames=0,
                                       domain_roles=0,
                                       maxlistusers=DEFAULTMAXLISTUSERS,
                                       auto_add=0,
                                       case_insensitive=0,
                                       REQUEST=None):
        """
        Sets the properties of the user folder.
        """
        self.anon_prefix = anon_prefix
        self.auth_prefix = auth_prefix
        self.simple_usernames = simple_usernames
        self.domain_roles = domain_roles
        self.auto_add = auto_add
        self.case_insensitive = case_insensitive
	
        
        return BasicUserFolder.manage_setUserFolderProperties(self, encrypt_passwords,
                                       update_passwords,
                                       maxlistusers,
                                       REQUEST)

    def _changeUser(self,name,password,confirm,roles,domains,REQUEST=None):
        # Our users don't use passwords
        return UserFolder._changeUser(self,name,None,None,roles,domains,REQUEST)

    def _doAddUser(self, name, password, roles, domains, **kw):
        """Create a new user"""
        if password is not None and self.encrypt_passwords:
            password = self._encryptPassword(password)
        self.data[name]=User(name,password,roles,domains)

def manage_addRemoteUserFolder(self,dtself=None,REQUEST=None,**ignored):
    """ """
    f=RemoteUserFolder()
    self=self.this()
    try:    self._setObject('acl_users', f)
    except: return MessageDialog(
                   title  ='Item Exists',
                   message='This object already contains a User Folder',
                   action ='%s/manage_main' % REQUEST['URL1'])
    self.__allow_groups__=f
    if REQUEST is not None:
        REQUEST['RESPONSE'].redirect(self.absolute_url()+'/manage_main')
