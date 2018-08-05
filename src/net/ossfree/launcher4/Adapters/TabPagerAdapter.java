package net.ossfree.launcher4.Adapters;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import net.ossfree.launcher4.AppsList;
import net.ossfree.launcher4.AppsService;
import net.ossfree.launcher4.DocList;
import net.ossfree.launcher4.Structures.TabPage;
	
	
public class TabPagerAdapter extends FragmentStatePagerAdapter {
	
	private SparseArray<AppsList> pages = new SparseArray<AppsList>();
	private int totalPages = -1;

	public TabPagerAdapter(FragmentManager fm) {super(fm);}
	public void postAllFilter(String filter, Parcelable state ){ pages.get(0).postAllFilter(filter, state);}	
	public void postFilter(String filter, int i){if (i < totalPages) pages.get(i).postFilter(filter);}
	
	@Override
	public int getCount() {return totalPages;}
	
	@Override
	public Fragment getItem(int i) {if(i < totalPages) return pages.get(i); return null;}

	public void addTabs(List<TabPage> tabPages) {
		totalPages = 0;
		for(TabPage tp : tabPages)	  
		  if(tp.getID() == AppsService.DOCID)    pages.put(totalPages++, new DocList<Object>(tp));
		  else if(tp.getID() == AppsService.SDID)   pages.put(totalPages++, new DocList<Object>(tp));
 		  else if(!tp.isFolder())               pages.put(totalPages++, new AppsList(tp));
    }
	
}