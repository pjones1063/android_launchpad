package net.ossfree.launcher4;


import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.ossfree.launcher4.Adapters.AlphaAdapter;
import net.ossfree.launcher4.Adapters.AppsAdapter;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.TabPage;
import net.ossfree.launcher4.ViewHolders.AppsHolder;
                                                                                                                                                                                                                                   
@SuppressLint("DefaultLocale")
public class AppsList extends Fragment   {

	protected AppsAdapter appsAdapter = null;
	protected TabPage tabPage = null;
	protected ListView sideList;
	protected AlphaAdapter ala  = null;
	
	@SuppressLint("ValidFragment")
    public AppsList(TabPage pg) {
		super();
		tabPage = pg;	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (MainActivity.isGridview())
			return buildGrid(inflater, container);
		else
			return buildList(inflater, container);
	}
	


	private View buildGrid(LayoutInflater inflater, ViewGroup container) {
		
		final View apps = inflater.inflate(R.layout.apps_grid, container, false);				
		final GridView bigList = (GridView) apps.findViewById(R.id.appsIndex);
		appsAdapter  = new AppsAdapter(getActivity().getApplicationContext(), R.layout.apps_grid_row, tabPage);		
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
		final Drawable wp= wallpaperManager.getDrawable();
		bigList.setBackground(wp);
		bigList.setAdapter(appsAdapter);
		bigList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	//setDragStarted(view);
                showTablist(view);
		    	return true;

		    }
		});
		
       
		bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
					ItemInfo app = appsAdapter.getItem(position);		
					if(app.isfolder)
						showFolder(app);
					else 
						showApp(app);
			}
		});
       
	
		return buildAlphaList(inflater, apps);
		
	}
	
	
   
	
	private View buildList(LayoutInflater inflater, ViewGroup container) {
		final View apps = inflater.inflate(R.layout.apps_list, container, false);
		appsAdapter  = new AppsAdapter(getActivity().getApplicationContext(), R.layout.apps_list_row, tabPage);
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
		final Drawable wp = wallpaperManager.getDrawable();		
		final ListView bigList = (ListView) apps.findViewById(R.id.appsIndex);
		bigList.setBackground(wp);
		bigList.setAdapter(appsAdapter);
		bigList.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(tabPage.getID() == AppsService.ALLID  || tabPage.getID() == AppsService.FRQID  
						|| tabPage.getID() == AppsService.NEWID || tabPage.getID() == AppsService.DOCID) {
					
				} else {
					((MainActivity)getActivity()).buildTabOptions();
					((MainActivity)getActivity()).clearSearch();
				}
				return false;
			}
		});
		
		bigList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	//setDragStarted(view);
		    	showTablist(view);
                return true;
		    }
		});
		
		bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
				ItemInfo app = appsAdapter.getItem(position);		
				if(app.appPackage.startsWith(AppsService.FOLDER))
						showFolder(app);
				else 
					    showApp(app);
				
			}
		});
		
		return buildAlphaList(inflater, apps);
	}

	
	
	
	private View  buildAlphaList(LayoutInflater inflater, View apps ) {
		
 	   sideList = (ListView) apps.findViewById(R.id.sideIndex);
	   ala = new AlphaAdapter(getActivity(), R.layout.side_row);
	   sideList.setAdapter(ala);
	   sideList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Parcelable state = ((ListView)parent).onSaveInstanceState();
				String item = (String)  ((ListView)parent).getAdapter().getItem(position);
				((MainActivity)getActivity()).showAllView(item, state);
				((MainActivity)getActivity()).clearSearch();
			}
		});
		
	   
	   final ImageView fldr = (ImageView) apps.findViewById(R.id.fldr);
	   fldr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).openOptionsMenu();
					
			}
		});

	   final ImageView edit = (ImageView) apps.findViewById(R.id.edt);
	   if(tabPage.getID() == AppsService.ALLID  || tabPage.getID() == AppsService.FRQID  
			       || tabPage.getID() == AppsService.NEWID || tabPage.getID() == AppsService.DOCID) {
		   edit.setImageResource(R.drawable.ic_add_white_48dp);
		   edit.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View v) {			
				   ((MainActivity)getActivity()).processTabOptions(R.id.action_add);
				   ((MainActivity)getActivity()).clearSearch();		
			   }
		   });
		   
	   } else {
		   edit.setImageResource(R.drawable.ic_mode_edit_white_24dp);
		   edit.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View v) {			
				   ((MainActivity)getActivity()).buildTabOptions();
				   ((MainActivity)getActivity()).clearSearch();
			   }

		   });
	   }

	   
	   final ImageView home = (ImageView) apps.findViewById(R.id.clr);
		home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				((MainActivity)getActivity()).positionTab(0);
				((MainActivity)getActivity()).clearSearch();
				((MainActivity)getActivity()).clearAllView();
				sideList.smoothScrollToPosition(0);
				
			}
		});
		return apps;
	}
	

	private void showTablist (View view) {
        startActivityForResult(
                new Intent(((MainActivity)getActivity()), TabsList.class)
                        .putExtra(AppsService.DROPPER, true)
                        .putExtra(getString(R.string.action_APPINFO), ((AppsHolder)view.getTag()).ai.appPackage), 0);
    }

	/*
	private void setDragStarted(View view) {	
		startActivityForResult(
				new Intent(((MainActivity)getActivity()), TabsList.class)
				.putExtra(AppsService.DROPPER, true)
				.putExtra(getString(R.string.action_APPINFO), ((AppsHolder)view.getTag()).ai.appPackage), 0);
		Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(150);
    	String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
    	ClipData dd = new ClipData(((AppsHolder)view.getTag()).ai.appPackage, 
    			   mimeTypes, new ClipData.Item(((AppsHolder)view.getTag()).ai.appPackage));
    	dd.addItem(new ClipData.Item(tabPage.getTab()));
    	View.DragShadowBuilder ds = new DragShadowBuilder(view);
    	view.startDrag(dd, ds, view, 0); 		
	}  */

	public void postFilter(String filter)   {
		if(appsAdapter != null)  appsAdapter.getFilter().filter(filter);	
		if (ala != null)  ala.setFilter("");
	}


	public void postAllFilter(final String filter, final Parcelable state ) {
		if (appsAdapter != null)  appsAdapter.getFilter().filter(filter);
		
		if(sideList != null) {
			sideList.post(new Runnable() {
				@Override
				public void run() {
					if(state != null) sideList.onRestoreInstanceState(state);
					for (int a = 0; a < 24; a++) {
						TextView tv = (TextView) sideList.getChildAt(a);
						if (tv != null) {
							if (tv.getText().equals(filter.toUpperCase())) {
								tv.setBackgroundColor(Color.RED);
							} else {
								tv.setBackgroundColor(Color.BLACK);	
							}
						}
						if (ala != null) ala.setFilter(filter);
					}
				sideList.invalidate();
				}
			});
		}
	}
	
	private void showApp(ItemInfo app) {
		try {
			PackageManager pm = getActivity().getApplicationContext().getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage(app.appPackage);
			if (null != intent) {
				AppsService.addFreqApp(app.appPackage);
				startActivity(intent);
				((MainActivity) getActivity()).finish();
			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private void showFolder(ItemInfo app) {
		try{
			startActivityForResult(new Intent(getActivity(), AppsWindow.class)
					.putExtra(AppsService.FOLDER,  app.appName)
					.putExtra(AppsService.TABID,   app.appPackage), 0);
			
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}
