package com.sketchpunk.shared;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

/*
public class MyActivity extends ActionBarActivity implements ActivityBroadcastReceiver.Handler{

private ActivityBroadcastReceiver mReceiver = null;

protected void onCreate(Bundle savedInstanceState){
        mReceiver = new ActivityBroadcastReceiver(this, Sync.ACT_COMPLETE,AppUpdate.ACT_UPDATE_READY);

public void onResume(){
        mReceiver.register();

public void onPause(){
        mReceiver.unregister();

public void onBroadcastReceived(Context context, Intent intent){
	switch(intent.getAction()){


//Broadcast Results of the login.
Intent intent = new Intent(ACT_COMPLETE);
intent.putExtra("STATUS",sendStatus);
intent.putExtra("MSG",sendMsg);
intent.putExtra("NEWUPDATE",newUpdate);
zApp.getContext().sendBroadcast(intent);
*/

public class ActivityBroadcastReceiver extends BroadcastReceiver{
	//region Interface, Variables, Constructor, Init
	public static interface Handler{ public void onBroadcastReceived(Context context, Intent intent); }

	private final Activity mActivity;
	private Handler mHandler = null;

	private IntentFilter mIntentFilter = null;

	public ActivityBroadcastReceiver(ActionBarActivity act,String... filter){
		mActivity = act;
		if(act instanceof Handler) mHandler = (Handler) act;
		init(filter);
	}//func

	public ActivityBroadcastReceiver(Fragment frag,String... filter){
		mActivity = frag.getActivity();
		if(frag instanceof Handler) mHandler = (Handler) frag;
		init(filter);
	}//func

	private void init(String[] filter){
		mIntentFilter = new IntentFilter();
		for(int i=0; i < filter.length; i++) mIntentFilter.addAction(filter[i]);
	}//func
	//endregion

	//region Methods
	public void addAction(String action){ mIntentFilter.addAction(action); }//func

	public void register(){ mActivity.registerReceiver(this,mIntentFilter); }
	public void unregister(){ mActivity.unregisterReceiver(this); }
	//endregion

	//region Events
	@Override
	public void onReceive(Context context, Intent intent){
		if(mHandler != null) mHandler.onBroadcastReceived(context,intent);
	}//func
	//endregion
}//cls