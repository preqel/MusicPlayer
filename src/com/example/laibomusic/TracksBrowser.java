package com.example.laibomusic;

import static com.example.laibomusic.util.Consts.ALBUM_ID_KEY;
import static com.example.laibomusic.util.Consts.ALBUM_KEY;
import static com.example.laibomusic.util.Consts.ARTIST_KEY;
import static com.example.laibomusic.util.Consts.EXTERNAL;
import static com.example.laibomusic.util.Consts.MIME_TYPE;
import static com.example.laibomusic.util.Consts.PLAYLIST_QUEUE;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.laibomusic.adapter.TracksAdapter;
import com.example.laibomusic.service.ServiceToken;
import com.example.laibomusic.util.Consts;

import static com.example.laibomusic.util.Consts.*;

import com.example.laibomusic.util.MusicUtils;
import com.example.laibomusic.util.Musiclist;
public class TracksBrowser extends Activity implements OnItemClickListener,ServiceConnection,LoaderCallbacks<Cursor> {  
	
	long album_id;
	String miniType,artistname;
	private TextView tv_attistnameone,tv_artistnametwo ;
	private ListView mListView ;
	private ServiceToken mToken; 
	private Bundle mbundle;
	private Cursor mcursor ;
	TracksAdapter trackadapter;
	
	long mPlaylistId ;
	
