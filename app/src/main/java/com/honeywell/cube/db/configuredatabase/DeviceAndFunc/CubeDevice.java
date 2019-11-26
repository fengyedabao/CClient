package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/27. 10:54
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeDevice implements Parcelable {

    public int mId;
    public int mDeviceId;
    public String mDeviceSerial;
    public String mInfo_serialNumber;
    public String mInfo_firmWareVersion;
    public String mInfo_applicationVersion;
    public String mInfo_macAddress;
    public String mInfo_aliasName;

    //赋值初始化
    public CubeDevice(int deviceid, String deviceSerial, String info_serialNumber, String info_firmwareVersion, String info_applicationVersion, String info_macAddress, String info_aliasName) {
        this.mDeviceId = deviceid;
        this.mDeviceSerial = deviceSerial;
        this.mInfo_serialNumber = info_serialNumber;
        this.mInfo_firmWareVersion = info_firmwareVersion;
        this.mInfo_applicationVersion = info_applicationVersion;
        this.mInfo_macAddress = info_macAddress;
        this.mInfo_aliasName = info_aliasName;
    }

    public CubeDevice() {
    }

    //通过Parsel初始化
    public CubeDevice(Parcel parcel) {
        this.mId = parcel.readInt();
        this.mDeviceId = parcel.readInt();
        this.mDeviceSerial = parcel.readString();
        this.mInfo_serialNumber = parcel.readString();
        this.mInfo_firmWareVersion = parcel.readString();
        this.mInfo_applicationVersion = parcel.readString();
        this.mInfo_macAddress = parcel.readString();
        this.mInfo_aliasName = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeInt(mDeviceId);
        parcel.writeString(mDeviceSerial);
        parcel.writeString(mInfo_serialNumber);
        parcel.writeString(mInfo_firmWareVersion);
        parcel.writeString(mInfo_applicationVersion);
        parcel.writeString(mInfo_macAddress);
        parcel.writeString(mInfo_aliasName);
    }

    public static final Parcelable.Creator<BackaudioDevice> CREATOR = new Creator<BackaudioDevice>() {
        public BackaudioDevice createFromParcel(Parcel source) {
            return new BackaudioDevice(source);
        }

        public BackaudioDevice[] newArray(int size) {
            return new BackaudioDevice[size];
        }

    };
}
