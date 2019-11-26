package com.honeywell.cube.widgets;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.HomeRoomDetailsUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.lib.utils.ResourceUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherGroup extends LinearLayout {
    private Context context;
    private ViewPager viewPager;
    private LinearLayout ll_dot;
    private ImageView[] dots;
    private int currentIndex;
    int viewPager_size = 2;
    private List<View> list_Views;
    public static final String TAG = WeatherGroup.class.getSimpleName();

    WeatherView mWeatherView;
    ViewPager.OnPageChangeListener mOnPageChangeListener;
    private String mLocation;
    TextView mTitle;
    ArrayList<HomeRoomDetailsUIItem> mHomeRoomDetailsUIItem;
    JSONObject mTodayData;
    JSONObject mWeekData;

    public WeatherGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_weather_group, this);
        viewPager = (ViewPager) view.findViewById(R.id.vPager);
        ll_dot = (LinearLayout) view.findViewById(R.id.ll_channel_dots);
        initView();
    }

    private void initView() {
        list_Views = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v1 = inflater.inflate(R.layout.layout_weather_first, null);
        list_Views.add(v1);
        mWeatherView = (WeatherView) v1.findViewById(R.id.weatherView);
        if (mWeekData != null) {
            mWeatherView.setWeekWeatherData(mWeekData);
        }
        if (mTodayData != null) {
            mWeatherView.setTodayWeatherDate(mTodayData);
        }
        viewPager_size = 1;
        if (mHomeRoomDetailsUIItem != null) {
            final int size = mHomeRoomDetailsUIItem.size();
            for (int i = 0; i < size; i++) {
                View v2 = inflater.inflate(R.layout.layout_weather_second, null);
                final HomeRoomDetailsUIItem item = mHomeRoomDetailsUIItem.get(i);
                ((ImageView) v2.findViewById(R.id.iv_temperature)).setImageResource(item.roomImageId);
                ((TextView) v2.findViewById(R.id.tv_temperature_tip)).setText(item.name);
                v2.findViewById(R.id.rl_room_temperature).setBackgroundResource(item.roomBackgroundImageId);
                TextView tv = (TextView) v2.findViewById(R.id.tv_temperature);
                int weather_type = item.type;
                switch (weather_type) {
                    case ModelEnum.HOME_ROOM_TYPE_CO_2:
                    case ModelEnum.HOME_ROOM_TYPE_HUMIDITY:
                    case ModelEnum.HOME_ROOM_TYPE_PM2_5:
                        tv.setText(item.value + "");
                        break;
                    case ModelEnum.HOME_ROOM_TYPE_TEMPERATURE:
                        tv.setText(item.value + "℃");
                }

                list_Views.add(v2);
            }
            viewPager_size += size;
        }
        viewPager.setAdapter(new WeatherAdapter(list_Views));
        initDots();
    }

    public void setTodayWeatherData(JSONObject jsonObject) {
        mTodayData = jsonObject;
        mWeatherView.setTodayWeatherDate(jsonObject);
    }

    public void setWeekWeatherData(JSONObject jsonObject) {
        mWeekData = jsonObject;
        mWeatherView.setWeekWeatherData(jsonObject);
    }

    public void setLocation(String location) {
        mLocation = location;
        mTitle.setText(mLocation);
    }

    public void setTitle(TextView title) {
        mTitle = title;
    }

    public void setRoomLoop(ArrayList<HomeRoomDetailsUIItem> items) {
        mHomeRoomDetailsUIItem = items;
        initView();
    }

    private void initDots() {
        if (0 < viewPager_size) {
            ll_dot.removeAllViews();
            ll_dot.setVisibility(View.VISIBLE);
            for (int j = 0; j < viewPager_size; j++) {
                ImageView image = new ImageView(context);
                LayoutParams params = new LayoutParams(ResourceUtil.dp2px(getContext(), 6), ResourceUtil.dp2px(getContext(), 6));
                params.setMargins(ResourceUtil.dp2px(getContext(), 6), 0, ResourceUtil.dp2px(getContext(), 6), 0);
                image.setBackgroundResource(R.drawable.dot_unselected);
                ll_dot.addView(image, params);
            }
        }
        dots = new ImageView[viewPager_size];
        for (int i = 0; i < viewPager_size; i++) {
            dots[i] = (ImageView) ll_dot.getChildAt(i);
        }
        currentIndex = 0;
        dots[currentIndex].setBackgroundResource(R.drawable.dot_selected);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                setCurDot(arg0);
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(arg0);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(arg0, arg1, arg2);
                }

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(arg0);
                }

            }
        });
//        }
    }

    private void setCurDot(int positon) {
        if (positon < 0 || positon > viewPager_size - 1 || currentIndex == positon) {
            return;
        }
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(R.drawable.dot_unselected);
        }
        dots[positon].setBackgroundResource(R.drawable.dot_selected);
        currentIndex = positon;
        if (positon > 0) {
            mTitle.setText(mHomeRoomDetailsUIItem.get(positon - 1).roomTitle);
        } else {
            mTitle.setText(mLocation);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public class WeatherAdapter extends PagerAdapter {

        private List<View> lists;

        public WeatherAdapter(List<View> data) {
            lists = data;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ViewGroup parent = (ViewGroup) lists.get(arg1).getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                // container.addView(v);
                ((ViewPager) arg0).addView(lists.get(arg1), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return lists.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            try {
                ((ViewPager) arg0).removeView(lists.get(arg1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
