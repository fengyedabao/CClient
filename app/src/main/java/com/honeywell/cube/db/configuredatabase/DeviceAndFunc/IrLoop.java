package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.IRLoopCustom;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 14:02
 * Email:Shodong.Sun@honeywell.com
 */
public class IrLoop extends BasicLoop implements Parcelable {
    public String mLoopType = "";

    //self custom
    public IRLoopCustom customModel = new IRLoopCustom();

    public IrLoop() {
    }

    public IrLoop(Parcel parcel) {
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mSubDevId = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mLoopType = parcel.readString();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();
        customModel = parcel.readParcelable(IRLoopCustom.class.getClassLoader());
    }

    public IrLoop(long mLoopSelfPrimaryId, long mDevId, String mLoopName,
                  int mRoomid, int mLoopId,
                  int mSubGatewayId, String mIpAddr, String mMacAddr, String mLoopType) {
        super();
        this.mLoopSelfPrimaryId = mLoopSelfPrimaryId;
        this.mModulePrimaryId = mDevId;
        this.mLoopName = mLoopName;
        this.mRoomId = mRoomid;
        this.mLoopId = mLoopId;
        this.mSubDevId = mSubGatewayId;
        this.mIpAddr = mIpAddr;
        this.mMacAddr = mMacAddr;
        this.mLoopType = mLoopType;
    }

    public String toString() {
        return "IrLoop [mLoopSelfPrimaryId=" + mLoopSelfPrimaryId + ", mLoopType=" + mLoopType + ", mModulePrimaryId="
                + mModulePrimaryId + ", mSubDevId=" + mSubDevId + ", mLoopName="
                + mLoopName + ", mRoomId=" + mRoomId + ", mLoopId="
                + mLoopId + ", mIpAddr=" + mIpAddr + ", mMacAddr=" + mMacAddr
                + "]";
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("loop_type");
        returnValue.add("" + mLoopType);
        return returnValue;
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
        parcel.writeString(mLoopType);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeParcelable(customModel, flags);
    }

    public static Parcelable.Creator<IrLoop> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<IrLoop> CREATOR = new Creator<IrLoop>() {
        public IrLoop createFromParcel(Parcel source) {
            return new IrLoop(source);
        }

        public IrLoop[] newArray(int size) {
            return new IrLoop[size];
        }
    };
}
