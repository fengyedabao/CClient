package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.VentilationCustom;

/**
 * Created by H157925 on 16/8/2. 10:46
 * Email:Shodong.Sun@honeywell.com
 */
public class VentilationLoop extends BasicLoop implements Parcelable {
    public String controltype = "";
    public String power = "";
    public String fanspeed = "";
    public String cycletype = "";
    public String humidity = "";
    public String dehumidity = "";

    //self
    public VentilationCustom customModel = new VentilationCustom();

    public VentilationLoop() {
    }

    public VentilationLoop(Parcel parcel) {
        mLoopSelfPrimaryId = parcel.readLong();
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mModuleType = parcel.readInt();
        mLoopType = parcel.readInt();

        controltype = parcel.readString();
        power = parcel.readString();
        fanspeed = parcel.readString();
        cycletype = parcel.readString();
        humidity = parcel.readString();
        dehumidity = parcel.readString();
        customModel = parcel.readParcelable(VentilationCustom.class.getClassLoader());
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeLong(mModulePrimaryId);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mLoopId);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeInt(mModuleType);
        parcel.writeInt(mLoopType);

        parcel.writeString(controltype);
        parcel.writeString(power);
        parcel.writeString(fanspeed);
        parcel.writeString(cycletype);
        parcel.writeString(humidity);
        parcel.writeString(dehumidity);
        parcel.writeParcelable(customModel, flags);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static Parcelable.Creator<VentilationLoop> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<VentilationLoop> CREATOR = new Parcelable.Creator<VentilationLoop>() {
        public VentilationLoop createFromParcel(Parcel source) {
            return new VentilationLoop(source);
        }

        public VentilationLoop[] newArray(int size) {
            return new VentilationLoop[size];
        }
    };

}
