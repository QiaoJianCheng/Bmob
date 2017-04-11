package com.visionvera.bmob.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.visionvera.bmob.base.BaseFragment;
import com.visionvera.bmob.fragment.AppsTabFragment;
import com.visionvera.bmob.fragment.UsersTabFragment;

import java.util.ArrayList;

/**
 * Created by Qiao on 2017/4/11.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    private AppsTabFragment mAppsTabFragment;
    private UsersTabFragment mUsersTabFragment;
    private ArrayList<BaseFragment> mTabFragments;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        mTabFragments = new ArrayList<>();
        mAppsTabFragment = new AppsTabFragment();
        mUsersTabFragment = new UsersTabFragment();
        mTabFragments.add(mAppsTabFragment);
        mTabFragments.add(mUsersTabFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mTabFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTabFragments.size();
    }
}
