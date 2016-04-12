package com.sketchpunk.shared;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RecyclerViewFragment extends Fragment{
	protected RecyclerView mRecyclerView = null;
	protected TextView mTextView = null;
	protected RecyclerView.LayoutManager mLayoutManager = null;
	protected FrameLayout mFrameLayout = null;

	/* Interesting Idea but doesn't work.
	public static <E extends Fragment> E DynamicAdd(ActionBarActivity act){
		FragmentManager man = act.getFragmentManager();
		FragmentTransaction trans = man.beginTransaction();

		E frag = new E();
		trans.replace(android.R.id.content,frag);

		return frag;
	}//
	*/


	//region Fragment Events
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		mLayoutManager = new LinearLayoutManager(getActivity());

		FrameLayout.LayoutParams lpFrame = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		FrameLayout.LayoutParams lpText = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		lpText.gravity = Gravity.CENTER;

		mRecyclerView = new RecyclerView(getActivity());
		mRecyclerView.setLayoutParams(lpFrame);
		mRecyclerView.setLayoutManager(mLayoutManager);

		mTextView = new TextView(getActivity());
		mTextView.setLayoutParams(lpText);
		mTextView.setText("Loading");
		mTextView.setGravity(Gravity.CENTER);

		mFrameLayout = new FrameLayout(getActivity());
		mFrameLayout.setLayoutParams(lpFrame);
		mFrameLayout.addView(mRecyclerView);
		mFrameLayout.addView(mTextView);
		//mFrameLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light)); //Debugging UI Issues.

		return mFrameLayout;
	}//func
	//endregion

	//region Manage Recycler
	public void setRecyclerAdapter(RecyclerView.Adapter adapter){ mRecyclerView.setAdapter(adapter); }
	public void addItemDecoration(RecyclerView.ItemDecoration itm){ mRecyclerView.addItemDecoration(itm); }
	//endregion

	//region Manage Text Area
	public void hideTextView(){ mTextView.setVisibility(View.GONE); }
	public void showTextView(){ mTextView.setVisibility(View.VISIBLE); }
	public void showTextView(String txt){ mTextView.setText(txt); mTextView.setVisibility(View.VISIBLE); }
	public void setTextMsg(String txt){ mTextView.setText(txt); }
	//endregion
}//cls