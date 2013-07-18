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


package de.independit.scheduler.server.parser.triggerexpr;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.math.*;
import java.text.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.*;

public class ExprParser
{

	private int stop = 0;
	private SDMSSubmittedEntity sme = null;
	private SDMSResource r = null;
	private SDMSTrigger t = null;
	private SDMSTriggerQueue tq = null;
	private SystemEnvironment sysEnv = null;
	private SDMSScope evalScope = null;
	public boolean checkOnly = false;

	public static final String S_TIMES_CHECKED = "TIMES_CHECKED";
	public static final String S_TIMES_FIRED   = "TIMES_FIRED";

	public static final int STRING = 257;
	public static final int IDENTIFIER = 258;
	public static final int PARAMETER = 259;
	public static final int NUMBER = 260;
	public static final int BOOLEAN = 261;
	public static final int EQ = 262;
	public static final int GT = 263;
	public static final int GE = 264;
	public static final int LT = 265;
	public static final int LE = 266;
	public static final int NE = 267;
	public static final int MATCHES = 268;
	public static final int NOMATCH = 269;
	public static final int DIV = 270;
	public static final int MINUS = 271;
	public static final int MOD = 272;
	public static final int PLUS = 273;
	public static final int TIMES = 274;
	public static final int ABS = 275;
	public static final int AND = 276;
	public static final int DATE = 277;
	public static final int FIELD = 278;
	public static final int INT = 279;
	public static final int LOWERCASE = 280;
	public static final int NOT = 281;
	public static final int OR = 282;
	public static final int ROUND = 283;
	public static final int SUBSTR = 284;
	public static final int STR = 285;
	public static final int TRIM = 286;
	public static final int UPPERCASE = 287;
	public static final int yyErrorCode = 256;

	public static class yyException extends java.lang.Exception
	{
		public yyException (String message)
		{
			super(message);
		}
	}

	public interface yyInput
	{

		boolean advance () throws java.io.IOException, CommonErrorException;

		int token ();

		Object value ();
	}

	public void yyerror (String message)
	{
		yyerror(message, null);
	}

	public void yyerror (String message, String[] expected)
	{
		if (expected != null && expected.length > 0) {
			System.err.print(message+", expecting");
			for (int n = 0; n < expected.length; ++ n)
				System.err.print(" "+expected[n]);
			System.err.println();
		} else {
			System.err.println(message);
		}
	}

	protected static final int yyFinal = 19;

	protected String[] yyExpecting (int state)
	{
		int token, n, len = 0;
		boolean[] ok = new boolean[YyNameClass.yyName.length];

		if ((n = YySindexClass.yySindex[state]) != 0)
			for (token = n < 0 ? -n : 0;
			     token < YyNameClass.yyName.length && n+token < YyTableClass.yyTable.length; ++ token) {
				if (YyCheckClass.yyCheck[n+token] == token && !ok[token] && YyNameClass.yyName[token] != null) {
					++ len;
					ok[token] = true;
				}
			}
		if ((n = YyRindexClass.yyRindex[state]) != 0)
			for (token = n < 0 ? -n : 0;
			     token < YyNameClass.yyName.length && n+token < YyTableClass.yyTable.length; ++ token) {
				if (YyCheckClass.yyCheck[n+token] == token && !ok[token] && YyNameClass.yyName[token] != null) {
					++ len;
					ok[token] = true;
				}
			}

		String result[] = new String[len];
		for (n = token = 0; n < len;  ++ token)
			if (ok[token]) result[n++] = YyNameClass.yyName[token];
		return result;
	}

	public Object yyparse (yyInput yyLex, Object yydebug)
	throws java.io.IOException, yyException, SDMSException
	{

		return yyparse(yyLex);
	}

	protected int yyMax;

	protected Object yyDefault (Object first)
	{
		return first;
	}

