#
# Copyright (c) 2000-2018 "independIT Integrative Technologies GmbH"
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
#
from __future__ import nested_scopes

import socket
import string
import random
import locale
import time
import re
import threading
import os
import locale
from zExceptions import Unauthorized
from ZODB.POSException import ConflictError

try:
	import ldap
	have_ldap = True
except:
	have_ldap = False

try:
	import json
	have_json = True
except:
	have_json = False
try:
	import pytz
	import datetime
	have_pytz = True
except:
	have_pytz = False

try:
	from M2Crypto import SSL
except:
	pass
	# print "Warning: M2Crypto not installed, SSL not available"

lock = threading.Lock()
socketCache = {}

yesList = [ 'TRUE', 'YES', 'Y', '1' ]

def getSocketKey(server):
	return  server['HOST'] + ':' + \
		str(server['PORT']) + ':' + \
		server.get('SSL','') + ':' + \
		server.get('TRUSTSTORE','') + ':' + \
		server.get('KEYSTORE','')

def connect(server):
	if server.has_key('SSL') and server['SSL'].upper() in yesList:
		ctx = SSL.Context('sslv3')
		if server.has_key('TRUSTSTORE'):
			ctx.set_verify(SSL.verify_peer | SSL.verify_fail_if_no_peer_cert, depth=9)
			if ctx.load_verify_locations(server['TRUSTSTORE']) != 1: 
				raise Exception('No CA certs')
		if server.has_key('KEYSTORE'):
			ctx.load_cert_chain(server['KEYSTORE'])
		soc = SSL.Connection(ctx)
	else:
		soc = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
	try:
		soc.connect((server['HOST'],int(server['PORT'])))
	except:
		raise Exception('ConnectError')
	return soc

def getSocket(server):
	global lock
	global socketCache
	global yeslist
	lock.acquire()
	try:
		socketKey = getSocketKey(server)
		sockets = socketCache.get(socketKey,None)
		if sockets == None:
			sockets = []
			socketCache.update ( { socketKey : sockets } )
		if len(sockets) == 0 or server.get('CACHE','NO') not in yesList:
			soc = connect(server)
			# print "new connection for server:" +str(server)
		else:
			soc = sockets.pop()
			timeout = soc.gettimeout()
			soc.settimeout(0)
			try:
				dummy = soc.recv(0)
				# no exception here so we assume server closed connection
				# print "connection closed by server reconnect:" +str(server)
				try:
					# just to make sure socket is realle closed
					soc.close()
				except:
					pass
				soc = connect(server)
				
			except:
				# socket is still alive if an exception is raised here
				# print "reuse connection for server:" +str(server)
				soc.settimeout(timeout)
	finally:
		lock.release()

	return soc

def closeSocket(soc):
	try:
		soc.shutdown(2)
		soc.close()
	except:
		pass

def clearSockets(server):
	global lock
	global socketCache
	global yeslist
	if server.get('CACHE','NO').upper() in yesList:
		lock.acquire()
		try:
			l = socketCache[getSocketKey(server)]
			while len(l) > 0:
				closeSocket(l.pop())
		finally:
			lock.release()
	
def releaseSocket(server,soc):
	global lock
	global socketCache
	global yeslist
	if server.get('CACHE','NO').upper() in yesList:
		lock.acquire()
		try:
			socketCache[getSocketKey(server)].append(soc)
			# print "released connection for server:" +str(server)
		finally:
			lock.release()
	else:
		closeSocket(soc)
		# print "closed connection for server:" +str(server)

#
# old version for backward compatibility
# with user not case sensitive if not quoted
#
def SDMSConnectionOpen(host,port,user,pwd,session=''):
	if user[0] != "'":
		user = string.upper(user)
	else:
		user = string.replace(user,"'","")
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSConnectionOpenV2(server,user,pwd,session)

#
# new version
# Taking a server dict with attributes 'HOST', 'PORT', and optionally further attributes,
# neccessary for SSL connections to BICsuite servers
#
# user is case sensitive !
#
def SDMSConnectionOpenV2(server,user,pwd,session=''):
	return SDMSGenericConnectionOpenV2(server,user,pwd,'USER',session)

def SDMSJobConnectionOpen(host,port,jid,key,session=''):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSJobConnectionOpenV2(server,jid,key,session)

def SDMSJobConnectionOpenV2(server,jid,key,session=''):
	return SDMSGenericConnectionOpenV2(server,jid,key,'JOB',session)

def SDMSGenericConnectionOpen(host,port,user,pwd,type,session=''):
	if user[0] != "'":
		user = string.upper(user)
	else:
		user = string.replace(user,"'","")
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSGenericConnectionOpenV2(server,user,pwd,type,session)

