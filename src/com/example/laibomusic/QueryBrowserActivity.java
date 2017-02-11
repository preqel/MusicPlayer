package com.example.laibomusic;

import static com.example.laibomusic.util.Consts.ALBUM_KEY;
import static com.example.laibomusic.util.Consts.ARTIST_KEY;
import static com.example.laibomusic.util.Consts.MIME_TYPE;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.laibomusic.service.ServiceToken;
import com.example.laibomusic.util.MusicUtils;
 

public class QueryBrowserActivity extends ListActivity implements ServiceConnection{

	private ServiceToken mToken ;
	private ListView mTrackList ;
	private QueryListAdapter madpater ;
	boolean madaptersent ;
	private Cursor mQueryCursor;
	private String mFilterString = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		madpater = (QueryListAdapter) getLastNonConfigurationInstance();
		 mFilterString = getIntent().getStringExtra(SearchManager.QUERY);
		mToken = MusicUtils.bindToService(this,this);
		
	}

	private void initView() {
		 TextView textview  = (TextView) findViewById(R.id.title_middle_text);
		 textview.setText("搜索音乐");
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mFilterString = getIntent().getStringExtra(SearchManager.QUERY);
		if(madpater != null) getQueryCursor(madpater.getQueryHandler(), null );
		setContentView(R.layout.query_browser);
		initView();
		mTrackList = getListView();
		mTrackList.setTextFilterEnabled(true);
		if (madpater == null) {
			madpater = new QueryListAdapter(getApplication(), this,
					R.layout.listview_items, null, new String[] {},
					new int[] {});
			setListAdapter(madpater);
			if (TextUtils.isEmpty(mFilterString)) {   //if mFilterString Ϊ��
				getQueryCursor(madpater.getQueryHandler(), null);
			} else { 
				mTrackList.setFilterText(mFilterString);
				mFilterString = null;
			}

		} else {
			madpater.setActivity(this);
			setListAdapter(madpater);
			mQueryCursor = madpater.getCursor();
			if (mQueryCursor != null) {
				init(mQueryCursor);
			} else {
				getQueryCursor(madpater.getQueryHandler(), mFilterString);
			}
		}
		
//        LinearLayout emptyness = (LinearLayout)findViewById(R.id.empty_view);
//        emptyness.setVisibility(View.GONE);
	}
    
	
	@Override
	public void onServiceDisconnected(ComponentName name) {

	}
	
	
	 private Cursor getQueryCursor(AsyncQueryHandler async, String filter) {
	        if (filter == null) {
	            filter = "";
	        }
	        String[] ccols = new String[] {
	                BaseColumns._ID, Audio.Media.MIME_TYPE, Audio.Artists.ARTIST, Audio.Albums.ALBUM,
	                Audio.Media.TITLE, "data1", "data2"
	        }; 
	        Uri search = Uri.parse("content://media/external/audio/search/fancy/" + Uri.encode(filter));

	        Cursor ret = null;
	        if (async != null) {
	            async.startQuery(0, null, search, ccols, null, null, null);
	        } else {
	            ret = MusicUtils.query(this, search, ccols, null, null, null);
	        }
	        return ret;
	    }
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		this.mQueryCursor .moveToPosition(position);
		if(mQueryCursor.isBeforeFirst() && mQueryCursor.isAfterLast()){
			return;
		}
		String selectType = mQueryCursor.getString(mQueryCursor.getColumnIndexOrThrow(Audio.Media.MIME_TYPE));
		if("artist".equals(selectType)){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, TracksBrowser.class);
			TextView tv1 = (TextView) findViewById(R.id.textview_lineone);
            String artistname = tv1.getText().toString();
            Bundle bundle = new Bundle ();
            bundle.putString(ARTIST_KEY, artistname);
            bundle.putString( MIME_TYPE,selectType);
            bundle.putLong(BaseColumns._ID, id);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
		}
		else if("album".equals(selectType)){
			
			  TextView tv1 = (TextView)v.findViewById(R.id.textview_lineone);
	            TextView tv2 = (TextView)v.findViewById(R.id.textview_linetwo);
	            String artistName = tv2.getText().toString();          //����������
	            String albumName = tv1.getText().toString();           //ר������
	            Bundle bundle = new Bundle();                         //����bundle
	            bundle.putString(MIME_TYPE, Audio.Albums.CONTENT_TYPE);
	            bundle.putString(ARTIST_KEY, artistName);
	            bundle.putString(ALBUM_KEY, albumName);
	            bundle.putLong(BaseColumns._ID, id);
	            Intent intent = new Intent(Intent.ACTION_VIEW);      //����Intent
	            intent.setClass(this, TracksBrowser.class);
	            intent.putExtras(bundle);
	            startActivity(intent);            //������̨activity
	            finish();
			
		}else if (position >= 0 && id >= 0) {
            long[] list = new long[] {
                    id
                };
                MusicUtils.playAll(this, list, 0);
            } else {
                Log.e("QueryBrowser", "invalid position/id: " + position + "/" + id);
            }
		super.onListItemClick(l, v, position, id);
	}
	

	@Override
	public Object onRetainNonConfigurationInstance() {
		madaptersent = true;
		return madpater;
	}


	public void init(Cursor c) {
		if (madpater == null)
			return;
		madpater.changeCursor(c);
		
		if(mQueryCursor == null){
			setListAdapter(null);
			return;
		}
	}

	private class QueryListAdapter extends SimpleCursorAdapter {

		QueryBrowserActivity activity = null;
		private String mConstraint = null;
		private boolean mConstraintIsValid  = false;
		private QueryHandler asyncqueryhandler;

		public QueryListAdapter(Context context, QueryBrowserActivity act, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			this.activity = act;
			asyncqueryhandler = new QueryHandler(context.getContentResolver());
		}

		public QueryHandler getQueryHandler() {
			return asyncqueryhandler;
		}
		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			 String s = constraint.toString() ;
			 if(mConstraintIsValid && ((s==null && mConstraint == null) || (s!=null && s.equals(constraint)))){
				 return getCursor();
			 }
			 Cursor c = activity.getQueryCursor(null, s);
			 mConstraint = s;
			 mConstraintIsValid = true;
			 return c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv1=  (TextView) view.  findViewById(R.id.textview_lineone);
			tv1.setTextColor(Color.BLACK);
			TextView tv2 = (TextView)view. findViewById(R.id.textview_linetwo);
			tv2.setTextColor(Color.BLACK);
			DatabaseUtils.dumpCursor(cursor);
			String mimetype = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Media.MIME_TYPE));
		    if (mimetype == null) {
                mimetype = "audio/";
            }
            if (mimetype.equals("artist")) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Artists.ARTIST));
                String displayname = name;
                boolean isunknown = false;
                if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
                    displayname = context.getString(R.string.unknown);
                    isunknown = true;
                }
                tv1.setText(displayname);

                int numalbums = cursor.getInt(cursor.getColumnIndexOrThrow("data1"));
                int numsongs = cursor.getInt(cursor.getColumnIndexOrThrow("data2"));

                String songs_albums = MusicUtils.makeAlbumsLabel(context, numalbums, numsongs,
                        isunknown);

                tv2.setText(songs_albums);

            } else if (mimetype.equals("album")) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Albums.ALBUM));
                String displayname = name;
                if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
                    displayname = context.getString(R.string.unknown);
                }
                tv1.setText(displayname);

                name = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Artists.ARTIST));
                displayname = name;
                if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
                    displayname = context.getString(R.string.unknown);
                }
                tv2.setText(displayname);

            } else if (mimetype.startsWith("audio/") || mimetype.equals("application/ogg")
                    || mimetype.equals("application/x-ogg")) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Media.TITLE));
                tv1.setText(name);

                String displayname = cursor.getString(cursor
                        .getColumnIndexOrThrow(Audio.Artists.ARTIST));
                if (displayname == null || displayname.equals(MediaStore.UNKNOWN_STRING)) {
                    displayname = context.getString(R.string.unknown);
                }
                name = cursor.getString(cursor.getColumnIndexOrThrow(Audio.Albums.ALBUM));
                if (name == null || name.equals(MediaStore.UNKNOWN_STRING)) {
                    name = context.getString(R.string.unknown);
                }
                tv2.setText(displayname + " - " + name);
            }
		}
 
		
		public void setActivity(QueryBrowserActivity activity) {
			this.activity = activity;
		}
		
		public void changeCursor(Cursor cursor ){
//			if( activity .isFinishing() && cursor != null){
//				cursor.close();
//				cursor = null;
//			}
			  if (cursor != activity.mQueryCursor) {
				activity .mQueryCursor = cursor;
				super.changeCursor(cursor);
			  }
		}
 	
		
		class QueryHandler extends AsyncQueryHandler {

			public QueryHandler(ContentResolver cr) {
				super(cr);
			}

			@Override
			protected void onQueryComplete(int token, Object cookie,
					Cursor cursor) {
				activity.init(cursor);
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		MusicUtils.unbindFromService(mToken);
		if(!madaptersent && madpater != null){
			madpater.changeCursor(null);
		}
		try{
			setListAdapter(null);
		}catch(NullPointerException e ){
		}
		madpater = null;
		super.onDestroy();
	}


}