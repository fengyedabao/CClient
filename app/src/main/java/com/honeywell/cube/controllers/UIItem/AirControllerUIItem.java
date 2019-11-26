package com.honeywell.cube.controllers.UIItem;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BacnetAirConditioner;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/8/5. 16:35
 * Email:Shodong.Sun@honeywell.com
 */
public class AirControllerUIItem implements Parcelable {
    public MenuDeviceIRIconItem powerIconUIItem = new MenuDeviceIRIconItem();
    public int setTemp = 10;
    public int curTemp = 10;
    public ArrayList<MenuDeviceIRIconItem> fanspeedIconItemList = new ArrayList<>();
    public ArrayList<MenuDeviceIRIconItem> airModeIconItemList = new ArrayList<>();

    public BacnetAirConditioner airConditioner = new BacnetAirConditioner();

    public AirControllerUIItem() {
    }

    public AirControllerUIItem(Parcel parcel) {
        powerIconUIItem = parcel.readParcelable(MenuDeviceIRIconItem.class.getClassLoader());
        setTemp = parcel.readInt();
        curTemp = parcel.readInt();
        fanspeedIconItemList = parcel.readArrayList(MenuDeviceIRIconItem.class.getClassLoader());
        airModeIconItemList = parcel.readArrayList(MenuDeviceIRIconItem.class.getClassLoader());
        airConditioner = parcel.readParcelable(BacnetAirConditioner.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(powerIconUIItem, flags);
        parcel.writeInt(setTemp);
        parcel.writeInt(curTemp);
        parcel.writeList(fanspeedIconItemList);
        parcel.writeList(airModeIconItemList);
        parcel.writeParcelable(airConditioner, flags);
    }

    public static Parcelable.Creator<AirControllerUIItem> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<AirControllerUIItem> CREATOR = new Creator<AirControllerUIItem>() {
        public AirControllerUIItem createFromParcel(Parcel source) {
            return new AirControllerUIItem(source);
        }

        public AirControllerUIItem[] newArray(int size) {
            return new AirControllerUIItem[size];
        }
    };
}
