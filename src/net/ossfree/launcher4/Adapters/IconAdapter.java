package net.ossfree.launcher4.Adapters;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IconAdapter extends ArrayAdapter<String> {

	private List<Integer> images;

	public IconAdapter(Context context, String[] items, Integer[] images) {
		super(context, android.R.layout.select_dialog_item, items);
		this.images = Arrays.asList(images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
					images.get(position), 0, 0, 0);
		} else {
			textView.setCompoundDrawablesWithIntrinsicBounds(
					images.get(position), 0, 0, 0);
		}
		
		textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,3 , getContext().getResources().getDisplayMetrics()));
		textView.setTextSize(17);
		return view;
	}

}