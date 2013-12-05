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

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

public interface W32API extends StdCallLibrary, W32Errors
{

	Map UNICODE_OPTIONS = new HashMap() {
		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
		}
	};

	Map ASCII_OPTIONS = new HashMap() {
		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
		}
	};

	Map DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;

	public class HANDLE extends PointerType
	{
		@Override
		public Object fromNative(Object nativeValue, FromNativeContext context)
		{
			Object o = super.fromNative(nativeValue, context);
			if (INVALID_HANDLE_VALUE.equals(o))
				return INVALID_HANDLE_VALUE;
			return o;
		}
	}

	HANDLE INVALID_HANDLE_VALUE = new HANDLE() {
		{
			super.setPointer(Pointer.createConstant(-1));
		}
		@Override
		public void setPointer(Pointer p) {
			throw new UnsupportedOperationException("Immutable reference");
		}
	};
}