def SDMSGenericConnectionOpenV2(server,user,pwd,type,session=''):
	global yesList
	try:
		soc = getSocket(server)
	except Exception, e:
		return { 'ERROR' : { 'ERRORCODE' : 'ZSI-10001', 'ERRORMESSAGE' : 'ConnectError(' + str(e) + ')' }}
	try:
		soc.setblocking(1)
	except:
		pass
	if type == 'SERVER':
		connect_type = 'SERVER '
	if type == 'JOB':
		connect_type = 'JOB '
	if type == 'USER':
                user = "'" + user + "'"
		connect_type = ''
	pwd = string.replace(pwd, '\\','\\\\')
	pwd = string.replace(pwd, '\'','\\\'') 
	connectCommand = "CONNECT " + connect_type + user + " IDENTIFIED BY '" + pwd + "' WITH PROTOCOL = PYTHON"
	if server.get('CACHE','NO').upper() in yesList:
		timeout = 60 # default timeout of 60 seconds
		if server.has_key('TIMEOUT'):
			try:
				timeout = int(server['TIMEOUT'])
			except:
				pass
		connectCommand = connectCommand + ', TIMEOUT = ' + str(timeout)
	if session != '':
		session = string.replace(session,'\\','\\\\')
		session = string.replace(session,'\'','\\\'')
		connectCommand = connectCommand + ", SESSION = '" + session + "'"
	connectCommand = connectCommand + ';'
	output = sendCommand(soc, connectCommand)
	cData = eval(output)
	if cData.has_key('ERROR'):
		return cData
	return soc

def SDMSConnectionClose(soc):
	server = { 'CACHE' : 'NO' }
	releaseSocket(server, soc)

def SDMSCommandWithSoc(soc,command):
	data = eval(sendCommand(soc, command))
	return data

#
# This wrapper was used from BICsuite!webs Common.Util.SDMSCommand()
# to have non blocking connections of type USER as default
# we leave this for backward compatibility
#
def SDMSUserCommandNoBlock(host, port, user, pwd , command, session='', context = None):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSUserConnectCommandV2(server, user, pwd , command, 0, None, 'USER', 0, 1, session, context)

#
# New version of wrapper used from BICsuite!webs Common.Util.SDMSCommand()
# Taking a server dict with attributes 'HOST', 'PORT', and optionally further attributes,
# neccessary for SSL connections to BICsuite servers
#
def SDMSUserCommandNoBlockV2(server, user, pwd , command, session = '', context = None):
	return SDMSUserConnectCommandV2(server, user, pwd , command, 0, None, 'USER', 0, 1, session, context)

#
# This Function uses the Server Connect with command feature to execute the command
# to reduce communication between Server and client for clients like BICsuite!web,
# doing a connect for each command to execute
#
# old version for backward compatibility
# with user not case sensitive if not quoted
#
def SDMSUserConnectCommand(host, port, user, pwd , command, repeatable=0, outdict=None, type='USER', timeout=None, cycle=1, session = '', context = None):
	if user[0] != "'":
		user = string.upper(user)
	else:
		user = string.replace(user,"'","")
	server = { 'HOST' : host, 'PORT' : port }
	return  SDMSUserConnectCommandV2(server, user, pwd , command, repeatable, outdict, type, timeout, cycle, session, context)

ssoConf = None
ssoConfFileName = None
def initSsoConf ():
	global ssoConf
	global ssoConfFileName
	if ssoConf != None:
		return True
	ssoConf = {}
	ssoConfFileDir = os.getenv('BICSUITECONFIG', None)
	if ssoConfFileDir == None:
		print "initSsoConf:Exception (" + str(e) + ") getting BICSUITECONFIG "
		return False
	ssoConfFileName = ssoConfFileDir + '/ZopeSSO.conf'
	try:
		ssoConfFile = open(ssoConfFileName, "r")
		ssoConf = eval(ssoConfFile.read())
	except Exception,e:
		print "initSsoConf:Exception (" + str(e) + ") reading sso config from " + ssoConfFileName
		return False
	return True

def getConfig(key, default, server = None, domain = None):
	global ssoConf
	if not initSsoConf():
		return None
	if server == None:
		if domain == None:
			return ssoConf.get(key, default)
		else:
			return ssoConf.get('DOMAINS', {}).get(domain, {}).get(key, ssoConf.get(key, default))
	else:
		ssoConfKey = server['HOST'] + ':' + server['PORT']
		srv = ssoConf.get('SERVERS', {}).get(ssoConfKey, {})
		return srv.get('DOMAINS', {}).get(domain, {}).get(key, srv.get(key, ssoConf.get('DOMAINS', {}).get(domain, {}).get(key, ssoConf.get(key, default))))

def randomPassword():
	return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(16))

