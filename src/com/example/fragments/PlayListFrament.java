package com.example.fragments;

import com.example.laibomusic.R;
import com.example.laibomusic.TracksBrowser;
import com.example.laibomusic.adapter.PlayListAdapter;

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
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import static com.example.laibomusic.util.Consts.*;
public class PlayListFrament extends Fragment implements LoaderCallbacks<Cursor> ,OnItemClickListener{
	
	ListView listview ;
	PlayListAdapter mPlaylistAdapter ;
	Cursor mCursor ;
	LayoutInflater mflater ;
    private static final int PLAY_SELECTION = 11; //播放选择
    private static final int DELETE_PLAYLIST = 12; //删除播放列表
    private static final int RENAME_PLAYLIST = 13; //重命名播放列表
    public static  int mPlaylistNameIndex ,mPlaylistIdIndex ; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mflater = LayoutInflater.from(getActivity());
		View view = mflater.inflate(R.layout.listview, null	);
		listview = (ListView) view.findViewById(R.id.listview_playlist);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		this.mPlaylistAdapter = new PlayListAdapter(getActivity(),R.layout.listview_items,null,new String[]{},new int[]{},0);
		listview .setAdapter(mPlaylistAdapter);
		listview.setOnItemClickListener(this);
		getLoaderManager().initLoader(0,null, this);
		
		super.onActivityCreated(savedInstanceState);
	}

	 @Override
	    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	        String[] projection = new String[] {
	                BaseColumns._ID, PlaylistsColumns.NAME
	        };
	        Uri uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
	        String sortOrder = Audio.Playlists.DEFAULT_SORT_ORDER;
	        return new CursorLoader(getActivity(), uri, projection, null, null, sortOrder);
	    }

	    @Override
	    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	        if (data == null) { //�����ݿ����
	            return;
	        }
 	        mPlaylistIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
 	        mPlaylistNameIndex = data.getColumnIndexOrThrow(PlaylistsColumns.NAME);
	        mPlaylistAdapter.changeCursor(data);
	        mCursor = data;
	    }

	    @Override
	    public void onLoaderReset(Loader<Cursor> loader) { //��������
	        if (mPlaylistAdapter != null)
	            mPlaylistAdapter.changeCursor(null);
	    }

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			tracksBrowser(id);
		}

		private void tracksBrowser(long id) {
			
	        String playlistName = mCursor.getString(mPlaylistNameIndex);

	        Bundle bundle = new Bundle();
	        bundle.putString(MIME_TYPE, Audio.Playlists.CONTENT_TYPE);
	        bundle.putString(PLAYLIST_NAME, playlistName);
	        bundle.putLong(BaseColumns._ID, id);

	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setClass(getActivity(), TracksBrowser.class);
	        intent.putExtras(bundle);
	        getActivity().startActivity(intent);
	        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right); 
		}
	
	
	
	
	
	

}
