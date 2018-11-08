package com.sdhy.cpucardoper.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/15 0015.
 */

public class Fragment_Adapter extends FragmentPagerAdapter {
    List<Fragment> list=new ArrayList<Fragment>();
    public Fragment_Adapter(FragmentManager fm,List<Fragment> items) {
        super(fm);
        this.list = items;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            return (Fragment) list.get(position);
        case 1:
            return (Fragment) list.get(position);
        case 2:
            return (Fragment) list.get(position);
        case 3: 
            return (Fragment) list.get(position);
        case 4: 
            return (Fragment) list.get(position);
        }
        return (Fragment) list.get(position);

    }

    @Override
    public int getCount() {
        return list.size();
    }
}
