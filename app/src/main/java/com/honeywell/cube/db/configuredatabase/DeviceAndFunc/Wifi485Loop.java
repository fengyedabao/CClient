package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BacnetAirConditioner;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/12. 13:30
 * Email:Shodong.Sun@honeywell.com
 */
public class Wifi485Loop extends BasicLoop implements Parcelable {
    public String mBrandName = "";
    public int mPortId = -1;
    public String mLoopType = "";
    public int mSlaveAddr = -1;
    //Custom
    public BacnetAirConditioner customModel = new BacnetAirConditioner();

    public Wifi485Loop() {
    }

    public Wifi485Loop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mSubDevId = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mBrandName = parcel.readString();
        mPortId = parcel.readInt();
        mLoopType = parcel.readString();
        mSlaveAddr = parcel.readInt();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        customModel = parcel.readParcelable(BacnetAirConditioner.class.getClassLoader());
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    public Wifi485Loop(long mDevId, String mLoopName,
                       int mRoomid, int mLoopId,
                       int mSubGatewayId, String mIpAddr, String mMacAddr,
                       String mBrandName, int mPortId,
                       String mLoopType, int mSlaveAddr) {
        super();
        this.mModulePrimaryId = mDevId;
        this.mLoopName = mLoopName;
        this.mRoomId = mRoomid;
        this.mLoopId = mLoopId;
        this.mSubDevId = mSubGatewayId;
        this.mIpAddr = mIpAddr;
        this.mMacAddr = mMacAddr;
        this.mLoopType = mLoopType;
        this.mBrandName = mBrandName;
        this.mPortId = mPortId;
        this.mLoopType = mLoopType;
        this.mSlaveAddr = mSlaveAddr;
    }

    public static Parcelable.Creator<Wifi485Loop> getCreator() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "BacnetLoop [mDevId=" + mModulePrimaryId
                + ", mLoopName=" + mLoopName + ", mRoomid=" + mRoomId
                + ", mLoopId=" + mLoopId
                + ", mSubGatewayId=" + mSubDevId + ", mIpAddr=" + mIpAddr
                + ", mMacAddr=" + mMacAddr + "mBrandName:" + mBrandName
                + "mPortId:" + mPortId + "mLoopType:" + mLoopType + "mSlaveAddr:" + mSlaveAddr + "]";
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("loop_type");
        returnValue.add("" + mLoopType);
        returnValue.add("port_id");
        returnValue.add("" + mPortId);
        returnValue.add("slaveaddr");
        returnValue.add("" + mSlaveAddr);
        returnValue.add("brandname");
        returnValue.add("" + mBrandName);
        return returnValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mModulePrimaryId);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mLoopId);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeString(mBrandName);
        parcel.writeInt(mPortId);
        parcel.writeString(mLoopType);
        parcel.writeInt(mSlaveAddr);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeParcelable(customModel, flags);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static final Parcelable.Creator<Wifi485Loop> CREATOR = new Creator<Wifi485Loop>() {
        public Wifi485Loop createFromParcel(Parcel source) {
            return new Wifi485Loop(source);
        }

        public Wifi485Loop[] newArray(int size) {
            return new Wifi485Loop[size];
        }
    };
}
