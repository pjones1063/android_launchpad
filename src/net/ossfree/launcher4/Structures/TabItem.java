package net.ossfree.launcher4.Structures;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class TabItem implements Serializable { 
 
	private static final long serialVersionUID = 6174017709029349576L;
	public String tabName;
	public int tabID;
	public Drawable tabIcon; 
	
	public TabItem(String tn,  Drawable ic, int ti) {tabName = tn; tabIcon = ic; tabID = ti;}
	   @
	   Override
		public boolean equals(Object object) {
	        if (object != null && object instanceof TabItem)  return tabName.equals( ((TabItem)object).tabName ); 
	        return false;
	    }
	   
}

