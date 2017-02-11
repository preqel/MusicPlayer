package com.example.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TwoStateImageView extends ImageView {

	private static final int ENABLE_ALPHA = 255;
	private static final int DISABLE_ALPHA = (int)(255* 0.4);
	private boolean filterEnable = true;
	
	public TwoStateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TwoStateImageView(Context context) {
		super(context );
	}
	public void enableFilter(Boolean enalled){
		this.filterEnable = enalled;
	}
	public void setEnable(boolean enabled){
		super.setEnabled(enabled);
		if(filterEnable ){
			if(enabled)setAlpha(ENABLE_ALPHA);
			else setAlpha(DISABLE_ALPHA);
		}
	}
	
}
