 package net.ossfree.launcher4;
 
 
import net.ossfree.launcher4.Logger.LLg;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class UpdateManager extends BroadcastReceiver {

	public static final String TAG = "UpdateManager";

	public void onReceive(Context context, Intent intent) {	 
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) ) {
			LLg.i("onReceive:android.intent.action.BOOT_COMPLETED");
			final ComponentName comp = new ComponentName(context.getPackageName(), AppsService.class.getName());
			final ComponentName service = context.startService(new Intent().setComponent(comp));
			if (null == service){
				LLg.e( "Could not start service ! " + comp.toString());
			}

		} else if ( "android.intent.action.PACKAGE_ADDED".equals(intent.getAction()) ||
			  	    "android.intent.action.PACKAGE_CHANGE".equals(intent.getAction()) ||
				    "android.intent.action.PACKAGE_REMOVED".equals(intent.getAction()) ) {
			LLg.i("Package Update");
			AppsService.getAppsByLaunchIntent(context);
		} 
	}
}
