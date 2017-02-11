package com.example.widget;

import com.example.laibomusic.util.LRCUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *    自定义歌词view @author 王康
 *
 */
public class LrcView extends TextView {

	private float width;
	private float height;
	Paint paint  = new Paint();   //普通
	Paint mPaint  = new Paint();   //高亮
	private static final int SIZEWORD = 36;  //普通字体大小
	private static final int SIZEWORD_H = 60;   //高亮字体大小
	String mText  ="正在努力从网络上加载歌词";
	private int index = 0;
	private float textheight = 60;

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTextSize(SIZEWORD);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		mPaint = new Paint();
		mPaint.setColor(0XFF00FF00);
		mPaint.setTextSize(SIZEWORD_H);
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Paint.Align.CENTER);
	}


	public String getmText() {
		return mText;
	}

	public void setmText(String mText) {
		this.mText = mText;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(LRCUtils.lrclist == null || LRCUtils.lrclist.size() <= 0) return ;
		canvas.drawText(LRCUtils.lrclist.get(index).getLrcstr(), width/2, height/2, mPaint);
		float tmepY = height/2;
		for(int i = index -1;i >= 0;i--){
			tmepY  -=  textheight;
			canvas.drawText(LRCUtils.lrclist.get(i).getLrcstr(), width/2, tmepY , paint);
		}
		tmepY = height / 2;
		for(int i = index + 1; i < LRCUtils.lrclist.size(); i++) {
			tmepY = tmepY + textheight;
			canvas.drawText(LRCUtils.lrclist.get(i).getLrcstr(), width/2, tmepY , paint);
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void updateIndex(int progress){
		int value = LRCUtils.getLRCIndex(progress);
		if (value != 0) {
			this.index = value;
			this.invalidate();
		}
	}


	@Override
	public String toString() {
		return "LrcView [mText=" + mText + "]";
	}
}
