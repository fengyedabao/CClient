package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.DeviceStatus.RoomCustom;

/**
 * Created by H157925 on 16/4/11. 15:48
 * Email:Shodong.Sun@honeywell.com
 */
public class RoomLoop implements Parcelable {
    public long mId = -1;
    public int mPrimaryId = -1;
    public String mRoomName = "";
    public String mImageName = "";

    public RoomCustom customModel = new RoomCustom();

    public RoomLoop(Parcel parcel) {
        mId = parcel.readLong();
        mPrimaryId = parcel.readInt();
        mRoomName = parcel.readString();
        mImageName = parcel.readString();
        customModel = parcel.readParcelable(RoomCustom.class.getClassLoader());
    }

    public RoomLoop() {
    }

    public RoomLoop(String mRoomName, String mImageName) {
        super();
        this.mRoomName = mRoomName;
        this.mImageName = mImageName;
    }

    @Override
    public String toString() {
        return "RoomLoop [mPrimaryId=" + mPrimaryId + ", mRoomName=" + mRoomName
                + ", mImageName=" + mImageName + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeInt(mPrimaryId);
        parcel.writeString(mRoomName);
        parcel.writeString(mImageName);
        parcel.writeParcelable(customModel, flags);
    }

    public static Parcelable.Creator<RoomLoop> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<RoomLoop> CREATOR = new Creator<RoomLoop>() {
        public RoomLoop createFromParcel(Parcel source) {
            return new RoomLoop(source);
        }

        public RoomLoop[] newArray(int size) {
            return new RoomLoop[size];
        }
    };
}
