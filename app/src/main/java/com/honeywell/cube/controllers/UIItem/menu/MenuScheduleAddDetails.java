package com.honeywell.cube.controllers.UIItem.menu;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleRuleInfo;

import org.json.JSONArray; import org.json.JSONObject;

/**
 * Created by H157925 on 16/7/6. 14:57
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuScheduleAddDetails {
    //UI
    //编辑或者添加界面
    public String title = "";//编辑 标题
    public String name = "";//编辑 名称
    public String repeat = "";// 编辑 重复
    //action
    public String action_title = "";

    public String action_time="00:00:00";//数据格式 00:00:00

    public ScheduleRuleInfo scheduleRuleInfo = null;//计划
    //参数
    public int action_type = 0;//0 scenario ； 1 device

    public JSONObject action_scenario_actioninfo = null;//action info 的信息
    public long action_schedule_scenario_id = -1;//Schedule Scenario id
    public ScenarioLoop action_scenarioloop = null;

    public JSONObject action_device_actioninfo = null;
    public String action_device_module_type = "";
    public long action_schedule_device_loop_id = -1;//
    public int action_schedule_device_id = -1;//ScheduleDevice本身的Primary id
    public Object action_device = null;

    public MenuScheduleAddDetails(){}

    @Override
    public String toString()
    {
        return "menu schedule add details item :[ "
                + " title : " + title
                + " name : " + name
                + " repeat : " + repeat
                + " action_title : " + action_title
                + " action_type : " + action_type
                + " action_scenarioloop : " + action_scenarioloop.toString()
                + " action_device : " + action_device.toString()
                + " ] .";
    }
}
