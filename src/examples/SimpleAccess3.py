import sdms

server = { 'HOST' : 'localhost',
	   'PORT' : '2506',
	   'USER' : 'SYSTEM',
	   'PASSWORD' : 'VerySecret' }
conn = sdms.SDMSConnectionOpenV2(server, server['USER'], server['PASSWORD'], "Simple Access Example")
try:
	if 'ERROR' in conn:
		print(str(conn))
		exit(1)
except:
	pass

stmt = "LIST SESSIONS;"
result = sdms.SDMSCommandWithSoc(conn, stmt)
if 'ERROR' in result:
	print(str(result['ERROR']))
else:
	for row in result['DATA']['TABLE']:
		print("{0:3} {1:8}  {2:32}  {3:9}  {4:15}  {5:>15}  {6}".format(\
			str(row['THIS']), \
			str(row['UID']), \
			str(row['USER']), \
			str(row['TYPE']), \
			str(row['START']), \
			str(row['IP']), \
			str(row['INFORMATION'])))

conn.close()
