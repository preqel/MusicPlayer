package com.example.laibomusic.adapter;

import java.util.ArrayList;
import java.util.List; 

import com.example.fragments.AlbumsFragment;
 
import com.example.fragments.ArtistFragment;
import com.example.fragments.AllSongFragment;
import com.example.fragments.PlayListFrament;
import com.example.fragments.RefreshFragment;
import com.example.fragments.TestFragement;

 
  

 

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;;
 
  
public class PagerAdapter extends FragmentPagerAdapter {
	
	public  final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	
    public PagerAdapter(FragmentManager fm ) { 
        super(fm);
	
	}
	
	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	
	public void addFragment(Fragment fragement){
		fragments.add(fragement);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return fragments.size();
	}

	public void refresh() {
		for (int i = 0; i < fragments.size(); i++) {
			if (fragments.get(i) instanceof RefreshFragment) {
				((RefreshFragment) fragments.get(i)).refresh();
			}
		}

	}
	
	
	
	
	
	

}
