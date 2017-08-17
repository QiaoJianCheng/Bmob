package com.visionvera.bmob.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.visionvera.bmob.base.BaseFragment;
import com.visionvera.bmob.fragment.AppsTabFragment;
import com.visionvera.bmob.fragment.MoodsTabFragment;
import com.visionvera.bmob.fragment.PlanTabFragment;
import com.visionvera.bmob.fragment.UsersTabFragment;

import java.util.ArrayList;

/**
 * Created by Qiao on 2017/4/11.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    private ArrayList<BaseFragment> mTabFragments;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        mTabFragments = new ArrayList<>();
        mTabFragments.add(new MoodsTabFragment());
        mTabFragments.add(new AppsTabFragment());
        mTabFragments.add(new UsersTabFragment());
        mTabFragments.add(new PlanTabFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mTabFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTabFragments.size();
    }

    public void refresh(int currentItem) {
        BaseFragment fragment = mTabFragments.get(currentItem);
        if (fragment instanceof MoodsTabFragment) {
            ((MoodsTabFragment) fragment).pullToRefresh();
        } else if (fragment instanceof AppsTabFragment) {
            ((AppsTabFragment) fragment).pullToRefresh();
        } else if (fragment instanceof UsersTabFragment) {
            ((UsersTabFragment) fragment).pullToRefresh();
        }
    }
}
