package com.example.laibomusic.adapter;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.example.laibomusic.util.CharactersUtils;
import com.example.laibomusic.util.MusicUtils;
import com.example.view.ViewHolderList;

public class AlllistAdapter extends SimpleCursorAdapter {
	
	private WeakReference<ViewHolderList> holdReferences; 
	private Context context; 
	private SparseIntArray positionOfSection ;
	private SparseIntArray sectionOfPosition ;
	private String[] letters;
	 Map<Integer,String> map = new HashMap<Integer,String>();
	
	public AlllistAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to,int flags) {
		super(context, layout, c, from, to,flags);
		context = context;
   } 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
	 	final View view = super.getView(position, convertView, parent);
		Cursor cursor = (Cursor) getItem(position); 
 		final ViewHolderList viewholder; 
 		if (view != null) {
 			viewholder = new ViewHolderList(view);
 			holdReferences = new WeakReference<ViewHolderList>(viewholder);
 			view.setTag(holdReferences.get()); 
 			
 		}else
	 		viewholder = (ViewHolderList) convertView.getTag(0);
 		
 		    holdReferences.get().mPlaying.setVisibility(View.INVISIBLE);
	 		holdReferences.get().mViewHolderLineOne.setText(cursor.getString(1));
	 		
	 		char a =  CharactersUtils.getFristSpells( holdReferences.get().mViewHolderLineOne.getText().toString() );
	 	    map.put(position,String.valueOf(a) );
	 	    
	 	    holdReferences.get().mViewHolderLineTwo.setText(cursor.getString(2));
	 		holdReferences.get().m_id = String.valueOf(cursor.getInt(0));
	 	    if(MusicUtils.getCurrentAudioId() != -1 && cursor.getLong(0)==(MusicUtils.getCurrentAudioId() ))holdReferences.get().mPlaying.setVisibility(View.VISIBLE);
	 		
		return view; 
	}
	
	public String [] getSectionString(){
		Cursor curo = getCursor();
		int count = curo.getCount();
	
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
	 
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		curo.moveToFirst();
		letters = new String[count];
		for(int i = 0 ;i < count ; i++){
			letters[i] =  String.valueOf(CharactersUtils.getFristSpells(curo.getString(1))).toUpperCase();
			curo.moveToNext();
		}
		
		return letters;
		//to-do
	}

}
