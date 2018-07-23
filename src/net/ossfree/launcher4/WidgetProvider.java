package net.ossfree.launcher4;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Logger.LLg;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;


public class WidgetProvider extends AppWidgetProvider {


	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		final Bundle extras = intent.getExtras();
		if(extras!=null && AppsService.LNCHAPP.equals(intent.getAction())) {
			final int widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			final int tabid    = extras.getInt(AppsService.TABID, 0);
			if(widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				AppsService.currentTab = tabid;				
				Intent ai = new Intent(context.getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
				LLg.i("onReceive:Launching App:"+widgetId+":"+tabid);
				context.startActivity(ai);
			}
		}
		
		updateAllWidgets(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateAllWidgetsInternal(context, appWidgetManager, appWidgetIds);
	}


	private static void updateAllWidgetsInternal(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int i=0; i<appWidgetIds.length; i++) {
			int mAppWidgetId = appWidgetIds[i];
			RemoteViews widget = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
			appWidgetManager.updateAppWidget(mAppWidgetId, updateView(context, widget, mAppWidgetId));
		}
	}


	public static void updateAllWidgets(Context context) {
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
		updateAllWidgetsInternal(context, appWidgetManager, appWidgetIds);
	}
	
	
	
	private static RemoteViews  updateView(Context context, RemoteViews widget, int mAppWidgetId ) {
		final SharedPreferences editor = context.getSharedPreferences(AppsService.PREFS, Context.MODE_MULTI_PROCESS);
		final String key = AppsService.TABID + mAppWidgetId;
		final String tag  = editor.getString(key, "");
		if(tag.contains(":")) {
			final String tags [] = tag.split(":",2);
			widget.setTextViewText(R.id.name1, tags[1]);			
			final Intent clickIntent = new Intent(context, WidgetProvider.class)
				.setAction(AppsService.LNCHAPP)
				.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
				.putExtra(AppsService.LISTER, false)
				.putExtra(AppsService.TABID,Integer.parseInt(tags[0]));
			PendingIntent clickPI = PendingIntent.getBroadcast(context, mAppWidgetId, clickIntent, 0);		
			widget.setOnClickPendingIntent(R.id.button1, clickPI);
			LLg.i("updateView:Pending -"+mAppWidgetId+":"+tag);
		}
		return widget;
	}




}
