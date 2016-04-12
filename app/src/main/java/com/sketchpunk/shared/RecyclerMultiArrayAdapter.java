package com.sketchpunk.shared;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RecyclerMultiArrayAdapter<T> extends RecyclerView.Adapter<ViewBindHolder>{
	private T[][] mArray = null;
	private int mItemLayout = 0;
	private Callback mCallBack = null;
	private LayoutInflater mInflater = null;

	public static interface Callback{ public ViewBindHolder onCreateViewHolder(View v); }//interface

	public RecyclerMultiArrayAdapter(Context context,int itmLayout){
		mItemLayout = itmLayout;
		mInflater = LayoutInflater.from(context);
	}//func

	//region setter/getters
	public void setCallback(Callback cb){ mCallBack = cb; }
	public void setArray(T[][] ary){ mArray = ary; }
	public T[] get(int pos){ return (mArray != null)? mArray[pos] : null; }
	//endregion

	//region Adapter
	@Override
	public ViewBindHolder onCreateViewHolder(ViewGroup parent, int viewType){
		if(mCallBack == null) return null;

		View view = mInflater.inflate(mItemLayout,parent,false);
		return mCallBack.onCreateViewHolder(view);
	}//func

	@Override
	public void onBindViewHolder(ViewBindHolder holder, int position){ holder.bindData(position); }

	@Override public int getItemCount(){ return (mArray != null)? mArray.length : 0; }
	//endregion
}//func
