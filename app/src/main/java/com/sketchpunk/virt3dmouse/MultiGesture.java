package com.sketchpunk.virt3dmouse;

import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.sketchpunk.shared.App;

public class MultiGesture implements View.OnTouchListener{
	public static interface OnMultiGestureListener{
		public void onMultiGesture(int mode, float deltaX, float deltaY);
	}//interface

	public static final int MOVEMENT_THRESHOLD = ViewConfiguration.get(App.getContext()).getScaledTouchSlop();
	public static final int MODE_PAN = 1;
	public static final int MODE_PYAW = 2;
	public static final int MODE_ROLL = 4;
	public static final int MODE_ZOOM = 3;

	private int mMode = MODE_PAN;
	private float[] mPrevious = new float[2];
	private float[] mCurrent = new float[2];
	private int mSkip = 0; //When switching modes there is an effect

	private OnMultiGestureListener mListener = null;

	public void setOnMultiGestureListener(OnMultiGestureListener listener){ mListener = listener; }//func

	@Override
	public boolean onTouch(View v, MotionEvent e){
		switch(e.getActionMasked()){
			case MotionEvent.ACTION_DOWN: mMode = MODE_PAN;
				mPrevious[0] = e.getX();
				mPrevious[1] = e.getY();
				break; //case MotionEvent.ACTION_UP: break;

			case MotionEvent.ACTION_MOVE:
				mCurrent[0] = e.getX();
				mCurrent[1] = e.getY();
				if(hasMoved()){
					float deltaX,deltaY = 0;

					//if(mMode == MODE_ROLL || mMode == MODE_ZOOM) deltaX = getDistance();
					//else{
						deltaX = mCurrent[0] - mPrevious[0];
						deltaY = mCurrent[1] - mPrevious[1];
					//}//if
					if(mSkip == 0) mListener.onMultiGesture(mMode,deltaX,deltaY);
					else mSkip--;

					mPrevious[0] = mCurrent[0];
					mPrevious[1] = mCurrent[1];
				}//if
				break;

			case MotionEvent.ACTION_POINTER_DOWN: mMode = e.getPointerCount(); break;
			case MotionEvent.ACTION_POINTER_UP:
				int ptrCnt = e.getPointerCount() - 1; //Even though its up the count shows its still there.

				if(mMode == MODE_PYAW && ptrCnt == 1){
					mMode = MODE_ROLL;
					mSkip = 1;
				}else mMode = ptrCnt;
				break;
		}//switch
		return true;
	}//func

	private float getDistance(){ return FloatMath.sqrt(FloatMath.pow(mCurrent[0] - mPrevious[0], 2f) + FloatMath.pow(mCurrent[1] - mPrevious[1], 2f)); }//func

	private boolean hasMoved(){
		float moveX = Math.abs(mCurrent[0] - mPrevious[0]), moveY = Math.abs(mCurrent[1] - mPrevious[1]);
		return (moveX > MOVEMENT_THRESHOLD || moveY > MOVEMENT_THRESHOLD);
	}//func

	private float rotation(float[] ary) {
		double deltaX = ary[0] - ary[2];
		double deltaY = ary[1] - ary[3];
		double radians = Math.atan2(deltaY, deltaX);
		System.out.println(radians);
		return (float) Math.toDegrees(radians);
	}

	private Vert midpoint(float[] ary){
		float x = (ary[0] - ary[2]) / 2.0f;
		float y = (ary[1] - ary[3]) / 2.0f;
		return new Vert(x,y);
	}

/*


		if(ptrCnt < 2) return true;

		for(int i=0; i < 4; i+=2){
			mCurrent[i] = e.getX(i/2);
			mCurrent[i+1] = e.getY(i/2);
		}//for

		System.out.format("Pointer %d %f % f\n", e.getPointerCount(), e.getX(0), e.getY(0));
		System.out.println(rotation(mCurrent));

		Vert vert = midpoint(mCurrent);
		System.out.format("midpoint %f %f\n",vert.x,vert.y);

		System.arraycopy(mCurrent, 0, mPrevious, 0,4);


 */

}//cls
