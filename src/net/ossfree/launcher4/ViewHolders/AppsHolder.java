package net.ossfree.launcher4.ViewHolders;

import net.ossfree.launcher4.MainActivity;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Structures.ItemInfo;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class AppsHolder {
	public ItemInfo ai;
	public TextView an, pn;
	public ImageView iv;
	public void setView(Context c) {		
		an.setText(MainActivity.isTextview() || !MainActivity.isGridview() ? ai.appName: null);
		pn.setText(ai.appPackage);
		if(ai.isfolder)
			iv.setImageDrawable(c.getResources().getDrawable(R.drawable.im64_folder));
		else
			iv.setImageDrawable(ai.appIcon);
	}
}