package net.ossfree.launcher4.Adapters;

import java.util.ArrayList;
import java.util.List;

import net.ossfree.launcher4.R;
import net.ossfree.launcher4.AppsService;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.TabPage;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class EditAdapter extends ArrayAdapter <ItemInfo>  {
	private  List<ItemInfo> allList = null;
	private TabPage tabPage;
	private LayoutInflater layoutInflater = null;;
	
	public EditAdapter(Context context, int textViewResourceId, TabPage tabPageID) {
		super(context, textViewResourceId, AppsService.applist);
		context.getPackageManager();	
		tabPage = tabPageID; 
		
		allList = new ArrayList<ItemInfo>();
		allList.clear();
		allList.addAll(AppsService.applist);
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
		
	@Override
	public int getCount() {
		return ((null != allList) ? allList.size() : 0);
	}

	@Override
	public ItemInfo getItem(int position) {
		return ((allList != null) ? allList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;
		if ((v == null) || (v.getTag() == null)) {
			v = layoutInflater.inflate(R.layout.edit_row, null);
			holder = new ViewHolder();
			holder.an = (TextView)  v.findViewById(R.id.app_name);
			holder.iv = (ImageView) v.findViewById(R.id.app_icon);
			holder.cb = (CheckBox)  v.findViewById(R.id.apps_chk);
			v.setTag(holder);			
		} else {
			holder = (ViewHolder) v.getTag();
		}  
		holder.ai = allList.get(position);
		holder.setView();		
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		ItemInfo ai;
		TextView an;
		ImageView iv;
		CheckBox cb;
		private void setView() {
			an.setText(ai.appName);			
			iv.setImageDrawable(ai.appIcon);
			cb.setChecked(tabPage.hasApp(ai.appPackage));
			cb.setOnClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View arg0) {
			        final boolean isChecked = cb.isChecked();
			        if(isChecked)
			        	tabPage.addApp(ai.appPackage);
			        else 
			        	tabPage.delApp(ai.appPackage);
			    }
			});
		}
	} 
}
 
 