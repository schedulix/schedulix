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
package de.independit.scheduler.server.exception;

import java.util.*;
import de.independit.scheduler.server.*;

public class DeadlockException extends SerializationException
{

	private static HashMap<String,Long> stackTraces = new HashMap<String,Long>();

	public DeadlockException()
	{
		super();
	}

	public DeadlockException(String msg)
	{
		super(msg);
	}

	public DeadlockException(SDMSMessage msg)
	{
		super(msg);
	}

	public static void countAndTraceDeadlock(String stackTrace)
	{
		synchronized (stackTraces) {
			Long ctr = stackTraces.get(stackTrace);
			if (ctr == null) {
				stackTraces.put(stackTrace, SDMSConstants.lONE);
			} else {
				stackTraces.put(stackTrace, Long.valueOf(ctr.longValue() + 1));
			}
		}
	}

	public static Map.Entry<String,Long>[] getStackTraces(int topHits)
	{
		@SuppressWarnings("unchecked")
		Map.Entry<String,Long> m[] = new Map.Entry[1];
		int topHitCtr = (topHits <= 0 ? Integer.MAX_VALUE : topHits);
		synchronized(stackTraces) {
			Vector<Map.Entry<String,Long>> v = new Vector<Map.Entry<String,Long>>();
			Vector<Map.Entry<String,Long>> w = new Vector<Map.Entry<String,Long>>();
			v.addAll(stackTraces.entrySet());
			Collections.sort(v, new Comparator<Map.Entry<String,Long>>() {
				public int compare(Map.Entry<String,Long> o1, Map.Entry<String,Long> o2) {
					if (o1.getValue().longValue() > o2.getValue().longValue())
						return -1;
					if (o1.getValue().longValue() < o2.getValue().longValue())
						return 1;
					return o1.getKey().compareTo(o2.getKey());
				}
			});
			Iterator<Map.Entry<String,Long>> i = (Iterator<Map.Entry<String,Long>>) v.iterator();
			while (i.hasNext() && topHitCtr > 0) {
				topHitCtr --;
				Map.Entry<String,Long> me = i.next();
				me.setValue(Long.valueOf(me.getValue()));
				w.add(me);
			}
			m = w.toArray(m);
		}

		return m;
	}

	public int getExceptionNumber()
	{
		return 4;
	}
}

