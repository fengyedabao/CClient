package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/4/11. 11:29
 * Email:Shodong.Sun@honeywell.com
 */
public class IrCode implements Parcelable {
    public long mId = -1;
    public long mLoopId = -1;
    public String mName = "";
    public String mImageName = "";
    public String mData1 = "";
    public String mData2 = "";


    public IrCode() {
        super();
    }

    public IrCode(long mLoopId, String mName, String mImageName, String mData1,
                  String mData2) {
        super();
        this.mLoopId = mLoopId;
        this.mName = mName;
        this.mImageName = mImageName;
        this.mData1 = mData1;
        this.mData2 = mData2;
    }

    public IrCode(Parcel parcel) {
        mId = parcel.readLong();
        mLoopId = parcel.readLong();
        mName = parcel.readString();
        mImageName = parcel.readString();
        mData1 = parcel.readString();
        mData2 = parcel.readString();

    }

    @Override
    public String toString() {
        return "IrCode [mLoopId=" + mLoopId + ", mName=" + mName
                + ", mImageName=" + mImageName + ", mData1=" + mData1
                + ", mData2=" + mData2 + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeLong(mLoopId);
        parcel.writeString(mName);
        parcel.writeString(mImageName);
        parcel.writeString(mData1);
        parcel.writeString(mData2);
    }

    public static Parcelable.Creator<IrCode> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<IrCode> CREATOR = new Creator<IrCode>() {
        public IrCode createFromParcel(Parcel source) {
            return new IrCode(source);
        }

        public IrCode[] newArray(int size) {
            return new IrCode[size];
        }
    };
}
