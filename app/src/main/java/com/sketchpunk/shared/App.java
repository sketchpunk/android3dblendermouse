package com.sketchpunk.shared;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class App extends Application {
    private static Context mContext = null;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler = null;

    public void onCreate(){
        super.onCreate();
        App.mContext = getApplicationContext();

        //mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler(); //Save Reference to default.
        //Thread.setDefaultUncaughtExceptionHandler(mUncaughtExceptionHandler);
    }//func

    //region Static Global Functions
    public static Context getContext(){ return App.mContext; }//func
    public static void broadcast(Intent intent){ App.mContext.sendBroadcast(intent); }//func
    //endregion

    //region Error Handling
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler = new Thread.UncaughtExceptionHandler(){
        @Override public void uncaughtException(Thread thread, Throwable throwable){
            //Logger.error("Uncaught.Exception",throwable);

            //Pass this exception back to the system (very important).
            //if(mDefaultExceptionHandler != null) mDefaultExceptionHandler.uncaughtException(thread,throwable);
        }//func
    };
    //endregion
}//cls
