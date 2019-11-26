package com.honeywell.cube.utils;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.utils.Loger.Loger;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by H157925 on 16/6/16. 10:30
 * Email:Shodong.Sun@honeywell.com
 */
public class RoomManager {
    public static final String TAG = RoomManager.class.getSimpleName();

    /**
     * 通过协议名获取对应的名称的资源id
     *
     * @param protocolName
     * @return
     */
    public static String checkDefaultNameWithProtocolName(Context context, String protocolName) {
        ArrayList<Object> items = PlistUtil.parceMultiArrayWithName("DefaultRoom.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = (Map<String, Object>) items.get(i);
            ArrayList<Object> protocolArr = (ArrayList<Object>) item.get("protocol");
            if (protocolArr == null || protocolArr.size() == 0) continue;
            for (Object protocol : protocolArr) {
                String roomName = (String) protocol;
                if (roomName.equalsIgnoreCase(protocolName)) {
                    String name = (String) item.get("name");
                    return RoomManager.transferRoomStr(context, name);
                }
            }
        }
        return protocolName;
    }

    /**
     * 将转换为资源ID，用于Room
     *
     * @param roomName
     * @return 0 失败
     */
    public static String transferRoomStr(Context context, String roomName) {
        if (roomName == null) {
            return "";
        }
        if (roomName.equalsIgnoreCase("master_bedroom")) {
            return context.getString(R.string.room_master_bedroom);
        } else if (roomName.equalsIgnoreCase("second_bedroom")) {
            return context.getString(R.string.room_second_bedroom);
        } else if (roomName.equalsIgnoreCase("guest_bedroom")) {
            return context.getString(R.string.room_guest_bedroom);
        } else if (roomName.equalsIgnoreCase("kitchen")) {
            return context.getString(R.string.room_kitchen);
        } else if (roomName.equalsIgnoreCase("dining_room")) {
            return context.getString(R.string.room_dining_room);
        } else if (roomName.equalsIgnoreCase("living_room")) {
            return context.getString(R.string.room_living_room);
        } else if (roomName.equalsIgnoreCase("master_bathroom")) {
            return context.getString(R.string.room_master_bathroom);
        } else if (roomName.equalsIgnoreCase("second_bathroom")) {
            return context.getString(R.string.room_second_bathroom);
        }
        return "";
    }

    /**
     * 将本地名称转换为需要的名字
     *
     * @param context
     * @param infoName
     * @return
     */
    public static String getProtocolNameWithInfo(Context context, String infoName) {
        ArrayList<Object> items = PlistUtil.parceMultiArrayWithName("DefaultRoom.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = (Map<String, Object>) items.get(i);
            ArrayList<Object> protocolArr = (ArrayList<Object>) item.get("protocol");
            for (Object protocol : protocolArr) {
                String roomName = (String) protocol;
                if (roomName.equalsIgnoreCase(infoName)) {
                    String name = (String) item.get("name");
                    return name;
                }
            }
        }
        return infoName;
    }

    /**
     * 根据 type 获取协议名称
     *
     * @param context
     * @param typeStr
     * @return
     */
    public static String getRuleConditionRoomTypeProtocol(Context context, String typeStr) {
        if (typeStr.equalsIgnoreCase(context.getString(R.string.rule_room_type_AirQ))) {
            return "currentairqual";
        } else if (typeStr.equalsIgnoreCase(context.getString(R.string.rule_room_type_PM))) {
            return "pm2_5";
        } else if (typeStr.equalsIgnoreCase(context.getString(R.string.rule_room_type_temperature))) {
            return "temperature";
        } else if (typeStr.equalsIgnoreCase(context.getString(R.string.rule_room_type_humidity))) {
            return "humidity";
        } else {
            return typeStr;
        }
    }

    /**
     * 根据触发条件来返回相应的协议
     *
     * @param context
     * @param triggerStr
     * @return
     */
    public static String getRuleConditionRoomTriggerProtocol(Context context, String triggerStr) {
        if (triggerStr.equalsIgnoreCase(context.getString(R.string.rule_room_trigger_high))) {
            return "valuehigherthan";
        } else if (triggerStr.equalsIgnoreCase(context.getString(R.string.rule_room_trigger_low))) {
            return "valuelowerthan";
        }
        return triggerStr;
    }

    /**
     * 根据 房间 条件类型获取 值
     *
     * @param context
     * @param type
     * @param value
     * @return
     */
    public static String getRuleConditionRoomValue(Context context, String type, String value) {
        if ("currentairqual".equalsIgnoreCase(type)) {
            if (value.equalsIgnoreCase(context.getString(R.string.rule_roomAQ_value_clean))) {
                return "clean";
            } else if (value.equalsIgnoreCase(context.getString(R.string.rule_roomAQ_value_slight))) {
                return "slight";
            } else if (value.equalsIgnoreCase(context.getString(R.string.rule_roomAQ_value_moderate))) {
                return "moderate";
            } else if (value.equalsIgnoreCase(context.getString(R.string.rule_roomAQ_value_serious))) {
                return "serious";
            }
            return value;
        } else {
            return value;
        }
    }

    /**
     * 转换 value
     *
     * @param context
     * @param value
     * @return
     */
    public static String transferRuleConditionRoomValue(Context context, String value) {
        if ("clean".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_roomAQ_value_clean);
        } else if ("slight".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_roomAQ_value_slight);
        } else if ("moderate".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_roomAQ_value_moderate);
        } else if ("serious".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_roomAQ_value_serious);
        }
        return value;
    }

    /**
     * 转换 trigger
     *
     * @param context
     * @param value
     * @return
     */
    public static String transferRuleConditionRoomTrigger(Context context, String value) {
        if ("valuehigherthan".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_room_trigger_high);
        } else if ("valuelowerthan".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_room_trigger_low);
        }
        return value;
    }

    /**
     * 转换type
     *
     * @param context
     * @param value
     * @return
     */
    public static String transferRuleConditionRoomType(Context context, String value) {
        if ("currentairqual".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_room_type_AirQ);
        } else if ("pm2_5".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_room_type_PM);
        } else if ("temperature".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_room_type_temperature);
        } else if ("humidity".equalsIgnoreCase(value)) {
            return context.getString(R.string.rule_room_type_humidity);
        }
        return value;
    }

}
