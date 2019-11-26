package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/5/29. 11:34
 * Email:Shodong.Sun@honeywell.com
 */
public class IRLoopCustom implements Parcelable {
    public ArrayList<IrScenarioCodeCustom> scenarioCodes = new ArrayList<>();

    public IRLoopCustom() {
    }

    public IRLoopCustom(Parcel parcel) {
        scenarioCodes = parcel.readArrayList(IrScenarioCodeCustom.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeList(scenarioCodes);
    }


    public static final Parcelable.Creator<IRLoopCustom> CREATOR = new Creator<IRLoopCustom>() {
        public IRLoopCustom createFromParcel(Parcel source) {
            return new IRLoopCustom(source);
        }

        public IRLoopCustom[] newArray(int size) {
            return new IRLoopCustom[size];
        }
    };
}
