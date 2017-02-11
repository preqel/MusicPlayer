package com.example.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.laibomusic.R;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

@SuppressLint("InflateParams")
public class NewToast {
	
	private Context context;
//	private NewToastView mView;
	private View mView;
	public ViewGroup viewgroup;
	private ViewHelper viewhleper;
	private int  mTranslationY = 0;
	private boolean mInflated = false;
	private boolean mShowCalled = false;
	public NewToast(Context context){
		this.context = context;
		this.mView = LayoutInflater.from(context).inflate(R.layout.dialog_newtoast_exit, null);
		this.viewgroup  = (ViewGroup) ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
		this.viewgroup .addView(mView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
	    viewhleper.setAlpha(mView, 0);
	    viewgroup.postDelayed(new Runnable(){
			@Override
			public void run() {
				ViewHelper.setTranslationX(mView, (viewgroup.getWidth() - mView.getWidth())/2);
				ViewHelper.setTranslationY(mView, -mView.getHeight()+ mTranslationY);
				mInflated = true;
	            if( mShowCalled) show();
			}}, 1);
	}
	
	public void successs(){
		
	}
	public void fial(){
		
		
	}

	
	  public NewToast show(){
	        if(!mInflated){
	            mShowCalled = true;
	            return this;
	        }
	        //mView.show();
	        ViewHelper.setTranslationX(mView, (viewgroup.getWidth() - mView.getWidth()) / 2);
	        ViewHelper.setAlpha(mView, 0f);
	        ViewHelper.setTranslationY(mView, -mView.getHeight() + mTranslationY);
	        //mView.setVisibility(View.VISIBLE);
	        ViewPropertyAnimator.animate(mView).alpha(1f).translationY(25 + mTranslationY)
	                .setInterpolator(new DecelerateInterpolator())
	                .setDuration(300).setStartDelay(0).start();
	        return this;
	    }
	  
	  
	  public void slideup(){
 
		  ViewPropertyAnimator .animate(mView).setStartDelay(1000).alpha(0f).translationY(-mView.getHeight() + mTranslationY)
		 .setInterpolator(new AccelerateInterpolator()).setDuration(300).start(); 
	  }
}
