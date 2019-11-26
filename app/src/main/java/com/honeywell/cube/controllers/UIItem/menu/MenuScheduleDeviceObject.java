package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;

/**
 * Created by H157925 on 16/7/6. 15:14
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuScheduleDeviceObject implements Parcelable {
    public int type;//用于区分是开头还是内容
    public String section = "";
    public String title = "";
    public Object loop = null;
    public String loopType = "";//用于区分loop的类型

    public MenuScheduleDeviceObject() {
    }

    //通过Parsel初始化
    public MenuScheduleDeviceObject(Parcel parcel) {
        type = parcel.readInt();
        section = parcel.readString();
        title = parcel.readString();
        loopType = parcel.readString();
        readObjectFromParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(type);
        parcel.writeString(section);
        parcel.writeString(title);
        parcel.writeString(loopType);
        writeObjectToParcel(parcel, flags);
    }

    public void writeObjectToParcel(Parcel parcel, int flags) {
        if (ModelEnum.SPARKLIGHTING.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((SparkLightingLoop) loop, flags);
        } else if (ModelEnum.WIFI_485.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((Wifi485Loop) loop, flags);
        } else if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((Wireless315M433MLoop) loop, flags);
        } else if (ModelEnum.LOOP_RELAY.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((RelayLoop) loop, flags);
        } else if (ModelEnum.LOOP_BACKAUDIO.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((BackaudioLoop) loop, flags);
        } else if (ModelEnum.LOOP_ZONE.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((WiredZoneLoop) loop, flags);
        } else if (ModelEnum.LOOP_IPVDP.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((IpvdpZoneLoop) loop, flags);
        } else if (ModelEnum.LOOP_BACNET.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((BacnetLoop) loop, flags);
        } else if (ModelEnum.LOOP_IR_DVD.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_TV.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_STB.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_AC.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_CUSTOM.equalsIgnoreCase(loopType)) {
            parcel.writeParcelable((IrLoop) loop, flags);
        }
    }

    public void readObjectFromParcel(Parcel parcel) {
        if (ModelEnum.SPARKLIGHTING.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(SparkLightingLoop.class.getClassLoader());
        } else if (ModelEnum.WIFI_485.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(Wifi485Loop.class.getClassLoader());
        } else if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(Wireless315M433MLoop.class.getClassLoader());
        } else if (ModelEnum.LOOP_RELAY.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(RelayLoop.class.getClassLoader());
        } else if (ModelEnum.LOOP_BACKAUDIO.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(BackaudioLoop.class.getClassLoader());
        } else if (ModelEnum.LOOP_ZONE.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(WiredZoneLoop.class.getClassLoader());
        } else if (ModelEnum.LOOP_IPVDP.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(IpvdpZoneLoop.class.getClassLoader());
        } else if (ModelEnum.LOOP_BACNET.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(BacnetLoop.class.getClassLoader());
        } else if (ModelEnum.LOOP_IR_DVD.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_TV.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_STB.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_AC.equalsIgnoreCase(loopType) || ModelEnum.LOOP_IR_CUSTOM.equalsIgnoreCase(loopType)) {
            loop = parcel.readParcelable(IrLoop.class.getClassLoader());
        }
    }

    public static final Parcelable.Creator<MenuScheduleDeviceObject> CREATOR = new Creator<MenuScheduleDeviceObject>() {
        public MenuScheduleDeviceObject createFromParcel(Parcel source) {
            return new MenuScheduleDeviceObject(source);
        }

        public MenuScheduleDeviceObject[] newArray(int size) {
            return new MenuScheduleDeviceObject[size];
        }
    };
}
