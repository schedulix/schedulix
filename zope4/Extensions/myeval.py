def myeval(string, context = None):
	try:
		if context == None:
			return eval(string)
		else:
			return eval(string, { 'context' : context } )
	except:
		string = string.replace('\xc2\xa0', '')
		if context == None:
			return eval(string)
		else:
			return eval(string, { 'context' : context } )
