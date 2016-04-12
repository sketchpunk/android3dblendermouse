package com.sketchpunk.virt3dmouse;

import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.sketchpunk.shared.App;

public class Vert{
	public static final int MOVEMENT_THRESHOLD = ViewConfiguration.get(App.getContext()).getScaledTouchSlop();
	public static final int DISTANCE_THRESHOLD = ViewConfiguration.get(App.getContext()).getScaledDoubleTapSlop();

    public float x = 0, y = 0;

	public Vert(){}
	public Vert(float fx, float fy){ x = fx; y = fy; }

	public String toString(){ return String.format("VER X:%f Y:%f",x,y); }

    //region Setters
    public void reset(){ x=0; y=0; }
    public Vert set(Vert v){ x = v.x; y = v.y; return this; }
	public Vert set(MotionEvent me, int index){ x = me.getX(index); y = me.getY(index); return this; }//func
    public Vert set(MotionEvent me){
        int i = me.getActionIndex();
		x = me.getX(i);
        y = me.getY(i);
		return this;
    }//func

    //endregion

    //region Calculations
    public float distance(Vert v){ return FloatMath.sqrt(FloatMath.pow(x-v.x,2f) + FloatMath.pow(y-v.y,2f)); }//func

	public boolean hasMoved(Vert v){
		float moveX = Math.abs(v.x - x), moveY = Math.abs(v.y - y);
		return (moveX > MOVEMENT_THRESHOLD || moveY > MOVEMENT_THRESHOLD);
	}//func

	public Vert difference(Vert v){ return new Vert(x - v.x,y - v.y); }

	public boolean hasDifted(Vert v){
		return ( Math.abs(distance(v)) > MOVEMENT_THRESHOLD );
	}//func

	public Vert midPoint(Vert v){
		float fx = (x+v.x)/2,  fy = (y+v.y)/2;
		return new Vert(fx,fy);
	}//func

	public double angle(Vert v){
		double xdiff = v.x - x, ydiff = v.y - y;
		return Math.atan2(ydiff,xdiff) * 180.0 / Math.PI;
	}//func
    //endregion
}//cls