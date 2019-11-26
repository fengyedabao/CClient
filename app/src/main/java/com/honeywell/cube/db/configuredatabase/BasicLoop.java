package com.honeywell.cube.db.configuredatabase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 10:16
 * Email:Shodong.Sun@honeywell.com
 */
public class BasicLoop implements Parcelable {
    public long mLoopSelfPrimaryId = -1;//loop self primaryid
    public long mModulePrimaryId = -1;//perialdevice primaryid
    public int mSubDevId = -1;//只有sparklighting，wireless315M433M用到
    public String mLoopName = "";
    public int mRoomId = 0;
    public int mLoopId = -1;
    public String mIpAddr = "";
    public String mMacAddr = "";
    public int mModuleType = -1;
    public int mLoopType = -1;//str，区分灯光，窗帘，5804EU/5816EU/5800-PIR-AP

    public boolean isOnline = false;
    public BasicLoop() {
        super();
    }

    //通过Parsel初始化
    public BasicLoop(Parcel parcel) {
        mLoopSelfPrimaryId = parcel.readLong();
        mModulePrimaryId = parcel.readLong();
        mSubDevId = parcel.readInt();
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mLoopId = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mModuleType = parcel.readInt();
        mLoopType = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add("_id");
        returnValue.add("" + mLoopSelfPrimaryId);
        returnValue.add("dev_id");
        returnValue.add("" + mModulePrimaryId);
        returnValue.add("sub_dev_id");
        returnValue.add("" + mSubDevId);
        returnValue.add("module_type");
        returnValue.add("" + mModuleType);
        returnValue.add("loop_id");
        returnValue.add("" + mLoopId);
        returnValue.add("loop_name");
        returnValue.add("" + mLoopName);
        returnValue.add("loop_type");
        returnValue.add("" + mLoopType);
        returnValue.add("room_name");
        returnValue.add("" + mRoomId);
        returnValue.add("ip");
        returnValue.add("" + mIpAddr);
        returnValue.add("mac");
        returnValue.add("" + mMacAddr);
        return returnValue;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeLong(mModulePrimaryId);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mLoopId);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeInt(mModuleType);
        parcel.writeInt(mLoopType);
    }

    public static final Parcelable.Creator<BasicLoop> CREATOR = new Creator<BasicLoop>() {
        public BasicLoop createFromParcel(Parcel source) {
            return new BasicLoop(source);
        }

        public BasicLoop[] newArray(int size) {
            return new BasicLoop[size];
        }

    };
}