	public Object yyparse (yyInput yyLex)
	throws java.io.IOException, yyException, SDMSException
	{
		if (yyMax <= 0) yyMax = 256;
		int yyState = 0, yyStates[] = new int[yyMax];
		Object yyVal = null, yyVals[] = new Object[yyMax];
		int yyToken = -1;
		int yyErrorFlag = 0;

		yyLoop:	for (int yyTop = 0;; ++ yyTop) {
			if (yyTop >= yyStates.length) {
				int[] i = new int[yyStates.length+yyMax];
				System.arraycopy(yyStates, 0, i, 0, yyStates.length);
				yyStates = i;
				Object[] o = new Object[yyVals.length+yyMax];
				System.arraycopy(yyVals, 0, o, 0, yyVals.length);
				yyVals = o;
			}
			yyStates[yyTop] = yyState;
			yyVals[yyTop] = yyVal;

			yyDiscarded:	for (;;) {
				int yyN;
				if ((yyN = YyDefRedClass.yyDefRed[yyState]) == 0) {
					if (yyToken < 0) {
						yyToken = yyLex.advance() ? yyLex.token() : 0;

					}
					if ((yyN = YySindexClass.yySindex[yyState]) != 0 &&
					    (yyN += yyToken) >= 0 &&
					    yyN < YyTableClass.yyTable.length &&
					    YyCheckClass.yyCheck[yyN] == yyToken) {

						yyState = YyTableClass.yyTable[yyN];
						yyVal = yyLex.value();
						yyToken = -1;
						if (yyErrorFlag > 0) -- yyErrorFlag;
						continue yyLoop;
					}
					if ((yyN = YyRindexClass.yyRindex[yyState]) != 0 &&
					    (yyN += yyToken) >= 0 &&
					    yyN < YyTableClass.yyTable.length &&
					    YyCheckClass.yyCheck[yyN] == yyToken)
						yyN = YyTableClass.yyTable[yyN];
					else
						switch (yyErrorFlag) {

						case 0:
							yyerror("syntax error", yyExpecting(yyState));

						case 1:
						case 2:
							yyErrorFlag = 3;
							do {
								if ((yyN = YySindexClass.yySindex[yyStates[yyTop]]) != 0 &&
								    (yyN += yyErrorCode) >= 0 &&
								    yyN < YyTableClass.yyTable.length &&
								    YyCheckClass.yyCheck[yyN] == yyErrorCode) {

									yyState = YyTableClass.yyTable[yyN];
									yyVal = yyLex.value();
									continue yyLoop;
								}

							} while (-- yyTop >= 0);

							throw new yyException("irrecoverable syntax error");

						case 3:
							if (yyToken == 0) {

								throw new yyException("irrecoverable syntax error at end-of-file");
							}

							yyToken = -1;
							continue yyDiscarded;
						}
				}
				int yyV = yyTop + 1-YyLenClass.yyLen[yyN];

				yyVal = yyDefault(yyV > yyTop ? null : yyVals[yyV]);
				switch (yyN) {
				case 1:

				{
					if(stop == 0) return new Boolean(true);
				}
				break;
				case 2:

				{
					if(stop == 0) return(((Boolean)yyVals[0+yyTop]));
				}
				break;
				case 3:

				{
					yyVal = ((Boolean)yyVals[0+yyTop]);
				}
				break;
				case 4:

				{
					yyVal = new Boolean(((Boolean)yyVals[-2+yyTop]).booleanValue() || ((Boolean)yyVals[0+yyTop]).booleanValue());
				}
				break;
				case 5:

				{
					yyVal = ((Boolean)yyVals[0+yyTop]);
				}
				break;
				case 6:

				{
					yyVal = new Boolean(((Boolean)yyVals[-2+yyTop]).booleanValue() && ((Boolean)yyVals[0+yyTop]).booleanValue());
				}
				break;
				case 7:

				{
					yyVal = ((Boolean)yyVals[-1+yyTop]);
				}
				break;
				case 8:

				{
					yyVal = new Boolean(! ((Boolean)yyVals[0+yyTop]).booleanValue());
				}
				break;
				case 9:

				{
					yyVal = ((ParseObject)yyVals[-2+yyTop]).compare(((ParseObject)yyVals[0+yyTop]), ((Integer)yyVals[-1+yyTop]), checkOnly);
				}
				break;
				case 10:

				{
					yyVal = ((Boolean)yyVals[0+yyTop]);
				}
				break;
				case 11:

				{
					yyVal = ((ParseObject)yyVals[0+yyTop]);
				}
				break;
				case 12:

				{
					yyVal = ((ParseObject)yyVals[-2+yyTop]).add(((ParseObject)yyVals[0+yyTop]), ((Integer)yyVals[-1+yyTop]), checkOnly);
				}
				break;
				case 13:

				{
					yyVal = ((ParseObject)yyVals[0+yyTop]);
				}
				break;
				case 14:

				{
					yyVal = ((ParseObject)yyVals[-2+yyTop]).mult(((ParseObject)yyVals[0+yyTop]), ((Integer)yyVals[-1+yyTop]), checkOnly);
				}
				break;
				case 15:

				{
					yyVal = new Integer(PLUS);
				}
				break;
				case 16:

				{
					yyVal = new Integer(MINUS);
				}
				break;
				case 17:

				{
					yyVal = new Integer(TIMES);
				}
				break;
				case 18:

				{
					yyVal = new Integer(DIV);
				}
				break;
				case 19:

				{
					yyVal = new Integer(MOD);
				}
				break;
				case 20:

				{
					yyVal = new Integer(GT);
				}
				break;
				case 21:

				{
					yyVal = new Integer(GE);
				}
				break;
				case 22:

				{
					yyVal = new Integer(LT);
				}
				break;
				case 23:

				{
					yyVal = new Integer(LE);
				}
				break;
				case 24:

				{
					yyVal = new Integer(EQ);
				}
				break;
				case 25:

				{
					yyVal = new Integer(NE);
				}
				break;
				case 26:

				{
					yyVal = new Integer(MATCHES);
				}
				break;
				case 27:

				{
					yyVal = new Integer(NOMATCH);
				}
				break;
				case 28:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]);
				}
				break;
				case 29:

				{
					yyVal = ((ParseObject)yyVals[0+yyTop]).changeSign(((Integer)yyVals[-1+yyTop]), checkOnly);
				}
				break;
				case 30:

				{
					yyVal = resolve(((String)yyVals[0+yyTop]));
				}
				break;
				case 31:

				{
					yyVal = ((ParseObject)yyVals[0+yyTop]);
				}
				break;
				case 32:

				{
					yyVal = ((ParseObject)yyVals[0+yyTop]);
				}
				break;
				case 33:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).abs(checkOnly);
				}
				break;
				case 34:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).to_int(checkOnly);
				}
				break;
				case 35:

				{
					yyVal = ((ParseObject)yyVals[-3+yyTop]).to_date(((ParseObject)yyVals[-1+yyTop]), checkOnly);
				}
				break;
				case 36:

				{
					yyVal = field(new ParseObject(((String)yyVals[-1+yyTop])));
				}
				break;
				case 37:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).lowercase(checkOnly);
				}
				break;
				case 38:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).round(checkOnly);
				}
				break;
				case 39:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).adjust(ParseObject.STRING, checkOnly);
				}
				break;
				case 40:

				{
					yyVal = ((ParseObject)yyVals[-5+yyTop]).substr(((ParseObject)yyVals[-3+yyTop]), ((ParseObject)yyVals[-1+yyTop]), checkOnly);
				}
				break;
				case 41:

				{
					yyVal = ((ParseObject)yyVals[-3+yyTop]).substr(((ParseObject)yyVals[-1+yyTop]), null, checkOnly);
				}
				break;
				case 42:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).trim(checkOnly);
				}
				break;
				case 43:

				{
					yyVal = ((ParseObject)yyVals[-1+yyTop]).uppercase(checkOnly);
				}
				break;
				case 44:

				{
					yyVal = new ParseObject(((BigDecimal)yyVals[0+yyTop]));
				}
				break;
				case 45:

				{
					yyVal = new ParseObject(((String)yyVals[0+yyTop]));
				}
				break;

				}
				yyTop -= YyLenClass.yyLen[yyN];
				yyState = yyStates[yyTop];
				int yyM = YyLhsClass.yyLhs[yyN];
				if (yyState == 0 && yyM == 0) {

					yyState = yyFinal;
					if (yyToken < 0) {
						yyToken = yyLex.advance() ? yyLex.token() : 0;

					}
					if (yyToken == 0) {

						return yyVal;
					}
					continue yyLoop;
				}
				if ((yyN = YyGindexClass.yyGindex[yyM]) != 0 &&
				    (yyN += yyState) >= 0 &&
				    yyN < YyTableClass.yyTable.length && YyCheckClass.yyCheck[yyN] == yyState)
					yyState = YyTableClass.yyTable[yyN];
				else
					yyState = YyDgotoClass.yyDgoto[yyM];

				continue yyLoop;
			}
		}
	}

	protected static final class YyLhsClass
	{

		public static final short yyLhs [] = {              -1,
		                                                    0,    0,    8,    8,    7,    7,    6,    6,    6,    6,
		                                                    1,    1,    5,    5,    9,    9,   10,   10,   10,   11,
		                                                    11,   11,   11,   11,   11,   11,   11,    4,    4,    4,
		                                                    4,    4,    2,    2,    2,    2,    2,    2,    2,    2,
		                                                    2,    2,    2,    3,    3,
		                                     };
	}

	protected static final class YyLenClass
	{

		public static final short yyLen [] = {           2,
		                                                 0,    1,    1,    3,    1,    3,    3,    2,    3,    1,
		                                                 1,    3,    1,    3,    1,    1,    1,    1,    1,    1,
		                                                 1,    1,    1,    1,    1,    1,    1,    3,    2,    1,
		                                                 1,    1,    4,    4,    6,    4,    4,    4,    4,    8,
		                                                 6,    4,    4,    1,    1,
		                                     };
	}

	protected static final class YyDefRedClass
	{

		public static final short yyDefRed [] = {            0,
		                                                     45,   30,   44,   10,   16,   15,    0,    0,    0,    0,
		                                                     0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                     32,   31,   13,    0,    5,    0,    0,    0,    0,    0,
		                                                     0,    0,    0,    8,    0,    0,    0,    0,    0,    0,
		                                                     0,   24,   20,   21,   22,   23,   25,   26,   27,    0,
		                                                     0,   18,   19,   17,    0,    0,    0,    0,   29,    0,
		                                                     0,    0,    0,    0,    0,    0,    0,    0,    0,   28,
		                                                     7,    0,    0,   14,    6,    0,    0,   33,    0,   36,
		                                                     34,   37,   38,    0,   39,   42,   43,    0,    0,   35,
		                                                     41,    0,    0,   40,
		                                        };
	}

	protected static final class YyDgotoClass
	{

		public static final short yyDgoto [] = {            19,
		                                                    20,   21,   22,   23,   24,   25,   26,   27,   28,   55,
		                                                    51,
		                                       };
	}

	protected static final class YySindexClass
	{

		public static final short yySindex [] = {          -40,
		                                                   0,    0,    0,    0,    0,    0,  -35,  -28,  -25,  -23,
		                                                   -19,  -40,  -14,  -10,   -8,   -7,   -6,  -40,    0, -124,
		                                                   0,    0,    0, -261,    0, -260, -255,   42,   42,   42,
		                                                   -223,   42,   42,    0,   42,   42,   42,   42,   42,  -39,
		                                                   -34,    0,    0,    0,    0,    0,    0,    0,    0,   42,
		                                                   42,    0,    0,    0,   42,  -40,  -40,   42,    0,  -22,
		                                                   35,   -5,  -17,  -16,  -13,   36,  -12,    2,    5,    0,
		                                                   0, -261, -265,    0,    0, -260,    8,    0,   42,    0,
		                                                   0,    0,    0,   42,    0,    0,    0,    9,  -21,    0,
		                                                   0,   42,   32,    0,
		                                        };
	}

	protected static final class YyRindexClass
	{

		public static final short yyRindex [] = {           37,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    1,    0,   10,   38,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,   22,   18,    0,    0,   14,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,
		                                        };
	}

	protected static final class YyGindexClass
	{

		public static final short yyGindex [] = {            0,
		                                                     39,    0,    0,  -24,  -11,   -9,   -4,   23,   44,    0,
		                                                     0,
		                                        };
	}

	protected static final class YyTableClass
	{

		public static final short yyTable [] = {            18,
		                                                    11,   70,   34,   59,   29,    5,   71,    6,   52,    3,
		                                                    53,   30,   54,    4,   31,   56,   32,    9,   78,   91,
		                                                    33,   12,   92,   81,   82,   35,   57,   83,   85,   36,
		                                                    74,   37,   38,   39,   62,   80,    1,    2,   72,    0,
		                                                    41,   11,   86,    0,   11,   87,   75,    0,   70,   90,
		                                                    3,    0,   76,    0,    4,    0,   40,    0,    9,    0,
		                                                    0,    0,   12,   50,    0,   12,    0,   60,   61,    0,
		                                                    63,   64,   94,   65,   66,   67,   68,   69,   79,   84,
		                                                    0,   58,    0,   50,    0,    0,    0,    0,    0,   73,
		                                                    0,    0,    0,    0,    0,    0,   77,    0,    0,    0,
		                                                    0,    0,    0,   50,   50,    0,   50,   50,   50,   50,
		                                                    50,   50,   50,    0,    0,    0,   50,   88,    0,    0,
		                                                    50,    0,   89,    0,    0,    0,    0,    0,    0,    0,
		                                                    93,   50,   50,    0,    0,    0,   50,   42,   43,   44,
		                                                    45,   46,   47,   48,   49,    0,    5,    0,    6,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		                                                    0,    0,    0,    0,    0,    0,    1,    0,    2,    3,
		                                                    4,    0,   42,   43,   44,   45,   46,   47,   48,   49,
		                                                    5,    5,    6,    6,    7,    0,    8,    9,   10,   11,
		                                                    12,    0,   13,   14,   15,   16,   17,   57,    5,    5,
		                                                    6,    6,    0,    5,    5,    6,    6,    5,    5,    6,
		                                                    6,    0,   11,   11,   11,   11,   11,   11,   11,   11,
		                                                    0,   11,    5,   11,    6,    5,   11,    6,    5,    5,
		                                                    6,    6,   11,   12,   12,   12,   12,   12,   12,   12,
		                                                    12,    3,   12,    9,   12,    4,    0,   12,    1,    9,
		                                                    2,    3,    5,   12,    6,    5,    5,    6,    6,    0,
		                                                    0,    0,    5,    0,    6,    0,    7,    0,    8,    9,
		                                                    10,   11,    0,    0,   13,   14,   15,   16,   17,
		                                       };
	}

	protected static final class YyCheckClass
	{

		public static final short yyCheck [] = {            40,
		                                                    0,   41,   12,   28,   40,  271,   41,  273,  270,    0,
		                                                    272,   40,  274,    0,   40,  276,   40,    0,   41,   41,
		                                                    40,    0,   44,   41,   41,   40,  282,   41,   41,   40,
		                                                    55,   40,   40,   40,  258,   41,    0,    0,   50,   -1,
		                                                    18,   41,   41,   -1,   44,   41,   56,   -1,   41,   41,
		                                                    41,   -1,   57,   -1,   41,   -1,   18,   -1,   41,   -1,
		                                                    -1,   -1,   41,   20,   -1,   44,   -1,   29,   30,   -1,
		                                                    32,   33,   41,   35,   36,   37,   38,   39,   44,   44,
		                                                    -1,   40,   -1,   40,   -1,   -1,   -1,   -1,   -1,   51,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   58,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   60,   61,   -1,   63,   64,   65,   66,
		                                                    67,   68,   69,   -1,   -1,   -1,   73,   79,   -1,   -1,
		                                                    77,   -1,   84,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    92,   88,   89,   -1,   -1,   -1,   93,  262,  263,  264,
		                                                    265,  266,  267,  268,  269,   -1,  271,   -1,  273,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
		                                                    -1,   -1,   -1,   -1,   -1,   -1,  257,   -1,  259,  260,
		                                                    261,   -1,  262,  263,  264,  265,  266,  267,  268,  269,
		                                                    271,  271,  273,  273,  275,   -1,  277,  278,  279,  280,
		                                                    281,   -1,  283,  284,  285,  286,  287,  282,  271,  271,
		                                                    273,  273,   -1,  271,  271,  273,  273,  271,  271,  273,
		                                                    273,   -1,  262,  263,  264,  265,  266,  267,  268,  269,
		                                                    -1,  271,  271,  273,  273,  271,  276,  273,  271,  271,
		                                                    273,  273,  282,  262,  263,  264,  265,  266,  267,  268,
		                                                    269,  282,  271,  276,  273,  282,   -1,  276,  257,  282,
		                                                    259,  260,  271,  282,  273,  271,  271,  273,  273,   -1,
		                                                    -1,   -1,  271,   -1,  273,   -1,  275,   -1,  277,  278,
		                                                    279,  280,   -1,   -1,  283,  284,  285,  286,  287,
		                                       };
	}

	protected static final class YyNameClass
	{

		public static final String yyName [] = {
			"end-of-file",null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			"'('","')'",null,null,"','",null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,"STRING","IDENTIFIER",
			"PARAMETER","NUMBER","BOOLEAN","EQ","GT","GE","LT","LE","NE",
			"MATCHES","NOMATCH","DIV","MINUS","MOD","PLUS","TIMES","ABS","AND",
			"DATE","FIELD","INT","LOWERCASE","NOT","OR","ROUND","SUBSTR","STR",
			"TRIM","UPPERCASE",
		};
	}

	private ParseObject field(ParseObject o)
	{
		if(checkOnly) return new ParseObject(o);
		return o;
	}

	private ParseObject resolve(String o)
	throws SDMSException
	{
		if(checkOnly) return new ParseObject("");
		String p;
		p = resolveTriggerVariable(o);
		if(p != null) return new ParseObject(p);
		if(sme != null)
			p = sme.getVariableValue(sysEnv, o, false, ParseStr.S_LIBERAL, true , evalScope);
		else if (r != null)
			p = r.getVariableValue(sysEnv, o);
		else
			p = "";
		return new ParseObject(p);
	}

	public void set(SystemEnvironment env, SDMSSubmittedEntity sme, SDMSResource rs, SDMSTrigger tr, SDMSTriggerQueue trq, SDMSScope s)
	{
		this.sysEnv = env;
		this.sme = sme;
		this.r = rs;
		this.t = tr;
		this.tq = trq;
		this.evalScope = s;
	}

	public String resolveTriggerVariable(String key)
	{
		if(tq == null) return null;
		try {
			if(key.equals(S_TIMES_CHECKED)) {
				return tq.getTimesChecked(sysEnv).toString();
			}
			if(key.equals(S_TIMES_FIRED)) {
				return tq.getTimesTriggered(sysEnv).toString();
			}
		} catch(SDMSException e) {  }
		return null;
	}

}