def getSystemConnectData(server):
	connectUser = getConfig ('AdminUser', None, server = server)
	connectPass = getConfig ('AdminPassword', None, server = server)
	if connectUser == None or connectPass == None:
		print "getSystemConnectData(): Missing AdminUser and/or AdminPassword for server " + server['HOST'] + ':' + server['PORT']
		return None
	return{ 'USER' : connectUser, 'PASS' : connectPass }

def getSDMSgroups(server, userName):
	connectData = getSystemConnectData(server)
	if connectData == None:
		return None
	data = SDMSUserConnectCommandV2(server, connectData['USER'], connectData['PASS'], "SHOW USER '" + userName + "'")
	if data.has_key('ERROR'):
		print "getSDMSgroups(): " + data['ERROR']['ERRORMESSAGE']
		return None
	defaultGroup = data['DATA']['RECORD']['DEFAULT_GROUP']
	groups = []
	for rec in data['DATA']['RECORD']['GROUPS']['TABLE']:
		if rec['NAME'] != 'PUBLIC':
			groups.append(rec['NAME'])
	groups.sort()
	ret = { 'GROUPS' : groups, 'DEFAULT_GROUP' : defaultGroup }
	return ret

def getUserDomains(context):
	return  context.REQUEST['AUTHENTICATED_USER'].getDomains()

def getLdapGroups(server, userName, context):
	ldapGroups = False
	domains = getUserDomains(context)
	defaultGroup = "PUBLIC"
	groups = []
	for domain in domains:
		ServerUseLdapGroups = getConfig('ServerUseLdapGroups', False, domain = domain, server = server)
		if not ServerUseLdapGroups:
			continue
		ldapGroups = True
		ServerBicsuitePrefix = getConfig('ServerBicsuitePrefix', 'BICSUITE', domain = domain, server = server)
		ServerName = getConfig('ServerName', 'DEFAULT', domain = domain, server = server)
		ServerDefaultGroupSuffix = getConfig('ServerDefaultGroupSuffix', '_ISDEFAULT', domain = domain, server = server)
		groupPfx = ServerBicsuitePrefix + '_' + ServerName + '_'
		LdapServer = getConfig('LdapServer', None, domain = domain)
		LdapBaseDn = getConfig('LdapBaseDn', None, domain = domain)
		LdapUsername = getConfig('LdapUsername', None, domain = domain)
		LdapPassword = getConfig('LdapPassword', None, domain = domain)
		if LdapServer == None or LdapBaseDn == None or LdapUsername == None or LdapPassword == None:
			print 'getLdapGroups(): Missing or uncomplete LDAP settings!'
		else:
			ldap_filter='(&(objectClass=user)(sAMAccountName=' + userName + '))'
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
						isDefaultGroup = False
						if grp[0:len(groupPfx)] == groupPfx:
							grp = grp[len(groupPfx):]
							if grp[-len(ServerDefaultGroupSuffix):] == ServerDefaultGroupSuffix:
								isDefaultGroup = True
								grp = grp[0:-len(ServerDefaultGroupSuffix)]

							ServerGroupNameCase = getConfig('ServerGroupNameCase', 'UPPER', domain = domain, server = server)
							if ServerGroupNameCase == 'UPPER':
								grp = grp.upper()
							elif ServerGroupNameCase == 'LOWER':
								grp = grp.lower()

							ServerIncludeGroupDomainNames = getConfig('ServerIncludeGroupDomainNames', False, domain = domain, server = server)
							if grp != "ADMIN" and ServerIncludeGroupDomainNames:
								grp = domain + '_' + grp
							if isDefaultGroup:
								defaultGroup = grp
							if grp not in groups:
								groups.append(grp)
			ldap_client.unbind()
	if not ldapGroups:
		return None
	groups.sort()
	ret = { 'GROUPS' : groups, 'DEFAULT_GROUP' : defaultGroup }
	return ret

def syncLdapGroups(server, userName, context):
	if server.get('SSO', False):
		return False
	# we do not sync for the BICsuite SYSTEM user
	if userName == 'SYSTEM':
		return False
	ldapGroups = getLdapGroups(server, userName, context)
	if ldapGroups == None:
		return False
	sdmsGroups = getSDMSgroups(server, userName)
	if sdmsGroups == None:
		return None
	changed = False
	cmd = "BEGIN MULTICOMMAND\n"
	if sdmsGroups['DEFAULT_GROUP'] != ldapGroups['DEFAULT_GROUP']:
		cmd = cmd + "ALTER USER '" + userName + "' WITH DEFAULT GROUP = PUBLIC;\n"
	if not sdmsGroups['GROUPS'] == ldapGroups['GROUPS']:
		cmd = cmd + "ALTER USER '" + userName + "' WITH GROUP = (PUBLIC"
		for grp in ldapGroups['GROUPS']:
			cmd = cmd + ",'" + grp + "'"
		cmd = cmd + ");\n"
		changed = True
	if sdmsGroups['DEFAULT_GROUP'] != ldapGroups['DEFAULT_GROUP']:
		cmd = cmd + "ALTER USER '" + userName + "' WITH DEFAULT GROUP = '" + ldapGroups['DEFAULT_GROUP'] + "';\n"
		changed = True
	cmd = cmd + "END MULTICOMMAND"
	if changed:
		connectData = getSystemConnectData(server)
		if connectData == None:
			return None
		data = SDMSUserConnectCommandV2(server, connectData['USER'], connectData['PASS'], cmd)
		if data.has_key('ERROR'):
			print data['ERROR']['ERRORMESSAGE']
			return None
		return True
	else:
		return False

