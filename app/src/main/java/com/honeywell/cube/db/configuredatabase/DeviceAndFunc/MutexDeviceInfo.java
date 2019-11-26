package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/4/11. 14:36
 * Email:Shodong.Sun@honeywell.com
 */
public class MutexDeviceInfo implements Parcelable{
    public long mId = -1;
    public long mMutexId = -1;
    public long mDeviceLoopPrimaryId = -1;
    public int mModuleType = -1;

    public MutexDeviceInfo(Parcel parcel) {
        mId = parcel.readLong();
        mMutexId = parcel.readLong();
        mDeviceLoopPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
    }
    public MutexDeviceInfo() {
    }

    public MutexDeviceInfo(long mId, long mMutexId, long mDeviceLoopPrimaryId,
                           int mModuleType) {
        super();
        this.mId = mId;
        this.mMutexId = mMutexId;
        this.mDeviceLoopPrimaryId = mDeviceLoopPrimaryId;
        this.mModuleType = mModuleType;
    }
    public static Parcelable.Creator<MutexDeviceInfo> getCreator() {
        return CREATOR;
    }
    public String toString() {
        return "MutexDeviceInfo [mId=" + mId + ", mMutexId=" + mMutexId
                + ", mDeviceLoopPrimaryId=" + mDeviceLoopPrimaryId
                + ", mModuleType=" + mModuleType + "]";
    }
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeLong(mMutexId);
        parcel.writeLong(mDeviceLoopPrimaryId);
        parcel.writeInt(mModuleType);
    }

    public static final Parcelable.Creator<MutexDeviceInfo> CREATOR = new Parcelable.Creator<MutexDeviceInfo>() {
        public MutexDeviceInfo createFromParcel(Parcel source) {
            return new MutexDeviceInfo(source);
        }
        public MutexDeviceInfo[] newArray(int size) {
            return new MutexDeviceInfo[size];
        }
    };
}
