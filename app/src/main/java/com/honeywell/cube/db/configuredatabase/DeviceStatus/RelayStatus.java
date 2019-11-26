package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/14. 16:23
 * Email:Shodong.Sun@honeywell.com
 */
public class RelayStatus implements Parcelable {
    public boolean status;

    public RelayStatus() {
    }

    public RelayStatus(Parcel parcel) {
        status = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(status ? 1 : 0);

    }

    public static Parcelable.Creator<RelayStatus> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<RelayStatus> CREATOR = new Creator<RelayStatus>() {
        public RelayStatus createFromParcel(Parcel source) {
            return new RelayStatus(source);
        }

        public RelayStatus[] newArray(int size) {
            return new RelayStatus[size];
        }

    };
}
