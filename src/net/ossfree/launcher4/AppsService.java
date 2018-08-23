package net.ossfree.launcher4;
 
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;

import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.Structures.DocInfo;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.PackageInfo;
import net.ossfree.launcher4.Structures.TabItem;
import net.ossfree.launcher4.Structures.TabPage;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressLint("DefaultLocale")
public class AppsService extends Service {

	public  static final String PKGDATA        = "net.ossfree.launcher4";
	public  static final String PAGEDATA       = "net.ossfree.launcher4.AppsData.dat";
	public  static final String FREQDATA       = "net.ossfree.launcher4.FreqData.dat";
	public  static final String NEWDATA        = "net.ossfree.launcher4.NewAppsData.dat";
	public  static final String FOLDER	 	   = "net.ossfree.launcher4.FolderItem.";
	public  static final String TABPOS         = "net.ossfree.launcher4.TabPosition.";
	public  static final String TABID          = "net.ossfree.launcher4.TabID.";
	public  static final String CLICK          = "net.ossfree.launcher4.WidetClicker.";
	public  static final String PREFS          = "net.ossfree.launcher4.SharedPrefs.";
	public  static final String GRIDMODE       = "net.ossfree.launcher4.GridMode.";	
	public  static final String GRIDMODEDOC    = "net.ossfree.launcher4.GridModeDoc.";
	public  static final String TEXTMODE       = "net.ossfree.launcher4.TextMode.";
	public  static final String PAGEMODE       = "net.ossfree.launcher4.PageMode.";	
	public  static final String SORTMODE       = "net.ossfree.launcher4.SortMode.";
    public  static final String SDPATH         = "net.ossfree.launcher4.SDFilePath.";
    public  static final String LNCHAPP		   = "net.ossfree.launcher4.LaunchApp.";
	public  static final String LISTER		   = "net.ossfree.launcher4.ListerApp.";
	public  static final String DROPPER		   = "net.ossfree.launcher4.DropperApp.";
	public  static final String FIRSTRUN	   = "net.ossfree.launcher4.FirstRun.";
	public  static final String TARGET   	   = "net.ossfree.launcher4.TargetTab.";

		
	public static final int TABEXACT  = 1963;
	public static final int FLOWOVER  = 10;
	public static final int DEPTHOVER = 20;
	public static final int ZOOMOVER  = 30;
	public static final int SLIDEOVER = 40;
	public static final int ALPHASORT = 110;
	public static final int DATESORT  = 120;
	public static final int FREQSORT  = 130;
  	public static final int ALLID     = 196310;
	public static final int FRQID     = 196320;
	public static final int NEWID     = 196330;
	public static final int DOCID     = 196340;
	public static final int SDID      = 196345;
	public static final int OTHID     = 196350;
    public static final int STATID    = 196360;

	private static boolean ongoing = false;	
	public  static int currentTab = 0;	
	public  static List<ItemInfo>    applist  = null;
	public  static List<PackageInfo> freqList = null;
	public  static List<TabPage>     pagelist = null;
	public  static String tab_MyApps  = null;

	public  static String tab_AllApps = null;
	public  static String tab_FrqApps = null;
	public  static String tab_NewApps = null;		
	public  static String tab_MyDocs  = null;
	public  static String tab_MySD    = null;
	public  static String tab_Stat    = null;
 
	public static ItemInfo getAppInfo(PackageInfo pi){
		if(applist == null || pi == null) return null;				
	        	for (ItemInfo ai : applist) if(ai.appPackage.equals(pi.appPackage)) return ai;
		return null;
	}
	
	public  void doPackDelete(String appPackage){
		Uri packageURI = Uri.parse("package:"+appPackage);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent);	
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();      
    	
		if(applist == null && !ongoing) getAppsByLaunchIntent(this);
		
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent activity = PendingIntent.getActivity(this, 0, intent,0);
		final Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		final Notification nx  = new Notification.Builder(getApplicationContext())
			.setTicker(tab_MyApps)
			.setContentTitle(tab_MyApps)			
			.setSmallIcon(R.drawable.ic_launcher)
			.setLargeIcon(mBitmap) 
			.setContentIntent(activity)
			.setAutoCancel(false).build();
		
