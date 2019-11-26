package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/8/2. 10:49
 * Email:Shodong.Sun@honeywell.com
 */
public class VentilationCustom implements Parcelable {
    public String power = "";// on/off
    public String mode = "";// humidity/dedumidity
    public String fanspeed = "";// high/low/middle
    public String cycletype = "";// inner/outside

    public VentilationCustom() {

    }

    public VentilationCustom(Parcel parcel) {
        power = parcel.readString();
        mode = parcel.readString();
        fanspeed = parcel.readString();
        cycletype = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(power);
        parcel.writeString(mode);
        parcel.writeString(fanspeed);
        parcel.writeString(cycletype);
    }
    public static Parcelable.Creator<VentilationCustom> getCreator() {
        return CREATOR;
    }
    public static final Parcelable.Creator<VentilationCustom> CREATOR = new Parcelable.Creator<VentilationCustom>() {
        public VentilationCustom createFromParcel(Parcel source) {
            return new VentilationCustom(source);
        }
        public VentilationCustom[] newArray(int size) {
            return new VentilationCustom[size];
        }
    };
}
