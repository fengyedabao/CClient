package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BackAudioCustom;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 10:15
 * Email:Shodong.Sun@honeywell.com
 */
public class BackaudioLoop extends BasicLoop implements Parcelable {
    String mSerialNumber;

    //custom
    public BackAudioCustom customModel = new BackAudioCustom();


    public BackaudioLoop() {
    }

    public BackaudioLoop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mSubDevId = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mSerialNumber = parcel.readString();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        isOnline = parcel.readInt() == 1 ? true : false;
        customModel = parcel.readParcelable(BackAudioCustom.class.getClassLoader());
    }

    public BackaudioLoop(long mDevId, String mLoopName,
                         int mRoomid, int mLoopId,
                         String mSerialNumber) {
        super();
        this.mModulePrimaryId = mDevId;
        this.mLoopName = mLoopName;
        this.mRoomId = mRoomid;
        this.mLoopId = mLoopId;
        this.mSerialNumber = mSerialNumber;
    }

    public static Parcelable.Creator<BackaudioLoop> getCreator() {
        return CREATOR;
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("serialnumber");
        returnValue.add("" + mSerialNumber);
        return returnValue;
    }

    @Override
    public String toString() {
        return "BackaudioLoop [mDevId=" + mModulePrimaryId
                + ", mLoopName=" + mLoopName + ", mRoomid=" + mRoomId
                + ", mLoopId=" + mLoopId
                + ", mSubGatewayId=" + mSubDevId + ", mIpAddr=" + mIpAddr
                + ", mSerialNumber=" + mSerialNumber + "]";
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
        parcel.writeString(mSerialNumber);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeInt(isOnline ? 1 : 0);
        parcel.writeParcelable(customModel, flags);
    }

    public static final Parcelable.Creator<BackaudioLoop> CREATOR = new Creator<BackaudioLoop>() {
        public BackaudioLoop createFromParcel(Parcel source) {
            return new BackaudioLoop(source);
        }

        public BackaudioLoop[] newArray(int size) {
            return new BackaudioLoop[size];
        }
    };
}
