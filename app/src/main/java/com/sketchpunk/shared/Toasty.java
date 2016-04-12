package com.sketchpunk.shared;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Toasty{
	public static void center(String msg,Boolean isLong){ center(App.getContext(),msg,isLong); }//func

	public static void center(Context context,String msg,Boolean isLong){
		Toast toast = Toast.makeText(context,msg,(isLong)?Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}//func
}//cls
