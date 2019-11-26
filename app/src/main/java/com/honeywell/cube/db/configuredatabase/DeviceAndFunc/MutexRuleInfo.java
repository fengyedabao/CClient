package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 14:40
 * Email:Shodong.Sun@honeywell.com
 */
public class MutexRuleInfo {
    public long mId = -1;
    public String mSwitchStatus = CommonData.SWITCHSTATUS_TYPE_OFF;
    public String mName = "";
    public String mDescription = "";

    public MutexRuleInfo() {
    }

    public MutexRuleInfo(long mId, String mSwitchStatus,String mName,
                         String mDescription) {
        super();
        this.mId = mId;
        this.mSwitchStatus = mSwitchStatus;
        this.mName = mName;
        this.mDescription = mDescription;
    }

    public String toString() {
        return "MutexRuleInfo [mId=" + mId + ", mSwitchStatus=" + mSwitchStatus
                + ", mName=" + mName + ", mDescription=" + mDescription + "]";
    }
}
