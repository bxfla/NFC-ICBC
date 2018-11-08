package com.sdhy.cpucardoper.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import com.alpha.live.R;
import com.nohttp.util.NoScrollViewPager;
import com.sdhy.cpucardoper.fragment.FragmentInfor;
import com.sdhy.cpucardoper.fragment.FragmentMent;
import com.sdhy.cpucardoper.fragment.FragmentMoney;
import com.sdhy.cpucardoper.fragment.FragmentRechager;
import com.sdhy.cpucardoper.fragment.Fragment_Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity1 extends FragmentActivity implements OnCheckedChangeListener{
	
	 public Fragment fragment1, fragment2, fragment3,fragment4;
	 private RadioButton rb1,rb2,rb3,rb4;
	 private TextView tvHeader;
	 List<Fragment> list = new ArrayList<Fragment>();
	 private NoScrollViewPager fragmentContainer;;
	 private RadioGroup mainRg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		
		fragmentContainer = (NoScrollViewPager) findViewById(R.id.fragment_container);
		mainRg = (RadioGroup) findViewById(R.id.main_rg);
		rb1 = (RadioButton) findViewById(R.id.rb0);
		rb2 = (RadioButton) findViewById(R.id.rb1);
		rb3 = (RadioButton) findViewById(R.id.rb2);
		rb4 = (RadioButton) findViewById(R.id.rb3);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		
		fragment1 = new FragmentRechager();
        fragment2 = new FragmentMent();
        fragment3 = new FragmentInfor();
        fragment4 = new FragmentMoney();
        list.add(fragment1);
        list.add(fragment2);
        list.add(fragment3);
        list.add(fragment4);
        mainRg.setOnCheckedChangeListener(this);
        fragmentContainer.setAdapter(new Fragment_Adapter(getSupportFragmentManager(), list));
        fragmentContainer.setCurrentItem(0,false);
        tvHeader.setText(rb1.getText().toString());
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
        case R.id.rb0:
            fragmentContainer.setCurrentItem(0, false);
            tvHeader.setText(rb1.getText().toString());
            break;
        case R.id.rb1:
            fragmentContainer.setCurrentItem(1, false);
            tvHeader.setText(rb2.getText().toString());
            break;
        case R.id.rb2:
            fragmentContainer.setCurrentItem(2, false);
            tvHeader.setText(rb3.getText().toString());
            break;
        case R.id.rb3:
            fragmentContainer.setCurrentItem(3, false);
            tvHeader.setText(rb4.getText().toString());
            break;
    }
		
	}

}
