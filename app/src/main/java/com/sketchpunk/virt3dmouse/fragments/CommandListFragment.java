package com.sketchpunk.virt3dmouse.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sketchpunk.shared.RecyclerArrayAdapter;
import com.sketchpunk.shared.RecyclerDivider;
import com.sketchpunk.shared.RecyclerMultiArrayAdapter;
import com.sketchpunk.shared.RecyclerViewFragment;
import com.sketchpunk.shared.ViewBindHolder;
import com.sketchpunk.virt3dmouse.BluetoothSerial;
import com.sketchpunk.virt3dmouse.R;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

public class CommandListFragment extends RecyclerViewFragment{
	private RecyclerMultiArrayAdapter<String> mAdapter = null;
	private String[][] mDatasource = {
		{"Toggle Edit Mode","exe~bpy.ops.object.editmode_toggle()"}
		,{"Mesh Select All","exe~bpy.ops.mesh.select_all(action='TOGGLE')"}
		,{"Extrude","exe~bpy.ops.view3d.edit_mesh_extrude_move_normal('INVOKE_DEFAULT')"}
		,{"Center Cursor","exe~bpy.ops.view3d.snap_cursor_to_center()"}
		,{"Add Cube","exe~bpy.ops.mesh.primitive_cube_add()"}
		,{"Scale (Z)","exe~bpy.ops.transform.resize('INVOKE_DEFAULT',constraint_axis=(False, False, True))"}
		,{"Scale (XY)","exe~bpy.ops.transform.resize('INVOKE_DEFAULT',constraint_axis=(True, True, False))"}
		,{"Scale (XYZ)","exe~bpy.ops.transform.resize('INVOKE_DEFAULT',constraint_axis=(True, True, True))"}
		,{"Grab (X)","exe~bpy.ops.transform.translate('INVOKE_DEFAULT',constraint_axis=(True, False, False))"}
		,{"Grab (Y)","exe~bpy.ops.transform.translate('INVOKE_DEFAULT',constraint_axis=(False, True, False))"}
		,{"Grab (Z)","exe~bpy.ops.transform.translate('INVOKE_DEFAULT',constraint_axis=(False, False, True))"}
		,{"Rotate (Z)","exe~bpy.ops.transform.rotate('INVOKE_DEFAULT',axis=(0,0,1))"}
		,{"Rotate (X)","exe~bpy.ops.transform.rotate('INVOKE_DEFAULT',axis=(1,0,0))"}
		,{"Rotate (Y)","exe~bpy.ops.transform.rotate('INVOKE_DEFAULT',axis=(0,1,0))"}
	};

	public CommandListFragment(){}

	//region Fragment Events
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View v = super.onCreateView(inflater,container,savedInstanceState);

		//..............................................
		mAdapter = new RecyclerMultiArrayAdapter<String>(this.getActivity(),R.layout.list_item);
		mAdapter.setCallback(new RecyclerMultiArrayAdapter.Callback(){
			@Override public ViewBindHolder onCreateViewHolder(View v){ return (ViewBindHolder)new VHolder(v); }
		});
		mAdapter.setArray(mDatasource);

		//..............................................
		setRecyclerAdapter(mAdapter);
		addItemDecoration(new RecyclerDivider());
		hideTextView();

        return v;
    }//func
	//endregion

	private class VHolder extends ViewBindHolder implements View.OnClickListener{
		private TextView mLblTitle = null, mLblDesc = null;
		private String mCmd = "";

		public VHolder(View v){
			super(v);
			v.setOnClickListener(this);
			mLblTitle = (TextView) v.findViewById(android.R.id.text1);
		}//func

		@Override
		public void bindData(int pos){
			String[] ary = mAdapter.get(pos);
			mLblTitle.setText(ary[0]);
			mCmd = ary[1];
		}//func

		//region Click Events
		@Override public void onClick(View v){
			System.out.println(mLblTitle.getText());
			BluetoothSerial.SendData(mCmd);
		}//func
		//endregion
	}//cls
}//cls
