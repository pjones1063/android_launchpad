package net.ossfree.launcher4;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import net.ossfree.launcher4.Adapters.DocAdapter;
import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.Structures.DocInfo;
import net.ossfree.launcher4.Structures.TabPage;

@SuppressLint({"DefaultLocale", "ValidFragment"})
public class DocList<E> extends AppsList   {

	protected DocAdapter docAdapter = null;
	protected List<DocInfo> docList = null;
	private ImageDownloaderTask idm;

	public DocList(TabPage pg) {
		super(pg);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(tabPage.getID() == AppsService.SDID)
			docList = AppsService.getDocuments(MainActivity.getSDPath());
	    else
			docList = AppsService.getDocuments(Environment.getExternalStorageDirectory().toString());

		if (MainActivity.isGridviewdoc())
			return buildGrid(inflater, container);
		else
			return buildList(inflater, container);
	}
	 
	@Override
	public void onResume(){
	    super.onResume();
		idm = new ImageDownloaderTask();
		idm.execute(0);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    if(idm != null) idm.cancel(true);
	}
	
	
	private View buildGrid(LayoutInflater inflater, ViewGroup container) {

		final View apps = inflater.inflate(R.layout.doc_grid, container, false);
		final GridView bigList = (GridView) apps.findViewById(R.id.mediaIndex);

		docAdapter = new DocAdapter(getActivity().getApplicationContext(), R.layout.doc_grid_row, docList);
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
		final Drawable wp = wallpaperManager.getDrawable();
		bigList.setBackground(wp);
		bigList.setAdapter(docAdapter);
		bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				DocInfo di = docAdapter.getItem(position);
				if(di.isfolder)
					showFolder(di);
				else
					showDocument(di);
			}
		});
	
		bigList.setOnScrollListener(new OnScrollListener() {
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }

		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		        	    idm.cancel(true);
			            int s = bigList.getFirstVisiblePosition();
			            LLg.i("Scroll to "+s);
				        idm = new ImageDownloaderTask();
				   		idm.execute(s);
		        }
		    }
		});
		return apps;

	}

	private View buildList(LayoutInflater inflater, ViewGroup container) {
		final View apps = inflater.inflate(R.layout.doc_list, container, false);
		docAdapter = new DocAdapter(getActivity().getApplicationContext(), R.layout.doc_list_row, docList);
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
		final Drawable wp = wallpaperManager.getDrawable();
		final ListView bigList = (ListView) apps.findViewById(R.id.mediaIndex);
		bigList.setBackground(wp);
		bigList.setAdapter(docAdapter);
		bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				DocInfo di = docAdapter.getItem(position);
				if(di.isfolder)
					showFolder(di);
				else
					showDocument(di);
			}
		});
		bigList.setOnScrollListener(new OnScrollListener() {
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }

		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		        	    idm.cancel(true);
			            int s = bigList.getFirstVisiblePosition();
			            LLg.i("Scroll to "+s);
				        idm = new ImageDownloaderTask();
				   		idm.execute(s);
		        }
		    }
		});
		return apps;
	}



	private void showFolder(DocInfo doc) {
		try{
			startActivityForResult(new Intent(getActivity(), DocWindow.class)
					.putExtra(AppsService.FOLDER,  doc.appName)
					.putExtra(AppsService.TABID,   doc.appPackage), 0);
			
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	private void showDocument(DocInfo doc) {
		
		Intent intent = AppsService.getShowDocumentIntent(doc);
		if(intent != null){
			try {
				intent.setAction(android.content.Intent.ACTION_VIEW);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				intent.setType("text/*");
				startActivity(intent);
			}
		}
	}

 
	private class ImageDownloaderTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int start = params[0];
			for (int x = start; x < docList.size(); x++) {
				if (isCancelled()) {
					x = docList.size();
				} else {
					DocInfo di = docList.get(x);
					if (AppsService.isDocumentImage(di.appPackage) && di.appIcon == null) {
						Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(di.appPackage), 100, 100);						
						di.appIcon = new BitmapDrawable(DocList.this.getResources(), bm);
						getActivity().runOnUiThread(new Runnable() {
								public void run() {
					 				docAdapter.notifyDataSetChanged();
								}
							});

					}
				}
			}
			return null;
		}

	}

 
 
}
