
package com.honeywell.cube.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.honeywell.cube.R;

public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private int mTabTitle[] = new int[]{
            R.string.edit_scenario_device, R.string.edit_scenario_zone
    };

    private Context mContext;
    private ScenarioEditDeviceFragment mDeviceFragment;
    private ScenarioEditZoneFragment mZoneFragment;

    public TabViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return getFragment(position);
    }

    public Fragment getFragment(int position) {
        if (position == 0) {
            return mDeviceFragment == null ? mDeviceFragment = new ScenarioEditDeviceFragment() : mDeviceFragment;
        } else {
            return mZoneFragment == null ? mZoneFragment = new ScenarioEditZoneFragment() : mZoneFragment;
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mTabTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mTabTitle[position]);
    }

    public void updateUI() {
        if (null != mDeviceFragment) {
            mDeviceFragment.updateUI();
        }
        if (null != mZoneFragment) {
            mZoneFragment.updateUI();
        }
    }
}