def getServerUser(server, context):
	#
	# if WebIncludeDomainNames = False for more than one domain
	# and so a web user was created with more than one domain,
	# The name casing rules of those domains may differ
	# we do not handle this case at the moment and just use the configuration of
	# just one of those domains (alphabetical first)
	#
	userName = str(context.REQUEST['AUTHENTICATED_USER'])
	domains = getUserDomains(context)
	domain = None
	if domains == None or len(domains) < 1:
		# This should not happen !
		print "getServerUser(): Cannot obtain domains for user " + str(context.REQUEST['AUTHENTICATED_USER']) + ", server settings will be used"
		ServerIncludeUserDomainNames = getConfig('ServerIncludeGroupDomainNames', False, server = server)
		WebIncludeDomainNames = getConfig('WebIncludeDomainNames', False)
		ServerUserNameCase = getConfig('ServerUserNameCase', 'UPPER')
	else:
		domain = domains[0]
		ServerIncludeUserDomainNames = getConfig('ServerIncludeGroupDomainNames', False, server = server, domain = domain)
		WebIncludeDomainNames = getConfig('WebIncludeDomainNames', False, domain = domain)
		ServerUserNameCase = getConfig('ServerUserNameCase', 'UPPER', domain = domain)
	if ServerIncludeUserDomainNames:
		if not WebIncludeDomainNames and domain != None:
			userName = domain + '_' + userName
	else:
		if WebIncludeDomainNames and domain != None:
			userName = userName[len(domain)+1:]
	if ServerUserNameCase == 'UPPER':
		userName = userName.upper()
	elif ServerUserNameCase == 'LOWER':
		userName = userName.lower()
	return userName

def createSsoUser(server, userName, context = None):
	data = getSystemConnectData(server)
	if data == None:
		return None
	connectUser=data['USER']
	connectPass=data['PASS']
	data = SDMSUserConnectCommandV2(server, connectUser, connectPass, "CREATE USER '" + userName + "' WITH DEFAULT GROUP = PUBLIC, PASSWORD = '" + randomPassword() + "', ENABLE", context = context)
	if data.has_key('ERROR'):
		print "createSsoUser(): " + data['ERROR']['ERRORMESSAGE']
		return None
	if syncLdapGroups(server, userName, context) == None:
		return None
	return True

def printCommand(command):
	command = re.sub(r"([Pp][Aa][Ss][Ss][Ww][Oo][Rr]['Dd] *= *')[^']*'", r"\1******'", command)
	print command

