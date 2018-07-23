package net.ossfree.launcher4.ViewHolders;

import android.graphics.Color;
import android.widget.TextView;

public class AlphaHolder {
	public String alpha;
	public TextView tv;
	public int color = Color.BLACK;	
	public void setView() {
		tv.setText(alpha);
		tv.setBackgroundColor(color);
	}
}