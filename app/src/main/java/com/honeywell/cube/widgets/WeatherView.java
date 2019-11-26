package com.honeywell.cube.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ResourceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhujunyu on 16/6/6.
 */
public class WeatherView extends LinearLayout {


    private AttributeSet mAttributeSet;

    private Context mContext;

    private Drawable mIvBackground;
    private Drawable mIvTodayWeather;
    private Drawable mIvWeek_1_Weather;
    private Drawable mIvWeek_2_Weather;
    private Drawable mIvWeek_3_Weather;
    private Drawable mIvWeek_4_Weather;
    private Drawable mIvWeek_5_Weather;
    private Drawable mIvWeek_6_Weather;

    private String week1;
    private String week2;
    private String week3;
    private String week4;
    private String week5;
    private String week6;

    private int mTodayTemp;
    private int mTodayPm2_5;


    //布局元素定义

    private RelativeLayout mRelativeLayout;


    private ImageView mImageViewToday;
    private ImageView mImageViewWeek1;
    private ImageView mImageViewWeek2;
    private ImageView mImageViewWeek3;
    private ImageView mImageViewWeek4;
    private ImageView mImageViewWeek5;
    private ImageView mImageViewWeek6;

    private TextView mTextViewTodayTemp;
    private TextView mTextViewTodayPm;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;


