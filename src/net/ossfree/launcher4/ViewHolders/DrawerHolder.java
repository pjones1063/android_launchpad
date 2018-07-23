package net.ossfree.launcher4.ViewHolders;

import android.widget.ImageView;
import android.widget.TextView;
import net.ossfree.launcher4.Structures.TabItem;

public class DrawerHolder { 
	public TabItem tabItem;
	public TextView tv;
	public ImageView iv;
	public void setView() {
		tv.getResources();
		tv.setText(tabItem.tabName);
		iv.setImageDrawable(tabItem.tabIcon);	
	}
}