class ParseObject
{

	private BigDecimal number;
	private String     string;

	private int type;

	public static final int VOID   = 0;
	public static final int STRING = 1;
	public static final int NUMBER = 2;
	public static final int DATE   = 3;

	public static final BigDecimal one = new BigDecimal("1");

	public ParseObject()
	{
		type = VOID;
		number = null;
		string = null;
	}

	public ParseObject(Object o)
	{
		if(o instanceof BigDecimal) {
			type = NUMBER;
			number = (BigDecimal) o;
			string = null;
		} else if(o instanceof String) {
			type = STRING;
			string = (String) o;
			number = null;
		} else if(o.getClass().getName().endsWith("Something date like")) {
			type = DATE;
			string = null;
			number = null;
		} else {

			System.err.println("Fatal Exception! Unexpected Object: " + o.toString() + "\n");
			System.exit(1);
		}
	}

	public ParseObject(String s)
	{
		type = STRING;
		string = s;
		number = null;
	}

	public ParseObject(BigDecimal d)
	{
		type = NUMBER;
		number = d;
		string = null;
	}

	public ParseObject(ParseObject p)
	{
		type = p.type;
		number = p.number;
		string = p.string;
	}

	public ParseObject adjust(ParseObject po, boolean checkOnly)
	{
		return adjust(po.type, checkOnly);
	}

