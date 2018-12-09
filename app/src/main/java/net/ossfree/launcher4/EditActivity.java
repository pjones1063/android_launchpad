package net.ossfree.launcher4;

import java.util.List; 

import net.ossfree.launcher4.R;
import net.ossfree.launcher4.Adapters.EditAdapter;
import net.ossfree.launcher4.Structures.ItemInfo;
import net.ossfree.launcher4.Structures.TabPage;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class EditActivity extends ListActivity {	 
	
	private EditAdapter editAdapter = null;
	private TabPage page = null;
	public  List<ItemInfo> applist = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_list);
		Intent mIntent = getIntent();
		String tabName = mIntent.getStringExtra(AppsService.TABPOS);
		page = AppsService.getPage(tabName);
		setTitle(getString(R.string.tab_SelApps)+" "+page.getTab());
		editAdapter = new EditAdapter(this, R.layout.apps_list,  page);
		setListAdapter(editAdapter);
     }
	   
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    View view = getWindow().getDecorView();
	    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	    lp.gravity =  Gravity.TOP | Gravity.RIGHT;
	    lp.y = 25;
	    lp.y = 115;
	    getWindowManager().updateViewLayout(view, lp);
	}
	
	
	@Override
		public void finish() { AppsService.saveLayout(getApplicationContext()); super.finish(); }
	   
}