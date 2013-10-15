#
# Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH"
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


BASECLASSES = Node.class Parser.class SDMSParser.class Scanner.class

HELPERCLASSES = \
	ChownChecker.class \
	EsmMap.class EspState.class EventParameter.class \
	FolderLister.class Formatter.class \
	HierarchyLister.class \
	IntervalUtil.class \
	ManipObjectMonitor.class ManipParameters.class \
	NamedResourceLister.class \
	ObjectURL.class \
	ParseStr.class \
	ResourceTreeLister.class \
	ScheduleUtil.class ScopeConfig.class ScopeLister.class ScopeParameter.class SsRFormatter.class StatusTranslation.class \
	Token.class TreeLister.class \
	WithHash.class WithItem.class


NODECLASSES = \
	ActivateDistribution.class \
	AlterComment.class AlterEnvironment.class AlterDistribution.class AlterEvent.class AlterExitStatProf.class \
	AlterExitStateMapping.class AlterExitStateTranslation.class \
	AlterFolder.class AlterFootprint.class AlterGroup.class AlterInterval.class AlterJob.class \
	AlterJobDefinition.class AlterJobDefDependents.class \
	AlterNamedResource.class \
	AlterObjectMonitor.class AlterPool.class AlterResource.class \
	AlterRsm.class AlterRsp.class AlterSchedule.class AlterScope.class \
	AlterScheduledEvent.class AlterServer.class AlterSession.class \
	AlterTrigger.class AlterUser.class AlterWt.class \
	CleanupFolder.class \
	Connect.class ConnectCtrl.class \
	CopyFolder.class CopyNamedResource.class \
	CopyScope.class \
	CreateComment.class CreateDistribution.class CreateEnvironment.class CreateEvent.class CreateExitStatProf.class \
	CreateExitStateTranslation.class CreateExitStatusDef.class \
	CreateExitStatusMapping.class CreateFolder.class CreateFootprint.class CreateGroup.class \
	CreateInterval.class CreateJobDefinition.class \
	CreateNamedResource.class CreateObjectMonitor.class CreatePool.class CreateResource.class \
	CreateResourceStatus.class CreateRsm.class \
	CreateRsp.class CreateSchedule.class CreateScheduledEvent.class \
	CreateScope.class \
	CreateTrigger.class CreateUser.class CreateWt.class \
	Disconnect.class \
	DropComment.class DropDistribution.class DropEnvironment.class DropEvent.class DropExitStatProf.class \
	DropExitStateMapping.class DropExitStateTranslation.class \
	DropExitStatusDef.class DropFolder.class DropFootprint.class DropGroup.class \
	DropInterval.class DropJobDefinition.class DropNamedResource.class \
	DropObjectMonitor.class DropPool.class DropResource.class DropResourceStatus.class \
	DropRsm.class DropRsp.class DropSchedule.class \
	DropScheduledEvent.class DropScope.class \
	DropTrigger.class DropUser.class DropWt.class \
	DumpCommand.class \
	EmptyCmd.class \
	FinishJob.class \
	GetNextJob.class \
	GetParameter.class \
	GetSubmitTag.class \
	Grant.class \
	JobDistribution.class \
	KillSession.class \
	ListCalendar.class ListDependencyDefinition.class ListDependencyHierarchy.class \
	ListEnvironment.class ListEvent.class ListExitStateMapping.class \
	ListExitStateProfile.class ListExitStateTranslation.class \
	ListExitStatusDef.class ListFolder.class ListFootprint.class ListGrant.class ListGroup.class \
	ListInterval.class ListJobDefinitionHierarchy.class \
	ListNamedResource.class ListObjectMonitor.class ListPool.class ListResourceStatus.class \
	ListRsm.class ListRsp.class ListSchedule.class ListScheduledEvent.class \
	ListScope.class ListSession.class \
	ListSubmitted.class ListTrigger.class ListUser.class ListWt.class \
	ManipGroup.class ManipJobDefinition.class ManipParameters.class ManipResource.class ManipTrigger.class ManipUser.class \
	MoveFolder.class MoveJobDefinition.class MovePool.class MoveSchedule.class \
	MoveScope.class \
	MultiCommand.class \
	ReassureJob.class \
	RegisterServer.class \
	RenameDistribution.class RenameEnvironment.class RenameEvent.class \
	RenameExitStatProf.class RenameExitStateMapping.class \
	RenameExitStateTranslation.class RenameExitStatusDef.class \
	RenameFootprint.class RenameGroup.class RenameInterval.class RenameNamedResource.class \
	RenameObjectMonitor.class RenameResourceStatus.class \
	RenameRsm.class RenameRsp.class \
	RenameTrigger.class RenameUser.class RenameWt.class \
	ResourceScopeLister.class \
	ResumeServer.class \
	RunTest.class \
	SelectCmd.class \
	SetParameter.class SetState.class \
	ShowComment.class ShowCommented.class ShowDistribution.class ShowEnvironment.class ShowEvent.class ShowExitStateMapping.class \
	ShowExitStateProfile.class ShowExitStateTranslation.class \
	ShowExitStatusDef.class ShowFolder.class ShowFootprint.class ShowGroup.class \
	ShowInterval.class ShowNamedResource.class ShowObjectMonitor.class ShowPool.class ShowResource.class ShowResourceStatus.class \
	ShowRsm.class ShowRsp.class ShowSchedule.class \
	ShowScheduledEvent.class ShowScope.class ShowSession.class \
	ShowSubmitted.class ShowSystem.class ShowTrigger.class ShowUser.class ShowWt.class \
	ShutdownServer.class \
	StopServer.class \
	SubmitJob.class \
	SuspendServer.class \
	SyntaxError.class

SUBDIRS = cmdline filter triggerexpr 

all: generate subdirs $(BASECLASSES) $(NODECLASSES) $(HELPERCLASSES)

new: clean all

generate: commongensubdirs Parser.java Scanner.java

subdirs: commonsubdirs

clean: commonclean
	rm -f *.pyc Parser.java Parser.java.raw Scanner.java \
		y.output yycheck.data yytable.data

#
# This is just a syntax-definition check
#
syntax:
	$(JAY) Parser.jay < /dev/null

Parser.java:    Parser.jay compress.awk
	rm -f yytable.data yycheck.data
	$(JAY) $(JAY_FLAGS) Parser.jay < $(SKELETON) > Parser.java.raw
	$(AWK) -f compress.awk < Parser.java.raw > Parser.java

include $(SDMSHOME)/src/Makefile.CommonRules

