package com.example.fragments;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.laibomusic.R;
import com.example.laibomusic.TracksBrowser;
import com.example.laibomusic.adapter.AlbumAdapter;
import com.example.laibomusic.util.Consts;
 
public class AlbumsFragment extends Fragment implements OnItemClickListener,LoaderCallbacks<Cursor> {

	private AlbumAdapter albumadapter;
	private GridView gridview ;
	String mCurrentAlbumId;   //id
	ImageView imageview;
    public static int mAlbumIdIndex, mAlbumNameIndex, mArtistNameIndex;  
	Cursor mCursor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
	    View view = 	inflater.inflate(R.layout.gridview, container,false);
		gridview = (GridView) view.findViewById(R.id._gridview);
		this.gridview.setOnItemClickListener(this);
		//return super.onCreateView(inflater, container, savedInstanceState);
	    return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
		Cursor cursor = null;
		this.albumadapter = new AlbumAdapter(this.getActivity(),
		R.layout.gridview_items, null, new String[] {},
		new int[] {}, 0);
		this.gridview.setAdapter(albumadapter);
//		cursor = Musiclist.query(getActivity(), uri, projection, null, null,
//				sortOrder); 
//		cursor.moveToFirst();
		getLoaderManager().initLoader(0, null, this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		trackBrowser(id);
	}

	/**
	 * ��ת��tracksbrowser
	 */
	private void trackBrowser(long id) {
		
		Intent intent = new Intent(this.getActivity(), TracksBrowser.class);
		Bundle bundle = new Bundle();
		bundle.putString(Consts.MIME_TYPE, Audio.Albums.CONTENT_TYPE);
		bundle.putString( Consts.ALBUM_ID_KEY, mCurrentAlbumId);
		bundle.putLong(BaseColumns._ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
		String projection[] = { BaseColumns._ID, AlbumColumns.ALBUM,
				AlbumColumns.ARTIST, AlbumColumns.ALBUM_ART };
		String sortOrder = Audio.Albums.DEFAULT_SORT_ORDER;
		return new CursorLoader(getActivity(), uri, projection,null ,
				null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    //		mCursor  = data;
		    if(data == null) return ;
	        mAlbumIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
	        mAlbumNameIndex = data.getColumnIndexOrThrow(AlbumColumns.ALBUM);
	        mArtistNameIndex = data.getColumnIndexOrThrow(AlbumColumns.ARTIST);
	        mCursor = data;
	        albumadapter.changeCursor(mCursor);
	        mCursor.moveToFirst();
			try {
				mCurrentAlbumId = mCursor.getString(mCursor.getColumnIndexOrThrow(BaseColumns._ID));
			}catch(Exception e ){
				e.printStackTrace();
			}

	 }

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	     if(albumadapter != null) albumadapter.changeCursor(null);
	}
}
