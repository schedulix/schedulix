import sdms

server = { 'HOST' : 'localhost',
	   'PORT' : '2506',
	   'USER' : 'SYSTEM',
	   'PASSWORD' : 'VerySecret' }
conn = sdms.SDMSConnectionOpenV2(server, server['USER'], server['PASSWORD'], "Simple Access Example")
try:
	if conn.has_key('ERROR'):
		print str(conn)
		exit(1)
except:
	pass

stmt = "LIST SESSIONS;"
result = sdms.SDMSCommandWithSoc(conn, stmt)
if result.has_key('ERROR'):
	print str(result['ERROR'])
else:
	for row in result['DATA']['TABLE']:
		print "{0:3} {1:8}  {2:32}  {3:9}  {4:15}  {5:>15}  {6}".format(\
			row['THIS'], \
			row['UID'], \
			row['USER'], \
			row['TYPE'], \
			row['START'], \
			row['IP'], \
			row['INFORMATION'])

conn.close()
