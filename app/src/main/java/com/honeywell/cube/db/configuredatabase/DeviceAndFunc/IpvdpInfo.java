package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

/**
 * Created by H157925 on 16/4/11. 11:09
 * Email:Shodong.Sun@honeywell.com
 */
public class IpvdpInfo {
    public long mId= -1;
    public long mDevId = -1;
    public int mDeviceId = -1;
    public String mHnsserveraddr = "";

    public IpvdpInfo() {
        super();
    }

    public IpvdpInfo(long mDevId, int mDeviceId,String mHnsserveraddr) {
        super();
        this.mDevId = mDevId;
        this.mDeviceId = mDeviceId;
        this.mHnsserveraddr = mHnsserveraddr;
    }

    @Override
    public String toString() {
        return "IpvdpInfo [mDevId=" + mDevId + ", mDeviceId=" + mDeviceId
                + ", mHnsserveraddr=" + mHnsserveraddr + "]";
    }
}
