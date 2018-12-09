package net.ossfree.launcher4.Adapters;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Structures.DocInfo;
import net.ossfree.launcher4.ViewHolders.DocHolder;

@SuppressLint("DefaultLocale")
public class DocWindowAdapter extends ArrayAdapter <DocInfo>  {

	private  List<DocInfo> allList = null;;
	private LayoutInflater layoutInflater = null;
	int rowid = 0;

	
	public DocWindowAdapter(Context context, int textViewResourceId,  List<DocInfo> docs) {
		super(context, textViewResourceId,  docs);
		rowid = textViewResourceId;
		allList = docs;
		Collections.sort(allList, new Comparator<DocInfo>() {
			@Override
			public int compare(DocInfo  a, DocInfo  b){
				 return a.appName.compareTo(b.appName);
			}
		});	
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return ((null != allList) ? allList.size() : 0);
	}
	
	
	@Override
	public DocInfo getItem(int position) {
		return ((allList != null) ? allList.get(position) : null);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final DocHolder holder;
		if ((v == null) || (v.getTag() == null)) {
			v = layoutInflater.inflate(rowid, null);
			holder = new DocHolder();
			holder.an = (TextView)  v.findViewById(R.id.media_name);
			holder.dt = (TextView)  v.findViewById(R.id.media_date);
			holder.iv = (ImageView) v.findViewById(R.id.media_icon);
			v.setTag(holder);			
		} else {
			holder = (DocHolder) v.getTag();
		}  
		
		holder.ai = allList.get(position);
		holder.setView(getContext());
		v.setTag(holder);
		return v;
	}

}
 
 