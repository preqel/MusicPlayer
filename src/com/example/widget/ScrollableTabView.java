package com.example.widget;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.laibomusic.adapter.TabAdapter;

public class ScrollableTabView extends HorizontalScrollView implements
		ViewPager.OnPageChangeListener {

	private ViewPager mpager;
	
	private TabAdapter tabadapter;
	
	private final LinearLayout mContainer;
	
	private final ArrayList<View> Tabs = new ArrayList<View> (); 
	
	public ScrollableTabView(Context context) {
	        this(context, null);
	 }
	public ScrollableTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	 

	public ScrollableTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setHorizontalFadingEdgeEnabled(false);
		setHorizontalScrollBarEnabled(false);
		this.mContainer= new LinearLayout(context);
		LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.setLayoutParams(layoutparams);
	    mContainer.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(mContainer); 
	}

 

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && mpager != null) {
			selectTab(mpager.getCurrentItem());
		}
	}
    

	public void setViewPager(ViewPager viewpager) {

		this.mpager = viewpager;
		mpager.setOnPageChangeListener(this);
		if (mpager != null && tabadapter != null) {
			initTabs();
		}

	}


	public void setAdapter(TabAdapter tabadapter)
	{
		
		this.tabadapter= tabadapter;
		if (mpager != null && tabadapter != null) {
			initTabs();
		}
	}
	
	private void initTabs() {
		
		mContainer.removeAllViews();
		Tabs.clear();
		if(tabadapter == null ) return ;
		for(int i = 0;i < mpager.getAdapter().getCount();i ++){
			final int index = i;
			View tab = tabadapter.getView(i);
			mContainer.addView(tab);
			tab.setFocusable(true);
			Tabs.add(tab);
			tab.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {

					if (mpager.getCurrentItem() == index)
						selectTab(index);
					else
						mpager.setCurrentItem(index, true);
				}
			});
		}
		
	}

	protected void selectTab(int position) {
		for(int i= 0,pos = 0;i < mContainer.getChildCount(); i++,pos++){
			View tab = mContainer.getChildAt(i);
			tab.setSelected(pos == position);
		}
		View selectedtab= mContainer.getChildAt(position);
		final int w = selectedtab.getMeasuredWidth();
		
		final int l = selectedtab.getLeft();
		final int x = l - this.getWidth() / 2 + w / 2;
		smoothScrollTo(x, this.getScrollY()); 
		
	}



	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		selectTab(arg0);
	}

}
