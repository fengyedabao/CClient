package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.WiredZoneStatus;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/12. 13:38
 * Email:Shodong.Sun@honeywell.com
 */
public class WiredZoneLoop extends BasicLoop implements Parcelable {
    public String mZoneType = "";
    public String mAlarmType = "";
    public int mDelayTimer = -1;
    public int mIsEnable = CommonData.ARM_TYPE_ENABLE;

    //custom 纪录当前的状态 这个参数不会传入数据库
    public WiredZoneStatus customZoneStatus = new WiredZoneStatus();

    public WiredZoneLoop() {
    }

    public WiredZoneLoop(Parcel parcel) {
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


    public WiredZoneLoop(String mZoneType, String mAlarmType, int mDelayTimer,
                         int mIsEnable) {
        super();
        this.mZoneType = mZoneType;
        this.mAlarmType = mAlarmType;
        this.mDelayTimer = mDelayTimer;
        this.mIsEnable = mIsEnable;
    }

    public static Parcelable.Creator<WiredZoneLoop> getCreator() {
        return CREATOR;
    }

    public String toString() {
        return "WiredZoneLoop [mZoneType=" + mZoneType + ", mAlarmType="
                + mAlarmType + ", mDelayTimer=" + mDelayTimer + ", mIsEnable="
                + mIsEnable + ", mLoopSelfPrimaryId=" + mLoopSelfPrimaryId
                + ", mModulePrimaryId=" + mModulePrimaryId + ", mSubDevId="
                + mSubDevId + ", mLoopName=" + mLoopName + ", mRoomId="
                + mRoomId + ", mLoopId=" + mLoopId + ", mIpAddr=" + mIpAddr
                + ", mMacAddr=" + mMacAddr + ", mModuleType=" + mModuleType
                + ", mLoopType=" + mLoopType + "]";
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("zone_type");
        returnValue.add("" + mLoopType);
        returnValue.add("alarm_type");
        returnValue.add("" + mAlarmType);
        returnValue.add("alarm_time");
        returnValue.add("" + mDelayTimer);
        returnValue.add("is_enable");
        returnValue.add("" + mIsEnable);
        return returnValue;
    }

    public int describeContents() {
        return 0;
    }

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

    public static final Parcelable.Creator<WiredZoneLoop> CREATOR = new Parcelable.Creator<WiredZoneLoop>() {
        public WiredZoneLoop createFromParcel(Parcel source) {
            return new WiredZoneLoop(source);
        }

        public WiredZoneLoop[] newArray(int size) {
            return new WiredZoneLoop[size];
        }
    };
}
