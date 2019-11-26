package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BacnetAirConditioner;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 10:33
 * Email:Shodong.Sun@honeywell.com
 */
public class BacnetLoop extends BasicLoop implements Parcelable {

    //custom
    public BacnetAirConditioner ac_customModel = new BacnetAirConditioner();

    public BacnetLoop() {
        this.mModuleType = CommonData.MODULE_TYPE_BACNET;
    }

    public BacnetLoop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        ac_customModel = parcel.readParcelable(BacnetAirConditioner.class.getClassLoader());
        isOnline = parcel.readInt() == 1 ? true : false;
    }
    public BacnetLoop(int mDevId, String mLoopName,
                      int mRoomId, int mLoopId,
                      int mSubGatewayId, String mIpAddr, String mMacAddr) {
        super();
        this.mModulePrimaryId = mDevId;
        this.mLoopName = mLoopName;
        this.mRoomId = mRoomId;
        this.mLoopId = mLoopId;
        this.mSubDevId = mSubGatewayId;
        this.mIpAddr = mIpAddr;
        this.mMacAddr = mMacAddr;
        this.mModuleType = CommonData.MODULE_TYPE_BACNET;
    }
    @Override
    public String toString() {
        return "BacnetLoop [mDevId=" + mModulePrimaryId
                + ", mLoopName=" + mLoopName + ", mRoomId=" + mRoomId
                + ", mLoopId=" + mLoopId
                + ", mSubGatewayId=" + mSubDevId + ", mIpAddr=" + mIpAddr
                + ", mMacAddr=" + mMacAddr + "]";
    }
    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
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
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeParcelable(ac_customModel, flags);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static final Parcelable.Creator<BacnetLoop> CREATOR = new Creator<BacnetLoop>() {
        public BacnetLoop createFromParcel(Parcel source) {
            return new BacnetLoop(source);
        }

        public BacnetLoop[] newArray(int size) {
            return new BacnetLoop[size];
        }
    };
}
