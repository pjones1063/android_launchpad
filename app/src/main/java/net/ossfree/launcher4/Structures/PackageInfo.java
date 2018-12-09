package net.ossfree.launcher4.Structures;

import java.io.Serializable;

public class PackageInfo implements Serializable {

	private static final long serialVersionUID = 8294328976350208557L;
	public String appPackage = null;
	public int appUsedCount = 1;
			 
    public PackageInfo(String ap) {
		this.appPackage = ap;
		appUsedCount = 1;
	}
    
	@Override
	public boolean equals(Object object) {
        if (object != null && object instanceof PackageInfo) return appPackage.equals( ((PackageInfo)object).appPackage );
        return false;
    }
	
}
