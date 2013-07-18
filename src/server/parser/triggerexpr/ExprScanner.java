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
import java.math.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class ExprScanner implements de.independit.scheduler.server.parser.triggerexpr.ExprParser.yyInput
{

	public static final int YYEOF = -1;

	private static final int ZZ_BUFFERSIZE = 16384;

	public static final int YYINITIAL = 0;
	public static final int DQSTRING = 1;

	private static final char [] ZZ_CMAP = {
		0,  0,  0,  0,  0,  0,  0,  0,  3,  3, 36,  0,  0,  3,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		3, 17, 32,  1, 33, 21,  0,  0,  0,  0, 22, 13,  0, 20, 12, 19,
		2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  0,  0, 16, 14, 15,  0,
		1,  9, 23, 30, 25,  7,  8,  1,  1, 26,  1,  1, 10, 28, 24, 27,
		29,  1,  5, 11,  4,  6,  1, 31,  1,  1,  1,  0, 37,  0,  0,  1,
		0,  9, 23, 30, 25,  7,  8,  1,  1, 26,  1,  1, 10, 28, 24, 27,
		29,  1,  5, 11,  4,  6,  1, 31,  1,  1,  1, 34,  0, 35, 18,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	};

	private static final int [] ZZ_ACTION = zzUnpackAction();

	private static final String ZZ_ACTION_PACKED_0 =
	        "\2\0\1\1\1\2\1\3\7\1\1\4\1\1\1\5"+
	        "\1\6\1\1\1\7\1\10\1\11\1\12\4\1\1\13"+
	        "\1\1\1\14\1\15\1\14\1\0\1\2\12\0\1\16"+
	        "\1\17\1\20\1\21\1\22\1\23\3\0\1\24\1\25"+
	        "\1\0\1\26\7\0\1\27\1\30\1\0\1\31\1\0"+
	        "\1\32\1\0\1\33\1\0\1\2\1\34\1\35\6\0"+
	        "\1\36\1\37\1\2\1\40\1\0\1\41\1\42\2\0"+
	        "\1\2\2\0\1\43\4\0\1\44\1\45";

	private static int [] zzUnpackAction()
	{
		int [] result = new int[99];
		int offset = 0;
		offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackAction(String packed, int offset, int [] result)
	{
		int i = 0;
		int j = offset;
		int l = packed.length();
		while (i < l) {
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

	private static final String ZZ_ROWMAP_PACKED_0 =
	        "\0\0\0\46\0\114\0\162\0\230\0\276\0\344\0\u010a"+
	        "\0\u0130\0\u0156\0\u017c\0\u01a2\0\114\0\u01c8\0\u01ee\0\u0214"+
	        "\0\u023a\0\114\0\114\0\114\0\114\0\u0260\0\u0286\0\u02ac"+
	        "\0\u02d2\0\114\0\u02f8\0\114\0\114\0\u031e\0\u0344\0\u036a"+
	        "\0\u0390\0\u03b6\0\u03dc\0\u0402\0\u0428\0\u044e\0\u0474\0\u049a"+
	        "\0\u04c0\0\u04e6\0\114\0\114\0\114\0\114\0\114\0\114"+
	        "\0\u050c\0\u0532\0\u0558\0\114\0\u057e\0\u05a4\0\114\0\u05ca"+
	        "\0\u05f0\0\u0616\0\u063c\0\u0662\0\u0688\0\u06ae\0\114\0\114"+
	        "\0\u06d4\0\114\0\u06fa\0\114\0\u0720\0\114\0\u0746\0\u076c"+
	        "\0\114\0\114\0\u0792\0\u07b8\0\u07de\0\u0804\0\u082a\0\u0850"+
	        "\0\114\0\114\0\u0876\0\114\0\u089c\0\114\0\114\0\u08c2"+
	        "\0\u08e8\0\114\0\u090e\0\u0934\0\114\0\u095a\0\u0980\0\u09a6"+
	        "\0\u09cc\0\114\0\114";

	private static int [] zzUnpackRowMap()
	{
		int [] result = new int[99];
		int offset = 0;
		offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackRowMap(String packed, int offset, int [] result)
	{
		int i = 0;
		int j = offset;
		int l = packed.length();
		while (i < l) {
			int high = packed.charAt(i++) << 16;
			result[j++] = high | packed.charAt(i++);
		}
		return j;
	}

	private static final int [] ZZ_TRANS = zzUnpackTrans();

	private static final String ZZ_TRANS_PACKED_0 =
	        "\2\3\1\4\1\5\1\6\1\7\1\10\1\3\1\11"+
	        "\1\12\1\13\1\14\1\3\1\15\1\16\1\17\1\20"+
	        "\1\21\1\3\1\22\1\23\1\24\1\25\1\3\1\26"+
	        "\1\27\1\30\1\31\4\3\1\32\1\33\2\3\1\5"+
	        "\1\3\40\34\1\35\3\34\1\0\1\36\50\0\1\4"+
	        "\4\0\1\37\4\0\1\40\34\0\1\5\40\0\1\5"+
	        "\6\0\1\41\73\0\1\42\47\0\1\43\21\0\1\44"+
	        "\20\0\1\45\42\0\1\46\1\47\50\0\1\50\16\0"+
	        "\1\51\1\0\1\52\55\0\1\53\3\0\1\54\41\0"+
	        "\1\55\45\0\1\56\45\0\1\57\3\0\1\60\56\0"+
	        "\1\61\23\0\1\62\64\0\1\63\22\0\1\64\41\0"+
	        "\1\65\2\0\10\65\13\0\11\65\2\0\1\66\43\0"+
	        "\1\67\22\0\1\70\6\0\1\70\23\0\1\40\4\0"+
	        "\1\37\44\0\1\71\23\0\1\72\21\0\1\73\74\0"+
	        "\1\74\22\0\1\75\42\0\1\76\51\0\1\77\63\0"+
	        "\1\100\53\0\1\101\13\0\1\102\67\0\1\103\22\0"+
	        "\1\104\45\0\1\105\45\0\1\106\42\0\2\65\1\0"+
	        "\10\65\13\0\11\65\7\0\1\107\2\0\10\107\13\0"+
	        "\11\107\10\0\1\110\52\0\1\111\72\0\1\112\41\0"+
	        "\1\113\24\0\1\114\51\0\1\115\44\0\1\116\42\0"+
	        "\1\117\51\0\1\120\41\0\1\121\37\0\2\107\1\0"+
	        "\10\107\13\0\11\107\3\0\1\122\4\0\1\123\74\0"+
	        "\1\124\21\0\1\125\47\0\1\126\67\0\1\127\21\0"+
	        "\1\130\44\0\1\131\43\0\1\132\101\0\1\133\45\0"+
	        "\1\134\14\0\1\135\51\0\1\136\45\0\1\137\47\0"+
	        "\1\140\45\0\1\141\41\0\1\142\45\0\1\143\36\0";

	private static int [] zzUnpackTrans()
	{
		int [] result = new int[2546];
		int offset = 0;
		offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackTrans(String packed, int offset, int [] result)
	{
		int i = 0;
		int j = offset;
		int l = packed.length();
		while (i < l) {
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			value--;
			do result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	private static final int ZZ_UNKNOWN_ERROR = 0;
	private static final int ZZ_NO_MATCH = 1;
	private static final int ZZ_PUSHBACK_2BIG = 2;

	private static final String ZZ_ERROR_MSG[] = {
		"Unkown internal scanner error",
		"Error: could not match input",
		"Error: pushback value was too large"
	};

	private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

	private static final String ZZ_ATTRIBUTE_PACKED_0 =
	        "\2\0\1\11\11\1\1\11\4\1\4\11\4\1\1\11"+
	        "\1\1\2\11\1\1\1\0\1\1\12\0\6\11\3\0"+
	        "\1\11\1\1\1\0\1\11\7\0\2\11\1\0\1\11"+
	        "\1\0\1\11\1\0\1\11\1\0\1\1\2\11\6\0"+
	        "\2\11\1\1\1\11\1\0\2\11\2\0\1\11\2\0"+
	        "\1\11\4\0\2\11";

	private static int [] zzUnpackAttribute()
	{
		int [] result = new int[99];
		int offset = 0;
		offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackAttribute(String packed, int offset, int [] result)
	{
		int i = 0;
		int j = offset;
		int l = packed.length();
		while (i < l) {
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	private java.io.Reader zzReader;

	private int zzState;

	private int zzLexicalState = YYINITIAL;

	private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

	private int zzMarkedPos;

	private int zzPushbackPos;

	private int zzCurrentPos;

	private int zzStartRead;

	private int zzEndRead;

	private int yyline;

	private int yychar;

	private int yycolumn;

	private boolean zzAtBOL = true;

	private boolean zzAtEOF;

	private static boolean debug = false;

	public int yyline()
	{
		return yyline + 1;
	}

	private int token;
	private Object value;
	private boolean eofProhibited = false;

	public boolean advance() throws
		java.io.IOException, CommonErrorException
	{
		token = yylex();
		if(debug) SDMSThread.doTrace(null, "Token : " + token + "\n", SDMSThread.SEVERITY_DEBUG);
		return (token != YYEOF);
	}

	private void proto(String s)
	{
		if(debug) System.out.println(s + "( " + yytext() + " )");
	}

	public int token()
	{
		return token;
	}

	public Object value()
	{
		return value;
	}

	public ExprScanner(java.io.Reader in)
	{
		this.zzReader = in;
	}

	public ExprScanner(java.io.InputStream in)
	{
		this(new java.io.InputStreamReader(in));
	}

	private boolean zzRefill() throws java.io.IOException
	{

		if (zzStartRead > 0) {
			System.arraycopy(zzBuffer, zzStartRead,
			                 zzBuffer, 0,
			                 zzEndRead-zzStartRead);

			zzEndRead-= zzStartRead;
			zzCurrentPos-= zzStartRead;
			zzMarkedPos-= zzStartRead;
			zzPushbackPos-= zzStartRead;
			zzStartRead = 0;
		}

		if (zzCurrentPos >= zzBuffer.length) {

			char newBuffer[] = new char[zzCurrentPos*2];
			System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
			zzBuffer = newBuffer;
		}

		int numRead = zzReader.read(zzBuffer, zzEndRead,
		                            zzBuffer.length-zzEndRead);

		if (numRead < 0) {
			return true;
		} else {
			zzEndRead+= numRead;
			return false;
		}
	}

	public final void yyclose() throws java.io.IOException
	{
		zzAtEOF = true;
		zzEndRead = zzStartRead;

		if (zzReader != null)
			zzReader.close();
	}

	public final void yyreset(java.io.Reader reader)
	{
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
		zzEndRead = zzStartRead = 0;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
		yyline = yychar = yycolumn = 0;
		zzLexicalState = YYINITIAL;
	}

	public final int yystate()
	{
		return zzLexicalState;
	}

	public final void yybegin(int newState)
	{
		zzLexicalState = newState;
	}

	public final String yytext()
	{
		return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
	}

	public final char yycharat(int pos)
	{
		return zzBuffer[zzStartRead+pos];
	}

	public final int yylength()
	{
		return zzMarkedPos-zzStartRead;
	}

	private void zzScanError(int errorCode)
	{
		String message;
		try {
			message = ZZ_ERROR_MSG[errorCode];
		} catch (ArrayIndexOutOfBoundsException e) {
			message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
		}

		throw new Error(message);
	}

	public void yypushback(int number)
	{
		if ( number > yylength() )
			zzScanError(ZZ_PUSHBACK_2BIG);

		zzMarkedPos -= number;
	}

	public int yylex() throws java.io.IOException
	{
		int zzInput;
		int zzAction;

		int zzCurrentPosL;
		int zzMarkedPosL;
		int zzEndReadL = zzEndRead;
		char [] zzBufferL = zzBuffer;
		char [] zzCMapL = ZZ_CMAP;

		int [] zzTransL = ZZ_TRANS;
		int [] zzRowMapL = ZZ_ROWMAP;
		int [] zzAttrL = ZZ_ATTRIBUTE;

		while (true) {
			zzMarkedPosL = zzMarkedPos;

			boolean zzR = false;
			for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
			     zzCurrentPosL++) {
				switch (zzBufferL[zzCurrentPosL]) {
				case '\u000B':
				case '\u000C':
				case '\u0085':
				case '\u2028':
				case '\u2029':
					yyline++;
					zzR = false;
					break;
				case '\r':
					yyline++;
					zzR = true;
					break;
				case '\n':
					if (zzR)
						zzR = false;
					else {
						yyline++;
					}
					break;
				default:
					zzR = false;
				}
			}

			if (zzR) {

				boolean zzPeek;
				if (zzMarkedPosL < zzEndReadL)
					zzPeek = zzBufferL[zzMarkedPosL] == '\n';
				else if (zzAtEOF)
					zzPeek = false;
				else {
					boolean eof = zzRefill();
					zzMarkedPosL = zzMarkedPos;
					zzBufferL = zzBuffer;
					if (eof)
						zzPeek = false;
					else
						zzPeek = zzBufferL[zzMarkedPosL] == '\n';
				}
				if (zzPeek) yyline--;
			}
			zzAction = -1;

			zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

			zzState = zzLexicalState;

			zzForAction: {
				while (true) {

					if (zzCurrentPosL < zzEndReadL)
						zzInput = zzBufferL[zzCurrentPosL++];
					else if (zzAtEOF) {
						zzInput = YYEOF;
						break zzForAction;
					} else {

						zzCurrentPos  = zzCurrentPosL;
						zzMarkedPos   = zzMarkedPosL;
						boolean eof = zzRefill();

						zzCurrentPosL  = zzCurrentPos;
						zzMarkedPosL   = zzMarkedPos;
						zzBufferL      = zzBuffer;
						zzEndReadL     = zzEndRead;
						if (eof) {
							zzInput = YYEOF;
							break zzForAction;
						} else {
							zzInput = zzBufferL[zzCurrentPosL++];
						}
					}
					int zzNext = zzTransL[ zzRowMapL[zzState] + (((zzInput >= 0) && (zzInput < zzCMapL.length)) ? zzCMapL[zzInput] : 0) ];
					if (zzNext == -1) break zzForAction;
					zzState = zzNext;

					int zzAttributes = zzAttrL[zzState];
					if ( (zzAttributes & 1) == 1 ) {
						zzAction = zzState;
						zzMarkedPosL = zzCurrentPosL;
						if ( (zzAttributes & 8) == 8 ) break zzForAction;
					}

				}
			}

			zzMarkedPos = zzMarkedPosL;

			switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
			case 30: {
				proto("DATE ");
				return ExprParser.DATE;
			}
			case 25: {
				proto("STR ");
				return ExprParser.STR;
			}
			case 7: {
				proto("/ ");
				return ExprParser.DIV;
			}
			case 15: {
				proto("=~ ");
				return ExprParser.MATCHES;
			}
			case 12: {
				proto("CHAR ");
				value = value + yytext();
				break;
			}
			case 8: {
				proto("- ");
				return ExprParser.MINUS;
			}
			case 27: {
				proto("INT ");
				return ExprParser.INT;
			}
			case 17: {
				proto("<= ");
				return ExprParser.LE;
			}
			case 9: {
				proto("% ");
				return ExprParser.MOD;
			}
			case 3: {
				proto("WS ");
				break;
			}
			case 28: {
				proto("TRUE ");
				value = Boolean.TRUE;
				return ExprParser.BOOLEAN;
			}
			case 33: {
				proto("FALSE ");
				value = Boolean.FALSE;
				return ExprParser.BOOLEAN;
			}
			case 37: {
				proto("LOWERCASE ");
				return ExprParser.LOWERCASE;
			}
			case 20: {
				proto("OR ");
				return ExprParser.OR;
			}
			case 18: {
				proto("!= ");
				return ExprParser.NE;
			}
			case 11: {
				proto("DQSTRING_START ");
				value = new String();
				yybegin(DQSTRING);
				break;
			}
			case 5: {
				proto("> ");
				return ExprParser.GT;
			}
			case 26: {
				proto("NOT ");
				return ExprParser.NOT;
			}
			case 32: {
				proto("ROUND ");
				return ExprParser.ROUND;
			}
			case 35: {
				proto("SUBSTR ");
				return ExprParser.SUBSTR;
			}
			case 6: {
				proto("< ");
				return ExprParser.LT;
			}
			case 16: {
				proto(">= ");
				return ExprParser.GE;
			}
			case 2: {
				proto("NUMBER ");
				value = new BigDecimal(yytext());
				return ExprParser.NUMBER;
			}
			case 29: {
				proto("TRIM ");
				return ExprParser.TRIM;
			}
			case 22: {
				proto("\\\" ");
				value = value + yytext();
				break;
			}
			case 36: {
				proto("UPPERCASE ");
				return ExprParser.UPPERCASE;
			}
			case 31: {
				proto("PARAM ");
				value = (yytext().substring(2, yytext().length() - 1)).toUpperCase();
				return ExprParser.PARAMETER;
			}
			case 1: {
				proto("CHAR ");
				return yytext().charAt(0);
			}
			case 24: {
				proto("AND ");
				return ExprParser.AND;
			}
			case 21: {
				proto("PARAM ");
				value = (yytext().substring(1)).toUpperCase();
				return ExprParser.PARAMETER;
			}
			case 4: {
				proto("+ ");
				return ExprParser.PLUS;
			}
			case 13: {
				proto("DQSTRING_END ");
				yybegin(YYINITIAL);
				return ExprParser.STRING;
			}
			case 23: {
				proto("ABS ");
				return ExprParser.ABS;
			}
			case 10: {
				proto("* ");
				return ExprParser.TIMES;
			}
			case 19: {
				proto("!~ ");
				return ExprParser.NOMATCH;
			}
			case 34: {
				proto("FIELD ");
				return ExprParser.FIELD;
			}
			case 14: {
				proto("== ");
				return ExprParser.EQ;
			}
			default:
				if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
					zzAtEOF = true;
					return YYEOF;
				} else {
					zzScanError(ZZ_NO_MATCH);
				}
			}
		}
	}

}
