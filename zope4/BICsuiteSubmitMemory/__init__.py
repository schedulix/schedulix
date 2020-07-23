from . import BICsuiteSubmitMemory

def initialize(context): 

	"Initialize the BICsuiteSubmitMemory product."

	context.registerClass(
               	BICsuiteSubmitMemory.BICsuiteSubmitMemory,
		constructors = ( BICsuiteSubmitMemory.manage_addBICsuiteSubmitMemory, )
	)
	print('BICsuiteSubmitMemory initialized')
