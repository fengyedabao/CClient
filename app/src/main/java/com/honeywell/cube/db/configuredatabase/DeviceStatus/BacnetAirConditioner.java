package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/14. 16:53
 * Email:Shodong.Sun@honeywell.com
 */
public class BacnetAirConditioner implements Parcelable {
    public int set_temp = 12;
    public int current_temp = 12;
    public String mode = "";
    public String fan_speed = "";
    public boolean status = false; //on/off

    public BacnetAirConditioner() {
    }

    public BacnetAirConditioner(Parcel parcel) {
        set_temp = parcel.readInt();
        current_temp = parcel.readInt();
        mode = parcel.readString();
        fan_speed = parcel.readString();
        status = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(set_temp);
        parcel.writeInt(current_temp);
        parcel.writeString(mode);
        parcel.writeString(fan_speed);
        parcel.writeInt(status ? 1 : 0);
    }

    public static Parcelable.Creator<BacnetAirConditioner> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<BacnetAirConditioner> CREATOR = new Parcelable.Creator<BacnetAirConditioner>() {
        public BacnetAirConditioner createFromParcel(Parcel source) {
            return new BacnetAirConditioner(source);
        }

        public BacnetAirConditioner[] newArray(int size) {
            return new BacnetAirConditioner[size];
        }

    };
}
