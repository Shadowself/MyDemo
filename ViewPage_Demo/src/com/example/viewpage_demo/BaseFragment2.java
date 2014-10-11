package com.example.viewpage_demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseFragment2 extends Fragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view =inflater.inflate(R.layout.layout2, null); 
		TextView tv_fragment = (TextView) view.findViewById(R.id.tv_fragment);
		tv_fragment.setText("tongxunlu");
		return view;
	}
}
