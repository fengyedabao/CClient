package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 16:38
 * Email:Shodong.Sun@honeywell.com
 */
public class ScheduleRuleInfo implements Parcelable{
    public long mId = -1;
    public int mPrimaryId=-1;
    public String mSwitchStatus = CommonData.SWITCHSTATUS_TYPE_OFF;
    public String mAvaibleTime = "";
    public String mName = "";
    public String mDescription = "";
    public ScheduleRuleInfo(Parcel parcel) {
        mId = parcel.readLong();
        mPrimaryId=parcel.readInt();
        mSwitchStatus = parcel.readString();
        mAvaibleTime = parcel.readString();
        mName = parcel.readString();
        mDescription = parcel.readString();
    }
    public ScheduleRuleInfo() {
    }
    public ScheduleRuleInfo(long mId, int primaryId, String mSwitchStatus, String mAvaibleTime,
                            String mName, String mDescription) {
        super();
        this.mId = mId;
        this.mPrimaryId=primaryId;
        this.mSwitchStatus = mSwitchStatus;
        this.mAvaibleTime = mAvaibleTime;
        this.mName = mName;
        this.mDescription = mDescription;
    }
    public static Parcelable.Creator<ScheduleRuleInfo> getCreator() {
        return CREATOR;
    }

    public String toString() {
        return "ScheduleRuleInfo [mPrimaryId=" + mPrimaryId + ", mSwitchStatus="
                + mSwitchStatus + ", mAvaibleTime=" + mAvaibleTime + ", mName="
                + mName + ", mDescription=" + mDescription
                + "]";
    }
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeInt(mPrimaryId);
        parcel.writeString(mSwitchStatus);
        parcel.writeString(mAvaibleTime);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
    }

    public static final Parcelable.Creator<ScheduleRuleInfo> CREATOR = new Parcelable.Creator<ScheduleRuleInfo>() {
        public ScheduleRuleInfo createFromParcel(Parcel source) {
            return new ScheduleRuleInfo(source);
        }
        public ScheduleRuleInfo[] newArray(int size) {
            return new ScheduleRuleInfo[size];
        }
    };
}
