package com.honeywell.cube.controllers.UIItem.menu;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerInfo;

/**
 * Created by H157925 on 16/6/24. 16:41
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * Menu Rule 二级页面 编辑 或者 添加
 */
public class MenuRuleAddDetails {
    //ui
    public String name = ""; //规则名称
    public String action_name = "";//任务
    public int delay_time = 0;//延时时间
    public boolean need_work_time = false;//是否需要工作时间
    public String start_time = "0:00";//开始工作时间 使用时转换格式
    public String end_time = "24:00";//结束时间  使用时转换格式
    public String repeat = "";//重复条件 格式 除去特定格式，其他周几之间用 , 隔开

    //repeat

    //Input
    public String condition_name = "";//条件
    public int condition_type = 0;//用于区分Condition的类型

    //Input room
    public MenuRuleConditionRoom conditionRoom = new MenuRuleConditionRoom();

    //Input sensor
    public String sensor_name = "";
    public Object sensor_object = null;

    //OutPut
    public int action_type = 0;//0 scenario ； 1 device
    public ScenarioLoop action_scenario = null;
    public Object action_device = null;

    //Scenario trigger
    public ScenarioTriggerInfo info = null;//对象

    public MenuRuleAddDetails() {
    }

    @Override
    public String toString() {
        return "menuRuleAddDetails :[ "
                + " name : " + name
                + " condition_name : " + condition_name
                + " action_name : " + action_name
                + " delay_time : " + delay_time
                + " need_work_time : " + need_work_time
                + " start_time : " + start_time
                + " end_time : " + end_time
                + " repeat : " + repeat
                + " condition_type : " + condition_type
                + " conditionRoom : " + (conditionRoom==null?"":action_scenario.toString())
                + " sensor_name : " + sensor_name
                + " action_type : " + action_type
                + " info : " + info.toString()
                + " ] .";
    }
}
