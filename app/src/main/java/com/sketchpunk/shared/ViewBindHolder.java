package com.sketchpunk.shared;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ViewBindHolder extends RecyclerView.ViewHolder{
	public ViewBindHolder(View itemView){ super(itemView); }
	public abstract void bindData(int pos);
}//func
