package com.example.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class RotateImageView extends TwoStateImageView {

	 private int mCurrentDegree = 0; // [0, 359]  
	 private int mStartDegree = 0;  
	 private int mTargetDegree = 0;  
	 private long mAnimationStartTime  = 0;
	 private long mAnimationEndTime  = 0;
	 
	  private static final int ANIMATION_SPEED = 270; // 270 deg/sec  
	  
	private boolean mClockwise = false,mEnableAnimation = true;
	
	public RotateImageView(Context context) {
		super(context);
	}

	public RotateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOrientation(int degree,boolean 	animation){
		mEnableAnimation = animation;
		degree = degree > 0 ? degree % 360:degree%360 + 360;
		if(degree == mTargetDegree){
			return ;
		}
		mTargetDegree = degree;
		if (mEnableAnimation) {
			mStartDegree = mCurrentDegree;
			mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();

			int diff = mTargetDegree - mCurrentDegree;
			diff = diff >= 0 ? diff : 360 + diff; // make it in range [0, 359]

			// Make it in range [-179, 180]. That's the shorted distance between
			// the
			// two angles
			diff = diff > 180 ? diff - 360 : diff;

			mClockwise = diff >= 0;
			mAnimationEndTime = mAnimationStartTime + Math.abs(diff) * 1000
					/ ANIMATION_SPEED;

		}else  mAnimationEndTime = mTargetDegree;
		invalidate ();
		
	}
	
	  @Override  
	    protected void onDraw(Canvas canvas) {  
	        Drawable drawable = getDrawable();  
	        if (drawable == null) return;  
	  
	        Rect bounds = drawable.getBounds();  
	         int w = bounds.right - bounds.left;  
	         int h = bounds.bottom - bounds.top;  
	   
	         if (w == 0 || h == 0) return; // nothing to draw  
	   
	         if (mCurrentDegree != mTargetDegree) {  
	             long time = AnimationUtils.currentAnimationTimeMillis();  
	             if (time < mAnimationEndTime) {  
	                 int deltaTime = (int)(time - mAnimationStartTime);  
	                 int degree = mStartDegree + ANIMATION_SPEED  
	                         * (mClockwise ? deltaTime : -deltaTime) / 1000;  
	                 degree = degree >= 0 ? degree % 360 : degree % 360 + 360;  
	                 mCurrentDegree = degree;  
	                 invalidate();  
	             } else {  
	                 mCurrentDegree = mTargetDegree;  
	             }  
	         }  
	   
	         int left = getPaddingLeft();  
	         int top = getPaddingTop();  
	         int right = getPaddingRight();  
	         int bottom = getPaddingBottom();  
	         int width = getWidth() - left - right;  
	         int height = getHeight() - top - bottom;  
	   
	         int saveCount = canvas.getSaveCount();  
	   
	         // Scale down the image first if required.  
	         if ((getScaleType() == ImageView.ScaleType.FIT_CENTER) &&  
	                 ((width < w) || (height < h))) {  
	             float ratio = Math.min((float) width / w, (float) height / h);  
	             canvas.scale(ratio, ratio, width / 2.0f, height / 2.0f);  
	         }  
	         canvas.translate(left + width / 2, top + height / 2);  
	         canvas.rotate(-mCurrentDegree);  
	         canvas.translate(-w / 2, -h / 2);  
	         drawable.draw(canvas);  
	         canvas.restoreToCount(saveCount);  
	     }  

	private Bitmap mThumb ;
	private Drawable[] mThumbs;
	private TransitionDrawable mThumbTransition ;
	
	public void setBitmap(Bitmap bitmap ){
		
		if(bitmap == null){
			mThumb = null;
			mThumbs = null;
			setImageDrawable(null	);
			setVisibility(GONE);
			return ;
		}
		LayoutParams param = getLayoutParams();
		final int miniThumbWidth = 400;
		final int miniThumbHeight = 400; 
	        
	      Log.i("yan", "param.width = " + param.width + " getPaddingLeft() = "  
	              + getPaddingLeft() + " getPaddingRight()" + getPaddingRight());  
	      Log.i("yan", "miniThumbWidth = " + miniThumbWidth);  
	      mThumb = ThumbnailUtils.extractThumbnail(  
	              bitmap, miniThumbWidth, miniThumbHeight);  
	      Drawable drawable;  
	      if (mThumbs == null || !mEnableAnimation) {  
	          mThumbs = new Drawable[2];  
	          mThumbs[1] = new BitmapDrawable(getContext().getResources(), mThumb);  
	          setImageDrawable(mThumbs[1]);  
	      } else {  
	          mThumbs[0] = mThumbs[1];  
	          mThumbs[1] = new BitmapDrawable(getContext().getResources(), mThumb);  
	          mThumbTransition = new TransitionDrawable(mThumbs);  
	          setImageDrawable(mThumbTransition);  
	          mThumbTransition.startTransition(500);  
	      }  
	      setVisibility(VISIBLE);
	}
	
	
}