#
# New version
# Taking a server dict with attributes 'HOST', 'PORT', and optionally further attributes,
# neccessary for SSL connections to BICsuite servers
#
# user is case sensitive !
#
def SDMSUserConnectCommandV2(server, user, pwd , command, repeatable=0, outdict=None, type='USER', timeout=None, cycle=1, session = '', context = None):
	global yesList
	executions = 0
	if timeout != None:
		timeout_secs = int(timeout) * 60
	else:
		timeout_secs = -1
	if cycle != None:
		cycle_secs = int(cycle)   * 60
	else:
		cycle_secs = 60
	start_time = time.time()
	last_time = start_time
	done = 0
	retries = 0
	first = 1
	user_error = 0
	error = None
	connectUser = user
	connectPass = pwd
	sso = False
	if user in ['', None] or pwd in ['', None]:
		if server.has_key('SSO') and server['SSO'] == True:
			data = getSystemConnectData(server)
			if data == None:
				return { 'ERROR' : { 'ERRORCODE' : 'ZSI-10010', 'ERRORMESSAGE' : 'Cannot obtain system credentials' }}
			connectUser = data['USER']
			connectPass = data['PASS']
			sso = True
		else:
			data = { 'ERROR' : { 'ERRORCODE' : 'ZSI-10004', 'ERRORMESSAGE' : 'Missing Credentials for user "' +  user + '" on non SSO connection' }}
			return data

	while user_error == 0 and (first == 1 or (done == 0 and (timeout_secs == -1 or int((timeout_secs-last_time+start_time)/60) > 0))):
		first = 0
		if retries > 0:
			if timeout_secs != -1:
				print str(int((timeout_secs-last_time+start_time)/60)) + ' Minutes left until timeout'
			print 'Waiting ' + str(cycle_secs/60) + ' Minutes before retry'
			time.sleep(cycle_secs)
		retries = retries + 1
		last_time = time.time()
		print time.asctime() + ' ' + user
		for retry in [0,1]:
			try:
				soc = getSocket(server)
			except Exception, e:
				data = { 'ERROR' : { 'ERRORCODE' : 'ZSI-10001', 'ERRORMESSAGE' : 'ConnectError(' + str(e) + ')' }}
				error = data['ERROR']
				print 'Error connecting to BICsuite server'
				print '    ERRORCODE ...: ' + error['ERRORCODE']
				print '    ERRORMESSAGE : ' + error['ERRORMESSAGE']
				break

			executions = executions + 1
			command = string.rstrip(command,'\n\t ;')
			repeats = 0
			while repeats < 2:
				repeats = repeats + 1
				connectCommand = "CONNECT '" + connectUser + "' IDENTIFIED BY '" + connectPass + "' WITH PROTOCOL = PYTHON"
				if server.get('CACHE','NO').upper() in yesList:
					timeout = 60 # default timeout of 60 seconds
					if server.has_key('TIMEOUT'):
						try:
							timeout = int(server['TIMEOUT'])
						except:
							pass
					connectCommand = connectCommand + ', TIMEOUT = ' + str(timeout)
				if session != '':
					session = string.replace(session,'\\','\\\\')
					session = string.replace(session,'\'','\\\'')
					connectCommand = connectCommand + ", SESSION = '" + session + "'"
				printCommand(command)
				if sso:
					userLst = user.split(':')
					setUserCommand = "ALTER SESSION SET USER = '" + userLst[0] + "'"
					if len(userLst) > 1:
						setUserCommand = setUserCommand + " FOR '" + userLst[1] + "'"
					setUserCommand = setUserCommand + '; '
					command = setUserCommand + command

				connectCommand = connectCommand + ", COMMAND = (" + command + ");"
				output = sendCommand(soc, connectCommand)
				evalOutput = eval(output)
				if sso and evalOutput.has_key('ERROR') and repeats == 1:
					#
					# Create user id server is SSO and user does not exist
					# Error: '03707161121' User not found
					#       If a plain set user ... without for clause was executed we try to create the non existing user
					# Error: '03707161122' Base User not found
					#       Only possible with for option, we try to create the base user
					# We try to create a user only if the SSOautoCreateUsers option is set in ZopeSSO.conf
					#
					if evalOutput['ERROR']['ERRORCODE'] == '03707161122' or (len(userLst) == 1 and evalOutput['ERROR']['ERRORCODE'] == '03707161121'):
						domains = getUserDomains(context)
						domain = None
						if domains == None or len(domains) < 1:
							# This should not happen !
							print "SDMSUserConnectCommandV2(): Cannot obtain domains for user " + str(context.REQUEST['AUTHENTICATED_USER']) + ", server settings will be used"
							ServerAutoCreateUsers = getConfig('ServerAutoCreateUsers', False, server = server)
						else:
							domain = domains[0]
							ServerAutoCreateUsers = getConfig('ServerAutoCreateUsers', False, server = server, domain = domain)

						if ServerAutoCreateUsers:
							if len(userLst) == 1:
								userToCreate = userLst[0]
							else:
								userToCreate = userLst[1]
							if createSsoUser(server, userToCreate, context) == None:
								output = { 'ERROR' : { 'ERRORCODE' : 'ZSI-10011', 'ERRORMESSAGE' : 'Error creating new SSO user ' + userToCreate }}
								repeats = 2
				else:
					repeats = 2
			try:
				data = eval(output)
			except:
				# this is nearly impossilble but can be the case if
				# server returns output not evalable which should be never the case
				data = { 'ERROR' : { 'ERRORCODE' : 'ZSI-10003', 'ERRORMESSAGE' : 'Invalid Server Response' }}

			error = None
			if data.has_key('ERROR'):
				error = data['ERROR']
				if error['ERRORCODE'] in [ 'ZSI-10001', '03202081740', '03202081739' ] or \
				   (error['ERRORCODE'] == 'ZSI-10002' and repeatable == 1): 
					user_error = 0
				else:
					user_error = 1
				error = data['ERROR']
				print 'Error executing command'
				print '    ERRORCODE ...: ' + error['ERRORCODE']
				print '    ERRORMESSAGE : ' + error['ERRORMESSAGE']
			else:
				done = 1

			releaseSocket(server, soc)

			if error != None and error['ERRORCODE'] == 'ZSI-10002':
				# server connection was broken
				# we clear the cache for that server and try again
				clearSockets(server)
			else:
				break

	if done == 0:
		if user_error == 0:
			if timeout_secs != 0:
				print 'Timeout reached'

	if outdict != None:
		outdict.update ( { 'EXECUTIONS' : executions } )

	return data
