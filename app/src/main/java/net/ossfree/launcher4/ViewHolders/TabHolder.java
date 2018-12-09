package net.ossfree.launcher4.ViewHolders;

import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;

import net.ossfree.launcher4.AppsService;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Structures.TabItem;

public class TabHolder {
	public TabItem tabItem;
	public TextView tv;
	public ImageView iv;

	public void setView() {
		Resources r = tv.getResources();
		tv.setText(tabItem.tabName);

		switch (tabItem.tabID) {
			case AppsService.OTHID:
				tv.setTextColor(r.getColor(R.color.beige));
				break;
			case AppsService.ALLID:
				tv.setTextColor(r.getColor(R.color.yellowgreen));
				break;
			case AppsService.FRQID:
				tv.setTextColor(r.getColor(R.color.aquamarine));
				break;
			case AppsService.NEWID:
				tv.setTextColor(r.getColor(R.color.lightcoral));
				break;
			case AppsService.DOCID:
				tv.setTextColor(r.getColor(R.color.cornsilk));
				break;
			case AppsService.SDID:
				tv.setTextColor(r.getColor(R.color.cornsilk));
				break;
			case AppsService.STATID:
				tv.setTextColor(r.getColor(R.color.lightcyan));
				break;
			default:
				tv.setTextColor(r.getColor(R.color.antiquewhite));
				break;
		}

		iv.setImageDrawable(tabItem.tabIcon);	
	}
}

