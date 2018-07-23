package net.ossfree.launcher4.Structures;


public class DocInfo extends ItemInfo {	
	public DocInfo(String an, String ap, boolean isf) {
		super(an, ap, -1, null, isf);
	}
	
	@Override
	public boolean equals(Object object) {
        if (object != null && object instanceof DocInfo) return appPackage.equals( ((DocInfo)object).appPackage);
        return false;
    }


	
}

