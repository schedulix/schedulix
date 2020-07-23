import string
from AccessControl import ModuleSecurityInfo

def count(s, sub):
	return s.count(sub)
def find(s, sub):
	return s.find(sub)
def join(words, sep=" "):
	return sep.join(words)
def replace(s, old, new):
	return s.replace(old, new)
def split(s, sep):
	return s.split(sep)
def strip(s):
	return s.strip()
def rstrip(s, chars=None):
	if chars == None:
		return s.rstrip()
	else:
		return s.rstrip(chars)
def upper(s):
	return s.upper()

def initialize(context): 
	string.count = count
	string.find = find
	string.join = join
	string.replace = replace
	string.split = split
	string.strip = strip
	string.rstrip = rstrip
	string.upper = upper

	ModuleSecurityInfo('functools').declarePublic('cmp_to_key')


