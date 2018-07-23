package net.ossfree.launcher4.Structures;

import android.graphics.drawable.Drawable;

public class ItemInfo  {		 
	
	public String appName, appPackage;
	public Drawable appIcon;
	public long appDate;
	public boolean isfolder;
	public ItemInfo(String an, String ap, long ad, Drawable ai, boolean isf) {appIcon = ai; appName = an; appDate = ad; appPackage = ap; isfolder =isf; }	
	@Override
	public boolean equals(Object object) {
        if (object != null && object instanceof ItemInfo) return appPackage.equals( ((ItemInfo)object).appPackage);
        return false;
    }
	
}