	public ParseObject adjust(int t, boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		if(type == t) return new ParseObject(this);
		ParseObject rc = new ParseObject();
		switch(t) {
		case STRING:
			rc.type = STRING;
			rc.number = null;
			if(type == NUMBER)	rc.string = number.toString();
			if(type == VOID)	rc.string = new String();
			break;
		case NUMBER:
			rc.type = NUMBER;
			rc.string = null;
			if(type == STRING)	rc.number = new BigDecimal(string);
			if(type == VOID)	rc.number = new BigDecimal("0");
			break;
		}
		return rc;
	}

	public ParseObject mult(ParseObject o2, Integer s, boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		int operator = s.intValue();
		ParseObject op1 = adjust(ParseObject.NUMBER, checkOnly);
		ParseObject op2 = o2.adjust(ParseObject.NUMBER, checkOnly);
		switch(operator) {
		case ExprParser.DIV:
			op1.number = op1.number.divide(op2.number, BigDecimal.ROUND_HALF_UP);
			break;
		case ExprParser.TIMES:
			op1.number = op1.number.multiply(op2.number);
			break;
		case ExprParser.MOD:
			BigInteger n1 = op1.number.toBigInteger();
			BigInteger n2 = op2.number.toBigInteger();
			op1.number= new BigDecimal(n1.mod(n2));
			break;
		}
		return op1;
	}

