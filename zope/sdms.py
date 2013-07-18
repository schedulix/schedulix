#
# Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH"
# 
# BICsuite!OpenScheduler Enterprise Job Scheduling System
# 
# independIT Integrative Technologies GmbH [http://www.independit.de]
# mailto:contact@independit.de
# 
# This file is part of BICsuite!OpenScheduler
# 
# BICsuite!OpenScheduler is is free software: 
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
import locale
import time
import re
import threading

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
		server['PORT'] + ':' + \
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
def SDMSUserCommandNoBlock(host, port, user, pwd , command, session=''):
	server = { 'HOST' : host, 'PORT' : port }
	return SDMSUserConnectCommandV2(server, user, pwd , command, 0, None, 'USER', 0, 1, session)

#
# New version of wrapper used from BICsuite!webs Common.Util.SDMSCommand()
# Taking a server dict with attributes 'HOST', 'PORT', and optionally further attributes,
# neccessary for SSL connections to BICsuite servers
#
def SDMSUserCommandNoBlockV2(server, user, pwd , command, session = ''):
	return SDMSUserConnectCommandV2(server, user, pwd , command, 0, None, 'USER', 0, 1, session)

#
# This Function uses the Server Connect with command feature to execute the command
# to reduce communication between Server and client for clients like BICsuite!web,
# doing a connect for each command to execute
#
# old version for backward compatibility
# with user not case sensitive if not quoted
#
def SDMSUserConnectCommand(host, port, user, pwd , command, repeatable=0, outdict=None, type='USER', timeout=None, cycle=1, session = ''):
	if user[0] != "'":
		user = string.upper(user)
	else:
		user = string.replace(user,"'","")
        server = { 'HOST' : host, 'PORT' : port }
	return  SDMSUserConnectCommandV2(server, user, pwd , command, repeatable, outdict, type, timeout, cycle, session)
#
# New version
# Taking a server dict with attributes 'HOST', 'PORT', and optionally further attributes,
# neccessary for SSL connections to BICsuite servers
#
# user is case sensitive !
#
def SDMSUserConnectCommandV2(server, user, pwd , command, repeatable=0, outdict=None, type='USER', timeout=None, cycle=1, session = ''):
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
			connectCommand = "CONNECT '" + user + "' IDENTIFIED BY '" + pwd + "' WITH PROTOCOL = PYTHON"
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
			connectCommand = connectCommand + ", COMMAND = (" + command + ");"
			print command 
			output = sendCommand(soc, connectCommand)

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

		print command + '\n'
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

def convertFormat(s, offset):
    # offset no longer needed
    return time.strftime('%d.%m.%Y %H:%M:%S',time.localtime(rfc822.mktime_tz(rfc822.parsedate_tz(s))))

def convertClockTime(ct):
    return time.strftime('%d.%m.%Y %H:%M:%S',time.localtime(ct))

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
    user = acl_users.getUser(username).__of__(acl_users)
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

# print SDMSCommand('localhost', 2506, 'DONALD', 'duck', 'create exit state definition xxx')
# print SDMSCommand('localhost', 2506, 'DONALD', 'duck', 'rename exit state definition xxx to \'\'')
# print SDMSCommand('localhost', 2506, 'DONALD', 'duck', 'list job with expand = all')
# print SDMSCommand('localhost', 2506, 'DONALD', 'duck', 'list exit state definition')
