package com.sketchpunk.virt3dmouse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sketchpunk.shared.App;
import com.sketchpunk.shared.ByteStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothSerial{
    //region Constants
 	public static final String ACTION_BTCONNECTION = "btserial.conn";
    public static final UUID UUID_INSECURE_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //insecure spp connection code.
    public static final int STATE_NOBT = 0;
    public static final int STATE_NOENABLED = 1;
	public static final int STATE_READY = 5;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_NODEVICES = 3;
	public static final int STATE_CONNECTED = 4;
	public static final int STATE_ERROR = -1;
	public static final int STATE_UNKNOWN = -99;
	public static final int BTENABLE_REQUEST = 12280;
    //endregion

    //region Private Variables
    private static boolean mIsConnected = false;
	private static String mDeviceAddr = ""; //98:D3:31:80:60:6F

	private static BluetoothAdapter mAdapter = null;
	private static BluetoothSocket mSocket = null;
	private static Thread mListenerThread = null;
    //endregion

	//region Getters/Setters
	public static boolean isConnected(){ return mIsConnected; }
	public static void setDeviceAddress(String str){ mDeviceAddr = str; }
	//endregion

    //region Connectivity Methods
	public static int Check(){
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mAdapter == null) return STATE_NOBT;
		if(!mAdapter.isEnabled()) return STATE_NOENABLED;

		return STATE_READY;
	}//func

    public static int Connect(){
        if(mIsConnected) return STATE_UNKNOWN;

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mAdapter == null) return STATE_NOBT;
        if(!mAdapter.isEnabled()) return STATE_NOENABLED;

        new ConnectTask().execute();
        return STATE_CONNECTING;
    }//func

    public static boolean Disconnect(){
		mIsConnected = false;
		StopListener();

		if(mSocket != null){
            try{
                mSocket.close();
                mSocket = null;
				return true;
            }catch (IOException e){
                e.printStackTrace();
            }//try
        }//if

		return false;
    }//func

    public static synchronized boolean SendData(String txt){
        if(mSocket == null || !mIsConnected) return false;
        txt += "\n";

        try{
            mSocket.getOutputStream().write(txt.getBytes());
			return true;
        }catch(IOException ex){
            ex.printStackTrace();
        }//try

		return false;
    }//func
    //endregion

	//region Misc Methods
	public static void SendEnableRequest(Context context){
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		//context.startActivity(intent);
		((Activity)context).startActivityForResult(intent,BTENABLE_REQUEST);
	}//func

	public static String[][] GetPairedDevices(){
		Set<BluetoothDevice> dlist = mAdapter.getBondedDevices();
		int len = dlist.size();

		if(len == 0) return null;

		int i = 0;
		String[][] rtn = new String[len][2];
		for(BluetoothDevice bt : dlist){
			rtn[i][0] = bt.getName();
			rtn[i][1] = bt.getAddress();
			i++;
		}//for

		return rtn;
	}//func
	//endregion

	//region Thread Controlling
	public static boolean StartListener(){
		if((mListenerThread != null && mListenerThread.isAlive()) || !mIsConnected) return false;

		mListenerThread = new Thread(new BluetoothListener());
		mListenerThread.start();

		return true;
	}//func

	public static void StopListener(){
		if(mListenerThread != null && mListenerThread.isAlive() && !mListenerThread.isInterrupted())
			mListenerThread.interrupt();
	}//func
	//endregion

    //region Threads
	//Connection is blocking, so push that task into a thread. TODO: No longer using Post/Pre, better off making a runnable thread instead.
    private static class ConnectTask extends AsyncTask<Void,Void,Void>{
        //@Override protected void onPreExecute(){ System.out.println("Background onPreExecute"); }
        //@Override protected void onPostExecute(Void result){ System.out.println("Background onPostExecute"); }//func
        @Override protected Void doInBackground(Void... params){
			Intent intent = new Intent(ACTION_BTCONNECTION);
            try{
                mAdapter.cancelDiscovery();

                BluetoothDevice btDevice = mAdapter.getRemoteDevice(mDeviceAddr);
                mSocket = btDevice.createInsecureRfcommSocketToServiceRecord(UUID_INSECURE_SPP); //create a rfcomm spp connection
                mSocket.connect();

				intent.putExtra("STATUS", STATE_CONNECTED);
				mIsConnected = true;
            }catch(IOException e){
				e.printStackTrace();
				intent.putExtra("STATUS", STATE_ERROR);
			}//try

			App.broadcast(intent);
            return null;
        }//func
    }//task

	//Listen on the bluetooth socket for available data
	private static class BluetoothListener implements Runnable{
		@Override public void run(){
			System.out.println("Listener Thread Starting");
			InputStream iStream = null;
			try{ iStream = mSocket.getInputStream(); }catch(IOException e){ e.printStackTrace(); return; }

			//..............................................
			final int bufLen = 50;							//Length of buffer to be use to read from serial
			int availLen = 0,								//How many bytes are available to be read from serial
				bRead = 0;									//How many bytes are read from serial

			byte[] buf = new byte[bufLen];					//Small byte buffer use to read from serial
			ByteStack mReadBuffer = new ByteStack(50);		//This is a cache buffer that keeps being added to from stream till delimiter is found.
			String[] bufResults = null;						//Get the results from the mReadBuffer when performing a full read on the cached data.

			System.out.println("Listener Thread Entering Loop");
			//..............................................
			while(!Thread.currentThread().isInterrupted() && mIsConnected){
				try{
					availLen = iStream.available();
					if(availLen > 0){
						System.out.format("Available %d\n",availLen);
						bRead = iStream.read(buf,0,bufLen);
						mReadBuffer.put(buf, 0, bRead);

						System.out.format("Read %d\n",bRead);

						bufResults = mReadBuffer.getAvailable('\n');
						if(bufResults != null){
							for(int i = 0; i < bufResults.length; i++){
								System.out.println(bufResults[i]);
							}//for
						}//for
					}//if
				}catch(IOException e){ e.printStackTrace(); }
			}//while

			try{ iStream.close(); } catch(IOException e){ e.printStackTrace(); }
			System.out.println("Listener Thread Ended");
		}//func
	}//cls
    //endregion
}//cls