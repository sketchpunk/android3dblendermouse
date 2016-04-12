package com.sketchpunk.virt3dmouse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sketchpunk.shared.Toasty;
import com.sketchpunk.virt3dmouse.BluetoothSerial;
import com.sketchpunk.virt3dmouse.R;
import com.sketchpunk.virt3dmouse.fragments.BTPairListFragment;

public class PairListActivity extends ActionBarActivity{
	private BTPairListFragment mFragment = null;

	//region Activity Events
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pair_list);

		mFragment = (BTPairListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

		CheckBluetooth();
	}//cls

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == BluetoothSerial.BTENABLE_REQUEST){
			if(resultCode == 0){
				mFragment.setTextMsg("You need to accept bluetooth permission to use this app.");
				return;
			}//if
			mFragment.loadList();
		}//if
	}//func
	//endregion

	//region action menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_pair, menu);
		return true;
	}//func

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == R.id.mnuRefresh) CheckBluetooth();
		return super.onOptionsItemSelected(item);
	}//func
	//endregion

	private void CheckBluetooth(){
		switch(BluetoothSerial.Check()){
			case BluetoothSerial.STATE_READY:		mFragment.loadList(); break;
			case BluetoothSerial.STATE_NOBT:		mFragment.setTextMsg("Device has no bluetooth"); break;
			case BluetoothSerial.STATE_NOENABLED:	BluetoothSerial.SendEnableRequest(this); break;
		}//switch
	}//cls
}//func
