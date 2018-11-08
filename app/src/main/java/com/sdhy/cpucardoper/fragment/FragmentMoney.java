package com.sdhy.cpucardoper.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alpha.live.R;

public class FragmentMoney extends Fragment{
	
	 private View view;
	
	@Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
       view = inflater.inflate(R.layout.fragment1, container, false);
       Toast.makeText(getActivity(), "4", 3).show();
       return view;
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);
   }
}
