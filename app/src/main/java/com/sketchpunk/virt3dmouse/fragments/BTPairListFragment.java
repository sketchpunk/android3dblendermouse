package com.sketchpunk.virt3dmouse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sketchpunk.shared.RecyclerDivider;
import com.sketchpunk.shared.RecyclerMultiArrayAdapter;
import com.sketchpunk.shared.RecyclerViewFragment;
import com.sketchpunk.shared.ViewBindHolder;
import com.sketchpunk.virt3dmouse.BluetoothSerial;
import com.sketchpunk.virt3dmouse.R;
import com.sketchpunk.virt3dmouse.activities.ControlActivity;

public class BTPairListFragment extends RecyclerViewFragment{
	private RecyclerMultiArrayAdapter<String> mAdapter = null;
	private String[][] mDatasource = null;

	public BTPairListFragment(){}

	//region Fragment Events
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View v = super.onCreateView(inflater,container,savedInstanceState);

		mAdapter = new RecyclerMultiArrayAdapter<String>(this.getActivity(),R.layout.list_item);
		mAdapter.setCallback(new RecyclerMultiArrayAdapter.Callback(){
			@Override public ViewBindHolder onCreateViewHolder(View v){ return (ViewBindHolder) new VHolder(v); }
		});

		this.addItemDecoration(new RecyclerDivider());

		return v;
	}//func
	//endregion

	public void loadList(){
		setTextMsg("Loading list...");
		mDatasource = BluetoothSerial.GetPairedDevices();
		if(mDatasource != null){
			mAdapter.setArray(mDatasource);
			setRecyclerAdapter(mAdapter);
			hideTextView();
		}else setTextMsg("No paired devices found");
	}//func

	private class VHolder extends ViewBindHolder implements View.OnClickListener{
		private TextView mLblTitle = null, mLblDesc = null;
		private String mAddr = "";

		public VHolder(View v){
			super(v);
			v.setOnClickListener(this);
			mLblTitle = (TextView) v.findViewById(android.R.id.text1);
		}//func

		@Override public void bindData(int pos){
			String[] ary = mAdapter.get(pos);
			mAddr = ary[1];
			mLblTitle.setText(ary[0]);
		}//func

		//region Click Events
		@Override public void onClick(View v){
			ControlActivity.show(getActivity(), mAddr, mLblTitle.getText().toString());
		}//func
		//endregion
	}//cls
}//cls
