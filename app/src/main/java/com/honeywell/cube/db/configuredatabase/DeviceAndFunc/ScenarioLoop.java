package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 16:03
 * Email:Shodong.Sun@honeywell.com
 */
public class ScenarioLoop implements Parcelable {
    public long mScenarioLoopPrimaryId = -1;
    public int mScenarioId = -1;
    public String mScenarioName = "";
    public long mDeviceLoopPrimaryId = -1;
    public String mActionInfo = "";
    public int mIsArm = CommonData.ARM_TYPE_DISABLE;
    public int mModuleType = -1;
    public String mImageName = "";

    //custom
    public int mClickedCount = 0;

    public ScenarioLoop(Parcel parcel) {
        mScenarioLoopPrimaryId = parcel.readLong();
        mScenarioId = parcel.readInt();
        mScenarioName = parcel.readString();
        mDeviceLoopPrimaryId = parcel.readLong();
        mActionInfo = parcel.readString();
        mIsArm = parcel.readInt();
        mModuleType = parcel.readInt();
        mImageName = parcel.readString();
        mClickedCount = parcel.readInt();
    }

    public ScenarioLoop() {

    }

    public ScenarioLoop(int mScenarioId, String mScenarioName, String mImageName) {
        super();
        this.mScenarioId = mScenarioId;
        this.mScenarioName = mScenarioName;
        this.mImageName = mImageName;
    }

    public static Parcelable.Creator<ScenarioLoop> getCreator() {
        return CREATOR;
    }

    public ScenarioLoop(int mPrimaryId, int mScenarioId, String mScenarioName,
                        long mDevId, String mActionInfo, int mIsArm, int mModuleType,
                        String mImageName) {
        super();
        this.mScenarioLoopPrimaryId = mPrimaryId;
        this.mScenarioId = mScenarioId;
        this.mScenarioName = mScenarioName;
        this.mDeviceLoopPrimaryId = mDevId;
        this.mActionInfo = mActionInfo;
        this.mIsArm = mIsArm;
        this.mModuleType = mModuleType;
        this.mImageName = mImageName;
    }

    public String toString() {
        return "ScenarioLoop [mPrimaryId=" + mScenarioLoopPrimaryId + ", mScenarioId="
                + mScenarioId + ", mScenarioName=" + mScenarioName
                + ", mDevId=" + mDeviceLoopPrimaryId + ", mActionInfo=" + mActionInfo
                + ", mIsArm=" + mIsArm + ", mModuleType=" + mModuleType
                + ", mImageName=" + mImageName
                + ", mClickedCount=" + mClickedCount
                + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mScenarioLoopPrimaryId);
        parcel.writeInt(mScenarioId);
        parcel.writeString(mScenarioName);
        parcel.writeLong(mDeviceLoopPrimaryId);
        parcel.writeString(mActionInfo);
        parcel.writeInt(mIsArm);
        parcel.writeInt(mModuleType);
        parcel.writeString(mImageName);
        parcel.writeInt(mClickedCount);
    }

    public static final Parcelable.Creator<ScenarioLoop> CREATOR = new Creator<ScenarioLoop>() {
        public ScenarioLoop createFromParcel(Parcel source) {
            return new ScenarioLoop(source);
        }

        public ScenarioLoop[] newArray(int size) {
            return new ScenarioLoop[size];
        }
    };
}
