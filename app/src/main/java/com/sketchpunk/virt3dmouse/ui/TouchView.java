package com.sketchpunk.virt3dmouse.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.sketchpunk.virt3dmouse.GestureTouch;
import com.sketchpunk.virt3dmouse.MultiGesture;
import com.sketchpunk.virt3dmouse.Vert;

public class TouchView extends View implements GestureDetector.OnGestureListener,ScaleGestureDetector.OnScaleGestureListener{

	public static interface OnGestureListener{
		public void onZoom();	//Pinch
		public void onPan();	//Long hold then drag
		public void onDrag(float x, float y);	//Single Finger drag.
	}//interface

    private Paint mPaint = null;
    private GestureDetector mGesture;
    private ScaleGestureDetector mScaleGesture;
	private MultiGesture mMultiGesture;
	private boolean mIsLongPress = false;
	private boolean mIsScale = false;
	private Vert mPreviousPos = null, mCurrentPos = null; //TODO, Don't really need it, swop it for array

	private OnGestureListener mListener = null;

    public TouchView(Context context){ super(context); init(context); }
    public TouchView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#A0000000"));
        mPaint.setStyle(Paint.Style.STROKE);

		if(this.isInEditMode()) return;

		mPreviousPos = new Vert();
		mCurrentPos = new Vert();

		mMultiGesture = new MultiGesture();
		this.setOnTouchListener(mMultiGesture);


        //mScaleGesture = new ScaleGestureDetector(context,this);
		//mGesture = new GestureDetector(context,this);
		//mGesture.setIsLongpressEnabled(false);
    }//func

	public void setOnMultiGestureListener(MultiGesture.OnMultiGestureListener context){ mMultiGesture.setOnMultiGestureListener(context); }//func

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
    }//func


	//region Gesture Event Handlers
	/*
	@Override public boolean onTouchEvent(MotionEvent e){
		if(!mIsScale && mIsLongPress && e.getActionMasked() == MotionEvent.ACTION_MOVE){
			mCurrentPos.set(e);
			float deltax = mPreviousPos.x - mCurrentPos.x;
			float deltay = mPreviousPos.y - mCurrentPos.y;

			//System.out.format("LongPress %f %f %b\n",deltax, deltay,mIsLongPress);

			if(mListener != null) mListener.onPan();

			mPreviousPos.set(mCurrentPos);
			return true;
		}//if

		int ptrCnt = e.getPointerCount();
		if(ptrCnt == 2) mScaleGesture.onTouchEvent(e);
		if(ptrCnt == 1) mGesture.onTouchEvent(e);

		//System.out.format("onTouchEvent %d\n", e.getPointerCount());
        return true;
    }//func
    */

	@Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        //System.out.format("SCROLL %f %f %b\n", distanceX, distanceY,mIsLongPress);

		if(mListener != null) mListener.onDrag(distanceX,distanceY);
        return true;
	}//func

    @Override public boolean onScale(ScaleGestureDetector detector){
		System.out.format("onScale %f %f\n", detector.getScaleFactor(), detector.getCurrentSpan());
		if(mListener != null) mListener.onZoom();
		return true;
    }//func

	@Override public void onLongPress(MotionEvent e){
		mIsLongPress = true;
		mPreviousPos.set(e,0);
		System.out.println("long press");
	}
	//endregion

	//region Unused events
	@Override public boolean onDown(MotionEvent e){ mIsLongPress = false; return true; }//needed to return true to get most of the gestures to work.
	@Override public void onShowPress(MotionEvent e){}
	@Override public boolean onSingleTapUp(MotionEvent e){ return false; }

	@Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){ return false; }
	@Override public boolean onScaleBegin(ScaleGestureDetector detector){ mIsScale = true; return true; }
	@Override public void onScaleEnd(ScaleGestureDetector detector){ mIsScale = false; }
	//endregion
}//cls
