package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

/**
 * Created by H157925 on 16/4/11. 16:50
 * Email:Shodong.Sun@honeywell.com
 */
public class TriggerDeviceInfo extends ConditionInfo{
    public TriggerDeviceInfo() {
        super();
    }

    public TriggerDeviceInfo(long mId,int primaryId, long mTriggerId, String mActionInfo,
                             long mLoopPrimaryId, int mModuleType) {
        super(mId, primaryId, mTriggerId, mActionInfo, mLoopPrimaryId, mModuleType);
    }
}
