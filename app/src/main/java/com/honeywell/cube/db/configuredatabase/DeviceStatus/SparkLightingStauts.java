package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/14. 15:49
 * Email:Shodong.Sun@honeywell.com
 */
public class SparkLightingStauts implements Parcelable {
    public boolean is_arm;
    public boolean is_alarm;
    public boolean status;
    public int openClosePercent;

    public SparkLightingStauts() {
    }

    public SparkLightingStauts(Parcel parcel) {
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

    public static Parcelable.Creator<SparkLightingStauts> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<SparkLightingStauts> CREATOR = new Parcelable.Creator<SparkLightingStauts>() {
        public SparkLightingStauts createFromParcel(Parcel source) {
            return new SparkLightingStauts(source);
        }

        public SparkLightingStauts[] newArray(int size) {
            return new SparkLightingStauts[size];
        }

    };
}
