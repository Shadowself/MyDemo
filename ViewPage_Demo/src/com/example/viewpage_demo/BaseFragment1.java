package com.example.viewpage_demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class BaseFragment1 extends Fragment implements OnClickListener {
	
	RelativeLayout findToFriend;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.layout_1, null);
		// TextView tv_fragment = (TextView)
		// view.findViewById(R.id.tv_fragment);
		// tv_fragment.setText("faxian");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		RelativeLayout findToFriend = (RelativeLayout)getActivity().findViewById(R.id.findToFriend);
		findToFriend.setOnClickListener(this);
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.findToFriend:
			Toast.makeText(getActivity(), "Äúµã»÷ÁËÅóÓÑÈ¦", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		
	}

}
