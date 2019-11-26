package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleRuleInfo;

import org.json.JSONArray; import org.json.JSONObject;

/**
 * Created by H157925 on 16/6/29. 10:41
 * Email:Shodong.Sun@honeywell.com
 * 首页元素
 */
public class MenuScheduleUIItem implements Parcelable {
    //UI
    //第一个界面
    public String start_time_str = "";//最大的时间
    public String details_name = "";//下面的小字 名称
    public String details_repeat = "";//下面的小字，重复的次数
    public boolean isOn = false;//是否启动

    public ScheduleRuleInfo scheduleRuleInfo = null;//计划


    public MenuScheduleUIItem() {
    }

    //通过Parsel初始化
    public MenuScheduleUIItem(Parcel parcel) {
        start_time_str = parcel.readString();
        details_name = parcel.readString();
        details_repeat = parcel.readString();
        isOn = parcel.readInt() == 1 ? true : false;
        scheduleRuleInfo = parcel.readParcelable(ScheduleRuleInfo.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "menu schedule item :[ "
                + " start_time_str : " + start_time_str
                + " details_name : " + details_name
                + " details_repeat : " + details_repeat
                + " isOn : " + isOn
                + " ] .";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(start_time_str);
        dest.writeString(details_name);
        dest.writeString(details_repeat);
        dest.writeInt(isOn ? 1 : 0);
        dest.writeParcelable(scheduleRuleInfo, flags);
    }

    public static final Parcelable.Creator<MenuScheduleUIItem> CREATOR = new Creator<MenuScheduleUIItem>() {
        public MenuScheduleUIItem createFromParcel(Parcel source) {
            return new MenuScheduleUIItem(source);
        }

        public MenuScheduleUIItem[] newArray(int size) {
            return new MenuScheduleUIItem[size];
        }
    };
}
