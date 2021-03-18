__doc__='''RemoteUserFolder init'''

import RemoteUserFolder


def initialize(context):

    context.registerClass(
        RemoteUserFolder.RemoteUserFolder,
        constructors=(RemoteUserFolder.manage_addRemoteUserFolderForm,
                      RemoteUserFolder.manage_addRemoteUserFolder),
		icon='RemoteUserFolder.gif'
        )
    context.registerHelp()