		nx.flags = Notification.FLAG_ONGOING_EVENT;
		nx.flags |=Notification.FLAG_NO_CLEAR;
		startForeground(1337, nx);  
		//setBadge(getApplicationContext(),0);
		currentTab = 0;
	}

	@Override
	public void onDestroy() {
		applist = null;		
		saveLayout(getApplicationContext());
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
 
	public static void addFreqApp(String packName){
        LLg.i("addFreqApp:"+packName);
		if(freqList == null) freqList = new ArrayList<PackageInfo>();
		int p = freqList.indexOf(new PackageInfo(packName));
		if(p > -1)  freqList.get(p).appUsedCount++;
		else freqList.add(new PackageInfo(packName));		
	}

	public static void removeFreqApp(String packName) {
        LLg.i("removeFreqApp:"+packName);
		if(freqList == null) return;		
		int p = freqList.indexOf(new PackageInfo(packName));
		if(p > -1)  freqList.remove(p);				
	}
 
	
	public static boolean isFolderVisable(String folder) {
		LLg.i("isFolderVisable:"+folder);
		for(TabPage tp : AppsService.pagelist) if(tp.hasApp(FOLDER + folder)) return true;				
		return false;
	}
	
		
	public static void renameFolder(Context context, String oldName, String newName) {
		LLg.i("renameFolder:"+oldName+":"+newName);
		int p = 0;
		if(applist != null) {
			do {
				p = applist.indexOf(new ItemInfo(oldName, FOLDER + oldName, 0, null, true));
				if(p > -1) {
					applist.get(p).appName = newName;
					applist.get(p).appPackage = FOLDER + newName;
					applist.get(p).appIcon = context.getResources().getDrawable(R.drawable.im64_folder);
				}
			} while(p > -1);
	     }

		for(TabPage tp : AppsService.pagelist)	{
			if(tp.isFolder() && tp.getTab().equals(oldName)) tp.setTab(newName);
			if(tp.hasApp(FOLDER + oldName)) {
				tp.delApp(FOLDER + oldName);
				tp.addApp(FOLDER + newName);
			}
		}	
		saveLayout(context);
	}
	
	
	public static void deleteFolder(Context context, String folderName) {
        LLg.i("deleteFolder:"+folderName);
		deleteFolder(folderName);
		saveLayout(context);
	}

	
	private static void deleteFolder(String folderName) {
		LLg.i("deleteFolder:"+folderName);
		if(pagelist != null) {
			for(TabPage tp : AppsService.pagelist) 
				if(tp.hasApp(FOLDER + folderName)) tp.delApp(FOLDER + folderName);

			TabPage t = new TabPage().setTab(folderName).setFolder(true);
			while (pagelist.contains(t)) pagelist.remove(t);				
		}

		if(applist != null) { 
			applist.remove(new ItemInfo(folderName, FOLDER + folderName, 0, null, true));
		}
		
	}
	
	public static void deleteTab(Context context, String tabName) {
		LLg.i("deleteTab:"+tabName);
		String apps [] = getPage(tabName).getApps();		
		for(String app : apps)
			if(app.startsWith(FOLDER)) deleteFolder(app.substring(FOLDER.length()));		
		
		if(pagelist != null) pagelist.remove(new TabPage().setTab(tabName));	
		
		saveLayout(context);
	}
	
	public static void clearFreqTab(Context context) {
		LLg.i("clearFreqTab");
		freqList.clear();
		saveLayout(context);
 	}
	
	public static void saveLayout(Context context) {
		LLg.i("saveLayout");
		if(pagelist == null) return;
		ObjectOutputStream out = null; 		
		try {	
				
		    out = new ObjectOutputStream(context.openFileOutput(PAGEDATA, Context.MODE_PRIVATE));
			out.writeObject(pagelist);		
		    out.flush();
		    out.close();
		    
		    out = new ObjectOutputStream(context.openFileOutput(FREQDATA, Context.MODE_PRIVATE));
			out.writeObject(freqList);		
		    out.flush();
		    out.close();		   
		    
		} catch (Exception e) {
			LLg.e("persistSave"+e.getMessage());
		}
 	}
 
	
	public static List<TabPage> loadLayout(Context context) {	
		LLg.i("loadLayout");
		pagelist = new ArrayList<TabPage>();		
		ObjectInputStream in = null;
		try {
		    in = new ObjectInputStream(context.openFileInput(PAGEDATA));
			ArrayList<TabPage> pageObject = (ArrayList<TabPage>)in.readObject();
			pagelist = pageObject;
		    in.close();
		    
		    in = new ObjectInputStream(context.openFileInput(FREQDATA));
			ArrayList<PackageInfo> freqObject = (ArrayList<PackageInfo>)in.readObject();
			freqList = freqObject;
		    in.close();
		    
		} catch (Exception e) {
			LLg.e("persistLoad:"+e.getMessage());
		} 
		
		if(pagelist.isEmpty()){
            LLg.i("new - loadLayout");

            AppsService.tab_MyApps	= context.getString(R.string.tab_MyApps);

	    	AppsService.tab_AllApps = context.getString(R.string.tab_AllApps);
	    	AppsService.tab_FrqApps = context.getString(R.string.tab_FrqApps);
	    	AppsService.tab_NewApps = context.getString(R.string.tab_NewApps);    
	    	AppsService.tab_MyDocs = context.getString(R.string.tab_MyDocs);
            AppsService.tab_MySD   = context.getString(R.string.tab_MySD);
			AppsService.tab_Stat = context.getString(R.string.tab_DeviceStatus);

            pagelist.add(new TabPage().setTab(tab_Stat).setID(STATID));
			pagelist.add(new TabPage().setTab(tab_AllApps).setID(ALLID).setAppsByAppInfo(applist));
			pagelist.add(new TabPage().setTab(tab_FrqApps).setID(FRQID));
			pagelist.add(new TabPage().setTab(tab_NewApps).setID(NEWID));
			pagelist.add(new TabPage().setTab(tab_MyDocs).setID(DOCID));
			pagelist.add(new TabPage().setTab(tab_MySD).setID(SDID));

		}
		appendFolders(context);
		return pagelist;
 	}
	
	
	public static void getAppsByLaunchIntent(Context context) {		
		LLg.i("getAppsByLaunchIntent");
		ongoing = true;
		PackageManager pm = context.getPackageManager();
		
		Set<ItemInfo> apps = Collections.synchronizedSet(new HashSet<ItemInfo>());		
		List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		
		for (ApplicationInfo info : list) {
			try {
				if (null != pm.getLaunchIntentForPackage(info.packageName) && !info.packageName.equals(PKGDATA)) {
				 
					ItemInfo ai = new ItemInfo(info.loadLabel(pm).toString(),
							                   info.packageName, 
							                   pm.getPackageInfo((info.packageName), 0).firstInstallTime,  
							                   info.loadIcon(pm), false);
					apps.add(ai);					
				}
				
			} catch (Exception e) {
				LLg.e("getAppsByLaunchIntent:"+e.getMessage());
			}
		}

		applist = new ArrayList<ItemInfo>(apps);
		appendFolders(context);
		ongoing = false;		
	}

 	

	private static void appendFolders (Context context) {
        LLg.i("appendFolders");
		if(pagelist != null && applist != null)
			for(TabPage fp :pagelist) {
				if(fp.isFolder()) {
					ItemInfo fi = new ItemInfo(fp.getTab(),AppsService.FOLDER + fp.getTab(), -99999999,  
							context.getResources().getDrawable(R.drawable.im64_folder ), true);
					if(!applist.contains(fi)) applist.add(fi);
				}
			}
	}

	
   public static List<TabPage> movePage(Context context, String from, String to) {
       LLg.i("movePage"+from+to);
		int f=0, t=0;
		for(int i=0;i < pagelist.size();i++) {
			TabPage tp = pagelist.get(i);
			if(tp != null)
				if(tp.getTab().equals(from)) f = i;
				else if( tp.getTab().equals(to))  t = i;			
		}
		if( t > 1 && t > f)      Collections.rotate(pagelist.subList(f, t+1), -1);
		else if( t > 1 && f > t) Collections.rotate(pagelist.subList(t+1, f+1), +1);

		
		saveLayout(context);

		return pagelist;
	}


   public static int getFolderIcon(String folder) {

	   if(folder.trim().startsWith("-A"))  return R.drawable.im64_home;
	    else if(folder.trim().startsWith("-F"))  return R.drawable.im64_app_all;
	    else if(folder.trim().startsWith("-N"))  return R.drawable.im64_app;
	    else if(folder.trim().startsWith("-M"))  return R.drawable.im64_card;
        else if(folder.trim().startsWith("-S"))  return R.drawable.im64_app_system;

       char i = folder.trim().toLowerCase().charAt(0);
	   switch (i) {
	   case 'a': return R.drawable.alpa_a;
	   case 'b': return R.drawable.alpa_b;
	   case 'c': return R.drawable.alpa_c;
	   case 'd': return R.drawable.alpa_d;
	   case 'e': return R.drawable.alpa_e;
	   case 'f': return R.drawable.alpa_f;
	   case 'g': return R.drawable.alpa_g;
	   case 'h': return R.drawable.alpa_h;
	   case 'i': return R.drawable.alpa_i;
	   case 'j': return R.drawable.alpa_j;
	   case 'k': return R.drawable.alpa_k;
	   case 'l': return R.drawable.alpa_l;
	   case 'm': return R.drawable.alpa_m;
	   case 'n': return R.drawable.alpa_n;
	   case 'o': return R.drawable.alpa_o;
	   case 'p': return R.drawable.alpa_p;
	   case 'q': return R.drawable.alpa_q;
	   case 'r': return R.drawable.alpa_r;
	   case 's': return R.drawable.alpa_s;
	   case 't': return R.drawable.alpa_t;
	   case 'u': return R.drawable.alpa_u;
	   case 'v': return R.drawable.alpa_v;
	   case 'w': return R.drawable.alpa_w;
	   case 'x': return R.drawable.alpa_x;
	   case 'y': return R.drawable.alpa_y;
	   case 'z': return R.drawable.alpa_z;
	   default: return R.drawable.im64_app_all;
	   }
   }


   
public static  ArrayList<DocInfo> getDocuments(String path) {
	   ArrayList<DocInfo>  docList = new ArrayList<DocInfo>();
	   try {
		   File directory = new File(path);
		   File[] files = directory.listFiles();
		   for (int i = 0; i < files.length; i++) {
			   if (!files[i].getName().startsWith("."))
				    docList.add(new DocInfo(files[i].getName(), files[i].getAbsolutePath(), files[i].isDirectory() ));
		   }	   
	   } catch (Exception e) {
		   LLg.e(e.getLocalizedMessage());
	   }
	   return docList;
   }
   

   public static int getDocumentType(String path) {
			int i = path .lastIndexOf('.');
			if (i > 0) {
				
				String item_ext = path.substring(i);
				if (item_ext.equalsIgnoreCase(".mp3") || item_ext.equalsIgnoreCase(".m4a")
						|| item_ext.equalsIgnoreCase(".wma")	|| item_ext.equalsIgnoreCase(".mp4")) 
					return R.drawable.im64_music;

				/* photo file selected */
				else if (item_ext.equalsIgnoreCase(".jpeg") || item_ext.equalsIgnoreCase(".jpg")
						|| item_ext.equalsIgnoreCase(".png") || item_ext.equalsIgnoreCase(".gif")
						|| item_ext.equalsIgnoreCase(".tiff")) 
					return R.drawable.im64_image;

				/* video file selected--add more video formats */
				else if (item_ext.equalsIgnoreCase(".m4v") || item_ext.equalsIgnoreCase(".3gp")
						|| item_ext.equalsIgnoreCase(".wmv") || item_ext.equalsIgnoreCase(".mp4")
						|| item_ext.equalsIgnoreCase(".ogg") || item_ext.equalsIgnoreCase(".wav"))  
					return R.drawable.im64_video;

				/* pdf file selected */
				else if (item_ext.equalsIgnoreCase(".pdf")) 
					return R.drawable.im64_pdf;

				/* Android application file */
				else if (item_ext.equalsIgnoreCase(".apk")) 	
					return R.drawable.alpa_a;

				/* HTML file */
				else if (item_ext.equalsIgnoreCase(".html")) 	
					return R.drawable.im64_file_generic;
				/* text file */
				
				/* excel file selected */
				else if (item_ext.equalsIgnoreCase(".xls") || item_ext.equalsIgnoreCase(".xlsx")   ) 
					return R.drawable.im64_xls;
				
				else if (item_ext.equalsIgnoreCase(".docx") || item_ext.equalsIgnoreCase(".doc")  
				  		|| item_ext.equalsIgnoreCase(".rtf")  ) 
					return R.drawable.im64_doc;
			    
				else 
					return  R.drawable.im64_document;
					
			}
		
			return getFolderIcon(path);
	
   }
   
   public static boolean isDocumentImage(String path) {
			int i = path .lastIndexOf('.');
			if (i > 0) {				
				String item_ext = path.substring(i);
				/* photo file selected */
				if (item_ext.equalsIgnoreCase(".jpeg") || item_ext.equalsIgnoreCase(".jpg")
						|| item_ext.equalsIgnoreCase(".png") || item_ext.equalsIgnoreCase(".gif")
						|| item_ext.equalsIgnoreCase(".tiff") || item_ext.equalsIgnoreCase(".bmp")) 
					return true;

			}
			
			return false;
   }

   public static boolean isDocumentMovie(String path) {
		int i = path .lastIndexOf('.');
		if (i > 0) {				
			String item_ext = path.substring(i);
			if (item_ext.equalsIgnoreCase(".m4v") || item_ext.equalsIgnoreCase(".3gp")
					|| item_ext.equalsIgnoreCase(".wmv") || item_ext.equalsIgnoreCase(".mp4")
					|| item_ext.equalsIgnoreCase(".ogg") || item_ext.equalsIgnoreCase(".wav")) 
				return true;
			
		}
		return false;
   }
   
   
   
   public static Intent getShowDocumentIntent(DocInfo doc) {
	   			
	   		File file = new File(doc.appPackage);
			int i = file.getAbsolutePath().lastIndexOf('.');
			if (i > 0) {
				String item_ext = file.getAbsolutePath().substring(i);
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);

				if (item_ext.equalsIgnoreCase(".mp3") || item_ext.equalsIgnoreCase(".m4a")
						|| item_ext.equalsIgnoreCase(".wma") || item_ext.equalsIgnoreCase(".mp4")) {
					intent.setDataAndType(Uri.fromFile(file), "audio/*");

				}

				/* photo file selected */
				else if (item_ext.equalsIgnoreCase(".jpeg") || item_ext.equalsIgnoreCase(".jpg")
						|| item_ext.equalsIgnoreCase(".png") || item_ext.equalsIgnoreCase(".gif")
						|| item_ext.equalsIgnoreCase(".tiff")) {

					intent.setDataAndType(Uri.fromFile(file), "image/*");
				}

				/* video file selected--add more video formats */
				else if (item_ext.equalsIgnoreCase(".m4v") || item_ext.equalsIgnoreCase(".3gp")
						|| item_ext.equalsIgnoreCase(".wmv") || item_ext.equalsIgnoreCase(".mp4")
						|| item_ext.equalsIgnoreCase(".ogg") || item_ext.equalsIgnoreCase(".wav")) {

					intent.setDataAndType(Uri.fromFile(file), "video/*");
				}

				/* pdf file selected */
				else if (item_ext.equalsIgnoreCase(".pdf")) {
					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
				}

				/* excel file selected */
				else if (item_ext.equalsIgnoreCase(".xls") || item_ext.equalsIgnoreCase(".xlsx")   ) {
					intent.setDataAndType(Uri.fromFile(file), "application/msexcel");
				}
				
				/* word file selected */
				else if (item_ext.equalsIgnoreCase(".docx") || item_ext.equalsIgnoreCase(".doc")  
				  		|| item_ext.equalsIgnoreCase(".rtf")  ) {
				intent.setDataAndType(Uri.fromFile(file), "application/msword");
			    }
				
				/* Android application file */
				else if (item_ext.equalsIgnoreCase(".apk")) {
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				}

				/* Android application file */
				else if (item_ext.equalsIgnoreCase(".zip")) {
					intent.setDataAndType(Uri.fromFile(file), "application/zip");
				}
				
				/* HTML file */
				else if (item_ext.equalsIgnoreCase(".html")) {
					intent.setDataAndType(Uri.fromFile(file), "text/html");
				}

				/* text file */
				else {
					intent.setDataAndType(Uri.fromFile(file), "text/plain");
				}

				return intent;
			}
		
	return null;
	
   }
   
   
   public static Intent getShareDocumentIntent(DocInfo doc) {
       File file = new File(doc.appPackage);	
		int i = file.getAbsolutePath().lastIndexOf('.');
		if (i > 0) {
			String item_ext = file.getAbsolutePath().substring(i);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			Uri u = Uri.parse("file:///"+doc.appPackage);
			intent.putExtra(Intent.EXTRA_STREAM, u);
			if (item_ext.equalsIgnoreCase(".mp3") || item_ext.equalsIgnoreCase(".m4a")
					|| item_ext.equalsIgnoreCase(".wma") || item_ext.equalsIgnoreCase(".mp4")) {
				intent.setType("audio/*");
			}

			/* photo file selected */
			else if (item_ext.equalsIgnoreCase(".jpeg")   || item_ext.equalsIgnoreCase(".jpg")
					|| item_ext.equalsIgnoreCase(".png")  || item_ext.equalsIgnoreCase(".gif")
					|| item_ext.equalsIgnoreCase(".tiff") || item_ext.equalsIgnoreCase(".bmp")) {
				intent.setType( "image/*");
			}

			/* video file selected--add more video formats */
			else if (item_ext.equalsIgnoreCase(".m4v") || item_ext.equalsIgnoreCase(".3gp")
					|| item_ext.equalsIgnoreCase(".wmv") || item_ext.equalsIgnoreCase(".mp4")
					|| item_ext.equalsIgnoreCase(".ogg") || item_ext.equalsIgnoreCase(".wav")) {
				intent.setType( "video/*");
			}

			/* pdf file selected */
			else if (item_ext.equalsIgnoreCase(".pdf")) {
				 intent.setType("application/pdf");
			}

			/* Android application file */
			else if (item_ext.equalsIgnoreCase(".apk")) {
				intent.setType("application/vnd.android.package-archive");
			}

			/* HTML file */
			else if (item_ext.equalsIgnoreCase(".html")) {
				intent.setType("text/html");
			}

			/* text file */
			else {
				intent.setType("text/plain");
			}
			return intent;
		}

		return null;
   }
   
	public static ArrayList<TabItem> buildDropList(Context context, String appid) {
	  appendFolders(context);
	  ArrayList<TabItem> pages = new ArrayList<TabItem>();
	  pages.clear();
	  
	  if(appid.startsWith(FOLDER)) {
		 pages.add(new TabItem(context.getString(R.string.action_DELFOLDER), context.getResources().getDrawable(R.drawable.tb_trash), AppsService.OTHID));
		 pages.add(new TabItem(context.getString(R.string.action_FOLDERINFO), context.getResources().getDrawable(R.drawable.action_search), AppsService.OTHID));		  
	   } else {	  
	     pages.add(new TabItem(context.getString(R.string.action_UNINSTALL), context.getResources().getDrawable(R.drawable.tb_trash), AppsService.OTHID));
	     pages.add(new TabItem(context.getString(R.string.action_APPINFO), context.getResources().getDrawable(R.drawable.action_search), AppsService.OTHID));
	  }
	  pages.add(new TabItem(context.getString(R.string.action_REMOVEAPS), context.getResources().getDrawable(R.drawable.tb_x),-1));
	  
	  for(TabPage tp: pagelist)
		    if(tp.getID() != ALLID && tp.getID() != FRQID && tp.getID() != NEWID && tp.getID() != DOCID  && tp.getID() != SDID && tp.getID() != STATID )
	       	    pages.add(new TabItem(tp.getTab(), context.getResources().getDrawable(getFolderIcon(tp.getTab())),tp.getID()));
	  	  
	  return pages;
	}
	
	
	public static ArrayList<TabItem> buildTabList(Context context) {
		  ArrayList<TabItem> pages = new ArrayList<TabItem>();
		  pages.clear();
		  for(TabPage tp: pagelist)
		      if(!tp.isFolder())  pages.add(new TabItem(tp.getTab(), context.getResources().getDrawable(getFolderIcon(tp.getTab())),tp.getID())); 
		  
		  return pages;
		}
	
	public static TabPage getPage(String pageName ) {
		  if(pagelist != null) for(TabPage tp: pagelist) if(tp.getTab().equals(pageName))  return tp;
		  return null;
		}

	
	public static List<TabPage>  categorizeAllApplications(Context context) {
		LLg.i("categorizeAllApplications");
		pagelist = new ArrayList<TabPage>();
        pagelist.add(new TabPage().setTab(tab_Stat).setID(STATID));
		TabPage all = new TabPage().setTab(tab_AllApps).setID(ALLID);
		pagelist.add(all);		
		pagelist.add(new TabPage().setTab(tab_FrqApps).setID(FRQID));
		pagelist.add(new TabPage().setTab(tab_NewApps).setID(NEWID));
		pagelist.add(new TabPage().setTab(tab_MyDocs).setID(DOCID));
		pagelist.add(new TabPage().setTab(tab_MySD).setID(SDID));


		for (ItemInfo ai: AppsService.applist) {			
			String ctgr = AppsByCatagory.getCategory(ai.appPackage);
			TabPage tb = (new TabPage()).setTab(ctgr).setFolder(false);
			int t = pagelist.indexOf(tb);						
			if(t == -1)
				pagelist.add(tb);
			else 
				tb = pagelist.get(t);
			
			tb.addApp(ai.appPackage);		
			all.addApp(ai.appPackage);
		}
		
		Collections.sort(pagelist, new Comparator<TabPage>() {
			@Override
			public int compare(TabPage a, TabPage b) {
				return a.getTab().compareTo(b.getTab());
			}
		});
		
		appendFolders(context);
		
		return pagelist;
	}

	
	public static void setBadge(Context context, int count) {
		final String launcherClassName = getLauncherClassName(context);
	    if (launcherClassName == null) {
	        return;
	    }
	    final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
	    intent.putExtra("badge_count", count);
	    intent.putExtra("badge_count_package_name", context.getPackageName());
	    intent.putExtra("badge_count_class_name", launcherClassName);
	    context.sendBroadcast(intent);
	}


	public static String getLauncherClassName(Context context) {

		final PackageManager pm = context.getPackageManager();
		final Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);

	    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
	    for (ResolveInfo resolveInfo : resolveInfos) {
	        String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
	        if (pkgName.equalsIgnoreCase(context.getPackageName())) {
	            String className = resolveInfo.activityInfo.name;
	            return className;
	        }
	    }
	    return null;
	}

}
 
