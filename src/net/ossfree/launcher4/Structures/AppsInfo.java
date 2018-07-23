package net.ossfree.launcher4.Structures;

import android.graphics.drawable.Drawable;

public class AppsInfo  {
	public String appName, appPackage;
	public Drawable appIcon;
	public long appDate;
	 

	public AppsInfo(String an, String ap, long ad, Drawable ai) {appIcon = ai; appName = an; appDate = ad; appPackage = ap;}
	 
	@Override
	public boolean equals(Object object) {
        if (object != null && object instanceof AppsInfo) return appPackage.equals( ((AppsInfo)object).appPackage );
        return false;
    }	
	
}
