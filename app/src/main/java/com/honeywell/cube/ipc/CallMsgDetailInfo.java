package com.honeywell.cube.ipc;

import android.os.Parcel;
import android.os.Parcelable;

public class CallMsgDetailInfo implements Parcelable {
    public String mCallMsg = Constants.CALL_MSG_INCOMING_CALL;
    public String mCallType = Constants.CALL_TYPE_NEIGHBOUR;
    public int mVideoPort = 11111;
    public String mVideoCodeType = "h.264";
    public int mAudioPort = -1;
    public String mAudioCodeType = "pcma";
    public String mVideoRate = "320*240";
    public String mUuid = "";
    public String mAliasName = "";
    public String mCallSessionId = "";
    public String mCallTypeTitle = "";


    public CallMsgDetailInfo() {
        super();
    }

    public CallMsgDetailInfo(String mCallMsg, String mCallType, int mVideoPort,
                             String mVideoCodeType, int mAudioPort, String mAudioCodeType,
                             String mVideoRate, String mUuid, String mAliasName,
                             String mCallSessionId,String mCallTypeTitle) {
        super();
        this.mCallMsg = mCallMsg;
        this.mCallType = mCallType;
        this.mVideoPort = mVideoPort;
        this.mVideoCodeType = mVideoCodeType;
        this.mAudioPort = mAudioPort;
        this.mAudioCodeType = mAudioCodeType;
        this.mVideoRate = mVideoRate;
        this.mUuid = mUuid;
        this.mAliasName = mAliasName;
        this.mCallSessionId = mCallSessionId;
        this.mCallTypeTitle = mCallTypeTitle;
    }

    public CallMsgDetailInfo(Parcel parcel) {
        mCallMsg = parcel.readString();
        mCallType = parcel.readString();
        mVideoPort = parcel.readInt();
        mVideoCodeType = parcel.readString();
        mAudioPort = parcel.readInt();
        mAudioCodeType = parcel.readString();
        mVideoRate = parcel.readString();
        mUuid = parcel.readString();
        mAliasName = parcel.readString();
        mCallSessionId = parcel.readString();
        mCallTypeTitle = parcel.readString();
    }

    public static Creator<CallMsgDetailInfo> getCreator() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "CallMsgDetailInfo [mCallMsg=" + mCallMsg + ", mCallType="
                + mCallType + ", mVideoPort=" + mVideoPort
                + ", mVideoCodeType=" + mVideoCodeType + ", mAudioPort="
                + mAudioPort + ", mAudioCodeType=" + mAudioCodeType
                + ", mVideoRate=" + mVideoRate + ", mUuid=" + mUuid
                + ", mAliasName=" + mAliasName + ", mCallSessionId="
                + mCallSessionId + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mCallMsg);
        parcel.writeString(mCallType);
        parcel.writeInt(mVideoPort);
        parcel.writeString(mVideoCodeType);
        parcel.writeInt(mAudioPort);
        parcel.writeString(mAudioCodeType);
        parcel.writeString(mVideoRate);
        parcel.writeString(mUuid);
        parcel.writeString(mAliasName);
        parcel.writeString(mCallSessionId);
        parcel.writeString(mCallTypeTitle);
    }

    public static final Creator<CallMsgDetailInfo> CREATOR = new Creator<CallMsgDetailInfo>() {
        public CallMsgDetailInfo createFromParcel(Parcel source) {
            return new CallMsgDetailInfo(source);
        }

        public CallMsgDetailInfo[] newArray(int size) {
            return new CallMsgDetailInfo[size];
        }

    };
} 
