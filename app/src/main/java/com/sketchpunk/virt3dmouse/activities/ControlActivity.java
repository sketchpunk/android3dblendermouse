package com.sketchpunk.virt3dmouse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sketchpunk.shared.ActivityBroadcastReceiver;
import com.sketchpunk.shared.Toasty;
import com.sketchpunk.virt3dmouse.BluetoothSerial;
import com.sketchpunk.virt3dmouse.MultiGesture;
import com.sketchpunk.virt3dmouse.R;
import com.sketchpunk.virt3dmouse.fragments.CommandListFragment;
import com.sketchpunk.virt3dmouse.ui.TouchView;

import java.text.DecimalFormat;

public class ControlActivity extends ActionBarActivity implements MultiGesture.OnMultiGestureListener, ActivityBroadcastReceiver.Handler{
	//region Static Methods
	public static void show(Context context,String addr,String dName){
		Intent intent = new Intent(context,ControlActivity.class);
		intent.putExtra("dAddress",addr);
		intent.putExtra("dName",dName);
		context.startActivity(intent);
	}//func
	//endregion

	//region Variables
	private ActivityBroadcastReceiver mReceiver = null;
	private float mFactorPan = 0.3f;
	private float mFactorZoom = 0.05f;
	private float mFactorRotate = 0.003f;
	private DecimalFormat mDFormat = new DecimalFormat("#.##");
	private String mDeviceName = "";
	private ProgressDialog mProgressDialog = null;
	//endregion

	//region Activity Events
	//private <E extends View> E findCastViewById(int id){ return (E) findViewById(id); }
	//private <E> E $(int id){ return (E) findViewById(id); }
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);

		//------------------------------------------
		//Get Passed Data
		Intent intent = getIntent();
		String attr = intent.getStringExtra("dAddress");
		mDeviceName = intent.getStringExtra("dName");
		if(!attr.isEmpty()) BluetoothSerial.setDeviceAddress(attr);

		//------------------------------------------
		mReceiver = new ActivityBroadcastReceiver(this, BluetoothSerial.ACTION_BTCONNECTION);
		((TouchView)findViewById(R.id.Overlay_Target)).setOnMultiGestureListener(this);

		//------------------------------------------
		//TODO: Based on device size or orientation, this fragment should be in a slide in menu.
		FragmentManager man = getSupportFragmentManager();
		FragmentTransaction trans = man.beginTransaction();
		CommandListFragment fragment = new CommandListFragment();
		trans.add(R.id.ListFragment, fragment);
		trans.commit();

		connectBT();
	}//func

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == BluetoothSerial.BTENABLE_REQUEST){
			if(resultCode == 0){
				Toasty.center(this,"You need to accept bluetooth permission to use this app.",true);
				return;
			}//if
			Toasty.center(this, "Try connecting again when bluetooth is ready.", true);
		}//if
	}//func

	@Override public void onResume(){
		super.onResume();
		mReceiver.register();
	}//func

	@Override protected void onPause(){
		super.onPause();
		BluetoothSerial.Disconnect();
		mReceiver.unregister();
	}//func
	//endregion

	//region Broadcasts
	@Override
	public void onBroadcastReceived(Context context, Intent intent){
		System.out.println("onBroadcastReceived");
		mProgressDialog.hide();

		if(intent.getAction().equals(BluetoothSerial.ACTION_BTCONNECTION)){
			switch(intent.getIntExtra("STATUS", BluetoothSerial.STATE_UNKNOWN)){
				case BluetoothSerial.STATE_CONNECTED:	Toasty.center(this,"Bluetooth device has been successfully connected.",false); break;
				case BluetoothSerial.STATE_ERROR:		Toasty.center(this,"Unable to connect. Check if device is on.",true); break;
				case BluetoothSerial.STATE_UNKNOWN:		Toasty.center(this,"Unknown status when connecting to bluetooth device.",true); break;
			}//switch

			invalidateOptionsMenu();
		}//if
	}//func
	//endregion

	//region Touch Events
	@Override
	public void onMultiGesture(int mode, float deltaX, float deltaY){
		switch(mode){
			case MultiGesture.MODE_PAN:
				deltaX = deltaX * -1 * mFactorPan;
				deltaY *= mFactorPan;
				BluetoothSerial.SendData("pan~" + mDFormat.format(deltaX) + "~" + mDFormat.format(deltaY));

				System.out.format("PAN %f %f \n",deltaX,deltaY);
				break;
			case MultiGesture.MODE_PYAW:
				deltaX *= mFactorRotate;
				deltaY *= mFactorRotate;

				BluetoothSerial.SendData("pyaw~" + mDFormat.format(deltaX) + "~" + mDFormat.format(deltaY));
				System.out.format("PYAW %f %f \n",deltaX,deltaY);

				break;
			case MultiGesture.MODE_ZOOM:
				deltaY *= mFactorZoom;

				BluetoothSerial.SendData("zoom~" + mDFormat.format(deltaY));
				System.out.format("ZOOM %f \n", deltaY);

				break;
			case MultiGesture.MODE_ROLL:
				deltaX *= mFactorRotate * -1;
				BluetoothSerial.SendData("roll~" + mDFormat.format(deltaX));
				System.out.format("Roll %f \n",deltaX);
				break;
		}//switch
	}//func
	//endregion

	//region Menu Events
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem mitem = menu.findItem(R.id.mnuConnect);
		mitem.setIcon( (BluetoothSerial.isConnected())? R.drawable.ic_bluetooth_searching_24dp : R.drawable.ic_bluetooth_disabled_24dp );

		return true;
	}//func

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == R.id.mnuConnect){
			if(BluetoothSerial.isConnected()){
				if(BluetoothSerial.Disconnect()) Toasty.center(this,"Disconnect Successful",false);
				else Toasty.center(this,"Error while disconnecting bluetooth device.",true);

				invalidateOptionsMenu();
			}else connectBT();
		}//if

		return super.onOptionsItemSelected(item);
	}//func
	//endregion

	//region Bluetooth Functions
	private void connectBT(){
		switch(BluetoothSerial.Connect()){
			case BluetoothSerial.STATE_NOBT:		Toasty.center(this,"There is no Bluetooth Module in the phone",true); break;
			case BluetoothSerial.STATE_NOENABLED:	BluetoothSerial.SendEnableRequest(this); break;
			case BluetoothSerial.STATE_CONNECTING:
				if(mProgressDialog == null){
					mProgressDialog = new ProgressDialog(this);
					mProgressDialog.setTitle("");
					mProgressDialog.setMessage("Trying to connect to " + mDeviceName);
					mProgressDialog.setCancelable(false);
					mProgressDialog.setIndeterminate(true);
				}//if

				mProgressDialog.show();
				break;
		}//switch
	}//func
	//endregion
}//cls
