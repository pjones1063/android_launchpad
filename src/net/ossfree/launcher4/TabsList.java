package net.ossfree.launcher4;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import net.ossfree.launcher4.Adapters.TabAdapter;
import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.Structures.TabItem;
import net.ossfree.launcher4.Structures.TabPage;


@SuppressLint({ "RtlHardcoded", "InflateParams" })
public class TabsList extends Activity implements OnDragListener, OnItemClickListener, OnItemLongClickListener {
	
	private int mAppWidgetId;
	private ListView list;
	private boolean lister=false, dropper =false;
	private TabAdapter tabAdapter;
	private String appid = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.widget_list);		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
			lister = extras.getBoolean(AppsService.LISTER, false);
			dropper = extras.getBoolean(AppsService.DROPPER, false);
			appid = extras.getString(getString(R.string.action_APPINFO), "");
		}	
		
		list = (ListView) findViewById(R.id.wgdIndex);
		list.setOnDragListener(this);
		if(dropper) {
			tabAdapter= new TabAdapter(getApplicationContext(), AppsService.buildDropList(this,appid));
			list.setAdapter(tabAdapter);
			list.setOnItemClickListener(this);
		} else {
			tabAdapter = new TabAdapter(getApplicationContext(), AppsService.buildTabList(this));
			list.setAdapter(tabAdapter);
			list.setOnItemClickListener(this);
			list.setOnItemLongClickListener(this);
		}	
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    View view = getWindow().getDecorView();
	    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	    lp.gravity =  Gravity.TOP | Gravity.RIGHT;
	    lp.y = 25;
	    lp.y = 115;
	    getWindowManager().updateViewLayout(view, lp);
	}
	

	private void showAppWidget(String item, int position) {		
		if (mAppWidgetId != INVALID_APPWIDGET_ID) {
			String tag = position + ":" + item;
			String key = AppsService.TABID + mAppWidgetId;
			SharedPreferences.Editor editor = getSharedPreferences(AppsService.PREFS, MODE_MULTI_PROCESS).edit();
			editor.putString(key, tag);
			editor.commit();
			Intent bintent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, WidgetProvider.class);
			bintent.putExtra(key, tag);
			bintent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,new int[] { mAppWidgetId });
			sendBroadcast(bintent);
		}
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String item = ((TabItem) list.getAdapter().getItem(position)).tabName;
		if(lister)         AppsService.currentTab = position;
		else if (dropper)  dropApp(view, position);
		else               showAppWidget(item, position);
		setResult(RESULT_OK, getIntent());	
		finish();	
	}



	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    	if(position > 2) setDragStarted(view, position);
    	return true;
    }


    private void dropApp (View view, int position) {
		String tabItem = null;
		TabPage tabPage = null;
		switch(position) {
			case 0:
				if(appid.startsWith(AppsService.FOLDER)) {
					AppsService.deleteFolder(this, appid.substring(AppsService.FOLDER.length()));
					setResult(RESULT_OK);
					finish();

				} else {
					startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:"+appid)));
					AppsService.applist = null;
					finishAffinity();
				}
				break;

			case 1:
				if(appid.startsWith(AppsService.FOLDER)) {
					startActivity(new Intent(getApplicationContext(), EditActivity.class)
							.putExtra(AppsService.TABPOS, appid.substring(AppsService.FOLDER.length())));
					finish();
				} else {
					startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
							Uri.parse("package:" + appid)));
					finishAffinity();
				}
				break;

			case 2:
				tabItem = ((TabItem) list.getAdapter().getItem(position)).tabName;
				tabPage = AppsService.getPage(tabItem);
				if(tabPage.getID() != AppsService.ALLID)    tabPage.removeAppByPackage(appid);
				if(tabPage.getID() == AppsService.FRQID)    AppsService.removeFreqApp(appid);
				if(appid.startsWith(AppsService.FOLDER)) {
					String fldr = appid.substring(AppsService.FOLDER.length());
					if(!AppsService.isFolderVisable(fldr)) AppsService.deleteFolder(getApplicationContext(), fldr);
					finish();
				}
				if(!tabPage.isFolder()) MainActivity.postfilters(tabPage);
				finish();
				break;

			default:
				tabItem = ((TabItem) list.getAdapter().getItem(position)).tabName;
				tabPage = AppsService.getPage(tabItem);
				if(! (tabPage.isFolder() && tabItem.startsWith(AppsService.FOLDER)) ){
					tabPage.addAppByPackage(appid);
					if(!tabPage.isFolder()) MainActivity.postfilters(tabPage);
				}
				finish();
		}

	}

	private void setDragStarted(View view, int position) {
		String item = ((TabItem) list.getAdapter().getItem(position)).tabName;
		ViewGroup cv = (ViewGroup)getViewByPosition(position, list);
		((ImageView) cv.getChildAt(0)).setImageResource(R.drawable.ic_subdirectory_arrow_left_white_48dp); 
		
		Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(150);
    	String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
    	ClipData dd = new ClipData(item, mimeTypes, new ClipData.Item(item));
    	View.DragShadowBuilder ds = new DragShadowBuilder(cv);
    	view.startDrag(dd, ds, view, 0); 
	}

	public View getViewByPosition(int pos, ListView listView) {
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

	    if (pos < firstListItemPosition || pos > lastListItemPosition ) {
	        return listView.getAdapter().getView(pos, null, listView);
	    } else {
	        final int childIndex = pos - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    }
	}
	
	
	@Override
	public boolean onDrag(View v, DragEvent e1) {
		if(dropper) return true;
		else        return onTagDrag(v, e1);
	}


 public boolean onTagDrag (View v, DragEvent e1) {	
	    if (v.getId() != R.id.wgdIndex) return false;
	    View c = null,d = null;
		int ai = list.pointToPosition((int) e1.getX(), (int) e1.getY());
		int fvi = list.getFirstVisiblePosition();
		int lvi = list.getLastVisiblePosition();
		int viewIndex = ai - fvi;
		for (int i = 0; i < list.getChildCount(); i++) ((View) list.getChildAt(i)).setBackgroundColor(Color.BLACK);
		
		switch (e1.getAction()) {
		case DragEvent.ACTION_DRAG_ENDED:
			finish();
			break;

		case DragEvent.ACTION_DROP:
			ClipData cd = e1.getClipData();
			String from = cd.getItemAt(0).getText().toString();
			String to =((TabItem) list.getAdapter().getItem(ai)).tabName;
			LLg.d(from + ":" + to);
			if(!from.equals(AppsService.tab_AllApps) && !from.equals(AppsService.tab_FrqApps) 
	  		    && !from.equals(AppsService.tab_NewApps) && !from.equals(AppsService.tab_MyDocs)  ) {
				AppsService.movePage(getApplicationContext(),from, to);
				setResult(RESULT_OK);
			}
			finish();
			break;
			
		default:
			c = (View) list.getChildAt(viewIndex);
			if (c != null && ai > 2)  c.setBackgroundColor(Color.GRAY);
			
			if(viewIndex < tabAdapter.getCount()-1) d = (View) list.getChildAt(viewIndex+1);			
			if (d != null && ai > 2)  d.setBackgroundColor(Color.GRAY);
			
			if (ai == fvi && ai > 0) list.smoothScrollToPosition(ai - 1);
			else if (ai == lvi) list.smoothScrollToPosition(ai + 1);
			
			break;
		}

		return true;
	}

    protected void showAlertDialog(AlertDialog.Builder alertDialog)  {
	  AlertDialog ad = alertDialog.create();
	  ad.show(); 
	  View view = ad.getWindow().getDecorView();
	  WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	  lp.gravity =  Gravity.TOP | Gravity.RIGHT;
	  lp.y = 20;
	  lp.y = 117;
	  getWindowManager().updateViewLayout(view, lp);	 	
   }

 
}
