package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 16:23
 * Email:Shodong.Sun@honeywell.com
 */
public class ScenarioTriggerInfo implements Parcelable {
    public long mId = -1;
    public int mPrimaryId = -1;
    public String mSwitchStatus = CommonData.SWITCHSTATUS_TYPE_OFF;
    public int mDelayTime = 0;
    public String mAvaibleTime = "";//是一个JSON字符串
    public String mType = "";
    public String mName = "";
    public String mDescription = "";

    public ScenarioTriggerInfo(Parcel parcel) {
        mId = parcel.readLong();
        mPrimaryId = parcel.readInt();
        mSwitchStatus = parcel.readString();
        mDelayTime = parcel.readInt();
        mAvaibleTime = parcel.readString();
        mType = parcel.readString();
        mName = parcel.readString();
        mDescription = parcel.readString();
    }
    public ScenarioTriggerInfo() {

    }

    public ScenarioTriggerInfo(long mId, int primaryId, String mSwitchStatus, int mDelayTime,
                               String mAvaibleTime, String mType, String mName,
                               String mDescription) {
        super();
        this.mId = mId;
        this.mPrimaryId = primaryId;
        this.mSwitchStatus = mSwitchStatus;
        this.mDelayTime = mDelayTime;
        this.mAvaibleTime = mAvaibleTime;
        this.mType = mType;
        this.mName = mName;
        this.mDescription = mDescription;
    }
    public static Parcelable.Creator<ScenarioTriggerInfo> getCreator() {
        return CREATOR;
    }

    public String toString() {
        return "ScenarioTriggerInfo [mPrimaryId=" + mPrimaryId + ", mSwitchStatus="
                + mSwitchStatus + ", mDelayTime=" + mDelayTime
                + ", mAvaibleTime=" + mAvaibleTime + ", mType=" + mType
                + ", mName=" + mName + ", mDescription=" + mDescription
                + "]";
    }
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeInt(mPrimaryId);
        parcel.writeString(mSwitchStatus);
        parcel.writeInt(mDelayTime);
        parcel.writeString(mAvaibleTime);
        parcel.writeString(mType);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
    }

    public static final Parcelable.Creator<ScenarioTriggerInfo> CREATOR = new Creator<ScenarioTriggerInfo>() {
        public ScenarioTriggerInfo createFromParcel(Parcel source) {
            return new ScenarioTriggerInfo(source);
        }
        public ScenarioTriggerInfo[] newArray(int size) {
            return new ScenarioTriggerInfo[size];
        }
    };
}
