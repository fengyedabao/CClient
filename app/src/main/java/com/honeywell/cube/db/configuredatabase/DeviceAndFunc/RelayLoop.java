package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.RelayStatus;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 15:14
 * Email:Shodong.Sun@honeywell.com
 */
public class RelayLoop extends BasicLoop implements Parcelable {
    public int mTriggerTime = 0;
    public RelayStatus customStatus = new RelayStatus();

    public RelayLoop() {
    }

    public RelayLoop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mTriggerTime = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        customStatus = parcel.readParcelable(RelayStatus.class.getClassLoader());
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    public RelayLoop(long selfPrimaryId, int mDevId, int mRelayId, String mLoopName,
                     int mRoomId, int mTriggerTime, String mIpAddr, String mMacAddr) {

        this.mModulePrimaryId = mDevId;
        this.mLoopId = mRelayId;
        this.mLoopName = mLoopName;
        this.mRoomId = mRoomId;
        this.mTriggerTime = mTriggerTime;
        this.mIpAddr = mIpAddr;
        this.mMacAddr = mMacAddr;
        this.mLoopSelfPrimaryId = selfPrimaryId;
    }


    @Override
    public String toString() {
        return "RelayLoop [self primaryId = " + mLoopSelfPrimaryId + "mDevId=" + mModulePrimaryId + ", mRelayId=" + mLoopId
                + ", mLoopName=" + mLoopName + ", mRoomId=" + mRoomId
                + ", mTriggerTime=" + mTriggerTime + ", mIpAddr=" + mIpAddr
                + ", mMacAddr=" + mMacAddr + "]";
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("trigger_time");
        returnValue.add("" + mTriggerTime);
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
        parcel.writeInt(mLoopId);
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mTriggerTime);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeParcelable(customStatus, flags);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static Parcelable.Creator<RelayLoop> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<RelayLoop> CREATOR = new Creator<RelayLoop>() {
        public RelayLoop createFromParcel(Parcel source) {
            return new RelayLoop(source);
        }

        public RelayLoop[] newArray(int size) {
            return new RelayLoop[size];
        }

    };
}
