package net.ossfree.launcher4.Logger;

import android.util.Log;

public class LLg {

	private static final int VERBOSE = 10;
	private static final int DEBUG = 20;
	private static final int INFO = 30;
	private static final int WARN = 40;
	private static final int ERROR = 50;

	private static final void log(int state, final String msg) {
		StackTraceElement[] elements = new Throwable().getStackTrace();
		String callerClassName = "?";
		String callerMethodName = "?";
		if (elements.length >= 3) {
			callerClassName = elements[2].getClassName();
			callerClassName = callerClassName.substring(callerClassName.lastIndexOf('.') + 1);
			callerMethodName = elements[2].getMethodName();
			callerMethodName = callerMethodName.substring(callerMethodName.lastIndexOf('_') + 1);
		}

		switch (state) {
		case VERBOSE:
			Log.v(callerClassName, "[" + callerMethodName + "] " + msg);
			break ;
		case DEBUG:
			Log.d(callerClassName, "[" + callerMethodName + "] " + msg);
			break ;
		case INFO:
			Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
			break ;
		case WARN:
			Log.w(callerClassName, "[" + callerMethodName + "] " + msg);
			break ;
		case ERROR:
			Log.e(callerClassName, "[" + callerMethodName + "] " + msg);
			break ;
		default:
			break ;
		}
	}

	public static final void v(final String msg) {
		log(VERBOSE, msg);
	}

	public static final void d(final String msg) {
		log(DEBUG, msg);
	}

	public static final void i(final String msg) {
		log(INFO, msg);
	}

	public static final void w(final String msg) {
		log(WARN, msg);
	}

	public static final void e(final String msg) {
		log(ERROR, msg);
	}

}