	public ParseObject add(ParseObject o2, Integer s, boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		int operator = s.intValue();
		if(type == STRING && operator == ExprParser.PLUS) {
			ParseObject str = o2.adjust(STRING, checkOnly);
			str.string = string + str.string;
			return str;
		}
		ParseObject op1 = adjust(NUMBER, checkOnly);
		ParseObject op2 = o2.adjust(NUMBER, checkOnly);
		switch(operator) {
		case ExprParser.PLUS:
			op1.number = op1.number.add(op2.number);
			break;
		case ExprParser.MINUS:
			op1.number = op1.number.add(op2.number.negate());
			break;
		}
		return op1;
	}

	public int compareTo(ParseObject o2, boolean checkOnly)
	{
		ParseObject op2 = o2.adjust(type, checkOnly);
		int val = 0;
		switch(type) {
		case NUMBER:
			val = number.compareTo(op2.number);
			break;
		case STRING:
			val = string.compareTo(op2.string);
			break;
		}
		return val;
	}

	public Boolean compare(ParseObject o2, Integer s, boolean checkOnly)
	{
		if(checkOnly) return Boolean.TRUE;
		int operator = s.intValue();
		if(operator == ExprParser.MATCHES || operator == ExprParser.NOMATCH) {
			ParseObject op1 = adjust(STRING, checkOnly);
			ParseObject op2 = o2.adjust(STRING, checkOnly);
			if(operator == ExprParser.MATCHES && op1.string.matches(op2.string)) return Boolean.TRUE;
			if(operator == ExprParser.NOMATCH && !op1.string.matches(op2.string)) return Boolean.TRUE;
			return Boolean.FALSE;
		} else {
			int val = compareTo(o2, checkOnly);
			switch(operator) {
			case ExprParser.EQ:
				return new Boolean(val == 0);
			case ExprParser.GE:
				return new Boolean(val >= 0);
			case ExprParser.GT:
				return new Boolean(val > 0);
			case ExprParser.LE:
				return new Boolean(val <= 0);
			case ExprParser.LT:
				return new Boolean(val < 0);
			case ExprParser.NE:
				return new Boolean(val != 0);
			}
		}
		return Boolean.TRUE;
	}

