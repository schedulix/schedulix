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


package de.independit.scheduler.jobserver;

import java.io.File;

public class Descr
{
	public final String      id;
	public final String      run;
	public final String      cmd;
	public final String[]    args;
	public final File        dir;
	public final File        log;
	public final boolean     logapp;
	public final File        elog;
	public final boolean     elogapp;
	public final boolean     samelog;
	public final Environment env;
	public final Environment jobenv;

	public Descr (final long   id, final int run,
		      final String cmd, final String[] args,
		      final File dir,
		      final File log,  final boolean logapp,
		      final File elog, final boolean elogapp,
		      final boolean samelog,
	              final Environment env,
	              final Environment jobenv)
	{
		this.id      = String.valueOf (id);
		this.run     = String.valueOf (run);
		this.cmd     = cmd;
		this.dir     = dir;
		this.log     = log;
		this.logapp  = log != null ? logapp : false;
		this.elog    = elog;
		this.elogapp = elog != null ? elogapp : false;
		this.samelog = samelog;
		this.env     = env;
		this.jobenv  = jobenv;

		this.args = new String [args.length];
		for (int i = 0; i < args.length; ++i)
			this.args [i] = args [i];
	}
}
