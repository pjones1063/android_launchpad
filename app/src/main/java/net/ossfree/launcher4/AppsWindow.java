package net.ossfree.launcher4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.ossfree.launcher4.Adapters.AppsWindowAdapter;
import net.ossfree.launcher4.Adapters.IconAdapter;
import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.TabPage;
import net.ossfree.launcher4.ViewHolders.AppsHolder;

@SuppressLint("RtlHardcoded")
public class AppsWindow extends Activity {
	
	private String folderName;
	private AppsWindowAdapter fldrAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null) folderName = extras.getString(AppsService.FOLDER);
		
		if (MainActivity.isGridview()) buildGrid(); else  buildList();

		final ImageView edt = (ImageView) findViewById(R.id.edt);
		edt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buildFolderOptions();	
			}
		});

		final ImageView cncl = (ImageView) findViewById(R.id.cncl);
		cncl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		setTitle(folderName); 	
	}

	
	private void buildGrid() {
		setContentView(R.layout.apps_window_grid); 		
		TabPage tabPage  = AppsService.getPage(folderName);
		((TextView)findViewById(R.id.fl_title)).setText(folderName);
		GridView bigList = (GridView) findViewById(R.id.appsIndex);
	    fldrAdapter  = new AppsWindowAdapter(getApplicationContext(), R.layout.apps_grid_row, tabPage);
	    bigList.setAdapter(fldrAdapter);
		bigList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	//setDragStarted(view);
				showTabsList();
		    	return true;
		    }
		});
		bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
				ItemInfo app = fldrAdapter.getItem(position);		
				if(app.appPackage.startsWith(AppsService.FOLDER))
						showFolder(app);
				else 
					    showApp(app);
				
			}
		});
	}
	
	
	private void buildList() {
		setContentView(R.layout.apps_window_list);
		TabPage tabPage  = AppsService.getPage(folderName);
		((TextView)findViewById(R.id.fl_title)).setText(folderName);
		ListView bigList = (ListView) findViewById(R.id.appsIndex);
	    fldrAdapter  = new AppsWindowAdapter(getApplicationContext(), R.layout.apps_list_row, tabPage);
	    bigList.setAdapter(fldrAdapter);
		bigList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	//setDragStarted(view);
		    	showTabsList();
				return true;
		    }
		});
	    bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
				ItemInfo app = fldrAdapter.getItem(position);		
				if(app.isfolder)
						showFolder(app);
				else 
					    showApp(app);
				
			}
		});
	}
	
	
	
	private void buildFolderOptions() {    	

		final String options[] = new String[] {
				getResources().getString(R.string.action_editFolder), 
				getResources().getString(R.string.action_renFolder),
				getResources().getString(R.string.action_delFolder),
				getResources().getString(R.string.action_addfldr),
				getResources().getString(R.string.action_cancel)};

		final Integer[] icons = new Integer[] 
				{R.drawable.ic_action_accept,
						R.drawable.ic_action_edit,
						R.drawable.ic_action_delete,
						R.drawable.ic_action_expand,
						R.drawable.ic_action_cancel};

		ListAdapter adapter = new IconAdapter(this. getApplicationContext() , options, icons);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
		alertDialog.setCancelable(true)
		.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:				 
					startActivity(new Intent(getApplicationContext(), EditActivity.class).putExtra(AppsService.TABPOS, folderName));
					finish();
					return;

				case 1: case 3:
					processFolderOptions(item*-1);
					return;

				case 2:    					 
					showAlertDialog((new AlertDialog.Builder(AppsWindow.this, AlertDialog.THEME_HOLO_DARK))
						 	.setTitle(getString(R.string.action_delTab))
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									deleteLaunchFolder(); 
								}}).setNegativeButton(android.R.string.no, null));    
					
					return ;

				default:
					return ;
				}
			}});

		showAlertDialog(alertDialog);			 
	}

    
    
    protected void processFolderOptions(final int action) {
    	String title;
    	final LayoutInflater li = LayoutInflater.from(this);
    	final View pv = li.inflate(R.layout.prompt,null);
    	final TextView tvPrompt = (TextView) pv.findViewById(R.id.tvPrompt);
    	final EditText edAdd    = (EditText) pv.findViewById(R.id.edAdd);
    	
    	if(action == -1) {
    		title = getString(R.string.tab_Rename)+" "+folderName; 
    		tvPrompt.setText(R.string.category);
    		edAdd.setText(folderName);
    	}  else {
    		title = getString(R.string.tab_Add)+" "+folderName;
    		tvPrompt.setText(R.string.folder);
    		edAdd.setText("");
    	}	
    	
    	AlertDialog.Builder addPrmpt = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
    	addPrmpt.setView(pv)
         	.setTitle(title)
        	.setCancelable(false)
        	.setPositiveButton(getString(R.string.tab_OK),  new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog,int id) {		    	
    	   		 String tab = edAdd.getText().toString();
    	   		 
    			 if(tab != null && !tab.equals("")) {
     				if(action == -1) renameLaunchFolder(tab.trim());
    				if(action == -3) addLaunchFolder(tab.trim());
    			}
    		}
    	}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog,int id) {
    			dialog.cancel();
    		}
    	});
  	
    	showAlertDialog(addPrmpt);	
    }
    
    public void showAlertDialog(AlertDialog.Builder alertDialog)  {
    	AlertDialog ad = alertDialog.create();
    	ad.show(); 
    	View view = ad.getWindow().getDecorView();
  	    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
  	    lp.gravity =  Gravity.TOP | Gravity.RIGHT;
  	    lp.y = 20;
  	    lp.y = 117;
  	    getWindowManager().updateViewLayout(view, lp);	 	
    }
	
    
	private void showApp(ItemInfo app) {
		try {
			PackageManager pm = getApplicationContext().getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage(app.appPackage);
			if (null != intent) {
				AppsService.addFreqApp(app.appPackage);				startActivity(intent);
				
				finish();			   
			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	

	private void showFolder(ItemInfo app) {
		try {
		    startActivityForResult(
		    		new Intent(this, AppsWindow.class)
		    		 .putExtra(AppsService.FOLDER, app.appName)
		    		 .putExtra(AppsService.TABID, app.appPackage), 0);
		    
		    finish();
		    
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	private void addLaunchFolder(String fldr) {
		LLg.i("addFolderTab:"+folderName+":"+fldr);
		TabPage fp = new TabPage().setTab(fldr).setFolder(true);
		if(! AppsService.pagelist.contains(fp)) {
			AppsService.pagelist.add(fp);
			ItemInfo i = new ItemInfo(fldr,	AppsService.FOLDER + fldr, -99, 
					getResources().getDrawable(R.drawable.im64_folder), true);
			AppsService.getPage(folderName).addAppByAppInfo(i);    
			AppsService.applist.add(i);	    		
			AppsService.saveLayout(this);    
			setResult(RESULT_OK);
			finish();     	    
		}
	}


	private void renameLaunchFolder(String newName) {
		AppsService.renameFolder(getApplicationContext(), folderName, newName);
		setResult(RESULT_OK);
		finish();

	}

	private void deleteLaunchFolder() {
		AppsService.deleteFolder(getApplicationContext(), folderName);
		setResult(RESULT_OK);
		finish();

	}

	private void showTabsList() {
		startActivityForResult(new Intent(this, TabsList.class).putExtra(AppsService.DROPPER, true), 0);

	}



	private void setDragStarted(View view) {
		startActivityForResult(new Intent(this, TabsList.class).putExtra(AppsService.DROPPER, true), 0);
		Vibrator vb = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(150);
    	String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
    	ClipData dd = new ClipData(((AppsHolder)view.getTag()).ai.appPackage, mimeTypes, new ClipData.Item(((AppsHolder)view.getTag()).ai.appPackage));
    	dd.addItem(new ClipData.Item(folderName));
    	View.DragShadowBuilder ds = new DragShadowBuilder(view);
    	view.startDrag(dd, null, view, 0);
    	finish();
	}
	
	
}
