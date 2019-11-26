package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.SparkLightingStauts;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/11. 16:45
 * Email:Shodong.Sun@honeywell.com
 */

/*
 sub_dev_id: start from 1

 HBLS-D0206             2 路6A 调光模块
 HBLS-D0403             4 路3A 调光模块
 HBLS-D0602             6 路2A 调光模块
 HBLS-D0610-DC          6 路10A DC 0~10V 调光器模块
 HBLS-D0605-LED         6 路5A LED 调光模块
 HBLS-C02               2 路智能窗帘控制器
 HBLS-R0410             4 路10A 智能继电器
 HBLS-R0810             8 路10A 智能继电器
 HBLS-R1210             12 路10A 智能继电器
 HBLS-I04-LED           4 通道干接点传感器（带4 路LED 输出)
 HBLS-I08               8 通道干接点传感器
 HBLS-SIR               吸顶红外传感器
 HBLS-STLA              亮度、空气质量传感器组合
 HBLS-GW                总线控制模块
 HBLS-P2400             24V/2.4A 系统电源
 HBLS-Software          系统编程软件
 */


public class SparkLightingLoop extends BasicLoop implements Parcelable {
    public String mSubDevType;
    public int mIsEnable = CommonData.ARM_TYPE_ENABLE;
    //Custom
    public SparkLightingStauts customStatus = new SparkLightingStauts();

    public SparkLightingLoop() {
    }

    public SparkLightingLoop(Parcel parcel) {
        mLoopName = parcel.readString();
        mRoomId = parcel.readInt();
        mSubDevId = parcel.readInt();
        mSubDevType = parcel.readString();
        mLoopType = parcel.readInt();
        mLoopId = parcel.readInt();
        mIsEnable = parcel.readInt();
        mIpAddr = parcel.readString();
        mMacAddr = parcel.readString();
        mModulePrimaryId = parcel.readLong();
        mLoopSelfPrimaryId = parcel.readLong();
        mModuleType = parcel.readInt();

        customStatus = parcel.readParcelable(SparkLightingStauts.class.getClassLoader());
        isOnline = parcel.readInt() == 1 ? true : false;
    }

    public static Parcelable.Creator<SparkLightingLoop> getCreator() {
        return CREATOR;
    }

    public String toString() {
        return "SparkLightingLoop [mSubDevType=" + mSubDevType + ", mLoopType="
                + mLoopType + ", mIsEnable=" + mIsEnable + ", mPrimaryId="
                + mLoopSelfPrimaryId + ", mDevId=" + mModulePrimaryId + ", mSubDevId="
                + mSubDevId + ", mLoopName=" + mLoopName + ", mRoomId="
                + mRoomId + ", mLoopId=" + mLoopId + ", mIpAddr=" + mIpAddr
                + ", mMacAddr=" + mMacAddr + "]";
    }

    /**
     * 获取更多信息
     *
     * @return
     */
    public ArrayList<String> getDetailInfo() {
        ArrayList returnValue = super.getDetailInfo();
        returnValue.add("sub_dev_type");
        returnValue.add("" + mSubDevType);
        returnValue.add("is_enable");
        returnValue.add("" + mIsEnable);
        return returnValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mLoopName);
        parcel.writeInt(mRoomId);
        parcel.writeInt(mSubDevId);
        parcel.writeString(mSubDevType);
        parcel.writeInt(mLoopType);
        parcel.writeInt(mLoopId);
        parcel.writeInt(mIsEnable);
        parcel.writeString(mIpAddr);
        parcel.writeString(mMacAddr);
        parcel.writeLong(mModulePrimaryId);
        parcel.writeLong(mLoopSelfPrimaryId);
        parcel.writeInt(mModuleType);
        parcel.writeParcelable(customStatus, flags);
        parcel.writeInt(isOnline ? 1 : 0);
    }

    public static final Parcelable.Creator<SparkLightingLoop> CREATOR = new Creator<SparkLightingLoop>() {
        public SparkLightingLoop createFromParcel(Parcel source) {
            return new SparkLightingLoop(source);
        }

        public SparkLightingLoop[] newArray(int size) {
            return new SparkLightingLoop[size];
        }

    };
}
