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
package de.independit.scheduler.server.repository;

public class IntervalReference
{

	public RefererType 	refererType;
	public ReferenceUsage 	referenceUsage;
	public Long    		refererId;
	public String		refererName;
	public boolean		isDependent;

	public Long		childId = null;
	public String		childName = null;
	public String		childType = null;
	public String		seType = null;

	public IntervalReference (RefererType refererType, ReferenceUsage referenceUsage, Long refererId, String refererName, boolean isDependent)
	{
		this.refererType	= refererType;
		this.referenceUsage	= referenceUsage;
		this.refererId		= refererId;
		this.refererName 	= refererName;
		this.isDependent        = isDependent;
	}
}

