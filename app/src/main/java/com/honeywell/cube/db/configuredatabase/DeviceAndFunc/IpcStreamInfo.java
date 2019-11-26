package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.UIItem.IPCameraInfo;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 11:03
 * Email:Shodong.Sun@honeywell.com
 */
public class IpcStreamInfo implements Parcelable {

    public long mId = -1;
    public long mPrimaryId = -1;
    public long mDevId = -1;
    public String mIpcType = "";
    public String mMainStream = "";
    public String mSubStream = "";
    public int mStreamPort = CommonData.DEFAULTIPCSTREAMPORT;
    public String mUser = CommonData.DEFAULTIPCUSER;
    public String mPassword = CommonData.DEFAULTIPCPWD;
    public int mRoomId = 0;
    public boolean isOnline = true;

    // for custom
    public String mIPAddr = "";

    public IpcStreamInfo() {
        super();
    }

    public IpcStreamInfo(long mId, long mDevId, String mIpcType,
                         String mMainStream, String mSubStream, int mStreamPort,
                         String mUser, String mPassword, int mRoomid) {
        super();
        this.mId = mId;
        this.mPrimaryId = mId;
        this.mDevId = mDevId;
        this.mIpcType = mIpcType;
        this.mMainStream = mMainStream;
        this.mSubStream = mSubStream;
        this.mStreamPort = mStreamPort;
        this.mUser = mUser;
        this.mPassword = mPassword;
        this.mRoomId = mRoomid;
    }

    public String toString() {
        return "IpcStreamInfo [mPrimaryId=" + mId + ", mDevId=" + mDevId
                + ", mIpcType=" + mIpcType + ", mMainStream=" + mMainStream
                + ", mSubStream=" + mSubStream + ", mStreamPort=" + mStreamPort
                + ", mUser=" + mUser + ", mPassword=" + mPassword
                + ", mRoomName=" + mRoomId + ", mIpAddr = " + mIPAddr + " ]";
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add("dev_id");
        returnValue.add("" + mDevId);
        returnValue.add("main_stream");
        returnValue.add("" + mMainStream);
        returnValue.add("sub_stream");
        returnValue.add("" + mSubStream);
        returnValue.add("stream_port");
        returnValue.add("" + mStreamPort);
        returnValue.add("room_id");
        returnValue.add("" + mRoomId);
        returnValue.add("password");
        returnValue.add("" + mPassword);
        returnValue.add("_id");
        returnValue.add("" + mPrimaryId);
        returnValue.add("type");
        returnValue.add("" + mIpcType);
        returnValue.add("user");
        returnValue.add("" + mUser);
        return returnValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public IpcStreamInfo(Parcel parcel) {
        mId = parcel.readLong();
        mPrimaryId = parcel.readLong();
        mDevId = parcel.readLong();
        mIpcType = parcel.readString();
        mMainStream = parcel.readString();
        mSubStream = parcel.readString();
        mStreamPort = parcel.readInt();
        mUser = parcel.readString();
        mPassword = parcel.readString();
        mRoomId = parcel.readInt();
        mIPAddr = parcel.readString();
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeLong(mPrimaryId);
        parcel.writeLong(mDevId);

        parcel.writeString(mIpcType);
        parcel.writeString(mMainStream);
        parcel.writeString(mSubStream);

        parcel.writeInt(mStreamPort);
        parcel.writeString(mUser);
        parcel.writeString(mPassword);
        parcel.writeInt(mRoomId);
        parcel.writeString(mIPAddr);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static final Parcelable.Creator<IpcStreamInfo> CREATOR = new Creator<IpcStreamInfo>() {
        public IpcStreamInfo createFromParcel(Parcel source) {
            return new IpcStreamInfo(source);
        }

        public IpcStreamInfo[] newArray(int size) {
            return new IpcStreamInfo[size];
        }
    };
}
