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

package de.independit.scheduler.server.util;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class CheckSum
{

	public final static String __version = "@(#) $Id: CheckSum.java,v 2.3.18.1 2013/03/14 10:25:29 ronald Exp $";

	private static final int e11 = 0x4e;
	private static final int e12 = 0x00;
	private static final int e13 = 0x55;
	private static final int e14 = 0xcb;
	private static final int e15 = 0xce;
	private static final int e21 = 0x35;
	private static final int e22 = 0x9d;
	private static final int e23 = 0xdf;
	private static final int e24 = 0xc1;
	private static final int e25 = 0xcc;
	private static final int e31 = 0xa3;
	private static final int e32 = 0x9b;
	private static final int e33 = 0xda;
	private static final int e34 = 0x50;
	private static final int e35 = 0x67;
	private static final int e41 = 0xfc;
	private static final int e42 = 0x06;
	private static final int e43 = 0x7c;
	private static final int e44 = 0xaa;
	private static final int e45 = 0xcf;
	private static final int e51 = 0xa6;
	private static final int e52 = 0x8c;
	private static final int e53 = 0x1e;
	private static final int e54 = 0xdf;
	private static final int e55 = 0xd2;

	private static final int c1  = 0x93b6ddc3;
	private static final int c2  = 0xbef0935a;
	private static final int c3  = 0x04bea48a;
	private static final int c4  = 0x506dc8ba;
	private static final int c5  = 0x95bfaad1;

	private static final int r11 = 0x19f4;
	private static final int r12 = 0x64e6;
	private static final int r13 = 0x23e9;
	private static final int r14 = 0x81be;
	private static final int r15 = 0x8e4e;

	private static final long c6 = 0x776c56205dccbbbeL;
	private static final long c7 = 0x60c78c40e357bb3aL;
	private static final long c8 = 0x4a996ff889e4d157L;
	private static final long c9 = 0x16790ef2f805650aL;
	private static final long c0 = 0x20ced33013b70193L;

	private static final long p = 100000000000039L;

	private final static byte[] PADBYTES = { (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	                                         (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
	                                       };

	private final static int BASEA = 0x67452301;
	private final static int BASEB = 0xefcdab89;
	private final static int BASEC = 0x98badcfe;
	private final static int BASED = 0x10325476;

	private final static int[] T = { 0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
	                                 0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
	                                 0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
	                                 0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
	                                 0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
	                                 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
	                                 0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
	                                 0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
	                                 0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
	                                 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
	                                 0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
	                                 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
	                                 0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
	                                 0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
	                                 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
	                                 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
	                               };

	private final static int R[][] = {{ 7, 12, 17, 22 }, { 5, 9, 14, 20 }, { 4, 11, 16, 23 }, { 6, 10, 15, 21 }};

	private final static long MAX_INT  = 0xffffffffL;
	private final static int  MAX_BYTE = 0xff;

	private final static hash[] hf = { new F(), new G(), new H(), new I() };

	private final static int[][] seq = { 	{ 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15},
		{ 1,  6, 11,  0,  5, 10, 15,  4,  9, 14,  3,  8, 13,  2,  7, 12},
		{ 5,  8, 11, 14,  1,  4,  7, 10, 13,  0,  3,  6,  9, 12, 15,  2},
		{ 0,  7, 14,  5, 12,  3, 10,  1,  8, 15,  6, 13,  4, 11,  2,  9}
	};

	private final static int[][] perm = {	{ 0, 1, 2, 3 }, { 3, 0, 1, 2 }, { 2, 3, 0, 1 }, { 1, 2, 3, 0 } };

	private final static int[] pad(byte[] in)
	{
		int b;
		long s = in.length;
		int l = (int) (56 - s&0x3f);
		if(l <= 0) l+=64;
		int m = (int) ((s+l+8)/4);

		int[] out = new int[m];
		for(int i = 0; i < m - 2; i++) {
			out[i] = encode(in, i*4);
		}
		s <<= 3;
		out[m - 2] = (int) (s&MAX_INT);
		out[m - 1] = (int) ((s>>32)&MAX_INT);
		return out;
	}

	private final static int rotate_left(int a, int num)
	{
		return (a<<num | a>>>(32 - num));
	}

	private final static int mf(int a, int b, int c, int d, int k, int s, int i, hash h)
	{
		return (b + rotate_left(a + h.dohash(b,c,d) + k + T[i], s));
	}

	private final static int encode(byte[] in, int offset)
	{
		int result = 0;
		final int s = in.length;
		int b;
		for(int j = 0; j < 4; j++) {
			int i = offset + j;
			if(i < s)	b = in[i];
			else		b = PADBYTES[i - s];
			b&=MAX_BYTE;
			b <<= j*8;
			result |= b;
		}
		return result;
	}

	private final static byte[] decode(int[] in)
	{
		final int s = in.length;
		byte[] out = new byte[s*4];
		int i, j;

		for(i = 0; i < s; i++) {
			j = i*4;
			out[j] = (byte)(in[i] & MAX_BYTE);
			out[j+1] = (byte) ((in[i]>>8) & MAX_BYTE);
			out[j+2] = (byte) ((in[i]>>16) & MAX_BYTE);
			out[j+3] = (byte) ((in[i]>>24) & MAX_BYTE);
		}
		return out;
	}

	public final static byte[] md5(byte[] msg)
	{
		int[] X = new int[16];
		int[] m;
		int[] out = new int[4];
		int[] ws = new int[4];
		int i, j, k, r;

		out[0] = BASEA;
		out[1] = BASEB;
		out[2] = BASEC;
		out[3] = BASED;

		m = pad(msg);

		int max_i = m.length / 16;
		for(i = 0; i < max_i; i++) {
			for(j = 0; j < 16; j++) {
				X[j] = m[i*16 + j];
			}
			for(j=0; j < 4; j++) ws[j] = out[j];

			for(r = 0; r < 4; r++) {
				for(j = 0; j < 4; j++) {
					for(k = 0; k < 4; k++) {
						ws[perm[k][0]] = mf(ws[perm[k][0]],
						                    ws[perm[k][1]],
						                    ws[perm[k][2]],
						                    ws[perm[k][3]],
						                    X[seq[r][j*4+k]],
						                    R[r][k],
						                    r*16+j*4+k,
						                    hf[r]);
					}
				}
			}

			for(j=0; j < 4; j++) out[j] += ws[j];
		}

		return decode(out);
	}

	public final static String mkstr(int[] a)
	{
		byte[] b = decode(a);
		return mkstr(b);
	}

	public final static String mkstr(byte[] b)
	{
		StringBuffer sb = new StringBuffer(b.length * 3);

		for(int i = 0; i < b.length; i++) {
			if((i != 0) && ((i)%4 == 0))
				sb.append(' ');
			if (b[i] <= 0x0f && b[i] >= 0) {
				sb.append('0');
				sb.append(Integer.toHexString(b[i] & MAX_BYTE));
			} else {
				sb.append(Integer.toHexString(b[i] & MAX_BYTE));
			}
		}
		return new String(sb);
	}

	private static long pow(int b, int e)
	{
		long result = 1;
		long tmp = b;

		while(e > 0) {
			if((e&0x1) == 0) {
				result *= tmp;
				result %= p;
			}
			e >>= 1;
			tmp *= tmp;
			tmp %= p;
		}

		return result;
	}

	public final static long fastchksum(int x1, int x2, int x3, int x4)
	{
		long r1 = (c1 * (pow(x1, e11) - pow(r11^x2, e12) ^ pow(x3, e13) * pow(x1, e14) ^ pow(e11^x4, e15)))%c6;
		long r2 = (c2 * (pow(x1, e21) ^ pow(r12^x2, e22) - pow(x3, e23) ^ pow(x2, e24) * pow(e22^x4, e25)))%c7;
		long r3 = (c3 * (pow(x1, e31) * pow(r13^x2, e32) ^ pow(x3, e33) - pow(x3, e34) ^ pow(e33^x4, e35)))%c8;
		long r4 = (c4 * (pow(x1, e41) ^ pow(r14^x2, e42) * pow(x3, e43) ^ pow(x4, e44) - pow(e44^x4, e45)))%c9;
		long r5 = (c5 * (pow(x1, e51) + pow(r15^x2, e52) ^ pow(x3, e53) + pow(x1, e54) ^ pow(e55^x4, e55)))%c0;

		return r1 ^ r2 ^ r3 ^ r4 ^ r5;
	}

	private CheckSum()
	{

	}
}

interface hash
{
	public int dohash(int x, int y, int z);
}

class F implements hash
{
	public int dohash(int x, int y, int z)
	{
		return (x&y)|((~x)&z);
	}
}

class G implements hash
{
	public int dohash(int x, int y, int z)
	{
		return (x&z)|(y&(~z));
	}
}

class H implements hash
{
	public int dohash(int x, int y, int z)
	{
		return (x^y)^z;
	}
}

class I implements hash
{
	public int dohash(int x, int y, int z)
	{
		return y^(x|(~z));
	}
}

