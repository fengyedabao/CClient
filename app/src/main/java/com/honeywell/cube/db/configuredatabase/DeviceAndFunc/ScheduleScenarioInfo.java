package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

/**
 * Created by H157925 on 16/4/11. 16:42
 * Email:Shodong.Sun@honeywell.com
 */
public class ScheduleScenarioInfo extends ConditionInfo{
    public ScheduleScenarioInfo() {
        super();
    }

    public ScheduleScenarioInfo(long mId,int primaryId, long mTriggerId, String mActionInfo) {
        super(mId, primaryId,mTriggerId, mActionInfo, -1, -1);
    }
}
