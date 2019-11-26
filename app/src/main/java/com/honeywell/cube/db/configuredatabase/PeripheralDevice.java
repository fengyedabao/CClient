package com.honeywell.cube.db.configuredatabase;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 14:50
 * Email:Shodong.Sun@honeywell.com
 */
public class PeripheralDevice implements Parcelable {
    public int mPrimaryID = 0;//用于映射子表
    public int mType = 0;
    public String mName = "";
    public String mIpAddr = "";
    public String mMacAddr = "";
    public int mPort = 0;
    public int mIsConfig = CommonData.NOTCONFIG;//for wifi module add
    public int mIsOnline = CommonData.NOTONLINE;
    public int mBacnetId = -1;//for bacnet add
    public String mBrandName = "";
    public int mMaskId = -1;
    public long mId = -1;
    public String mVersion = "";

    public PeripheralDevice() {
        if (mType == CommonData.MODULE_TYPE_SPARKLIGHTING) {
            mIsConfig = CommonData.HASCONFIG;
        }
    }

    public PeripheralDevice(Parcel parcel) {
        mPrimaryID = parcel.readInt();
        mType = parcel.readInt();
        mName = parcel.readString();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mPort = parcel.readInt();
        mIsConfig = parcel.readInt();
        mIsOnline = parcel.readInt();
        mBacnetId = parcel.readInt();
        mBrandName = parcel.readString();
        mMaskId = parcel.readInt();
        mId = parcel.readLong();
        mVersion = parcel.readString();
    }

    public PeripheralDevice(int primariID, int mType, String mName, String mIpAddr, String mMacAddr, int mPort, int mBacnetId, String mBrandName, int mMaskId, long mId, String version) {
        super();
        this.mPrimaryID = primariID;
        this.mType = mType;
        this.mName = mName;
        this.mIpAddr = mIpAddr;
        this.mMacAddr = mMacAddr;
        this.mPort = mPort;
        if (this.mType == CommonData.MODULE_TYPE_SPARKLIGHTING) {
            mIsConfig = CommonData.HASCONFIG;
        }
        this.mBacnetId = mBacnetId;
        this.mBrandName = mBrandName;
        this.mMaskId = mMaskId;
        this.mId = mId;
        this.mVersion = version;
    }
    public static Parcelable.Creator<PeripheralDevice> getCreator() {
        return CREATOR;
    }
    @Override
    public String toString() {
        return "PeripheralDevice [mPrimaryID=" + mPrimaryID + " mType " + mType + ", mName=" + mName
                + ", mIpAddr=" + mIpAddr + ", mMacAddr=" + mMacAddr
                + ", mPort=" + mPort + ", mIsConfig=" + mIsConfig
                + ", mIsOnline=" + mIsOnline + ", mBacnetId=" + mBacnetId
                + ", mBrandName=" + mBrandName + ", mMaskId=" + mMaskId
                + ", mVersion=" + mVersion
                + ", mId=" + mId + "]";
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mPrimaryID);
        parcel.writeInt(mType);
        parcel.writeString(mName);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeInt(mPort);
        parcel.writeInt(mIsConfig);
        parcel.writeInt(mIsOnline);
        parcel.writeInt(mBacnetId);
        parcel.writeString(mBrandName);
        parcel.writeInt(mMaskId);
        parcel.writeLong(mId);
        parcel.writeString(mVersion);
    }

    public static final Parcelable.Creator<PeripheralDevice> CREATOR = new Creator<PeripheralDevice>() {
        public PeripheralDevice createFromParcel(Parcel source) {
            return new PeripheralDevice(source);
        }
        public PeripheralDevice[] newArray(int size) {
            return new PeripheralDevice[size];
        }
    };
}
