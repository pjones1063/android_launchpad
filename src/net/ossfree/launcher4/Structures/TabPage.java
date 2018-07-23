package net.ossfree.launcher4.Structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TabPage implements Serializable {

	private static final long serialVersionUID = 4425392102994939155L;
	private List<String> pageApps = new ArrayList<String>();
	private String pageName = null;
	private int tabID;
	private boolean isFolder = false;

	public String getTab() {return pageName;}
	public TabPage addApp(String app) {if(!pageApps.contains(app)) pageApps.add(app);return this;}
	public TabPage delApp(String app) {pageApps.remove(app);return this;}
	public boolean hasApp(String app) {return pageApps.contains(app);}
	public TabPage addAppByPackage(String appPackage) {if(!pageApps.contains(appPackage))   pageApps.add(appPackage); return this;}
	public TabPage addAppByAppInfo(ItemInfo app) {addAppByPackage(app.appPackage); return this;}
	public TabPage removeAppByAppInfo(ItemInfo app) {removeAppByPackage(app.appPackage); return this;}
	public TabPage removeAppByPackage(String appPackage) {if(pageApps.contains(appPackage)) pageApps.remove(appPackage);return this;}
	public TabPage setTab(String tab) {pageName = tab; return this; }
	public TabPage setID(int id) {tabID = id;  return this; }
	public int     getID() {return tabID; }
	public TabPage setFolder(boolean is){isFolder = is; return this;}
	public boolean isFolder(){return isFolder;}
	 
	
	public String [] getApps() {		
		String[] ps = new String[pageApps.size()];
		ps = pageApps.toArray(ps);		
		return  ps;
	}

	public TabPage setAppsByAppInfo(List<ItemInfo> applist) {
		pageApps.clear();
		if(applist != null) for (ItemInfo ai : applist) addApp(ai.appPackage);
		return this;
	}
	
	@Override
	public boolean equals(Object object) {
        if (object != null && object instanceof TabPage) return pageName.equals( ((TabPage)object).getTab());
        return false;
    }
	
}
