package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

/**
 * Created by H157925 on 16/4/12. 13:23
 * Email:Shodong.Sun@honeywell.com
 */
public class TriggerScenarioInfo extends ConditionInfo{

    public TriggerScenarioInfo() {
        super();
    }

    public TriggerScenarioInfo(long mId,int primaryId, long mTriggerId, String mActionInfo) {
        super(mId, primaryId,mTriggerId, mActionInfo, -1, -1);
    }
}
