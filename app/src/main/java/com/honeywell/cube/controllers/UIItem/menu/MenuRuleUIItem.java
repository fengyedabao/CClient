package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.R;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerInfo;

/**
 * Created by H157925 on 16/6/6. 09:46
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 用于组织Rule首页需要显示的数据
 */
public class MenuRuleUIItem implements Parcelable {
    public ScenarioTriggerInfo info = null;//对象
    public boolean info_states = false;// 状态
    public String info_title = "";//题目
    public String info_details = "";//时间细节
    public int info_input_imagename = R.mipmap.rules_sensor;//输入端图片名称
    public String info_input_name = "";//输入端名称
    public int info_output_imagename = R.mipmap.rules_sensor;//输出端图片名称
    public String info_output_name = "";//输出端名称

    public MenuRuleUIItem() {
    }

    //通过Parsel初始化
    public MenuRuleUIItem(Parcel parcel) {
        info = parcel.readParcelable(ScenarioTriggerInfo.class.getClassLoader());
        info_states = parcel.readInt() == 1 ? true : false;
        info_title = parcel.readString();
        info_details = parcel.readString();
        info_input_imagename = parcel.readInt();
        info_input_name = parcel.readString();
        info_output_imagename = parcel.readInt();
        info_output_name = parcel.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "menuRuleObject :[ "
                + " info_title : " + info_title
                + " info_states : " + (info_states ? "true" : "false")
                + " info_details : " + info_details
                + " info_input_imagename : " + info_input_imagename
                + " info_input_name : " + info_input_name
                + " info_output_imagename : " + info_output_imagename
                + " info_output_name : " + info_output_name
                + " info : " + info.toString()
                + " ] .";
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(info, flags);
        parcel.writeInt(info_states ? 1 : 0);
        parcel.writeString(info_title);
        parcel.writeString(info_details);
        parcel.writeInt(info_input_imagename);
        parcel.writeString(info_input_name);
        parcel.writeInt(info_output_imagename);
        parcel.writeString(info_output_name);
    }

    public static final Parcelable.Creator<MenuRuleUIItem> CREATOR = new Creator<MenuRuleUIItem>() {
        public MenuRuleUIItem createFromParcel(Parcel source) {
            return new MenuRuleUIItem(source);
        }

        public MenuRuleUIItem[] newArray(int size) {
            return new MenuRuleUIItem[size];
        }
    };
}
