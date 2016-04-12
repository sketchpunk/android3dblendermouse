package com.sketchpunk.virt3dmouse;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.sketchpunk.shared.App;

import java.util.ArrayList;

public class GestureTouch implements View.OnTouchListener{
	public static final int GESTURE_UNKNOWN = 0;
	public static final int GESTURE_PINCH = 1;
	public static final int GESTURE_DBLDRAG = 2;

	final ViewConfiguration viewConfig = ViewConfiguration.get(App.getContext());
	int mViewScaledTouchSlop = viewConfig.getScaledTouchSlop();

	//private int mActivePointCnt = 0;
    //private int mMaxIndex = 0;
    //private Vert mStartPos = new Vert();
    //private Vert mCurrentPos = new Vert();
    private long mDownTimestamp = 0;
    private int mGuestureType = 0;
	private int mSampleCnt = 0;
	private final int mSampleLimit = 3;
	private ArrayList<Vert[]>  alSample = new ArrayList<Vert[]>();

	private Vert[] mInitalPos = new Vert[]{new Vert(),new Vert()};
	private Vert[] mCurrentPos = new Vert[]{new Vert(),new Vert()};
	private Vert[] mPreviousPos = new Vert[]{new Vert(),new Vert()};

    private void initialTouch(MotionEvent me){
		mInitalPos[0].set(me, 0);
		mInitalPos[1].reset();
		mPreviousPos[0].set(mInitalPos[0]);
		mPreviousPos[1].reset();

		mSampleCnt = 0;
		alSample.clear();

       	mDownTimestamp = System.currentTimeMillis();
		//mMaxIndex = 0;
		mGuestureType = GESTURE_UNKNOWN;
		System.out.println(Vert.DISTANCE_THRESHOLD);
    }//func

    private void finalEndTouch(MotionEvent me){
    }//func

    private void newTouch(MotionEvent me){
		//mActivePointCnt++;

		if(me.getActionIndex() == 1 && me.getPointerCount() == 2){
			mInitalPos[1].set( mPreviousPos[1].set(me,1) );

			System.out.println(mInitalPos[0].distance(mInitalPos[1]));
		}//if
    }//func

    private void endTouch(MotionEvent me){
		//mActivePointCnt--;
    }//func


