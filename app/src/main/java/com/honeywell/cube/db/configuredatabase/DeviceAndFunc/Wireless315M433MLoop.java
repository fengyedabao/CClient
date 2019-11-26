package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.Wireless315M433MStatus;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/12. 13:42
 * Email:Shodong.Sun@honeywell.com
 */
public class Wireless315M433MLoop extends BasicLoop implements Parcelable {
    public String mZoneType = "";  //24h，instance ,delay
    public String mAlarmType = "";  //fire,help
    public int mDelayTimer = -1;
    public int mIsEnable = CommonData.ARM_TYPE_ENABLE;
    public String mSerialnumber = "";//433 sensor 每个都有个serial number，意义上不大，但是解析需要
    public String mDeviceType = "";//区分是maia，还是sensor

    //custom
    public Wireless315M433MStatus customStatus = new Wireless315M433MStatus();

    public Wireless315M433MLoop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mZoneType = parcel.readString();
        mAlarmType = parcel.readString();
        mDelayTimer = parcel.readInt();
        mIsEnable = parcel.readInt();
        mSerialnumber = parcel.readString();
        mSubDevId = parcel.readInt();
        mDeviceType = parcel.readString();
        mLoopType = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    public Wireless315M433MLoop() {
    }

    public static Parcelable.Creator<Wireless315M433MLoop> getCreator() {
        return CREATOR;
    }

    public String toString() {
        return "Wireless315M433MLoop [mZoneType=" + mZoneType + ", mAlarmType="
                + mAlarmType + ", mDelayTimer=" + mDelayTimer + ", mIsEnable="
                + mIsEnable + ", mSerialnumber=" + mSerialnumber
                + ", mDeviceType=" + mDeviceType + ", mLoopSelfPrimaryId="
                + mLoopSelfPrimaryId + ", mModulePrimaryId=" + mModulePrimaryId
                + ", mSubDevId=" + mSubDevId + ", mLoopName=" + mLoopName
                + ", mRoomId=" + mRoomId + ", mLoopId=" + mLoopId
                + ", mIpAddr=" + mIpAddr + ", mMacAddr=" + mMacAddr
                + ", mModuleType=" + mModuleType + ", mLoopType=" + mLoopType
                + "]";
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
        returnValue.add("serialnumber");
        returnValue.add("" + mSerialnumber);
        returnValue.add("device_type");
        returnValue.add("" + mDeviceType);
        return returnValue;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mModulePrimaryId);
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mLoopId);
        parcel.writeString(mZoneType);
        parcel.writeString(mAlarmType);
        parcel.writeInt(mDelayTimer);
        parcel.writeInt(mIsEnable);
        parcel.writeString(mSerialnumber);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mDeviceType);
        parcel.writeInt(mLoopType);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static final Parcelable.Creator<Wireless315M433MLoop> CREATOR = new Creator<Wireless315M433MLoop>() {
        public Wireless315M433MLoop createFromParcel(Parcel source) {
            return new Wireless315M433MLoop(source);
        }

        public Wireless315M433MLoop[] newArray(int size) {
            return new Wireless315M433MLoop[size];
        }
    };
}
