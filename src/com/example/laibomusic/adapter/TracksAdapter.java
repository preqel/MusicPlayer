package com.example.laibomusic.adapter;

import java.lang.ref.WeakReference;

import com.example.laibomusic.R;
import com.example.view.ViewHolderList;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class TracksAdapter extends SimpleCursorAdapter {

	private WeakReference<ViewHolderList> holdReferences = null;
	private Context mContext;
	public TracksAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.mContext = context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View view = super.getView(position, convertView, parent);
		 Cursor cursor = (Cursor) getItem(position);
		 String t_id = cursor.getString(0);
		 final ViewHolderList viewholder; 
 		 if (view != null) {
 			viewholder = new ViewHolderList(view);
 			holdReferences = new WeakReference<ViewHolderList>(viewholder);
 			view.setTag(R.id.tag_first,holdReferences.get()); 
 			view.setTag(R.id.tag_second,t_id);
 		 } else
	 	    viewholder = (ViewHolderList) convertView.getTag(R.id.tag_first);
	 		holdReferences.get().mViewHolderLineOne.setText(cursor.getString(1));
	 		holdReferences.get().mViewHolderLineTwo.setText(cursor.getString(2));
	 		holdReferences.get().m_id = String.valueOf( cursor.getInt(0));
			return view;
	}
}
