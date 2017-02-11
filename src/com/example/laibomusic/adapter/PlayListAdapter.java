package com.example.laibomusic.adapter;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.fragments.PlayListFrament;
import com.example.view.ViewHolderList;

public class PlayListAdapter extends SimpleCursorAdapter {

	private Context context;
	private WeakReference<ViewHolderList> holderReferences;
	
	public PlayListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to,int flag) {
		 super(context, layout, c, from, to, 1);
	     mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final View view =  super.getView(position, convertView, parent);
		Cursor mcursor = (Cursor) getItem(position);
		final ViewHolderList viewholderlist ;
		if(view != null){
			 viewholderlist = new  ViewHolderList(view);
			 holderReferences = new WeakReference<ViewHolderList>(viewholderlist);
			 view.setTag(holderReferences);
		}else {
			holderReferences = (WeakReference<ViewHolderList>) view.getTag();
		}
		String playlist_name = mcursor.getString(PlayListFrament.mPlaylistNameIndex);
		holderReferences.get().mViewHolderLineOne.setText(playlist_name);
		
		return view;
	}

	 
    
	
	
}
