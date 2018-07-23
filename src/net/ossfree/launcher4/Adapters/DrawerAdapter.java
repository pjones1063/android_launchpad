package net.ossfree.launcher4.Adapters;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Structures.TabItem;
import net.ossfree.launcher4.ViewHolders.DrawerHolder;

@SuppressLint("DefaultLocale")
public class DrawerAdapter extends ArrayAdapter<TabItem>  {
	
	private LayoutInflater layoutInflater = null; 
    private  ArrayList<TabItem> tabHolder = null;
    private int layoutResourceId;
      
	public DrawerAdapter(Context context,  int id, ArrayList<TabItem> ti) {
		super(context, id, ti);
		tabHolder = ti;
		layoutResourceId = id;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final DrawerHolder holder;
		if ((v == null) || (v.getTag() == null)) {
			v = layoutInflater.inflate(layoutResourceId ,parent, false);
			holder = new DrawerHolder();
			holder.tv = (TextView)  v.findViewById(R.id.drw_item_title);
			holder.iv = (ImageView) v.findViewById(R.id.drw_item_icon);
			v.setTag(holder);			
		} else {
			holder = (DrawerHolder) v.getTag();
		}  
		
		holder.tabItem = tabHolder.get(position);		
		holder.setView();		
		v.setTag(holder);
		return v;
	
	}
 

}
 
 