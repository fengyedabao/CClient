package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.WiredZoneStatus;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 11:21
 * Email:Shodong.Sun@honeywell.com
 */
public class IpvdpZoneLoop extends BasicLoop implements Parcelable {
    public String mZoneType = "";
    public String mAlarmType = "";
    public int mDelayTimer = -1;
    public int mIsEnable = CommonData.ARM_TYPE_ENABLE;

    //custom 纪录当前的状态 这个参数不会传入数据库
    public WiredZoneStatus customZoneStatus = new WiredZoneStatus();

    public IpvdpZoneLoop() {
    }

    public IpvdpZoneLoop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mZoneType = parcel.readString();
        mAlarmType = parcel.readString();
        mDelayTimer = parcel.readInt();
        mIsEnable = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    public IpvdpZoneLoop(String mZoneType, String mAlarmType, int mDelayTimer,
                         int mIsEnable) {
        super();
        this.mZoneType = mZoneType;
        this.mAlarmType = mAlarmType;
        this.mDelayTimer = mDelayTimer;
        this.mIsEnable = mIsEnable;
    }

    public String toString() {
        return "Wire dZoneLoop [mZoneType=" + mZoneType + ", mAlarmType="
                + mAlarmType + ", mDelayTimer=" + mDelayTimer + ", mIsEnable="
                + mIsEnable + ", mLoopSelfPrimaryId=" + mLoopSelfPrimaryId
                + ", mModulePrimaryId=" + mModulePrimaryId + ", mSubDevId="
                + mSubDevId + ", mLoopName=" + mLoopName + ", mRoomId="
                + mRoomId + ", mLoopId=" + mLoopId + ", mIpAddr=" + mIpAddr
                + ", mMacAddr=" + mMacAddr + ", mModuleType=" + mModuleType
                + ", mLoopType=" + mLoopType + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("zone_type");
        returnValue.add("" + mZoneType);
        returnValue.add("alarm_timer");
        returnValue.add("" + mDelayTimer);
        returnValue.add("alarm_type");
        returnValue.add("" + mAlarmType);
        returnValue.add("is_enable");
        returnValue.add("" + mIsEnable);
        return returnValue;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mModulePrimaryId);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mLoopId);
        parcel.writeString(mZoneType);
        parcel.writeString(mAlarmType);
        parcel.writeInt(mDelayTimer);
        parcel.writeInt(mIsEnable);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static Parcelable.Creator<IpvdpZoneLoop> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<IpvdpZoneLoop> CREATOR = new Creator<IpvdpZoneLoop>() {
        public IpvdpZoneLoop createFromParcel(Parcel source) {
            return new IpvdpZoneLoop(source);
        }

        public IpvdpZoneLoop[] newArray(int size) {
            return new IpvdpZoneLoop[size];
        }
    };

}