	public ParseObject changeSign(Integer s, boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		int operator = s.intValue();
		ParseObject retval = adjust(ParseObject.NUMBER, checkOnly);
		switch(operator) {
		case ExprParser.PLUS:

			break;
		case ExprParser.MINUS:
			retval.number = retval.number.negate();
			break;
		}
		return retval;
	}

	public ParseObject to_int(boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(ParseObject.NUMBER, checkOnly);
		retval.number = new BigDecimal(retval.number.toBigInteger());
		return retval;
	}

	public ParseObject to_date(ParseObject format, boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		if(type == NUMBER) return this.to_int(false);
		if(type == VOID) return this;
		ParseObject retval;
		if(format.type == STRING) {
			try {
				SimpleDateFormat sdf;
				if (format.string != null)
					sdf = new SimpleDateFormat(format.string);
				else
					sdf = (SimpleDateFormat) SystemEnvironment.staticSystemDateFormat.clone();
				Date d = sdf.parse(this.string);
				retval = new ParseObject(new BigDecimal(d.getTime()));
			} catch (ParseException pe) {
				retval = new ParseObject(new BigDecimal(0));
			}
		} else {
			retval = new ParseObject(new BigDecimal(0));
		}
		return retval;
	}

	public ParseObject abs(boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(ParseObject.NUMBER, checkOnly);
		retval.number = retval.number.abs();
		return retval;
	}

	public ParseObject round(boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(ParseObject.NUMBER, checkOnly);
		retval.number = retval.number.divide(one, BigDecimal.ROUND_HALF_UP);
		return retval;
	}

	public ParseObject substr(ParseObject v, ParseObject b, boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(STRING, checkOnly);
		int von = v.adjust(NUMBER, checkOnly).number.intValue();
		if(b != null) {
			int bis = b.adjust(NUMBER, checkOnly).number.intValue();
			retval.string = retval.string.substring(von, bis);
		} else {
			retval.string = retval.string.substring(von);
		}
		return retval;
	}

	public ParseObject trim(boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(STRING, checkOnly);
		retval.string = retval.string.trim();
		return retval;
	}

	public ParseObject uppercase(boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(STRING, checkOnly);
		retval.string = retval.string.toUpperCase();
		return retval;
	}

	public ParseObject lowercase(boolean checkOnly)
	{
		if(checkOnly) return new ParseObject();
		ParseObject retval = adjust(STRING, checkOnly);
		retval.string = retval.string.toLowerCase();
		return retval;
	}
}

