package de.independit.scheduler.server.util;

public class Here {
	public static String at () {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		String where = ste.getClassName() + "->" + ste.getMethodName() + "() at " + ste.getLineNumber() + " : ";
		return where;
	}
}

