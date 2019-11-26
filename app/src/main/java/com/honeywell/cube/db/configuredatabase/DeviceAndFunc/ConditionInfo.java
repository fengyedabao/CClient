package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

/**
 * Created by H157925 on 16/4/11. 10:45
 * Email:Shodong.Sun@honeywell.com
 */
public class ConditionInfo {
    public long mId = -1;
    public int mPrimaryId = -1;
    public long mTriggerOrRuleId = -1;
    public String mActionInfo = "";
    public long mLoopPrimaryId = -1;
    public int mModuleType = -1;

    public ConditionInfo() {
    }
    public ConditionInfo(long mId, int primaryId,long mTriggerId, String mActionInfo,
                         long mLoopPrimaryId, int mModuleType) {
        super();
        this.mId = mId;
        this.mPrimaryId = primaryId;
        this.mTriggerOrRuleId = mTriggerId;
        this.mActionInfo = mActionInfo;
        this.mLoopPrimaryId = mLoopPrimaryId;
        this.mModuleType = mModuleType;
    }
    public String toString() {
        return "ConditionInfo [mPrimaryId=" + mPrimaryId + ", mTriggerId=" + mTriggerOrRuleId
                + ", mActionInfo=" + mActionInfo + ", mLoopPrimaryId="
                + mLoopPrimaryId + ", mModuleType=" + mModuleType + "]";
    }
}