    public WeatherView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAttributeSet = attrs;
        mContext = context;
        initView();
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttributeSet = attrs;
        mContext = context;
        initView();
    }


    private void initView() {


        TypedArray typedArray = mContext.obtainStyledAttributes(mAttributeSet, R.styleable.WeatherView);
        mIvBackground = typedArray.getDrawable(R.styleable.WeatherView_weather_background);
        mIvTodayWeather = typedArray.getDrawable(R.styleable.WeatherView_todayWeather);
        mIvWeek_1_Weather = typedArray.getDrawable(R.styleable.WeatherView_week_1_icon);
        mIvWeek_2_Weather = typedArray.getDrawable(R.styleable.WeatherView_week_2_icon);
        mIvWeek_3_Weather = typedArray.getDrawable(R.styleable.WeatherView_week_3_icon);
        mIvWeek_4_Weather = typedArray.getDrawable(R.styleable.WeatherView_week_4_icon);
        mIvWeek_5_Weather = typedArray.getDrawable(R.styleable.WeatherView_week_5_icon);
        mIvWeek_6_Weather = typedArray.getDrawable(R.styleable.WeatherView_week_6_icon);
        week1 = typedArray.getString(R.styleable.WeatherView_week_1_title);
        week2 = typedArray.getString(R.styleable.WeatherView_week_2_title);
        week3 = typedArray.getString(R.styleable.WeatherView_week_3_title);
        week4 = typedArray.getString(R.styleable.WeatherView_week_4_title);
        week5 = typedArray.getString(R.styleable.WeatherView_week_5_title);
        week6 = typedArray.getString(R.styleable.WeatherView_week_6_title);

        mTodayTemp = typedArray.getInteger(R.styleable.WeatherView_temperature, 0);
        mTodayPm2_5 = typedArray.getInteger(R.styleable.WeatherView_pm2_5, 0);
        typedArray.recycle();
        initLayout();


    }


    private void initLayout() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_layout, null);
        this.addView(view);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.linearlayout_background);
        mImageViewToday = (ImageView) findViewById(R.id.iv_todayweather);
        mImageViewWeek1 = (ImageView) findViewById(R.id.iv_week_1);
        mImageViewWeek2 = (ImageView) findViewById(R.id.iv_week_2);
        mImageViewWeek3 = (ImageView) findViewById(R.id.iv_week_3);
        mImageViewWeek4 = (ImageView) findViewById(R.id.iv_week_4);
        mImageViewWeek5 = (ImageView) findViewById(R.id.iv_week_5);
        mImageViewWeek6 = (ImageView) findViewById(R.id.iv_week_6);


        mTextViewTodayTemp = (TextView) findViewById(R.id.tv_today_temp);

        mTextViewTodayPm = (TextView) findViewById(R.id.iv_today_pm);

        mTextView1 = (TextView) findViewById(R.id.tv_week_1);
        mTextView2 = (TextView) findViewById(R.id.tv_week_2);
        mTextView3 = (TextView) findViewById(R.id.tv_week_3);
        mTextView4 = (TextView) findViewById(R.id.tv_week_4);
        mTextView5 = (TextView) findViewById(R.id.tv_week_5);
        mTextView6 = (TextView) findViewById(R.id.tv_week_6);
        setViewDate();

    }

    private void setViewDate() {
        mRelativeLayout.setBackground(mIvBackground);
        mImageViewToday.setImageDrawable(mIvTodayWeather);
        mImageViewWeek1.setImageDrawable(mIvWeek_1_Weather);
        mImageViewWeek2.setImageDrawable(mIvWeek_2_Weather);
        mImageViewWeek3.setImageDrawable(mIvWeek_3_Weather);
        mImageViewWeek4.setImageDrawable(mIvWeek_4_Weather);
        mImageViewWeek5.setImageDrawable(mIvWeek_5_Weather);
        mImageViewWeek6.setImageDrawable(mIvWeek_6_Weather);

        if (mTodayTemp != 0 && mTodayPm2_5 != 0) {
            mTextViewTodayTemp.setText("" + mTodayTemp);
            mTextViewTodayPm.setText("" + mTodayPm2_5);
        }

        mTextView1.setText(week1);
        mTextView2.setText(week2);
        mTextView3.setText(week3);
        mTextView4.setText(week4);
        mTextView5.setText(week5);
        mTextView6.setText(week6);
    }


    public void setTodayWeatherDate(JSONObject jsonObject) {

        String todayTemp = jsonObject.optString("temperature");
        String todayPm = jsonObject.optString("pm25");

        String todayWeather = jsonObject.optString("weather");

        mImageViewToday.setImageResource(ResourceUtil.getResIdFromName(getContext(), todayWeather.toLowerCase()));
//        mImageViewToday.setImageResource(ResourceUtil.getResIdFromName(getContext(), "unknown"));
//        Log.e("todayTemp", todayTemp);

        mTextViewTodayTemp.setText(todayTemp + "℃");

        mTextViewTodayPm.setText(todayPm);
        mTextViewTodayPm.setText(Html.fromHtml("<font color = #FFFFFF> PM2.5</font>&#160;" + "<font color = #e1bf54>" + todayPm + "</font>"));
    }


    public void setWeekWeatherData(JSONObject jsonObject) {
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < array.length(); i++) {
            jsonObject = array.optJSONObject(i);
            setDataView(jsonObject, i);
        }
    }


    public void setDataView(JSONObject jsonObject, int i) {
        int resId = ResourceUtil.getResIdFromName(getContext(), jsonObject.optString("weatherDay").toLowerCase());
//        int resId = getResource(jsonObject.optString("weatherDay").toLowerCase());
        LogUtil.e("weather image:", "" + jsonObject.optString("weatherDay").toLowerCase() + " , resId = " + resId);
//        Log.e("week day:",""+jsonObject.optString("date"));
        String week = getWeekOfDate(jsonObject.optString("date"));
//        Log.e("week day:",""+week);
        switch (i) {
            case 1:
                mTextView1.setText(week);
                mImageViewWeek1.setImageResource(resId);
                break;
            case 2:
                mTextView2.setText(week);
                mImageViewWeek2.setImageResource(resId);
                break;
            case 3:
                mTextView3.setText(week);
                mImageViewWeek3.setImageResource(resId);
                break;
            case 4:
                mTextView4.setText(week);
                mImageViewWeek4.setImageResource(resId);
                break;
            case 5:
                mTextView5.setText(week);
                mImageViewWeek5.setImageResource(resId);
                break;
            case 6:
                mTextView6.setText(week);
                mImageViewWeek6.setImageResource(resId);
                break;
            default:
                break;
        }


    }

    public String getWeekOfDate(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] weekDaysName = {
                mContext.getString(R.string.week_sunday),
                mContext.getString(R.string.week_monday),
                mContext.getString(R.string.week_tuesday),
                mContext.getString(R.string.week_wednesday),
                mContext.getString(R.string.week_thursday),
                mContext.getString(R.string.week_friday),
                mContext.getString(R.string.week_saturday)};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }
}
