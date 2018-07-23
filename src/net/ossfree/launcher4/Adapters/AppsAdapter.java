package net.ossfree.launcher4.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import net.ossfree.launcher4.AppsService;
import net.ossfree.launcher4.MainActivity;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.PackageInfo;
import net.ossfree.launcher4.Structures.TabPage;
import net.ossfree.launcher4.ViewHolders.AppsHolder;

@SuppressLint("DefaultLocale")
@SuppressWarnings("unchecked")
public class AppsAdapter extends ArrayAdapter <ItemInfo>  {

	private  List<ItemInfo> filterList = null;
	private  List<ItemInfo> allList = null;;
	private TabPage tabPage;
	private AppFilter filter = null;	
	private LayoutInflater layoutInflater = null;
	int rowid = 0;

	private class AppFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults retvalue = new FilterResults();
			List<ItemInfo> filt = new ArrayList<ItemInfo>();
			
			if(tabPage.getID() == AppsService.NEWID)
				filt = filterNew(constraint);
			else if(tabPage.getID() == AppsService.FRQID)
				filt = filterFreq(constraint);
			else
				filt = filterAll(constraint);
			
			retvalue.count = filt.size();
			retvalue.values = filt;
			return retvalue;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			notifyDataSetChanged();
			LLg.d(results.toString());
			filterList.clear();
			if(results.count > 0) filterList.addAll((ArrayList<ItemInfo>) results.values);
		 
			notifyDataSetInvalidated();
		}
		
		
		private List<ItemInfo> filterAll(CharSequence constraint) {
			 
			String csnst = "";
			if(constraint != null) csnst = constraint.toString().toUpperCase();
			ArrayList<ItemInfo> filt = new ArrayList<ItemInfo>();
			for (ItemInfo ai : allList) {
				if(ai.appPackage != null && !ai.appPackage.equals("")) {
					String pl = ai.appName.toUpperCase();
					String pk = ai.appPackage;
					if (csnst.isEmpty() && tabPage.hasApp(pk))           filt.add(ai);
					else if(csnst.length() == 1 && pl.startsWith(csnst)) filt.add(ai);
					else if(csnst.length() >  1 && pl.contains(csnst))   filt.add(ai);
				}
			}

			if(MainActivity.getSortView() == AppsService.ALPHASORT)  {
				Collections.sort(filt, new Comparator<ItemInfo>() {
					@Override
					public int compare(ItemInfo  a, ItemInfo  b){
						return  a.appName.toUpperCase().compareTo(b.appName.toUpperCase());
					}
				});	
				return filt;

			} else if(MainActivity.getSortView() == AppsService.DATESORT) { 
				Collections.sort(filt, new Comparator<ItemInfo>() {
					@Override
					public int compare(ItemInfo  a, ItemInfo  b){
						if(a.appDate < b.appDate) return 1;
						if(a.appDate > b.appDate) return -1;
						else return 0;
					}
				});		
				return filt;

			} else {
				List<PackageInfo> packageList = new ArrayList<PackageInfo>(AppsService.freqList);
				ArrayList<ItemInfo> f2 = new ArrayList<ItemInfo>();
				Collections.sort(packageList, new Comparator<PackageInfo>() {
					@Override
					public int compare(PackageInfo  a, PackageInfo  b){
						if(a.appUsedCount < b.appUsedCount) return 1;
						if(a.appUsedCount > b.appUsedCount) return -1;
						else return 0;
					}
				});
				
				for(int i=0; i < packageList.size(); i++) {
					ItemInfo ai = AppsService.getAppInfo(packageList.get(i));
					if(ai != null && filt.contains(ai)) f2.add(ai);
				}
				
				for(int i=0; i < filt.size(); i++) {
					ItemInfo ai = filt.get(i);
					if(!f2.contains(ai)) f2.add(ai);
				}
				return f2;
			} 	
		}

		
		private List<ItemInfo> filterNew(CharSequence constraint) {	
			 
			List<ItemInfo> filt = new ArrayList<ItemInfo>();
			List<ItemInfo> all = new ArrayList<ItemInfo>(allList);
			Collections.sort(all, new Comparator<ItemInfo>() {
				@Override
				public int compare(ItemInfo  a, ItemInfo  b){
					if(a.appDate < b.appDate) return 1;
					if(a.appDate > b.appDate) return -1;
					else return 0;
				}
			});						
			for(int i=0; i < all.size() && i < 9; i++) filt.add(all.get(i));
			return filt;    
		}
		
		
		private List<ItemInfo> filterFreq(CharSequence constraint) {	
			 
			List<ItemInfo> filt = new ArrayList<ItemInfo>();
			List<PackageInfo> packageList = new ArrayList<PackageInfo>(AppsService.freqList);
			Collections.sort(packageList, new Comparator<PackageInfo>() {
				@Override
				public int compare(PackageInfo  a, PackageInfo  b){
					if(a.appUsedCount < b.appUsedCount) return 1;
					if(a.appUsedCount > b.appUsedCount) return -1;
					else return 0;
				}
			});			
			for(int i=0; i < packageList.size() && i < 9; i++) {
				ItemInfo ai = AppsService.getAppInfo(packageList.get(i));
				if(ai != null) filt.add(ai);
			}
			return filt;    
		}
	}
	

	

	public AppsAdapter(Context context, int textViewResourceId, TabPage tabPageID) {
		super(context, textViewResourceId, AppsService.applist);
		rowid = textViewResourceId;
		allList = new ArrayList<ItemInfo>(AppsService.applist);
		filterList = new ArrayList<ItemInfo>(allList);
		tabPage = tabPageID; 
		
		getFilter().filter("");
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return ((null != filterList) ? filterList.size() : 0);
	}

	public Filter getFilter() {
		if (filter == null){
			filter = new AppFilter();
		}
		return filter;
	}
	
	
	
	@Override
	public ItemInfo getItem(int position) {
		return ((filterList != null) ? filterList.get(position) : null);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final AppsHolder holder;
		if ((v == null) || (v.getTag() == null)) {
			v = layoutInflater.inflate(rowid, null);
			holder = new AppsHolder();
			holder.an = (TextView)  v.findViewById(R.id.app_name);
			holder.pn = (TextView)  v.findViewById(R.id.app_paackage);
			holder.iv = (ImageView) v.findViewById(R.id.app_icon);
			v.setTag(holder);			
		} else {
			holder = (AppsHolder) v.getTag();
		}  
		
		holder.ai = filterList.get(position);
		holder.setView(getContext());		
		v.setTag(holder);
		
		return v;
	}
 
 
}
 
 