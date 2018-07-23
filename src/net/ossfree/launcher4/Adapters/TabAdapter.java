package net.ossfree.launcher4.Adapters;

import java.util.ArrayList;

import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Structures.TabItem;
import net.ossfree.launcher4.ViewHolders.TabHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class TabAdapter extends ArrayAdapter<TabItem>  {
	
	private LayoutInflater layoutInflater = null; 
    private  ArrayList<TabItem> tabHolder = null;
      
	public TabAdapter(Context context, ArrayList<TabItem> ti) {
		super(context, R.layout.tab_list_item, ti);
		tabHolder = ti;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final TabHolder holder;
		if ((v == null) || (v.getTag() == null)) {
			v = layoutInflater.inflate(R.layout.tab_list_item,parent, false);
			holder = new TabHolder();
			holder.tv = (TextView)  v.findViewById(R.id.tab_item_title);
			holder.iv = (ImageView) v.findViewById(R.id.tab_item_icon);			
			v.setTag(holder);			
		} else {
			holder = (TabHolder) v.getTag();
		}  
		holder.tabItem = tabHolder.get(position);		
		holder.setView();		
		v.setTag(holder);
		return v;
	}
 
}
 
 