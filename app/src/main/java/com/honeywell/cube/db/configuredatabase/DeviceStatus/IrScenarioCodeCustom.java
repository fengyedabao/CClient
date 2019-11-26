package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;

/**
 * Created by H157925 on 16/8/1. 14:45
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 用在 Scenario Irloop code list
 */
public class IrScenarioCodeCustom implements Parcelable {
    public String timer = "5";
    public IrCode code = null;

    public IrScenarioCodeCustom(Parcel parcel) {
        timer = parcel.readString();
        code = parcel.readParcelable(IrCode.class.getClassLoader());
    }

    public IrScenarioCodeCustom() {
    }

    @Override
    public String toString() {
        return "IrScenarioCodeCustom [timer=" + timer
                + ", code=" + code + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(timer);
        parcel.writeParcelable(code, flags);
    }


    public static final Parcelable.Creator<IrScenarioCodeCustom> CREATOR = new Creator<IrScenarioCodeCustom>() {
        public IrScenarioCodeCustom createFromParcel(Parcel source) {
            return new IrScenarioCodeCustom(source);
        }

        public IrScenarioCodeCustom[] newArray(int size) {
            return new IrScenarioCodeCustom[size];
        }
    };
}
