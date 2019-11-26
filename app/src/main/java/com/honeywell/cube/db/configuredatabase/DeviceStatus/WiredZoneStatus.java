package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/14. 15:46
 * Email:Shodong.Sun@honeywell.com
 */
public class WiredZoneStatus implements Parcelable {
    public boolean is_arm;
    public boolean is_alarm;

    public WiredZoneStatus() {
    }


    public WiredZoneStatus(Parcel parcel) {
        is_arm = parcel.readInt() == 1 ? true : false;
        is_alarm = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(is_arm ? 1 : 0);
        parcel.writeInt(is_alarm ? 1 : 0);
    }

    public static Parcelable.Creator<WiredZoneStatus> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<WiredZoneStatus> CREATOR = new Parcelable.Creator<WiredZoneStatus>() {
        public WiredZoneStatus createFromParcel(Parcel source) {
            return new WiredZoneStatus(source);
        }

        public WiredZoneStatus[] newArray(int size) {
            return new WiredZoneStatus[size];
        }

    };
}
