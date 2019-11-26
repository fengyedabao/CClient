package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

/**
 * Created by H157925 on 16/4/11. 16:36
 * Email:Shodong.Sun@honeywell.com
 */
public class ScheduleDeviceInfo extends ConditionInfo {
    public ScheduleDeviceInfo() {
        super();
    }

    public ScheduleDeviceInfo(long mId, int primaryId, long mScheduleId, String mActionInfo,
                              long mLoopPrimaryId, int mModuleType) {
        super(mId, primaryId,mScheduleId, mActionInfo, mLoopPrimaryId, mModuleType);
    }
}
