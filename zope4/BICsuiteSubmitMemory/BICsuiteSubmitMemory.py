from threading import Lock
from OFS import SimpleItem
import time

class BICsuiteSubmitMemory (SimpleItem.SimpleItem):

	"BICsuiteSubmitMemory Object"

	meta_type = 'BICsuiteSubmitMemory'

	manage_options = (
		{'label': 'View', 'action': 'index_html'},
	)

	session_data = { 'SESSIONS' : {} , 'CLEAN_TS' : int(round(time.time())) }
	l=Lock()
	# keep timestamps and session data for 1 hour
	keep_secs = 3600
	# cleanup every 15 minutes
	clean_secs = 900

	def __init__(self, id):
		"initialise a new instance of BICsuiteSubmitMemory"
		self.id = id

	def index_html(self):
		"used to view content of the object"
		html = ''
		html = html + '<html><body>\n'
		html = html + '<h1>BICsuiteSubmitMemory</h1>\n'
		html = html + '<hr>\n'
		sessions = self.session_data['SESSIONS']
		s = len(sessions)
		html = html + '<table border="1"><tr><th>#session</th><th>clean ts</th></tr>\n'
		html = html + '<tr><td>' + str(s) + '</td><td>' + str(self.session_data['CLEAN_TS']) + '</td></tr>\n'
		html = html + '</table>'
		html = html + '<hr>\n'
		html = html + '<table border="1"><tr><th>session id</th><th>#ts</th><th>touch ts</th><th>min ts</th><th>max ts</th></tr>\n'
		for session in list(sessions.values()):
			min_ts = None
			max_ts = None
			for ts in list(session['SUBMIT_TS'].keys()):
				if min_ts == None or min_ts > ts:
					min_ts = ts
				if max_ts == None or max_ts < ts:
					max_ts = ts
			html = html + '<tr><td>' + session['SESSION_ID'] + '</td><td>' + \
					str(len(session['SUBMIT_TS'])) + '</td><td>' + \
					str(session['TOUCH_TS']) + '</td><td>' + \
					str(min_ts) + '</td><td>' + \
					str(max_ts) + '</td></tr>\n'
		html = html + '</table>'
		html = html + '\n'
		html = html + '</body></html>\n'
		return html

	#
	# return 0 if timestamp is not yet known
	# else record timestamp and return 1 or 2
	# if an unexpected exception occurs
	#
	def record_timestamp(self, sessionid, timestamp):
		# begin critical section
		self.l.acquire()
		# protect against unexpected exceptions
		if 1:
		# try:
			# get time now
			now = int(round(time.time()))
			# get or initialize session
			sessions = self.session_data['SESSIONS']
			# initialize sessionid session not yet known
			if sessionid not in sessions:
				sessions.update( { sessionid : { 'SESSION_ID' : sessionid , 'SUBMIT_TS' : {} , 'TOUCH_TS' : now } } )
			# get timestamp dict for session
			session = sessions[sessionid]
			session_submit_timestamps = session['SUBMIT_TS']
			# check for existance of timestamp
			if timestamp in session_submit_timestamps:
				result = 1
			else:
				session_submit_timestamps.update( { timestamp : now } )
				result = 0
			# touch the session
			session.update ( { 'TOUCH_TS' : now } )
			# check for need cleanup
			if self.session_data['CLEAN_TS'] < now - self.clean_secs:
				# run cleanup
				# check for expired sessions or timestamps
				for session in list(sessions.values()):
					if session['TOUCH_TS'] < now - self.keep_secs:
						# session is expired, remove totally
						del sessions[session['SESSION_ID']]
					else:
						# check for old ts in session
						tsd = session['SUBMIT_TS']
						for item in list(tsd.items()):
							if int(round(item[1])) < now - self.keep_secs:
								# timestamp expired, delete it
								del tsd[item[0]]
				self.session_data.update ( { 'CLEAN_TS' : now } )
		#except:
		#	# unexpected exception we return 2
		#	result = 2

			self.session_data.update ( { 'CLEAN_TS' : now } )
		# end critical section
		self.l.release()
		return result

def manage_addBICsuiteSubmitMemory(self, RESPONSE=None):
	"Add a BICsuiteSubmitMemory to a folder."
	self._setObject('BICsuiteSubmitMemory', BICsuiteSubmitMemory('BICsuiteSubmitMemory'))
	if RESPONSE != None:
		RESPONSE.redirect('manage_main')

