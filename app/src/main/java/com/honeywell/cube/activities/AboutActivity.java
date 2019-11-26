package com.honeywell.cube.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.honeywell.cube.R;

public class AboutActivity extends CubeTitleBarActivity {


    @Override
    protected int getContent() {
        return R.layout.activity_about;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.menu_about);
    }

    @Override
    protected void initView() {
        super.initView();
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                ((TextView) findViewById(R.id.tv_version)).setText(getString(R.string.version_number) + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
