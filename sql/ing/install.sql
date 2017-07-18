/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software: 
you can redistribute it and/or modify it under the terms of the 
GNU Affero General Public License as published by the 
Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
set autocommit on;
\g
\i ing_gen/AUDIT_TRAIL.sql
\i ing_gen/CALENDAR.sql
\i ing_gen/DEPENDENCY_DEFINITION.sql
\i ing_gen/DEPENDENCY_INSTANCE.sql
\i ing_gen/DEPENDENCY_STATE.sql
\i ing_gen/ENTITY_VARIABLE.sql
\i ing_gen/ENVIRONMENT.sql
\i ing_gen/EVENT_PARAMETER.sql
\i ing_gen/EVENT.sql
\i ing_gen/EXIT_STATE_DEFINITION.sql
\i ing_gen/EXIT_STATE_MAPPING_PROFILE.sql
\i ing_gen/EXIT_STATE_MAPPING.sql
\i ing_gen/EXIT_STATE_PROFILE.sql
\i ing_gen/EXIT_STATE.sql
\i ing_gen/EXIT_STATE_TRANSLATION.sql
\i ing_gen/EXIT_STATE_TRANS_PROFILE.sql
\i ing_gen/EXTENTS.sql
\i ing_gen/FOLDER.sql
\i ing_gen/FOOTPRINT.sql
\i ing_gen/GRANTS.sql
\i ing_gen/GROUPS.sql
\i ing_gen/HIERARCHY_INSTANCE.sql
\i ing_gen/IGNORED_DEPENDENCY.sql
\i ing_gen/INSTANCE_VALUE.sql
\i ing_gen/INTERVAL_HIERARCHY.sql
\i ing_gen/INTERVALL.sql
\i ing_gen/INTERVAL_SELECTION.sql
\i ing_gen/KILL_JOB.sql
\i ing_gen/MEMBER.sql
\i ing_gen/NAMED_ENVIRONMENT.sql
\i ing_gen/NAMED_RESOURCE.sql
\i ing_gen/NICE_PROFILE_ENTRY.sql
\i ing_gen/NICE_PROFILE.sql
\i ing_gen/OBJECT_COMMENT.sql
\i ing_gen/OBJECT_EVENT.sql
\i ing_gen/OBJECT_INSTANCE.sql
\i ing_gen/OBJECT_MONITOR_PARAMETER.sql
\i ing_gen/OBJECT_MONITOR.sql
\i ing_gen/PARAMETER_DEFINITION.sql
\i ing_gen/PERSISTENT_VALUE.sql
\i ing_gen/POOL_DIST_CONFIG.sql
\i ing_gen/POOL_DISTRIBUTION.sql
\i ing_gen/POOLED_RESOURCE.sql
\i ing_gen/POOL.sql
\i ing_gen/RESOURCE_ALLOCATION.sql
\i ing_gen/RESOURCE_REQ_STATES.sql
\i ing_gen/RESOURCE_REQUIREMENT.sql
\i ing_gen/RESOURCE_STATE_DEFINITION.sql
\i ing_gen/RESOURCE_STATE_MAPPING.sql
\i ing_gen/RESOURCE_STATE_MAP_PROF.sql
\i ing_gen/RESOURCE_STATE_PROFILE.sql
\i ing_gen/RESOURCE_STATE.sql
\i ing_gen/RESOURCE_TEMPLATE.sql
\i ing_gen/RESOURCE_TRACE.sql
\i ing_gen/RESOURCE_VARIABLE.sql
\i ing_gen/RESSOURCE.sql
\i ing_gen/RUNNABLE_QUEUE.sql
\i ing_gen/SCHEDULED_EVENT.sql
\i ing_gen/SCHEDULE.sql
\i ing_gen/SCHEDULING_ENTITY.sql
\i ing_gen/SCHEDULING_HIERARCHY.sql
\i ing_gen/SCOPE_CONFIG_ENVMAPPING.sql
\i ing_gen/SCOPE_CONFIG.sql
\i ing_gen/SCOPE.sql
\i ing_gen/SME_COUNTER.sql
\i ing_gen/SUBMITTED_ENTITY.sql
\i ing_gen/SUBMITTED_ENTITY_STATS.sql
\i ing_gen/TEMPLATE_VARIABLE.sql
\i ing_gen/TRIGGER_DEFINITION.sql
\i ing_gen/TRIGGER_QUEUE.sql
\i ing_gen/TRIGGER_STATE.sql
\i ing_gen/USERS.sql
\i ing_gen/VERSIONED_EXTENTS.sql
\i ing_gen/WATCH_TYPE_PARAMETER.sql
\i ing_gen/WATCH_TYPE.sql
\i ing/OBJECTCOUNTER.sql
\i ing/VERSIONCOUNTER.sql
\i REPOSITORY_LOCK.sql
\g
\i MASTER_STATE.sql
\g
\i ing/SME2LOAD.sql
\g
\i ing/index.sql
\g
\i init.sql
\g
\i ing/sci_sme_quarter.sql