	public TracksBrowser(){
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.track_browser); 
		whatBundle(savedInstanceState); 
		initUpperHalf();
		initTitleBar();
		init();
		mToken = MusicUtils.bindToService(TracksBrowser.this);
		mbundle = getIntent().getExtras();
		getLoaderManager().initLoader(0, null, this);
	}
	
	
	
	private void initUpperHalf() {
	 
		tv_attistnameone = (TextView) findViewById(R.id.half_artist_image_text);
		tv_artistnametwo = (TextView) findViewById(R.id.half_artist_image_texttow);
		mbundle  = getIntent().getExtras();
		if(Audio.Artists.CONTENT_TYPE.equals(miniType)){
 
			TextView mTitle = (TextView) findViewById(R.id.title_middle_text);
			mTitle.setText(mbundle.getString(ARTIST_KEY));
		} 
		else if(Audio.Albums.CONTENT_TYPE.equals(miniType)){
			TextView mTitle = (TextView) findViewById(R.id.title_middle_text);
			String albumname = MusicUtils.getAlbumName(TracksBrowser.this, mbundle.getLong(BaseColumns._ID), false);
			mTitle.setText(albumname);
		}
		
	}

	private void initTitleBar() {
		ImageView mUpImg = (ImageView) findViewById(R.id.title_up_img);
		mUpImg.setVisibility(View.VISIBLE);
		mUpImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init() {
	 
		if("ARTIST".equals(miniType)){
		    Uri uri   = Audio.Media.EXTERNAL_CONTENT_URI;
			String projection[] = new String[] { BaseColumns._ID,
					Audio.Media.TITLE, Audio.Media.ARTIST,
					Audio.Media.ARTIST_ID };
			String selection = AudioColumns.IS_MUSIC + "=1 AND "
					+ AudioColumns.DURATION + " >10000 AND "
					+ AudioColumns.ARTIST + "='" + this.artistname + "' ";
			String sortOrder = Audio.Artists.DEFAULT_SORT_ORDER;
		    mcursor = Musiclist.query(this, uri, projection, selection, null, sortOrder);
		}else if("ALBUMS".equals(miniType)){
			Uri uri  = Audio.Media.EXTERNAL_CONTENT_URI;
			String selection = AudioColumns.IS_MUSIC + "=1 AND " + AudioColumns.DURATION + " >10000 AND "+AudioColumns.ALBUM_ID+"='"+this.album_id+"' ";
			String projection[]  = new String[]{BaseColumns._ID,Audio.Media.TITLE,Audio.Media.ALBUM,Audio.Media.ALBUM_ID};
			String sortOrder = Audio.Albums.DEFAULT_SORT_ORDER;
			mcursor = Musiclist.query(this, uri, projection, selection, null, sortOrder);
		}
		else if(Audio.Playlists.CONTENT_TYPE.equals(miniType)){
			Uri uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
			String selection = AudioColumns.IS_MUSIC +"=1 ";
		    mcursor = Musiclist.query(this, uri, null, null, null, null);
		}
		
		trackadapter = new TracksAdapter(this,
				R.layout.listview_items, null, new String[] {}, new int[] {},
				0);
		mListView = (ListView) findViewById(R.id.trak_browser_listview_p);
		mListView.setAdapter(trackadapter);
		mListView.setOnItemClickListener(this);
	}

	private void whatBundle(Bundle savedInstanceState) {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle == null) bundle = new Bundle();
		this.miniType = bundle.getString(Consts.MIME_TYPE);
		if( Audio.Artists.CONTENT_TYPE.equals(miniType)){
			this.artistname = bundle.getString(ARTIST_KEY);
		} 
		else if( Audio.Albums.CONTENT_TYPE.equals(miniType)){ 
		    this.album_id = bundle.getLong("album_id"); 
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		 if (mcursor instanceof NowPlayingCursor) {
	            if (MusicUtils.service != null) {
	                MusicUtils.setQueuePosition(position);
	                return;
	            }
	        }
	        MusicUtils.playAll(this, mcursor, position);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		 MusicUtils.service = IMusicService.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		 MusicUtils.service = null;
	}
	
	 @Override
	    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	        String[] projection = new String[] {
	                BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST
	        };
	        StringBuilder where = new StringBuilder();
	        String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
	        where.append(AudioColumns.IS_MUSIC + "=1").append(" AND " + MediaColumns.TITLE + " != ''");
	        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
	        if (mbundle != null) {
	            mPlaylistId = mbundle.getLong(BaseColumns._ID);
	            String mimeType = mbundle.getString(MIME_TYPE);
	            if (Audio.Playlists.CONTENT_TYPE.equals(mimeType)) {
	                where = new StringBuilder();
	                where.append(AudioColumns.IS_MUSIC + "=1");
	                where.append(" AND " + MediaColumns.TITLE + " != ''");
	                switch ((int)mPlaylistId) {
	                    case (int)PLAYLIST_QUEUE:
	                        uri = Audio.Media.EXTERNAL_CONTENT_URI;
	                        long[] mNowPlaying = MusicUtils.getQueue();
	                        if (mNowPlaying.length == 0)
	                            return null;
	                        where = new StringBuilder();
	                        where.append(BaseColumns._ID + " IN (");
	                        if (mNowPlaying == null || mNowPlaying.length <= 0)
	                            return null;
	                        for (long queue_id : mNowPlaying) {
	                            where.append(queue_id + ",");
	                        }
	                        where.deleteCharAt(where.length() - 1);
	                        where.append(")");
	                        break;
	                    default: //(int)PLAYLIST_FAVORITES:
	                        long favorites_id = MusicUtils.getFavoritesId(this);
	                        projection = new String[] {
	                                Playlists.Members._ID, AudioColumns.ARTIST,
	                                MediaColumns.TITLE, AudioColumns.ALBUM, Playlists.Members.AUDIO_ID
	                        };
	                        uri = Playlists.Members.getContentUri(EXTERNAL, favorites_id);
	                        sortOrder = Playlists.Members.DEFAULT_SORT_ORDER;
	                        break;
//	                    default:
//	                        if (id < 0)
//	                            return null;
//	                        projection = new String[] {
//	                                Playlists.Members._ID, Playlists.Members.AUDIO_ID,
//	                                MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST,
//	                                AudioColumns.DURATION
//	                        };
//	                        uri = Playlists.Members.getContentUri(EXTERNAL, mPlaylistId);
//	                        sortOrder = Playlists.Members.DEFAULT_SORT_ORDER;
//	                        break;
	                }
	            } else if (Audio.Genres.CONTENT_TYPE.equals(mimeType)) {
	                long genreId = mbundle.getLong(BaseColumns._ID);
	                uri = Genres.Members.getContentUri(EXTERNAL, genreId);
	                projection = new String[] {
	                        BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM,
	                        AudioColumns.ARTIST
	                };
	                where = new StringBuilder();
	                where.append(AudioColumns.IS_MUSIC + "=1").append(
	                        " AND " + MediaColumns.TITLE + " != ''");
	                sortOrder = Genres.Members.DEFAULT_SORT_ORDER;
	            } else {
	                if (Audio.Albums.CONTENT_TYPE.equals(mimeType)) {
	                    long albumId = mbundle.getLong(BaseColumns._ID);
	                    where.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
	                    sortOrder = Audio.Media.TRACK + ", " + sortOrder;
	                } else if (Audio.Artists.CONTENT_TYPE.equals(mimeType)) {
	                    sortOrder = MediaColumns.TITLE;
	                    long artist_id = mbundle.getLong(BaseColumns._ID);
	                    where.append(" AND " + AudioColumns.ARTIST_ID + "=" + artist_id);
	                }
	            }
	        }
	        Log.d("TAG",uri.toString() + " where.tostring:" + where.toString());
	        return new CursorLoader(this, uri, projection, where.toString(), null, sortOrder);
	    }

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(data == null) return ;
		if(mbundle != null &&  mbundle.getString(MIME_TYPE).equals(Playlists.CONTENT_TYPE)){
		}else if (mbundle != null && mbundle.getString(MIME_TYPE).equals(Albums.CONTENT_TYPE)){
		}
		mcursor = data;
		trackadapter.changeCursor(mcursor);
		mListView.invalidateViews();
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		trackadapter.changeCursor(null);
	}
	
	 @Override
	protected void onStop() {
		if( MusicUtils.service != null ) MusicUtils.unbindFromService(mToken);
		super.onStop();
	}

	/**
     * @return artist name from Bundle
     */
    public String getArtist() { //获取艺术家名称
        if (mbundle.getString(ARTIST_KEY) != null)
            return mbundle.getString(ARTIST_KEY);
        return getResources().getString(R.string.app_name);
    }

    /**
     * @return album name from Bundle
     */
    public String getAlbum() { //获取专辑名称
        if (mbundle.getString(ALBUM_KEY) != null)
            return mbundle.getString(ALBUM_KEY);
        return getResources().getString(R.string.app_name);
    }

    /**
     * @return album name from Bundle
     */
    public String getAlbumId() { //获取专辑ID
        if (mbundle.getString(ALBUM_ID_KEY) != null)
            return mbundle.getString(ALBUM_ID_KEY);
        return getResources().getString(R.string.app_name);
    }
	
	
}
