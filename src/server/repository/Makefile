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

CLASSES = SDMSChangeListElement.class SDMSObject.class SDMSTable.class \
	  SDMSVersions.class SDMSTransaction.class SDMSLock.class \
	  SDMSRepository.class SDMSIndex.class SDMSProxy.class \
	  SDMSKey.class SDMSROTxList.class SDMSVersionList.class SDMSPurgeSet.class \
	  SDMSSeVersionList.class SDMSPrivilege.class VariableResolver.class \
	  SmeVariableResolver.class FolderVariableResolver.class ScopeVariableResolver.class \
	  ResourceVariableResolver.class SeVariableResolver.class

GENTABLES = \
	AuditTrail \
	Calendar \
	DependencyDefinition \
	DependencyInstance \
	DependencyState \
	EntityVariable \
	Environment \
	Event \
	EventParameter \
	ExitState \
	ExitStateDefinition \
	ExitStateMapping \
	ExitStateMappingProfile \
	ExitStateProfile \
	Folder \
	Footprint \
	Grant \
	Group \
	HierarchyInstance \
	IgnoredDependency\
	Interval \
	IntervalHierarchy \
	IntervalSelection \
	KillJob \
	NamedEnvironment \
	NamedResource \
	Member \
	ObjectComment \
	ParameterDefinition \
	PersistentValue \
	Resource \
	ResourceAllocation \
	ResourceReqStates \
	ResourceRequirement \
	ResourceState \
	ResourceStateDefinition \
	ResourceStateMapping \
	ResourceStateMappingProfile \
	ResourceStateProfile \
	ResourceVariable \
	RunnableQueue \
	Schedule \
	ScheduledEvent \
	SchedulingEntity \
	SchedulingHierarchy \
	Scope \
	ScopeConfig \
	ScopeConfigEnvMapping \
	SmeCounter \
	SubmittedEntity \
	Trigger \
	TriggerQueue \
	TriggerState \
	User \
	\
	npJobFootprint \
	npSrvrSRFootprint

#
# This part below is _very_ GNU make dependent
#
PREFIX = SDMS

GEN = Generic.class
TGEN = TableGeneric.class
PGEN = ProxyGeneric.class

JAV = Generic.java
TJAV = TableGeneric.java
PJAV = ProxyGeneric.java

NOGEN = .class
TNOGEN = Table.class

NOJAV = .java
TNOJAV = Table.java

GENCLASSES = $(foreach TAB,$(GENTABLES), $(PREFIX)$(TAB)$(GEN) $(PREFIX)$(TAB)$(TGEN) $(PREFIX)$(TAB)$(PGEN))

NOGENCLASSES = $(foreach TAB,$(GENTABLES), $(PREFIX)$(TAB)$(NOGEN) $(PREFIX)$(TAB)$(TNOGEN))

#
# GNU make dependent part ends
#

SUBDIRS = .

all: subdirs $(GENPY) $(GENCLASSES) $(NOGENCLASSES) $(CLASSES)

new: clean all

subdirs: commonsubdirs

clean: commonclean

include $(SDMSHOME)/src/Makefile.CommonRules

