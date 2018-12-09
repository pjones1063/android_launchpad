package net.ossfree.launcher4.ViewHolders;


import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import net.ossfree.launcher4.AppsService;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Structures.DocInfo;

public class DocHolder {
	public DocInfo ai;
	public TextView an, pn, dt;
	public ImageView iv;
	  
	public void setView(Context c) {		
		an.setText(ai.appName.trim());	
		dt.setText(ai.appPackage);
		if(ai.isfolder)
			iv.setImageDrawable(c.getResources().getDrawable(R.drawable.im64_folder));
		else if(ai.appIcon == null)
			iv.setImageDrawable(c.getResources().getDrawable(AppsService.getDocumentType(ai.appName) ));	 
		else
			iv.setImageDrawable(ai.appIcon);
	}		
}


