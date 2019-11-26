package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 09:14
 * Email:Shodong.Sun@honeywell.com
 * 对象序列化
 */
public class BackaudioDevice implements Parcelable {
    public long mId = 0;
    public long mPrimaryID = 0;
    public String mSerialNumber = "";
    public String mName = "";
    public int mMachineType = 0;
    public int mloopNum = 0;
    public int mIsOnline = CommonData.NOTONLINE;

    //赋值初始化
    public BackaudioDevice(long mId, long primaryID, String mSerialNumber, String mName,
                           int mMachineType, int mloopNum, int mIsOnline) {
        this.mId = mId;
        this.mPrimaryID = primaryID;
        this.mSerialNumber = mSerialNumber;
        this.mName = mName;
        this.mMachineType = mMachineType;
        this.mloopNum = mloopNum;
        this.mIsOnline = mIsOnline;
    }

    public BackaudioDevice() {
        super();
    }

    //通过Parsel初始化
    public BackaudioDevice(Parcel parcel) {
        mId = parcel.readLong();
        mPrimaryID = parcel.readLong();
        mSerialNumber = parcel.readString();
        mName = parcel.readString();
        mMachineType = parcel.readInt();
        mloopNum = parcel.readInt();
        mIsOnline = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeLong(mPrimaryID);
        parcel.writeString(mSerialNumber);
        parcel.writeString(mName);
        parcel.writeInt(mMachineType);
        parcel.writeInt(mloopNum);
        parcel.writeInt(mIsOnline);
    }

    public static final Parcelable.Creator<BackaudioDevice> CREATOR = new Creator<BackaudioDevice>() {
        public BackaudioDevice createFromParcel(Parcel source) {
            return new BackaudioDevice(source);
        }

        public BackaudioDevice[] newArray(int size) {
            return new BackaudioDevice[size];
        }

    };
}
