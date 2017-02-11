package com.example.fragments;

import com.example.laibomusic.R;
import com.example.laibomusic.TracksBrowser;
import com.example.laibomusic.adapter.ArtistAdapter;
import com.example.laibomusic.util.Consts;
import com.example.laibomusic.util.Musiclist;

 




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
import android.provider.MediaStore.Audio.ArtistColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ArtistFragment extends Fragment  implements OnItemClickListener ,LoaderCallbacks<Cursor>{
	
	private ArtistAdapter artistadapter ;
	
	private GridView gridview;
	private Cursor mcursor ;
	
	public static int mArtistNameIndex;
	private static int mArtistIdIndex;
	private static int mArtistNumAlbumsIndex;
	
	
	public ArtistFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.gridview,container, false);
		gridview = (GridView) root.findViewById(R.id._gridview); 
		return root;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		artistadapter= new ArtistAdapter(this.getActivity(), R.layout.gridview_items, null, new String[]{}, new int[]{}, 0);
		gridview.setAdapter(artistadapter);
		gridview.setTextFilterEnabled(true);
		gridview.setOnItemClickListener (this);
		getLoaderManager().initLoader(0, null,   this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		Intent intent = new Intent(this.getActivity(),TracksBrowser.class);
//		Bundle bundle = new Bundle();
//		bundle.putString(Consts.MIME_TYPE, "ARTIST");
//		bundle.putLong(BaseColumns._ID, id);
//		
//		TextView mLineOne=(TextView) view.findViewById(R.id.gridview_line_one);
//		bundle.putString("artistname", mLineOne.getText().toString() );
//		intent.putExtras(bundle); 
//		startActivity(intent);
		//Log.d("TAG","onItemClick "+ id);
		trackBrowser(id);
	}

	/**
	 * 跳转到tracksbrowser
	 */
	private void trackBrowser(long id) {
		
		Intent intent = new Intent(this.getActivity(), TracksBrowser.class);
		Bundle bundle = new Bundle();
		  
		bundle.putString(Consts.MIME_TYPE, Audio.Artists.CONTENT_TYPE);
		bundle.putString( Consts.ARTIST_KEY, mcursor.getString(mArtistNameIndex));
		bundle.putString( Consts.NUMALBUMS, mcursor.getString(mArtistNumAlbumsIndex));
		bundle.putLong(BaseColumns._ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		  
		Uri uri = Audio.Artists.EXTERNAL_CONTENT_URI;
		String sortOrder = Audio.Artists.DEFAULT_SORT_ORDER;
		String project[] = new String[]{BaseColumns._ID , ArtistColumns.ARTIST, ArtistColumns.NUMBER_OF_ALBUMS};
	    return new CursorLoader(this.getActivity(), uri, project, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(data == null) return ;
        mArtistIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
        mArtistNameIndex = data.getColumnIndexOrThrow(ArtistColumns.ARTIST);
        mArtistNumAlbumsIndex = data.getColumnIndexOrThrow(ArtistColumns.NUMBER_OF_ALBUMS);
		mcursor = data;
		artistadapter.changeCursor(mcursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		artistadapter.changeCursor(null);
	}
}