#
# This one is used from scripts which should work fault tolerant on server switches
# This one should be used if the command should not be repeated because it is not idempotent
#
def SDMSCommand(host, port, user, pwd , command, type='USER', timeout=None, cycle=1, session=''):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSCommandV2(server, user, pwd , command, type, timeout, cycle, session)

def SDMSCommandV2(server, user, pwd , command, type='USER', timeout=None, cycle=1, session=''):
	return SDMSBaseCommandV2(server, user, pwd , command, 0, None, type, timeout, cycle, session)

#
# This one is used from scripts which should work fault tolerant on server switches
# This one can be used for idempotent commands
# ..Out.. Version gives feedback whether command was repeated
#
def SDMSRepeatableCommand(host, port, user, pwd , command, type='USER', timeout=None, cycle=1, session=''):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSRepeatableCommandV2(host, port, user, pwd , command, type, timeout, cycle, session)

def SDMSRepeatableCommandV2(server, user, pwd , command, type='USER', timeout=None, cycle=1, session=''):
	return SDMSBaseCommandV2(server, user, pwd , command, 1, None, type, timeout, cycle, session)

def SDMSRepeatableOutCommand(host, port, user, pwd , command, outdict, type='USER', timeout=None, cycle=1, session=''):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSRepeatableOutCommandV2(server, user, pwd , command, outdict, type, timeout, cycle, session)

def SDMSRepeatableOutCommandV2(server, user, pwd , command, outdict, type='USER', timeout=None, cycle=1, session=''):
	return SDMSBaseCommandV2(server, user, pwd , command, 1, outdict, type, timeout, cycle, session)
#
# This one is used from scripts which should work fault tolerant on server switches
#
def SDMSBaseCommand(host, port, user, pwd , command, repeatable=0, outdict=None, type='USER', timeout=None, cycle=1, session=''):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSBaseCommandV2(server, user, pwd , command, repeatable, outdict, type, timeout, cycle, session)

def SDMSBaseCommandV2(server, user, pwd , command, repeatable=0, outdict=None, type='USER', timeout=None, cycle=1, session=''):
	executions = 0
	if timeout != None:
		timeout_secs = int(timeout) * 60
	else:
		timeout_secs = -1
	if cycle != None:
		cycle_secs = int(cycle)   * 60
	else:
		cycle_secs = 60
	start_time = time.time()
	last_time = start_time
	done = 0
	retries = 0
	first = 1
	user_error = 0
	while user_error == 0 and (first == 1 or (done == 0 and (timeout_secs == -1 or int((timeout_secs-last_time+start_time)/60) > 0))):
		first = 0
		if retries > 0:
			if timeout_secs != -1:
				print str(int((timeout_secs-last_time+start_time)/60)) + ' Minutes left until timeout'
			print 'Waiting ' + str(cycle_secs/60) + ' Minutes before retry'
			time.sleep(cycle_secs)
		retries = retries + 1
		last_time = time.time()
		print time.asctime() + ' ' + user
		soc = SDMSGenericConnectionOpenV2(server,user,pwd,type,session)
		try:
			if soc.has_key('ERROR'):	
				error = soc['ERROR']
				print 'Error connecting to BICsuite server'
				print '    ERRORCODE ...: ' + error['ERRORCODE']
				print '    ERRORMESSAGE : ' + error['ERRORMESSAGE']
				closeSocket(soc)
				#
				# if ERROR is not a broken connection, we don't retry the submit
				#
				# ZSI-10001   : Connect Error
				# ZSI-10002   : Broken Connection
				# 03202081740 : Server in restricted access mode (from job or job server connect)
				# 03202081739 : Server in restricted access mode (from user connect)
				#
				if error['ERRORCODE'] not in [ 'ZSI-10001', 'ZSI-10002', '03202081740', '03202081739' ]: 
					user_error = 1
				data = soc
				continue
		except:
			# soc is a socket everythings ok
			pass

		printCommand(command)
		output = sendCommand(soc, command)
		executions = executions + 1

		try:
			data = eval(output)
		except:
			# this is nearly impossilble but can be the case if
			# server returns an out not evaluable which should be never the case
			print user + ' ZSI-10003:Invalid Server Response'
			print 'Output was:'
			print output
			# we treat this as user error because looping until timeout not good in this fatal error case
			user_error = 1
			continue

		if data.has_key('ERROR'):
			if data['ERROR']['ERRORCODE'] == 'ZSI-10002' and repeatable == 1:
				# command idempotent and error is a temporary error
				# ZSI-10002   : Broken Connection
				continue
			error = data['ERROR']
			print 'Error executing command'
			print '    ERRORCODE ...: ' + error['ERRORCODE']
			print '    ERRORMESSAGE : ' + error['ERRORMESSAGE']
			closeSocket(soc)
			# treat as user_error (no retry) because we don't know whether the command has been executed
			user_error = 1
			continue

		releaseSocket(server, soc)
		done = 1

	if done == 0:
		if user_error == 0:
			if timeout_secs != 0:
				print 'Timeout reached'

	if outdict != None:
		outdict.update ( { 'EXECUTIONS' : executions } )

	return data

