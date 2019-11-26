package com.honeywell.cube.db.configuredatabase;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ConditionFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ConditionInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.MutexDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.MutexDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceInfo;
import com.honeywell.cube.common.CommonData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/8. 16:48
 * Email:Shodong.Sun@honeywell.com
 */
public class Util {

    public static void addLoopToDefaultScenario(ConfigCubeDatabaseHelper dbHelper,BasicLoop basicLoop,boolean isZone) {
        if(null == dbHelper || null == basicLoop){
            return;
        }
        ScenarioLoopFunc fuc = new ScenarioLoopFunc(dbHelper);
        ScenarioLoop loop = null;
        List<ScenarioLoop> loops = new ArrayList<ScenarioLoop>();
//		for(int scenaridId = CommonData.SCENARIO_ID_HOME;scenaridId <= CommonData.SCENARIO_ID_DISARMALL;scenaridId++){
        for(int scenaridId = CommonData.SCENARIO_ID_ARMALL;scenaridId <= CommonData.SCENARIO_ID_DISARMALL;scenaridId++){
            //非防区不加到armall 和disarmall 模式
            if(!isZone && (scenaridId == CommonData.SCENARIO_ID_ARMALL||scenaridId == CommonData.SCENARIO_ID_DISARMALL)){
                continue;
            }
            loop = fillDefaultLoop(basicLoop);
            if(scenaridId == CommonData.SCENARIO_ID_ARMALL){
                loop.mIsArm = CommonData.ARM_TYPE_ENABLE;
            }
            if(null != loop){
                loop.mScenarioId = scenaridId;
                loop.mScenarioName = getScenarioNameByScenarioId(scenaridId);
                loops.add(loop);
            }
        }
        if(loop != null && !loops.isEmpty()){
            fuc.addScenarioLoopList(loops,false);
        }
    }

    public static void deleteLoopFromScenarios(ConfigCubeDatabaseHelper dbHelper,long loopPrimaryId,int moduleType) {
        ScenarioLoopFunc scenarioLoopFuc = new ScenarioLoopFunc(dbHelper);
        int num = scenarioLoopFuc.deleteScenarioLoop(loopPrimaryId, moduleType);
        ConditionFunc conditionFuc = new ConditionFunc(dbHelper);
        List<ConditionInfo> conditionInfos = conditionFuc.getTriggerConditionInfoAllList();
        if(null != conditionInfos){
            for (int i = 0; i < conditionInfos.size(); i++) {
                ConditionInfo info = conditionInfos.get(i);
                if(loopPrimaryId == info.mLoopPrimaryId && moduleType == info.mModuleType){
                    conditionFuc.deleteConditionInfoLoop(info.mTriggerOrRuleId, loopPrimaryId, moduleType);
                }
            }
        }

        TriggerDeviceFunc triggerDeviceFuc = new TriggerDeviceFunc(dbHelper);
        List<TriggerDeviceInfo> triggerDeviceInfos = triggerDeviceFuc.getTriggerDeviceControlInfoAllList();
        if(null != triggerDeviceInfos){
            for (int i = 0; i < triggerDeviceInfos.size(); i++) {
                TriggerDeviceInfo info = triggerDeviceInfos.get(i);
                if(loopPrimaryId == info.mLoopPrimaryId && moduleType == info.mModuleType){
                    triggerDeviceFuc.deleteTriggerDeviceControlInfo(info.mTriggerOrRuleId, loopPrimaryId, moduleType);
                }
            }
        }

        MutexDeviceFunc mutexDeviceFuc = new MutexDeviceFunc(dbHelper);
        List<MutexDeviceInfo> mutexDeviceInfos = mutexDeviceFuc.getMutexDeviceInfoAllList();
        if(null != mutexDeviceInfos){
            for (int i = 0; i < mutexDeviceInfos.size(); i++) {
                MutexDeviceInfo info = mutexDeviceInfos.get(i);
                if(loopPrimaryId == info.mDeviceLoopPrimaryId && moduleType == info.mModuleType){
                    mutexDeviceFuc.deleteMutexDeviceInfo(info.mMutexId, loopPrimaryId, moduleType);
                }
            }
        }

        ScheduleDeviceFunc scheduleDeviceFuc = new ScheduleDeviceFunc(dbHelper);
        List<ScheduleDeviceInfo> scheduleDeviceInfos = scheduleDeviceFuc.getScheduleRuleDeviceControlInfoAllList();
        if(null != scheduleDeviceInfos){
            for (int i = 0; i < scheduleDeviceInfos.size(); i++) {
                ScheduleDeviceInfo info = scheduleDeviceInfos.get(i);
                if(loopPrimaryId == info.mLoopPrimaryId && moduleType == info.mModuleType){
                    scheduleDeviceFuc.deleteScheduleRuleDeviceControl(info.mTriggerOrRuleId, loopPrimaryId, moduleType);
                }
            }
        }
    }

    //返回默认的场景名称，其他的自定义场景 均不返回
    public static String getScenarioNameByScenarioId(int scenarioId){
        String name = null;
        if(scenarioId < CommonData.SCENARIO_ID_HOME || scenarioId > CommonData.SCENARIO_ID_DISARMALL){
            return name;
        }
        switch (scenarioId) {
            case CommonData.SCENARIO_ID_HOME:
                name = CommonData.SCENARIO_HOME_NAME;
                break;
            case CommonData.SCENARIO_ID_LEAVE:
                name = CommonData.SCENARIO_LEAVE_NAME;
                break;
            case CommonData.SCENARIO_ID_ARMALL:
                name = CommonData.SCENARIO_ARMALL_NAME;
                break;
            case CommonData.SCENARIO_ID_DISARMALL:
                name = CommonData.SCENARIO_DISARMALL_NAME;
                break;
            default:
                break;
        }
        return name;
    }

    public static void putPrimaryIdAndModuleType(BasicLoop loop,long loopPrimaryId, long modulePrimaryId,int moduleType) {
        if(null != loop){
            loop.mLoopSelfPrimaryId = loopPrimaryId;
            loop.mModulePrimaryId = modulePrimaryId;
            loop.mModuleType = moduleType;
        }
    }

    public static JSONObject generalJsonObject(String moduleType,String configType) throws JSONException {
        JSONObject object = new JSONObject();
        object.put(CommonData.JSON_COMMAND_ACTION,CommonData.JSON_COMMAND_ACTION_EVENT);
        object.put(CommonData.JSON_COMMAND_SUBACTION, configType);
        return object;
    }

    private static ScenarioLoop fillDefaultLoop(BasicLoop basicLoop) {
        if(null == basicLoop){
            return null;
        }
        ScenarioLoop loop = new ScenarioLoop();
        loop.mDeviceLoopPrimaryId = basicLoop.mLoopSelfPrimaryId;
        loop.mActionInfo = "";
        loop.mIsArm = CommonData.ARM_TYPE_DISABLE;
        loop.mModuleType = basicLoop.mModuleType;
        return loop;
    }

}
