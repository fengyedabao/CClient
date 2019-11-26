package com.honeywell.cube.activities;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.honeywell.cube.R;

public class CubeBaseActivity extends AppCompatActivity {
    protected int activityCloseEnterAnimation;

    protected int activityCloseExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        initAnimation();
    }

    protected void setTheme() {
        setTheme(R.style.CubeBaseTheme);
    }

    public void initAnimation() {
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowAnimationStyle
        });

        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);

        activityStyle.recycle();

        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{
                android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation
        });

        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);

        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);

        activityStyle.recycle();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }
}
