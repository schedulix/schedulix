package de.independit.scheduler.server.util;

public class Here {
	public static String at () {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		String where = ste.getClassName() + "->" + ste.getMethodName() + "() at " + ste.getLineNumber() + " : ";
		return where;
	}

	public static String atc () {	// at condensed / caller
		StackTraceElement st[] = Thread.currentThread().getStackTrace();
		StackTraceElement sth = st[3];
		StackTraceElement ste = null;
		StackTraceElement stec = null;
		if (st.length > 3) {
			ste = st[4];
			if (st.length > 4)
				stec = st[5];
		}
			
		String where = (stec != null ? stec.getMethodName() + "(" + stec.getLineNumber() + ")" : "null" ) + 
			       (ste != null ? " -> " + ste.getMethodName() + "(" + ste.getLineNumber() + ")" : "null") + " -> " +
			       sth.getMethodName() + "(" + sth.getLineNumber() + ") => ";
		return where;
	}
}

