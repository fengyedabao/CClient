package com.honeywell.cube.utils;

import com.honeywell.cube.R;
import com.honeywell.cube.db.ModelEnum;

import java.util.HashMap;

/**
 * Created by milton on 16/5/17.
 */
public class Constants {
    public static final String OPERATION_TYPE = "type";
    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_EDIT = "edit";
    public static final String OPERATION_DELETE = "delete";
    public static final String DEVICE_TYPE = "device_type";
    public static final String TITLE = "title11";
    public static final String CONTENT = "content11";
    public static final String CONTENT2 = "content22";
    public static final String SECTION = "section";
    public static final String LOOP_TYPE = "loop_type";
    public static final String SPARK_LOOP = "sparkLoop";
    public static final String SPARK_TITLE = "sparkTitle";
    public static final String SPARK_TYPE = "sparkType";
    public static final String TYPE = "type111";
    public static final String TYPE_STRING = "type_string";
    public static final int MODULE_EDIT_TYPE_COMMON = 0;
    public static final int MODULE_EDIT_TYPE_SPARK_LIGHTING = 1;
    public static final int MODULE_EDIT_TYPE_BACNET = 2;
    public static final int MODULE_EDIT_TYPE_IPVDP = 3;
    public static final String RESULT = "result";
    public static final String SUCCESS = "success";
    public static final String FINISH = "finish";
    public static final String IPC_STREAM_INFO = "IpcStreamInfo";
    public static final String IPC_IP_ADDR = "IpcIPAddr";
    public static final String TOKEN = "token";
    public static final String REGISTER = "register";

    public static final String N_ACTION_TYPE = "action";
    public static final String N_SUBACTION_TYPE = "subaction";
    public static final String N_MSG_ID = "msgId";
    public static final String N_CALL_MSG_TYPE = "callmsg";
    public static final String CALL_MSG_TERMINATE_CALL = "terminatecall";
    public static final String CALL_MSG_INCOMING_CALL = "incomingcall";
    public static final String ACTION_EVENT = "event";
    public static final String SUBACTION_CALL = "call";
    public static final String CUSTOM_CONTENT = "custom_content";
    public static final String IS_FROM_SCENARIO = "is_from_scenario";
    public static final int SELECT_TYPE_SCENARIO = 0;
    public static final int SELECT_TYPE_DEVICE = 1;
    public static final int SELECT_TYPE_ZONE = 2;
    public static final int SELECT_TYPE_ROOM_ENVIRONMENT = 3;

    public static final int AC_TYPE_DEVICE = 0;
    public static final int AC_TYPE_SET_STATUS = 1;

    public static final HashMap<String, Integer> DEVICE_TYPE_MAP = new HashMap<>();

    static {
        DEVICE_TYPE_MAP.put("main_light", R.string.device_type_light);
        DEVICE_TYPE_MAP.put("main_curtain", R.string.device_type_curtain);
        DEVICE_TYPE_MAP.put("main_relay", R.string.device_type_relay);
        DEVICE_TYPE_MAP.put("main_zone", R.string.device_type_zone);
        DEVICE_TYPE_MAP.put("main_ip_camera", R.string.device_type_ip_camera);
        DEVICE_TYPE_MAP.put("main_air_conditioner", R.string.device_type_air_conditioner);
        DEVICE_TYPE_MAP.put("main_backaudio", R.string.device_type_backaudio);
        DEVICE_TYPE_MAP.put("main_purifier", R.string.device_type_purifier);
        DEVICE_TYPE_MAP.put("main_ir_stb", R.string.device_type_ir_stb);
        DEVICE_TYPE_MAP.put("main_ir_dvd", R.string.device_type_ir_dvd);
        DEVICE_TYPE_MAP.put("main_ir_television", R.string.device_type_ir_television);
        DEVICE_TYPE_MAP.put("main_ir_ac", R.string.device_type_ir_ac);
        DEVICE_TYPE_MAP.put("main_ir_customize", R.string.device_type_ir_customize);
        DEVICE_TYPE_MAP.put("main_ventilation", R.string.device_type_ventilation);
        DEVICE_TYPE_MAP.put(ModelEnum.MAIN_CALL_ELEVATOR, R.string.device_type_call_elevator);

    }
}
