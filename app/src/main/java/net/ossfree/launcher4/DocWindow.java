package net.ossfree.launcher4;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.ossfree.launcher4.Adapters.DocAdapter;
import net.ossfree.launcher4.Adapters.DocWindowAdapter;
import net.ossfree.launcher4.Logger.LLg;
import net.ossfree.launcher4.Structures.DocInfo;

@SuppressLint("RtlHardcoded")
public class DocWindow extends Activity {
	
	private String folderName;
	private String pathName;
	private DocWindowAdapter docAdapter; 
	private ImageDownloaderTask idm;
	private List<DocInfo> docList = null;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null){
			folderName = extras.getString(AppsService.FOLDER);
			pathName   = extras.getString(AppsService.TABID);
		}
		
		docList = AppsService.getDocuments(pathName);	
		
		if (MainActivity.isGridviewdoc()) 
			buildGrid(); 
		else 
			buildList();
		
		final ImageView cncl = (ImageView) findViewById(R.id.cncl);
		cncl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		setTitle(folderName); 	
		
	
	}

	 
	@Override
	protected void onResume(){
	    super.onResume();
		idm = new ImageDownloaderTask();
		idm.execute(0);
	}
	
	
	@Override
	public void onPause() {
	    super.onPause();
	    if(idm != null) idm.cancel(true);
	}
	
	
	private void buildGrid() {
		setContentView(R.layout.doc_window_grid); 		
		((TextView)findViewById(R.id.fl_title)).setText(folderName);
		final GridView bigList = (GridView) findViewById(R.id.appsIndex);
	    docAdapter  = new DocWindowAdapter(getApplicationContext(), R.layout.doc_grid_row, docList);
	    bigList.setAdapter(docAdapter);
	    docAdapter.notifyDataSetChanged();
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
		
		bigList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	DocInfo di = docAdapter.getItem(position);
		    	if(!di.isfolder)	
		    		shareDocument(di);		
		    	return true;
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
	}
	
	
	private void buildList() {
		setContentView(R.layout.doc_window_list);
		((TextView)findViewById(R.id.fl_title)).setText(folderName);
		final ListView bigList = (ListView) findViewById(R.id.appsIndex);
	    docAdapter  = new DocWindowAdapter(getApplicationContext(), R.layout.doc_list_row, docList);

	    bigList.setAdapter(docAdapter);
	    bigList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
				DocInfo di = docAdapter.getItem(position);
				if(di.isfolder)
					showFolder(di);
				else
					showDocument(di);
			}
		});
	    
		bigList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	DocInfo di = docAdapter.getItem(position);
		    	if(!di.isfolder)	
		    		shareDocument(di);	
		    	return true;
		    }
		});
	    
	 
		bigList.setOnScrollListener(new OnScrollListener() {
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
		        

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
	}
	
	private void showFolder(DocInfo doc) {
		try {
		    startActivityForResult(
		    		new Intent(this, DocWindow.class)
		    		 .putExtra(AppsService.FOLDER, doc.appName)
		    		 .putExtra(AppsService.TABID, doc.appPackage), 0);
		    finish();
		    
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	
	private void showDocument(DocInfo doc) { 
		Intent intent = AppsService.getShowDocumentIntent(doc);
		if(intent != null){
			try {
				intent.setAction(android.content.Intent.ACTION_VIEW);
				startActivity(intent);
			} catch (Exception e) {
				LLg.e(e.getMessage());
				intent.setType("text/*");
				startActivity(intent);
			}
		}
	}

	
	private void shareDocument(DocInfo doc) {
		Intent intent = AppsService.getShareDocumentIntent(doc);
		if(intent != null){
		try{	
		startActivity(Intent.createChooser(intent, "Share Using"));
		} catch (Exception e) {
			 LLg.e(e.getMessage());
		}
		}
	
	}
	

	private class ImageDownloaderTask extends AsyncTask<Integer, DocAdapter, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int start = params[0];

			for (int x = start; x < docList.size(); x++) {
				Bitmap bm = null;
				if (isCancelled()) {
					x = docList.size();
				} else {
					DocInfo di = docList.get(x);
					if (di.appIcon == null) {
						if (AppsService.isDocumentImage(di.appPackage))
							bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(di.appPackage), 96, 96);
						else if (AppsService.isDocumentMovie(di.appPackage))
							bm = ThumbnailUtils.createVideoThumbnail(di.appPackage, Thumbnails.MICRO_KIND);
						if (bm != null) {
							di.appIcon = new BitmapDrawable(DocWindow.this.getResources(), bm);
							runOnUiThread(new Runnable() {
								public void run() {
									docAdapter.notifyDataSetChanged();
								}
							});
						}
					}
				}
			}
			return null;
		}
	}

	
}
