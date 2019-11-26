package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;

/**
 * Created by H157925 on 16/6/27. 13:52
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuRuleConditionRoom implements Parcelable {
    //Input  Room
    public String room_name = "";
    public String room_condition = "";//房间条件
    public String room_trigger_mode = "";
    public String room_value = ""+25;
    public int  room_id = -1;

    public MenuRuleConditionRoom() {
    }

    public MenuRuleConditionRoom(Parcel parcel) {
        room_name = parcel.readString();
        room_condition = parcel.readString();
        room_trigger_mode = parcel.readString();
        room_value = parcel.readString();
        room_id = parcel.readInt();
    }


    @Override
    public String toString() {
        return "menuRuleConditionRoom :[ "
                + " room_name : " + room_name
                + " room_condition : " + room_condition
                + " room_trigger_mode : " + room_trigger_mode
                + " room_value : " + room_value
                + " room_id : " + room_id
                + " ] .";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(room_name);
        parcel.writeString(room_condition);
        parcel.writeString(room_trigger_mode);
        parcel.writeString(room_value);
        parcel.writeInt(room_id);
    }

    public static Parcelable.Creator<MenuRuleConditionRoom> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<MenuRuleConditionRoom> CREATOR = new Creator<MenuRuleConditionRoom>() {
        public MenuRuleConditionRoom createFromParcel(Parcel source) {
            return new MenuRuleConditionRoom(source);
        }

        public MenuRuleConditionRoom[] newArray(int size) {
            return new MenuRuleConditionRoom[size];
        }
    };
}
