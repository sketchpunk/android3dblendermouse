package com.sketchpunk.shared;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Divider extends View {
	private Paint mPaint = null;
	private int mHeight = 1;

	public Divider(Context context){ super(context); init("#000000",2,0,0); }
	public Divider(Context context, AttributeSet attrs){
		super(context, attrs);
		init("#707070",10,0,0);

		/*
		String color = "#a0a0a0";
		int size = 1, dash = 2, gap = 5;

		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.sp2Divider);
		final int n = a.getIndexCount();
		*/

		/*
		for(int i=0; i < n; i++){
			int attr = a.getIndex(i);
			switch(attr){
				case R.styleable.spDivider_lineColor: color = a.getString(attr); break;
				case R.styleable.spDivider_lineSize: size = a.getInt(attr,1); break;
				case R.styleable.spDivider_lineStyle:
					System.out.println("LineSTyle");
					System.out.println(a.getInt(attr,0));
					switch(a.getInt(attr,0)){
						case 0: dash = 0; gap = 0; break; //line
						case 1: dash = 2; gap = 5; break; //Dotted
						case 2: dash = 7; gap = 11; break; //Dashes
					}//switch
					break;
			}//switch
		}//for
		*/

		//a.recycle();
		//init(color, size, dash, gap);
	}//func

	private void init(String color,int w, float wDash, float wGap){
		System.out.println("Divider.ini");

		DisplayMetrics dm = getResources().getDisplayMetrics() ;
		float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, dm);

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.parseColor(color));
		mPaint.setStrokeWidth(strokeWidth);
		if(wDash > 0 && wGap > 0) mPaint.setPathEffect(new DashPathEffect(new float[] {wDash,wGap},0));

		mHeight = w;
		setMinimumHeight(w);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}//func

	@Override
	public void onDraw(Canvas c){
		int left = getPaddingLeft(), right = getWidth() - getPaddingRight();
		System.out.println("Divider.onDraw");
		c.drawLine(left, 0, right, 0, mPaint);
	}//func

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int width,height
			,widthMode	= MeasureSpec.getMode(widthMeasureSpec)
			,widthSize	= MeasureSpec.getSize(widthMeasureSpec)
			,heightMode	= MeasureSpec.getMode(heightMeasureSpec)
			,heightSize	= MeasureSpec.getSize(heightMeasureSpec);

		//Measure Width
		if(widthMode == MeasureSpec.EXACTLY) width = widthSize;
		else if(widthMode == MeasureSpec.AT_MOST) width = widthSize;
		else width = 100;

		//Measure Height
		if(heightMode == MeasureSpec.EXACTLY) height = heightSize;
		else if(heightMode == MeasureSpec.AT_MOST) height = mHeight;
		else height = mHeight;

		setMeasuredDimension(width, height);
	}//func
}//cls