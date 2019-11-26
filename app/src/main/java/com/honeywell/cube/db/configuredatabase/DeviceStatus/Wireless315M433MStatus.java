package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/14. 16:34
 * Email:Shodong.Sun@honeywell.com
 */
public class Wireless315M433MStatus implements Parcelable {
    public boolean status;
    public int openClosePercent;
    public boolean is_arm;
    public boolean is_alarm;

    public Wireless315M433MStatus() {
    }

    public Wireless315M433MStatus(Parcel parcel) {
        status = parcel.readInt() == 1 ? true : false;
        openClosePercent = parcel.readInt();

        is_arm = parcel.readInt() == 1 ? true : false;
        is_alarm = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(status ? 1 : 0);
        parcel.writeInt(openClosePercent);

        parcel.writeInt(is_arm ? 1 : 0);
        parcel.writeInt(is_alarm ? 1 : 0);
    }

    public static Parcelable.Creator<Wireless315M433MStatus> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<Wireless315M433MStatus> CREATOR = new Parcelable.Creator<Wireless315M433MStatus>() {
        public Wireless315M433MStatus createFromParcel(Parcel source) {
            return new Wireless315M433MStatus(source);
        }

        public Wireless315M433MStatus[] newArray(int size) {
            return new Wireless315M433MStatus[size];
        }

    };
}
