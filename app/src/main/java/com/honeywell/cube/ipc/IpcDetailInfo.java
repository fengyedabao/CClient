package com.honeywell.cube.ipc;


import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.PeripheralDevice;

public class IpcDetailInfo implements Parcelable {
    public int mType;
    public String mName;
    public String mIpAddr;
    public String mMacAddr;
    public String mIpcType = "";
    public String mMainStream = "";
    public String mSubStream = "";
    public int mStreamPort = Constants.DEFAULTIPCSTREAMPORT;
    public String mUser = Constants.DEFAULTIPCUSER;
    public String mPassword = Constants.DEFAULTIPCPWD;


    public IpcDetailInfo() {
        super();
    }

    public IpcDetailInfo(PeripheralDevice device) {
        if (null != device) {
            this.mType = device.mType;
            this.mName = device.mName;
            this.mIpAddr = device.mIpAddr;
            this.mMacAddr = device.mMacAddr;
        }
    }

    public IpcDetailInfo(Parcel parcel) {
        mType = parcel.readInt();
        mName = parcel.readString();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mIpcType = parcel.readString();
        mMainStream = parcel.readString();
        mSubStream = parcel.readString();
        mStreamPort = parcel.readInt();
        mUser = parcel.readString();
        mPassword = parcel.readString();
    }

    public static Creator<IpcDetailInfo> getCreator() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "IpcDetailInfo [mIpcType=" + mIpcType + ", mMainStream="
                + mMainStream + ", mSubStream=" + mSubStream + ", mStreamPort="
                + mStreamPort + ", mUser=" + mUser + ", mPassword=" + mPassword
                + ", mName=" + mName + ", mIpAddr=" + mIpAddr + ", mMacAddr="
                + mMacAddr + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mType);
        parcel.writeString(mName);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeString(mIpcType);
        parcel.writeString(mMainStream);
        parcel.writeString(mSubStream);
        parcel.writeInt(mStreamPort);
        parcel.writeString(mUser);
        parcel.writeString(mPassword);
    }

    public static final Creator<IpcDetailInfo> CREATOR = new Creator<IpcDetailInfo>() {
        public IpcDetailInfo createFromParcel(Parcel source) {
            return new IpcDetailInfo(source);
        }

        public IpcDetailInfo[] newArray(int size) {
            return new IpcDetailInfo[size];
        }

    };
} 
