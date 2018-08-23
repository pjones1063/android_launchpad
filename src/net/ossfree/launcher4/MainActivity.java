package net.ossfree.launcher4;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import net.ossfree.launcher4.Adapters.DrawerAdapter;
import net.ossfree.launcher4.Adapters.IconAdapter;
import net.ossfree.launcher4.Adapters.TabPagerAdapter;
import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.PageTransitions.ReaderViewPagerTransformer;
import net.ossfree.launcher4.PageTransitions.ReaderViewPagerTransformer.TransformType;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.TabItem;
import net.ossfree.launcher4.Structures.TabPage;

import java.lang.reflect.Method;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
@SuppressLint({ "InflateParams", "RtlHardcoded" })
public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener, ActionBar.TabListener {

	private DrawerLayout drwrLayout;
	private ListView drwrList;
	private ActionBarDrawerToggle drwrToggle;
	private static TabPagerAdapter tabAdapter = null;
	private static ActionBar actBar = null; 
	private  SearchView srchView = null;
    private  ViewPager tabPager = null;
	private static boolean gridview = true;
	private static boolean gridviewdoc = false;
	private static boolean textview = true;
	private static int sortview = AppsService.ALPHASORT;
	private static int pageview = AppsService.DEPTHOVER;
	private boolean fireresult = false;
    private static String sd_path = "/storage/CC66-6F37";
	 	
    public  MainActivity(){}

 	public class CategorizeAllApplications extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;		
		@Override
		protected Void doInBackground(Void... params) {
			AppsService.getAppsByLaunchIntent(MainActivity.this);			
			AppsService.categorizeAllApplications(getApplicationContext());
			AppsService.saveLayout(getApplicationContext()); 				
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

				
		@Override
		protected void onPostExecute(Void result) {
			final SharedPreferences.Editor ed = getSharedPreferences(AppsService.PREFS, MODE_MULTI_PROCESS).edit();
			ed.putBoolean(AppsService.FIRSTRUN, false);
			ed.commit();
			buildTabs();			
			progress.dismiss();
			startService(new Intent(MainActivity.this, AppsService.class));	
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(MainActivity.this, null, getString(R.string.doprogress2));			
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
	
	public class LoadApplications extends AsyncTask<Void, Void, Void> {
		private Dialog progress = null;
		
	
		@Override
		protected Void doInBackground(Void... params) {
			LLg.i("LoadApplications:Loading Apps");
			
			AppsService.tab_MyApps	= getString(R.string.tab_MyApps);
	    	AppsService.tab_AllApps = getString(R.string.tab_AllApps);
	    	AppsService.tab_FrqApps = getString(R.string.tab_FrqApps);
	    	AppsService.tab_NewApps = getString(R.string.tab_NewApps);  
	    	AppsService.tab_MyDocs  = getString(R.string.tab_MyDocs);  
	    	
			if(AppsService.applist == null)  AppsService.getAppsByLaunchIntent(MainActivity.this);			
			if(AppsService.pagelist == null) AppsService.loadLayout(getApplicationContext());			
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

				
		@Override
		protected void onPostExecute(Void result) {
			buildTabs();			
			if(progress != null) { 
				progress.dismiss();
				startService(new Intent(MainActivity.this, AppsService.class));	
			}									
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			if(srchView != null) srchView.onActionViewCollapsed();
			if(AppsService.applist == null) progress = ProgressDialog.show(MainActivity.this, null, getString(R.string.doprogress1));
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
	
	 
	public static void postfilters(TabPage tp) {
		if(tabAdapter != null) tabAdapter.postFilter("", getTabIndex(tp));
	}
	
	public static int getTabIndex(TabPage tp){
		if(actBar != null)
			for(int t=0;t<actBar.getTabCount();t++) {
				TabPage at = (TabPage) actBar.getTabAt(t).getTag(); 		
				if(tp.equals(at)) return t;
			}
		return -1;
	}

	public static TabPage getMainTab(){
        if (actBar != null) return (TabPage) actBar.getTabAt(1).getTag();
		return null;
	}

    private void buildAppsView() { 	
		if(AppsService.applist == null || AppsService.pagelist == null) 
    	   new LoadApplications().execute();    	
		else if(!fireresult) buildTabs();
		fireresult = false;
	}
           
   
    private void buildTabs () {
    	
    	final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    	tabAdapter = new TabPagerAdapter(getSupportFragmentManager());
    	tabAdapter.addTabs(AppsService.pagelist);
    	if(tabPager != null) tabPager.setVisibility(View.INVISIBLE);
    	tabPager = (ViewPager)findViewById(R.id.pager);
    	
    	tabPager.setOnPageChangeListener(
    			new ViewPager.SimpleOnPageChangeListener() {
    				@Override
    				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){ }					
    				@Override
    				public void onPageSelected(int position) { positionTab(position); }
    			});

    	tabPager.setAdapter(tabAdapter);

    	switch (getPageview()) {
    	case AppsService.FLOWOVER:
    		tabPager.setPageTransformer(true, new ReaderViewPagerTransformer(TransformType.FLOW));
    		break;
    	case AppsService.DEPTHOVER:
    		tabPager.setPageTransformer(true, new ReaderViewPagerTransformer(TransformType.DEPTH));
    		break;
    	case AppsService.ZOOMOVER:
    		tabPager.setPageTransformer(true, new ReaderViewPagerTransformer(TransformType.ZOOM));
    		break;
    	case AppsService.SLIDEOVER:
    		tabPager.setPageTransformer(true, new ReaderViewPagerTransformer(TransformType.SLIDE_OVER));
    		break;
            default:
                tabPager.setPageTransformer(true, new ReaderViewPagerTransformer(TransformType.DEPTH));
    	} 		

    	int ct = AppsService.currentTab;
    	actBar = getActionBar();
    	actBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	actBar.setDisplayHomeAsUpEnabled(false);
    	actBar.setHomeButtonEnabled(true);
    	actBar.setDisplayShowTitleEnabled(false);
    	actBar.setIcon(R.drawable.ic_drawer);
    	actBar.removeAllTabs();
    	ArrayList<TabItem> sb = new ArrayList<TabItem>();
    	 			
    	sb.add(new TabItem(getString(R.string.action_settings), getResources().getDrawable(R.drawable.im64_settings),  AppsService.OTHID));
    	sb.add(new TabItem(getString(R.string.action_addTab),   getResources().getDrawable(R.drawable.im64_add), AppsService.OTHID));

        for (TabPage tp : AppsService.pagelist) {
            if (!tp.isFolder()) {
                View tb = inflater.inflate(R.layout.tab_row, null);
                TextView tbr = (TextView) tb.findViewById(R.id.tbr);
                tbr.setTypeface(Typeface.createFromAsset(getAssets(), "font/Prototype.ttf"));
                switch (tp.getID()) {
                    case AppsService.STATID:
                        tbr.setTextColor(getResources().getColor(R.color.lightcyan));
                        break;
                    case AppsService.ALLID:
                        tbr.setTextColor(getResources().getColor(R.color.aliceblue));
                        break;
                    case AppsService.FRQID:
                        tbr.setTextColor(getResources().getColor(R.color.aquamarine));
                        break;
                    case AppsService.NEWID:
                        tbr.setTextColor(getResources().getColor(R.color.lightcoral));
                        break;
                    case AppsService.DOCID:
                        tbr.setTextColor(getResources().getColor(R.color.cornflowerblue));
                        break;
                    case AppsService.SDID:
                        tbr.setTextColor(getResources().getColor(R.color.cornflowerblue));
                        break;
					default:
                        tbr.setTextColor(getResources().getColor(R.color.antiquewhite));
                        break;
                }
                tbr.setText(tp.getTab());
                final Tab tab = actBar.newTab();
                tab.setText(tp.getTab())
                        .setTabListener(this)
                        .setCustomView(tb)
                        .setTag(tp);
                actBar.addTab(tab);
            }

            if (tp != null && tp.getTab() != null) {
    		    String s = tp.getTab().trim();
                sb.add(new TabItem(s, getResources().getDrawable(AppsService.getFolderIcon(s)), tp.getID()));
            }
    	}

    	AppsService.currentTab = ct;
    	positionTab(AppsService.currentTab);	
    	tabAdapter.postFilter("", AppsService.currentTab); 
    	if(srchView != null) srchView.onActionViewCollapsed();
    	if(tabPager != null) tabPager.setVisibility(View.VISIBLE);
    	
    	drwrLayout = (DrawerLayout) findViewById(R.id.drawer_layout);    	  	   	
    	drwrLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    	drwrList = (ListView)findViewById(R.id.left_drawer); 
    	drwrList.setAdapter(new DrawerAdapter(this,R.layout.drawer_list_item, sb));
    	drwrList.setOnItemClickListener(new ListView.OnItemClickListener () {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
     		  	   drwrLayout.closeDrawer(drwrList);
     		  	   srchView.setQuery("", false);
        		   srchView.clearFocus();
        		   switch (position) {
        		   case 0:
        			   openOptionsMenu();
        			   break;   
        		   case 1:
        			   processTabOptions(R.id.action_add);
        			   break;    		
        		   default:
        			   if(!showFolder(AppsService.pagelist.get(position-2)))  
        				    positionTab(AppsService.pagelist.get(position-2));
        			   break;
        		   }
    		}
    	});
    	
    	drwrToggle = new ActionBarDrawerToggle(this, drwrLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
    		public void onDrawerClosed(View view) { invalidateOptionsMenu();  }
    		public void onDrawerOpened(View drawerView) { invalidateOptionsMenu(); }
    	};
    	drwrLayout.setDrawerListener(drwrToggle);
    	tabPager.setVisibility(View.VISIBLE);
        if(tabPager.getCurrentItem() == 0) positionTab(1);
 
    }
  
    public void clearSearch() {
    	if(srchView != null) {
    		srchView.setQuery("", false);
    		srchView.clearFocus();
    		srchView.onActionViewCollapsed();   		
    	}
    }

    private void addLauchTab(String tab) {
    	LLg.i("createTab:"+tab);
    	AppsService.pagelist.add(new TabPage().setTab(tab).setFolder(false));
    	AppsService.saveLayout(this);
    	buildTabs(); 	 
    }
    
 
    private void addFolderTab(String tab, String fldr) {    
    	LLg.i("addFolderTab:"+tab+":"+fldr);
    	TabPage fp = new TabPage().setTab(fldr).setFolder(true);
    	ItemInfo i = new ItemInfo(fldr,	AppsService.FOLDER + fldr, -99, getResources().getDrawable(R.drawable.im64_folder), true);    	
    	AppsService.getPage(tab).addAppByAppInfo(i);
    	AppsService.saveLayout(this);
    	if(! AppsService.pagelist.contains(fp)) {
    		AppsService.pagelist.add(fp); 		
    		AppsService.applist.add(i);
    		
    	}
    	buildTabs();
    	AppsService.saveLayout(this);
    }


    
    private void deleteTab() {
    	TabPage tp = (TabPage) actBar.getSelectedTab().getTag();
    	LLg.i("deleteTab:"+tp.getTab());;
    	AppsService.deleteTab(getApplicationContext(), tp.getTab());
    	AppsService.currentTab = 0;
    	buildTabs();   	
    }
   
    
    protected void processTabOptions(final int action) {
    	if(srchView != null) srchView.onActionViewCollapsed();
    	String title;
    	final String tp = ((TabPage) actBar.getSelectedTab().getTag()).getTab();
    	final LayoutInflater li = LayoutInflater.from(this);
    	final View pv = li.inflate(R.layout.prompt, null);
    	final TextView tvPrompt = (TextView) pv.findViewById(R.id.tvPrompt);
    	final EditText edAdd    = (EditText) pv.findViewById(R.id.edAdd);
    	
    	if(action == R.id.action_add) {
    		title = getString(R.string.action_newprompt);
    		tvPrompt.setText(R.string.category);
    		edAdd.setText("");
    	} else if(action == -1) {
    		title = getString(R.string.tab_Rename)+" "+tp; 
    		tvPrompt.setText(R.string.category);
    		edAdd.setText(tp);
    	}  else {
    		title =  getString(R.string.tab_Add)+" "+tp;
    		tvPrompt.setText(R.string.folder);
    		edAdd.setText("");
    	}	
    	
    AlertDialog.Builder addPrmpt = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
    	addPrmpt.setView(pv)
         	.setTitle(title)
        	.setCancelable(false)
        	.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog,int id) {		    	
    	   		 String tab = edAdd.getText().toString();
    	   		 
    			 if(tab != null && !tab.equals("")) {
    				if(action == R.id.action_add) addLauchTab(tab.trim());
    				if(action == -1) renameLaunchTab(tab.trim());
    				if(action == -3) addFolderTab(tp, tab);
    			}
    		}
    	}).setNegativeButton(getString(R.string.tab_Cancel), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog,int id) {
    			dialog.cancel();
    		}
    	});
  	
    	showAlertDialog(addPrmpt);	
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

 
    
    protected void buildTabOptions(){    	
    	    final TabPage tabPage = (TabPage) actBar.getSelectedTab().getTag();
    		final String options[] = new String[] {
    				getResources().getString(R.string.action_editTab), 
    				getResources().getString(R.string.action_renTab),
    				getResources().getString(R.string.action_delTab),
    				getResources().getString(R.string.action_addfldr),
    				getResources().getString(R.string.action_cancel)};
    		 
             final Integer[] icons = new Integer[] 
            		 {R.drawable.ic_action_accept,
            		  R.drawable.ic_action_edit,
            	  	  R.drawable.ic_action_delete,
            	  	  R.drawable.ic_action_expand,
            		  R.drawable.ic_action_cancel};
             
            ListAdapter adapter = new IconAdapter(getApplicationContext() , options, icons);
    		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
    		alertDialog.setTitle("Update: "+tabPage.getTab())
    			.setTitle("")
    	  		.setCancelable(true)
    	  		.setAdapter(adapter, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int item) {
    				switch (item) {

    				case 0:
    					if(actBar.getSelectedNavigationIndex() < 2){
    						Toast.makeText(MainActivity.this, getString(R.string.doedits1), Toast.LENGTH_LONG).show();
    					} else { 						
    						startActivity(new Intent(MainActivity.this, EditActivity.class).putExtra(AppsService.TABPOS, tabPage.getTab()));
    					}
    					return;
    					
    				case 1: case 3:
    					if(actBar.getSelectedNavigationIndex() < 2)
    						Toast.makeText(MainActivity.this, getString(R.string.doedits1), Toast.LENGTH_LONG).show();
    					else
    						processTabOptions(item*-1);
    					return;

    				case 2:
    					if(actBar.getSelectedNavigationIndex() < 2) {       	
    						Toast.makeText(MainActivity.this, getString(R.string.doedits2), Toast.LENGTH_LONG).show();
    					} else {
    						 showAlertDialog(new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK)
    								 .setTitle(getString(R.string.action_delTab))
    								 .setIcon(android.R.drawable.ic_dialog_alert)
    								 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
    									 public void onClick(DialogInterface dialog, int whichButton) { deleteTab();}})
    								 .setNegativeButton(android.R.string.no, null));       		    	
    					}
    					return;
     
    				default:
    					return;
    				}
    			}});

    		showAlertDialog(alertDialog);
    }

    
    
   
    
    @Override
	public void finish() { AppsService.saveLayout(getApplicationContext()); super.finish(); }
    
    public static  int getPageview() { return pageview; }
    public static  boolean isGridview() { return gridview; }
    public static  boolean isGridviewdoc() { return gridviewdoc; }
    public static  boolean isTextview() { return textview; }
    public static  int getSortView() { return sortview; }
    public static String getSDPath() {return sd_path;}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  	        
    	positionTab(AppsService.currentTab); 
    	fireresult = true;
    	if(resultCode == RESULT_OK) buildAppsView();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setBackgroundDrawableResource(R.color.black);
        setContentView(R.layout.apps_main);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	final MenuInflater inflater = getMenuInflater();		
	 	inflater.inflate(R.menu.main, menu);
    	MenuItem searchItem = menu.findItem(R.id.action_search); 	
	 	searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
     	srchView = (SearchView) searchItem.getActionView();
 		srchView.setIconifiedByDefault(false);
 		srchView.setOnQueryTextListener(this); 		
 		srchView.setQuery("", false);
		srchView.clearFocus();		
 		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if(menu == null)  return super.onMenuOpened(featureId, menu);
		
		if (featureId == Window.FEATURE_ACTION_BAR) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (NoSuchMethodException e) {
					LLg.e("onMenuOpened:" + e.getMessage());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		for(int m=0; m < menu.size(); m++){
			MenuItem item = menu.getItem(m);				
			int fc = Color.WHITE;

			LLg.e("onMenuOpened: " + item.getItemId()+" : "+item.getTitle());
			switch (item.getItemId()) {

			// item_view
			case  R.id.action_gridView:
				if(isGridview()) fc = Color.YELLOW;
				break;

			case  R.id.action_ListView:
				if(! isGridview()) fc = Color.YELLOW;
				break;

			case  R.id.action_gridViewDoc:
				if(isGridviewdoc()) fc = Color.YELLOW;
				break;
				
			case  R.id.action_ListViewDoc:
				if(! isGridviewdoc()) fc = Color.YELLOW;
				break;

				//item_appname	
			case  R.id.action_showappname:
				if(isTextview()) fc = Color.YELLOW;
				break;

			case  R.id.action_hideappname:
				if(!isTextview()) fc = Color.YELLOW;
				break;

				//item_sort	
			case  R.id.action_alphSort:
				if(AppsService.ALPHASORT == getSortView() ) fc = Color.YELLOW;
				break;

			case  R.id.action_dateSort:
				if( AppsService.DATESORT  == getSortView() ) fc = Color.YELLOW;;
				break;

			case  R.id.action_freqSort:
				if( AppsService.FREQSORT == getSortView()) fc = Color.YELLOW;
				break;

				//item_page
			case  R.id.action_Flow:
				if(getPageview() == AppsService.FLOWOVER) fc = Color.YELLOW;
				break;

			case  R.id.action_Depth:
				if(getPageview() == AppsService.DEPTHOVER) fc = Color.YELLOW;
				break;

			case  R.id.action_Zoom:
				if(getPageview() == AppsService.ZOOMOVER) fc = Color.YELLOW;
				break;

			case  R.id.action_Slide:
				if(getPageview() == AppsService.SLIDEOVER) fc = Color.YELLOW;
				break;

			}

			SpannableString spanString = new SpannableString(item.getTitle().toString());
	        spanString.setSpan(new ForegroundColorSpan(fc), 0, spanString.length(), 0); 
		    item.setTitle(spanString);
		    
		}


		return super.onMenuOpened(featureId, menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drwrToggle.onOptionsItemSelected(item)) return true;

        // Handle item selection
        switch (item.getItemId()) {

            case android.R.id.home:
                positionTab(1);
                clearSearch();
                clearAllView();
                return true;

            case R.id.action_add:
                processTabOptions(R.id.action_add);
                return true;

            case R.id.action_rearrange:
                startActivityForResult(new Intent(this, TabsList.class).putExtra(AppsService.LISTER, true), 0);
                return true;


            case R.id.action_gridView:
                if (!isGridview()) {
                    setGridview(true);
                    buildAppsView();
                }
                return true;

            case R.id.action_ListView:
                if (isGridview()) {
                    setGridview(false);
                    buildAppsView();

                }
                return true;

            case R.id.action_gridViewDoc:
                if (!isGridviewdoc()) {
                    setGridviewdoc(true);
                    buildAppsView();
                }
                return true;

            case R.id.action_ListViewDoc:
                if (isGridviewdoc()) {
                    setGridviewdoc(false);
                    buildAppsView();

                }
                return true;


            case R.id.action_hideappname:
                if (isTextview()) {
                    setTextview(false);
                    setGridview(true);
                    buildAppsView();
                }
                return true;

            case R.id.action_showappname:
                if (!isTextview()) {
                    setTextview(true);
                    buildAppsView();
                }
                return true;

            case R.id.action_alphSort:
                if (getSortView() != AppsService.ALPHASORT) {
                    setSortView(AppsService.ALPHASORT);
                    buildAppsView();
                }
                return true;

            case R.id.action_dateSort:
                if (getSortView() != AppsService.DATESORT) {
                    setSortView(AppsService.DATESORT);
                    buildAppsView();
                }
                return true;

            case R.id.action_freqSort:
                if (getSortView() != AppsService.FREQSORT) {
                    setSortView(AppsService.FREQSORT);
                    buildAppsView();
                }
                return true;

            case R.id.action_Flow:
                if (getPageview() != AppsService.FLOWOVER) {
                    setPageview(AppsService.FLOWOVER);
                    buildAppsView();
                }
                return true;

            case R.id.action_Depth:
                if (getPageview() != AppsService.DEPTHOVER) {
                    setPageview(AppsService.DEPTHOVER);
                    buildAppsView();
                }
                return true;

            case R.id.action_Zoom:
                if (getPageview() != AppsService.ZOOMOVER) {
                    setPageview(AppsService.ZOOMOVER);
                    buildAppsView();
                }
                return true;

            case R.id.action_Slide:
                if (getPageview() != AppsService.SLIDEOVER) {
                    setPageview(AppsService.SLIDEOVER);
                    buildAppsView();
                }
                return true;

            case R.id.action_clearfreqs:
                showAlertDialog(
                        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                                .setMessage(getString(R.string.doprogress5))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        AppsService.clearFreqTab(getApplicationContext());
                                        buildAppsView();
                                    }
                                }).setNegativeButton(android.R.string.no, null));

                return true;

            case R.id.action_sdpath:
                final EditText input = new EditText(this);
                input.setText(sd_path);
                showAlertDialog(
                        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                                .setMessage(getString(R.string.action_sdPath))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setView(input)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        setSDPath(input.getText().toString());
                                    }
                                }).setNegativeButton(android.R.string.no, null));

                return true;

            case R.id.action_catallapps:
                showAlertDialog(
                        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                                .setMessage(getString(R.string.doprogress3))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        new CategorizeAllApplications().execute();
                                    }
                                }).setNegativeButton(android.R.string.no, null));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
	public boolean onQueryTextChange(String filter) {
		LLg.i("onQueryTextChange:"+filter);
		showAllView(filter, null);
		return false;
	}

    
    @Override
	public boolean onQueryTextSubmit(String filter) {
		LLg.i("onQueryTextSubmit:"+filter);
		return false;
	}


	@Override
    protected void onResume() {
    	super.onResume();
		SystemInfo.getSI().collectData(this);
        final SharedPreferences sp = getSharedPreferences(AppsService.PREFS, MODE_MULTI_PROCESS);
    	gridview = sp.getBoolean(AppsService.GRIDMODE, true);
    	gridviewdoc = sp.getBoolean(AppsService.GRIDMODEDOC, false);
    	textview = sp.getBoolean(AppsService.TEXTMODE, true);
    	sortview = sp.getInt(AppsService.SORTMODE,AppsService.ALPHASORT);
    	pageview = sp.getInt(AppsService.PAGEMODE, AppsService.DEPTHOVER);
    	sd_path = sp.getString(AppsService.SDPATH, sd_path);
        buildAppsView(); 
		final SharedPreferences.Editor ed = sp.edit();
		ed.putBoolean(AppsService.FIRSTRUN, false);
		ed.commit();
	}

	
	@Override
    protected void onStart() {   
    	super.onStart();  	
    }
	
	
	public void positionTab(int newPosition){   	
    	if(actBar != null) {
    		if(newPosition < actBar.getTabCount()) {
    			AppsService.currentTab = newPosition;
    			actBar.setSelectedNavigationItem(newPosition);   
    		} else {
    			Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.action_notfound),Toast.LENGTH_SHORT);
    			toast.setGravity(Gravity.TOP, 0, 0);
    			toast.show();
    		}
    	}
    }

	public void positionTab(TabPage tp){
	for (int x=0;x < actBar.getTabCount();x++)
		if(tp.getTab().equals(actBar.getTabAt(x).getText()))
			positionTab(x);
    }
	
	private void renameLaunchTab(String tab) {
		final TabPage tabPage = (TabPage) actBar.getSelectedTab().getTag();
    	LLg.i("renameTab:"+tabPage.getTab()+":"+tab);
    	tabPage.setTab(tab);
    	AppsService.saveLayout(this);
    	buildTabs();
    }
	
	public void saveSharedPreferences(){
    	final SharedPreferences.Editor editor = getSharedPreferences(AppsService.PREFS, MODE_MULTI_PROCESS).edit();
    	editor.putBoolean(AppsService.GRIDMODE,    isGridview());
    	editor.putBoolean(AppsService.GRIDMODEDOC, isGridviewdoc());
    	editor.putBoolean(AppsService.TEXTMODE,    isTextview());
    	editor.putInt(AppsService.SORTMODE,        getSortView());
     	editor.putInt(AppsService.PAGEMODE,        getPageview());
        editor.putString(AppsService.SDPATH,       getSDPath());
    	editor.commit();
    }
	
	
	private boolean showFolder(TabPage tp) {
		if(!tp.isFolder()) return false;
		String folder = tp.getTab();
		try{
			startActivityForResult(new Intent(this, AppsWindow.class)
					.putExtra(AppsService.FOLDER,  folder)
					.putExtra(AppsService.TABID,  AppsService.FOLDER + folder), 0);
		
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	

	public void setGridview(boolean gv) {
		MainActivity.gridview = gv;
	    saveSharedPreferences();
	}
	
	public void setGridviewdoc(boolean gv) {
		MainActivity.gridviewdoc = gv;
	    saveSharedPreferences();
	}
	
	public void setTextview(boolean tv) {
		MainActivity.textview = tv;
	    saveSharedPreferences();
	}
	
	public void setSortView(int sv) {
		MainActivity.sortview = sv;
	    saveSharedPreferences();
	}
	
	public void setPageview(int pv) {
		MainActivity.pageview = pv;
	    saveSharedPreferences();
	}

    public void setSDPath(String sd) {
        MainActivity.sd_path = sd;
        saveSharedPreferences();
    }

	protected void showAllView(String filter,Parcelable state ) {
    	if(filter != null && filter.length() > 0 ) positionTab(1);
    	if(tabAdapter != null) tabAdapter.postAllFilter(filter, state);
    }

	public void clearAllView() {
		showAllView("",null);
    }
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		tabPager.setCurrentItem(tab.getPosition());		
		
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	
 

    
}