def sendCommand(soc, command):
	command = command.rstrip()
	if command[-1] != ';':
		command = command + ';'
	command = command + '\0'
	try:
		soc.send(command)
	except:
		return "{ 'ERROR' : { 'ERRORCODE' : 'ZSI-10002', 'ERRORMESSAGE' : 'Connection Broken' }}"
	data = []
	hasData = 1
	gotData = 0
	lvl = 0
	esc = 0
	instr = 0
	while hasData == 1:
		try:
			buf = soc.recv(1024)
		except:
			buf = ''
		if buf == '':
			print 'ZSI-10002:Connection broken'
			print 'data was:'
			print string.join(data,'')
			return "{ 'ERROR' : { 'ERRORCODE' : 'ZSI-10002', 'ERRORMESSAGE' : 'Connection Broken' }}"
		data.append(buf)
		for c in buf:
			if c == '\'':
				if esc == 1:
					esc = 0
				else:
					if instr == 0:
						instr = 1
					else:
						instr = 0
				continue;
			if c == '{':
				if instr == 1:
					continue
				if esc == 1:
					esc = 0
				else:
					lvl = lvl + 1
				gotData = 1
				continue
			if c == '}':
				if instr == 1:
					continue
				if esc == 1:
					esc = 0
				else:
					lvl = lvl - 1
					if lvl == 0 and gotData == 1:
						hasData = 0
				continue
			if c == '\\':
				if esc == 0:
					esc = 1
				else:
					esc = 0
				continue
			esc = 0
	return string.join(data,'')

import rfc822

def havePytz():
	return have_pytz

def convertFormat(s, timeformat = '%d.%m.%Y %H:%M:%S', timezone = None):
	return convertClockTime(rfc822.mktime_tz(rfc822.parsedate_tz(s)), timeformat, timezone)

def convertClockTime(ct, timeformat = '%d.%m.%Y %H:%M:%S', timezone = None):
	# print str(ct) + ' ' + timeformat + ' ' + str(timezone)
	if timezone == None or have_pytz == False:
		return time.strftime(timeformat, time.localtime(ct))
	else:
		dt = datetime.datetime.fromtimestamp(ct, pytz.timezone(timezone))
		return dt.strftime(timeformat)

def numericTime(s):
	# local not supported under windows
	# since we are using english anyway we skip that
	# locale.setlocale(locale.LC_ALL, 'en_GB')
	pt = rfc822.parsedate_tz(s[:20])
	# ts = time.mktime(time.strptime(s[:20], '%d %b %Y %H:%M:%S'))
	ts = time.mktime(pt[:9])
	return ts

def gmtime(secs):
	return time.gmtime(secs)

def localtime(secs):
	return time.localtime(secs)

def mktime (tuple):
	return time.mktime(tuple)

def strftime (format, tuple):
	return time.strftime (format, tuple)

def currentTime():
	return time.time()

def clockTime(s):
	# local not supported under windows
	# since we are using english anyway we skip that
	# locale.setlocale(locale.LC_ALL, 'en_GB')
	# return time.mktime(time.strptime(s[:20], '%d %b %Y %H:%M:%S'))
	return rfc822.mktime_tz(rfc822.parsedate_tz(s))
	# pt = rfc822.parsedate_tz(s[:20])
	# print pt
	# return time.mktime(pt[:9])

def validate_identifier(str):
	result = re.match('^[a-zA-Z_@#][a-zA-Z_@#0-9]*$',str)
	if result == None:
		return 0
	else:
		return 1

def readlog(fname):
	f = open(fname)
	lines = f.readlines()
	if len(lines) > 1000:
		lines = [ 'LOGFILE TOO LARGE !!!\n',
			  'ONLY LAST 1000 LINES OF LOGFILE SHOWN\n',
			  'USE OPERATING SYSTEM LEVEL EDITOR TO VIEW ENTIRE FILE\n',
			  '\n',
			  '-----------------------------------------------------\n'
			  '\n' ] + lines[-1000:]
	return string.join(lines,'')

def changeOwnership(self, username, obj):
	""" explicitly setup changeOwnership for TTW """
	acl_users = getattr(self, 'acl_users')    #UserFolder source
	user = acl_users.getUser(username)
	if user == None:
		web = getattr(self,'web')
		acl_users = getattr(web,'acl_users')
		user = acl_users.getUser(username)
	user = user.__of__(acl_users)
	obj.changeOwnership(user)

