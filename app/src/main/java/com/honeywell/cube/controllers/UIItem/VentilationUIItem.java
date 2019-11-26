package com.honeywell.cube.controllers.UIItem;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/8/2. 16:48
 * Email:Shodong.Sun@honeywell.com
 */
public class VentilationUIItem implements Parcelable {
    public MenuDeviceIRIconItem powerItem = null;//开关
    public ArrayList<MenuDeviceIRIconItem> fanSpeedItems = null;//风速

    public boolean cycleInner = false;
    public ArrayList<MenuDeviceIRIconItem> modeItems = null;//内循环 模式

    public VentilationLoop ventilationLoop = null;

    public VentilationUIItem() {
    }

    public VentilationUIItem(Parcel parcel) {
        powerItem = parcel.readParcelable(MenuDeviceIRIconItem.class.getClassLoader());
        fanSpeedItems = parcel.readArrayList(MenuDeviceIRIconItem.class.getClassLoader());
        cycleInner = parcel.readInt() == 1 ? true : false;
        modeItems = parcel.readArrayList(MenuDeviceIRIconItem.class.getClassLoader());
        ventilationLoop = parcel.readParcelable(VentilationLoop.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(powerItem, flags);
        parcel.writeList(fanSpeedItems);
        parcel.writeInt(cycleInner ? 1 : 0);
        parcel.writeList(modeItems);
        parcel.writeParcelable(ventilationLoop, flags);
    }

    public static Parcelable.Creator<VentilationUIItem> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<VentilationUIItem> CREATOR = new Creator<VentilationUIItem>() {
        public VentilationUIItem createFromParcel(Parcel source) {
            return new VentilationUIItem(source);
        }

        public VentilationUIItem[] newArray(int size) {
            return new VentilationUIItem[size];
        }
    };
}
