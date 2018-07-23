package net.ossfree.launcher4.Adapters;


import net.ossfree.launcher4.R;
import net.ossfree.launcher4.ViewHolders.AlphaHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class AlphaAdapter extends ArrayAdapter<String>  {
	
	public String filter = "";
	public final static String[] alpha = {  "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private int rowid = 0;
	private LayoutInflater layoutInflater = null;
	
	public AlphaAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId, alpha);
		rowid = textViewResourceId;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	 
	public void setFilter(String f) {filter = f;}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final AlphaHolder holder;
		if ((v == null) || (v.getTag() == null)) {
			v = layoutInflater.inflate(rowid, null);
			holder = new AlphaHolder();
			holder.tv = (TextView)  v.findViewById(R.id.tv);
			v.setTag(holder);			
		} else {
			holder = (AlphaHolder) v.getTag();
		}  
		holder.alpha = alpha[position];		
		if (filter != null && filter.equals(holder.alpha))
			holder.color = Color.RED;
		else
			holder.color = Color.BLACK;
		
		holder.setView();		
		v.setTag(holder);
		return v;
	}
 
}
 
 