def clock():
	return time.clock()

def SDMSQueryWithSoc(soc, cmd, qry):
	#
	# Local Functions
	#
	def tblqry(table,query):
		out = []
		columns = string.split(query,',')
		for row in table:
			rec = {}
			for col in columns:
				rec.update( { col : row[col] } )
			out = out + [rec]
		return out

	def recqry(record,query):
		out = {}
		attribs = string.split(query,':')
		for attr in attribs:
			try:
				name = string.split(attr,'.')[0]
				result = tblqry(record[name]['TABLE'], string.split(attr,'.')[-1])
				out.update( { name : result } )
			except:
				# attr is scalar
				result =  record[attr]
				out.update( { attr : result } )
		return out
	#
	# Execute Command
	#
	result = SDMSCommandWithSoc(soc,cmd)
	if result.has_key('ERROR'):
		error = result['ERROR']
		print 'ERRORCODE   : ' + error['ERRORCODE']
		print 'ERRORMESSAGE: ' + error['ERRORMESSAGE']
	#
	# Process qry
	#
	record = None;
	table = None;
	data = result['DATA']
	if data.has_key('RECORD'):
		# data is record
		return recqry(data['RECORD'],qry)
	else:
		# data is a table
		table = data['TABLE']
		return tblqry(data['TABLE'],qry)

def log(str):
	print str

def sleep(s):
	time.sleep(s)

def variant():
	return 'schedulix'

def re_sub(pattern, replacement, text, count=0):
	return re.sub(pattern, replacement, text, count)

docs = [
	{
		'ICON' : 'pdficon_large.gif',
		'TEXT' : variant() + '!Web Dokumentation',
		'MODE' : 'DOC',
		'FILE' : 'online_de.pdf',
		'TYPE' : 'application/pdf',
		'LANG' : 'de'
	},
	{
		'ICON' : 'pdficon_large.gif',
		'TEXT' : variant() + '!Web documentation',
		'MODE' : 'DOC',
		'FILE' : 'online_en.pdf',
		'TYPE' : 'application/pdf',
		'LANG' : 'en'
	},
	{
		'ICON' : 'pdficon_large.gif',
		'TEXT' : 'Beschreibung der ' + variant() + ' Kommandosprache',
		'MODE' : 'DOC',
		'FILE' : 'syntax_de.pdf',
		'TYPE' : 'application/pdf',
		'LANG' : 'de'
	},
#	{
#		'ICON' : 'pdficon_large.gif',
#		'TEXT' : variant() + ' Syntax documentation',
#		'MODE' : 'DOC',
#		'FILE' : 'syntax_en.pdf',
#		'TYPE' : 'application/pdf',
#		'LANG' : 'en'
#	},
	{
		'ICON' : 'globe.jpg',
		'TEXT' : variant() + ' Internet Biliothek',
		'MODE' : 'URL',
		'URL'  : 'http://www.independit.de/de/support/downloads',
		'LANG' : 'de'
	},
	{
		'ICON' : 'globe.jpg',
		'TEXT' : variant() + ' Web Library',
		'MODE' : 'URL',
		'URL'  : 'http://www.independit.de/en/support/downloads',
		'LANG' : 'en'
	}
]

def getDocs():
	return docs

def readDoc(index):
	doc = docs[index]
	filename = os.environ['BICSUITEHOME'] + '/doc/' + doc['FILE']
	f = open (filename, 'r')
	data = f.read()
	f.close()
	return data

def json_dumps(o):
	if have_json:
		return json.dumps(o)
	else:
		return '{ "ERROR" : { "ERRORCODE" : "WSI-0001", "ERRORMESSAGE" : "Web Service not available" }}'

def StringCompare(string1, string2):
	return locale.strcoll(string1, string2)

logoutBrowserIds = {}

def registerLogout(browserId):
	logoutBrowserIds.update ( { str(browserId) : True } )
	# print 'logoutBrowserIds = ' + str(logoutBrowserIds)

def raiseUnauthorized(browserId):
	# print "raiseUnauthorized called with browserId " + str(browserId)
	if browserId == None:
		raise Unauthorized
	else:
		# print 'logoutBrowserIds = ' + str(logoutBrowserIds)
		if str(browserId) in logoutBrowserIds:
			del logoutBrowserIds[str(browserId)]
			# print 'raise Unauthorized'
			raise Unauthorized

def abortAndRetryTransaction():
    raise ConflictError

translations = None

def translate(context, text, lang):
	global translations
	# print "translate(" + text + ")"
	# print str(translations)
	if translations == None:
		translations = context.Common.translations()
	try:
		return translations[text][lang]
	except:
		return text
