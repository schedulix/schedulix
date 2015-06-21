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

import java.util.*;

public class SDMSIndexMap
{
	HashMap map;
	public SDMSIndex index;

	SDMSIndexMap (SDMSIndex index)
	{
		map = new HashMap();
		this.index = index;
	}

	public Object put (Object key, Object value)
	{
		return map.put(key, value);
	}

	public Object get (Object key)
	{
		return map.get(key);
	}

	public Object remove(Object key)
	{
		return map.remove(key);
	}

	public Set keySet()
	{
		return map.keySet();
	}

	public String toShortString()
	{
		return "SDMSIndexMap(" + index.table.tableName() + ", " + index.indexName + ")";
	}
}