    @Override
    public boolean onTouch(View v, MotionEvent me) {
        //System.out.println(event.getPointerCount());
        int index = me.getActionIndex();
        //mMaxIndex = Math.max(mMaxIndex,index); //Get the max fingers ever put on screen.
        //if(me.getActionMasked() == MotionEvent.ACTION_MOVE) return true;

        switch(me.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                initialTouch(me);
                System.out.println("ON DOWN " + Integer.toString(index));
                break;
            case MotionEvent.ACTION_UP:
                finalEndTouch(me);
                System.out.println("ON UP " + Integer.toString(index));
                break;
            case MotionEvent.ACTION_MOVE:
				//mCurrentPos.set(me);
				//System.out.println("SAMPLE MOVE " + Integer.toString(index));
				//System.out.println("ON MOVE " + Integer.toString(me.getPointerCount()));
				if(mGuestureType == GESTURE_UNKNOWN){
					if(me.getPointerCount() == 2){
						mCurrentPos[0].set(me,0);
						mCurrentPos[1].set(me, 1);

						//float moveX = Math.abs(mCurrentPos[0].x - mInitalPos[0].x), moveY = Math.abs(mCurrentPos[0].y - mInitalPos[0].y);
						//System.out.format("movement %f %f\n",moveX,moveY);

						if( (mInitalPos[0].hasMoved(mCurrentPos[0]) || mInitalPos[0].hasMoved(mCurrentPos[1])) && mSampleCnt < mSampleLimit){
							mSampleCnt++;

							Vert[] vert = new Vert[]{new Vert(),new Vert()};
							vert[0].set(me, 0);
							vert[1].set(me, 1);
							alSample.add(vert);

							if(mSampleCnt < mSampleLimit) return true;

							System.out.println(alSample.size());
							float x1=0, x2=0, y1=0, y2=0;
							for(int i = 0; i < alSample.size(); i++){
								x1 += alSample.get(i)[0].x;
								x2 += alSample.get(i)[1].x;
								y1 += alSample.get(i)[0].y;
								y2 += alSample.get(i)[1].y;
							}

							x1 = x1 / alSample.size();
							x2 = x2 / alSample.size();
							y1 = y1 / alSample.size();
							y2 = y2 / alSample.size();

							System.out.format("%f %f %f %f",x1,y1,x2,y2);
							Vert a = new Vert(x1,y1);
							Vert b = new Vert(x2,y2);

							double directA = mInitalPos[0].angle(a);
							double directB = mInitalPos[1].angle(b);
							System.out.format("Angle %f %f %f\n",directA,directB, directB-directA);

							/*
							float distA = mInitalPos[0].distance(mInitalPos[1])
								,distB = mCurrentPos[0].distance(mCurrentPos[1]);

							//TODO, Try with focal point next.
							Vert iFocal = mInitalPos[0].midPoint(mInitalPos[1]);
							Vert cFocal = mCurrentPos[0].midPoint(mCurrentPos[1]);
							float fDistance = iFocal.distance(cFocal);

							double directA = mInitalPos[0].angle(mCurrentPos[0]);
							double directB = mInitalPos[1].angle(mCurrentPos[1]);

							System.out.format("Sample %d \n",mSampleCnt);
							System.out.format("Angle %f %f %f\n",directA,directB, directB-directA);

							//System.out.format("%s %s focal distance %f\n", iFocal.toString(),cFocal.toString(),fDistance);
							System.out.format("%s %s focal distance %f\n", iFocal.toString(),cFocal.toString(),fDistance);
							System.out.format("Distance %f %f %f\n", distA,distB, distA-distB);

							Vert diff0 = mInitalPos[0].difference(mCurrentPos[0]);
							Vert diff1 = mInitalPos[1].difference(mCurrentPos[1]);
							float mxx = (diff0.x * diff1.x);
							float myx = (diff0.y * diff1.y);

							float diff1x = mCurrentPos[0].x - mInitalPos[0].x;
							float diff1y = mCurrentPos[0].y - mInitalPos[0].y;

							float diff2x = mCurrentPos[1].x - mInitalPos[1].x;
							float diff2y = mCurrentPos[1].y - mInitalPos[1].y;

							float mx = (diff1x * diff2x);
							float my = (diff1y * diff2y);

							//System.out.format("distance %f %d\n",Math.abs(distA - distB),Vert.MOVEMENT_THRESHOLD);
							//System.out.format("direction %f %f\n",mx,my);
							//System.out.format("direction %f %f\n",mxx,myx);
							//System.out.format("diff x %f %f\n",diff0.x,diff1.x);
							//System.out.format("diff x %f %f\n",diff0.y,diff1.y);
							if(Math.abs(distA - distB) > Vert.MOVEMENT_THRESHOLD && mxx <= 0 && myx <=0 && fDistance < Vert.DISTANCE_THRESHOLD){
								System.out.println("PINCH !!!");
								//mGuestureType = GESTURE_PINCH;

							}else if( Math.abs(distB) <= Math.abs(distA + Vert.MOVEMENT_THRESHOLD)  && (mxx > 0 || myx > 0)){
								System.out.println("DOUBLE DRAG !!!");
								//mGuestureType = GESTURE_DBLDRAG;
							}//if

							//System.out.println(Math.abs(distA - distB));
							//System.out.println(mx);
							//System.out.println(my);

							//if(mInitalPos[0].hasDifted(mCurrentPos[0]) || mInitalPos)

*/
							//}
							//float distance = mCurrentPos[0].set(me,0).distance( mCurrentPos[1].set(me,1) );
							//System.out.println(distance);
						}//if

						mPreviousPos[0].set(mCurrentPos[0]);
						mPreviousPos[1].set(mCurrentPos[1]);
					}//if
				}//if

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                newTouch(me);
                System.out.println("ON POINTER DOWN " + Integer.toString(index));
			break;
            case MotionEvent.ACTION_POINTER_UP:
                endTouch(me);

                System.out.println("ON POINTER UP " + Integer.toString(index));
                break;
        }//switch

        //System.out.println("Total Fingers " + Integer.toString(mPointCount));
        return true;
    }//func

}//cls

