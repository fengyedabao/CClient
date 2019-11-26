package com.honeywell.cube.db.configuredatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.common.utils.ContentValuesFactory;

/**
 * Created by h157925 on 4/7/2016.09:53
 * Email:Shoudong.Sun@Honeywell.com
 */
public class ConfigCubeDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "ConfigCubeDatabaseHelper";

    private final static String DB_NAME = CommonData.APPDATABASEFILE;
    private final static int DB_VERSION = CommonData.APPDATABASEVersion;

    //Table_name
    public static final String TABLE_CUBE = "cube";//存储
    public static final String TABLE_PERIPHERALDEVICE = "peripheraldevice";//主表
    public static final String TABLE_SPARKLIGNTINGLOOP = "sparklightingloop";
    public static final String TABLE_VENTILATIONLOOP = "ventilationloopmap";
    public static final String TABLE_CUBEBASE = "cubebase";
    public static final String TABLE_CUBEBASECONFIG = "cubebaseconfig";

    public static final String TABLE_RELAYLOOP = "relayloop";
    public static final String TABLE_BACNETLOOP = "bacnetloop";
    public static final String TABLE_WIRDEDZONELOOP = "wiredzoneloop";
    public static final String TABLE_WIRELESS315M433MLOOP = "wireless315M433Mloop";
    public static final String TABLE_SCENARIOSETTINGS = "ScenarioSettings";
    public static final String TABLE_SCENARIOIDS = "Scenarioids";

    public static final String TABLE_IR_INFO = "irinfo";
    public static final String TABLE_IR_LOOP = "irloop";
    public static final String TABLE_IR_CODE = "ircode";

    public static final String TABLE_BACKAUDIO_DEVICE = "backaudiodevice";
    public static final String TABLE_BACKAUDIO_LOOP = "backaudioloop";

    public static final String TABLE_485_LOOP = "wifi485loop";

    public static final String TABLE_IPCSTREAM_INFO = "ipcstreaminfo";

    public static final String TABLE_IPVDP_INFO = "ipvdpinfo";
    public static final String TABLE_IPVDPZONELOOP = "ipvdpzoneloop";

    public static final String TABLE_SCENARIO_TRIGGER = "scenario_trigger";
    public static final String TABLE_CONDITION = "condition";
    public static final String TABLE_TRIGGER_SCENARIO = "trigger_scenario";
    public static final String TABLE_TRIGGER_DEVICE = "trigger_device";

    public static final String TABLE_MUTEX_RULE = "mutexrule";
    public static final String TABLE_MUTEX_DEVICE_MAP = "mutexdevicemap";

    public static final String TABLE_SCHEDULE_RULE = "schedule_rule";
    public static final String TABLE_SCHEDULE_SCENARIO = "schedule_scenario";
    public static final String TABLE_SCHEDULE_DEVICE = "schedule_device";
    public static final String TABLE_ROOM = "room";
    public static final String TABLE_ENVIRONMENT = "environment";
    public static final String TABLE_APPINFO = "appinfo";


    private static final String[] TABLE_NAMES = {
            TABLE_CUBE,
            TABLE_PERIPHERALDEVICE,
            TABLE_SPARKLIGNTINGLOOP,
            TABLE_VENTILATIONLOOP,
            TABLE_CUBEBASE,
            TABLE_RELAYLOOP,
            TABLE_BACNETLOOP,
            TABLE_WIRDEDZONELOOP,
            TABLE_WIRELESS315M433MLOOP,
            TABLE_SCENARIOSETTINGS,
            TABLE_SCENARIOIDS,
            TABLE_IR_INFO,
            TABLE_IR_LOOP,
            TABLE_IR_CODE,
            TABLE_BACKAUDIO_DEVICE,
            TABLE_BACKAUDIO_LOOP,
            TABLE_485_LOOP,
            TABLE_IPCSTREAM_INFO,
            TABLE_IPVDP_INFO,
            TABLE_IPVDPZONELOOP,
            TABLE_SCENARIO_TRIGGER,
            TABLE_CONDITION,
            TABLE_TRIGGER_SCENARIO,
            TABLE_TRIGGER_DEVICE,
            TABLE_MUTEX_RULE,
            TABLE_MUTEX_DEVICE_MAP,
            TABLE_SCHEDULE_RULE,
            TABLE_SCHEDULE_SCENARIO,
            TABLE_SCHEDULE_DEVICE,
            TABLE_ROOM,
            TABLE_ENVIRONMENT,
            TABLE_APPINFO,
            TABLE_CUBEBASECONFIG
    };

    //ALTER TABLE peripheraldevice ADD laichao varchar(128) default "0abc"
    //for basic column
    public final static String COLUMN_ID = "_id";//integer
    public final static String COLUMN_DEVICEID = "dev_id";//int,主外键，和peripheraldevice的id一致,根据外部传入的ip_addr和mac_addr，虽然不存储 。
    public final static String COLUMN_LOOPNAME = "loop_name";//str
    public final static String COLUMN_ROOM_ID = "room_id";//int
    public final static String COLUMN_ROOMNAME = "room_name";//str
    public final static String COLUMN_LOOPID = "loop_id";//int
    public final static String COLUMN_IS_ENABLE = "is_enable";//int
    public final static String COLUMN_LOOPTYPE = "loop_type";//byte
    public final static String COLUMN_ZONETYPE = "zone_type";//zonetype,string
    public final static String COLUMN_ALARMTYPE = "alarm_type";//alarmtype,string
    public final static String COLUMN_ALARMTIMER = "alarm_timer";//alarmtimer,int

    public final static String COLUMN_PRIMARYID = "primaryid";//alarmtimer,int
    public final static String COLUMN_MODULETYPE = "moduletype";//alarmtimer,int

    //colume for cube -- CUBE表
    public final static String COLUMN_CUBE_DEVICEID = "deviceid";//设备Id
    public final static String COLUMN_CUBE_DEVICE_SERIAL = "deviceSerial";//SerialNum
    public final static String COLUMN_CUBE_DEVICEINFO_SERIALNUMBER = "serialnumber";//设备信息-serialNum
    public final static String COLUMN_CUBE_DEVICEINFO_FIRMWAREVERSION = "firmwareversion";//设备信息-firmwareversion
    public final static String COLUMN_CUBE_DEVICEINFO_APPLICATIONVERSION = "applicationversion";//设备信息-application version
    public final static String COLUMN_CUBE_DEVICEINFO_MACADDRESS = "macaddress";//设备信息-mac address
    public final static String COLUMN_CUBE_DEVICEINFO_ALIASNAME = "aliasname";//设备信息-别名


    //colume for peripheraldevice－－小模块主表
    public final static String COLUMN_PERIPHERALDEVICE_PRIMARYID = "primaryid";//映射子表
    public final static String COLUMN_PERIPHERALDEVICE_TYPE = "type";//int,
    public final static String COLUMN_PERIPHERALDEVICE_NAME = "name";//str
    public final static String COLUMN_PERIPHERALDEVICE_IP = "ip_addr";//str
    public final static String COLUMN_PERIPHERALDEVICE_MAC = "mac_addr";//str
    public final static String COLUMN_PERIPHERALDEVICE_PORT = "port";//str
    public final static String COLUMN_PERIPHERALDEVICE_ISCONFIG = "isconfig";//int
    public final static String COLUMN_PERIPHERALDEVICE_ISONLINE = "isonline";//int
    public final static String COLUMN_PERIPHERALDEVICE_BACNETID = "bacnetid";//int
    public final static String COLUMN_PERIPHERALDEVICE_BRANDNAME = "brandname";
    public final static String COLUMN_PERIPHERALDEVICE_MASKID = "mask_id";//byte
    public final static String COLUMN_PERIPHERALDEVICE_VERSION = "version";//byte

    //colume for ventilation
    public final static String COLUMN_VENTILATION_CONTROLTYPE = "controltype";
    public final static String COLUMN_VENTILATION_POWER = "power";
    public final static String COLUMN_VENTILATION_FANSPEED = "fanspeed";
    public final static String COLUMN_VENTILATION_CYCLETYPE = "cycletype";
    public final static String COLUMN_VENTILATION_HUMIDITY = "humidity";
    public final static String COLUMN_VENTILATION_DEHUMIDITY = "dehumidity";

    //colume for sparkinglightingloop
    public final static String COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID = "sub_dev_id";//byte
    public final static String COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE = "sub_dev_type";//str

    //colume for cubebase
    public final static String COLUMN_CUBEBASE_CONFIGNAME = "conf_name";
    public final static String COLUMN_CUBEBASE_CONFIGVALUE = "conf_value";


    //colume for relayloop
    public final static String COLUMN_REALYLOOP_TRIGGERTIME = "trigger_time";//trigger_time,int

    //colume for bacnetloop
    public final static String COLUMN_BACNET_SUBGATEWAYID = "sub_gateway_id";//sub_gateway_id int

    //colume for wireless315M433Mloop
    public final static String COLUMN_WIREDLESS315433SERIALNO = "serial_number";//sn,str
    public final static String COLUMN_WIREDLESS315433DEVICEID = "device_id";//device_id,int
    public final static String COLUMN_WIREDLESS315433DEVICETYPE = "device_type";//device_type,str

    //column for scenario settings
    public final static String COLUMN_SCENARIO_ID = "scenario_id";//scenario_id
    public final static String COLUMN_SCENARIO_NAME = "scenario_name";//scenario_name
    public final static String COLUMN_SCENARIO_ACTIONINFO = "actioninfo";//
    public final static String COLUMN_SCENARIO_IS_ARM = "isarm";//
    public final static String COLUMN_SCENARIO_IMAGENAME = "imagename";//
    public final static String COLUMN_SCENARIO_MODULETYPE = "moduletype";//
    public final static String COLUMN_SCENARIO_CLICKCOUNT = "clickcount";//点击次数

    //ir info(和 peripheral 平级的)
    public final static String COLUMN_IR_INFO_TYPE = "ir_type";//str
    public final static String COLUMN_IR_INFO_NAME = "ir_name";//str
    public final static String COLUMN_IR_INFO_LOCK = "ir_lock";//int
    public final static String COLUMN_IR_INFO_PASSWORD = "ir_pwd";//String（本身是long，太长了，用str 替代）
    public final static String COLUMN_IR_INFO_ID = "ir_id";//int
    public final static String COLUMN_IR_INFO_SUBDEV = "ir_sub_dev";//int
    public final static String COLUMN_IR_INFO_KEY = "ir_key";//scenario_name

    //ir loop
    public final static String COLUMN_IR_LOOP_TYPE = "looptype";//str

    //ir code
    public final static String COLUMN_IR_CODE_LOOPID = "ir_loop_id";//int
    public final static String COLUMN_IR_CODE_NAME = "name";//str
    public final static String COLUMN_IR_CODE_IMAGENAME = "imagename";//str
    public final static String COLUMN_IR_CODE_DATA1 = "data1";//str
    public final static String COLUMN_IR_CODE_DATA2 = "data2";//str

    //backAudio device(没有外围设备表)
    public final static String COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER = "serialnumber";//str
    public final static String COLUMN_BACKAUDIO_DEVICE_NAME = "name";//str
    public final static String COLUMN_BACKAUDIO_DEVICE_MACHINETYPE = "machinetype";//str
    public final static String COLUMN_BACKAUDIO_DEVICE_LOOPNUMBER = "loopnumber";//int
    public final static String COLUMN_BACKAUDIO_DEVICE_ISONLINE = "isonline";//int

    //485 loop
    public final static String COLUMN_485_BRANDNAME = "brandname";
    public final static String COLUMN_485_PORTID = "portid";
    public final static String COLUMN_485_SLAVEADDR = "slaveaddr";

    //ipc_stream info (和 peripheral 平级的)
    public final static String COLUMN_IPCSTREAN_INFO_TYPE = "type";//str
    public final static String COLUMN_IPCSTREAN_INFO_MAIN_STREAM = "main_stream";//str
    public final static String COLUMN_IPCSTREAN_INFO_SUB_STREAM = "sub_stream";//str
    public final static String COLUMN_IPCSTREAN_INFO_STREAM_PORT = "stream_port";//int
    public final static String COLUMN_IPCSTREAN_INFO_USER = "user";//str
    public final static String COLUMN_IPCSTREAN_INFO_PASSWORD = "password";//str

    //ipvdp info
    public final static String COLUMN_IPVDP_INFO_DEVICEID = "deviceid";
    public final static String COLUMN_IPVDP_INFO_HNSSERVERADDR = "hnsserveraddr";

    //scenario trigger field
    public final static String COLUMN_SCENARIOTRIGGER_SWITCHSTATUS = "switchstatus";
    public final static String COLUMN_SCENARIOTRIGGER_DELAYTIME = "delaytime";
    public final static String COLUMN_SCENARIOTRIGGER_AVAIBLETIME = "avaibletime";
    public final static String COLUMN_SCENARIOTRIGGER_TYPE = "type";
    public final static String COLUMN_SCENARIOTRIGGER_NAME = "name";
    public final static String COLUMN_SCENARIOTRIGGER_DESCRIPTION = "description";

    /*condition is another table*/
    public final static String COLUMN_SCENARIOTRIGGER_SCENARIOACTION = "scenarionaction";
    /*device action is anothetr table*/

    //trigger condition table field
    public final static String COLUMN_CONDITION_PRIMARYID = "conditionprimaryid";
    public final static String COLUMN_CONDITION_TRIGGERID = "triggerid";
    public final static String COLUMN_CONDITION_ACTIONINFO = "actioninfo";

    //trigger scenario table field
    public final static String COLUMN_TRIGGERSCNARIO_TRIGGERID = COLUMN_CONDITION_TRIGGERID;
    public final static String COLUMN_TRIGGERSCNARIO_ACTIONINFO = COLUMN_CONDITION_ACTIONINFO;


    //trigger device control table field
    public final static String COLUMN_TRIGGERDEVICE_PRIMARYID = "triggerdeviceprimaryid";
    public final static String COLUMN_TRIGGERDEVICE_TRIGGERID = COLUMN_CONDITION_TRIGGERID;
    public final static String COLUMN_TRIGGERDEVICE_ACTIONINFO = COLUMN_CONDITION_ACTIONINFO;


    //mutex rule table field
    //scenario trigger field
    public final static String COLUMN_MUTEXRULE_SWITCHSTATUS = "switchstatus";
    public final static String COLUMN_MUTEXRULE_NAME = "name";
    public final static String COLUMN_MUTEXRULE_DESCRIPTION = "description";
    /*mutexmap is another table*/

    //mutex device map table field
    public final static String COLUMN_MUTEXDEVICEMAP_MUTEXID = "mutexid";

    public final static String COLUMN_SCHEDULERULE_SWITCHSTATUS = "switchstatus";
    public final static String COLUMN_SCHEDULERULE_AVAIBLETIME = "avaibletime";
    public final static String COLUMN_SCHEDULERULE_NAME = "name";
    public final static String COLUMN_SCHEDULERULE_DESCRIPTION = "description";
    public final static String COLUMN_SCHEDULERULE_SCENARIOACTION = "scenarionaction";

    public final static String COLUMN_SCHEDULESCENARIO_RULEID = "schedulerule_id";
    public final static String COLUMN_SCHEDULESCENARIO_ACTIONINFO = "actioninfo";

    public final static String COLUMN_SCHEDULEDEVICE_ID = "scheduledeviceid";
    public final static String COLUMN_SCHEDULEDEVICE_RULEID = COLUMN_SCHEDULESCENARIO_RULEID;
    public final static String COLUMN_SCHEDULEDEVICE_ACTIONINFO = COLUMN_SCHEDULESCENARIO_ACTIONINFO;
    public final static String COLUMN_ROOM_IMAGENAME = COLUMN_IR_CODE_IMAGENAME;
    public final static String COLUMN_ENVIRONMENT_ROOMID = "roomid";
    public final static String COLUMN_ENVIRONMENT_SUBGATEWAYID = COLUMN_BACNET_SUBGATEWAYID;
    public final static String COLUMN_ENVIRONMENT_PARAMTYPE = "paramtype";


    //app info
    public final static String COLUMN_APPINFO_DEVICETOKEN = "deviceToken";
    public final static String COLUMN_APPINFO_VERSION = "version";
    public final static String COLUMN_APPINFO_DATABASE_VERSION = "databaseversion";
    public final static String COLUMN_APPINFO_PHONE_PREFIX = "phone_prefix";
    public final static String COLUMN_APPINFO_USERNAME = "username";
    public final static String COLUMN_APPINFO_PASSWORD = "password";
    public final static String COLUMN_APPINFO_NICKNAME = "nickname";
    public final static String COLUMN_APPINFO_PHONEID = "phoneid";
    public final static String COLUMN_APPINFO_DEVICEID = "deviceid";
    public final static String COLUMN_APPINFO_USER_IMAGE_PATH = "user_image_path";
    public final static String COLUMN_APPINFO_CURRENT_SCENARIO_ID = "current_scenario_id";
    public final static String COLUMN_APPINFO_CUBE_LOCATION = "location";
    public final static String COLUMN_APPINFO_CUBE_IP = "cube_ip";
    public final static String COLUMN_APPINFO_CUBE_MAC = "cube_mac";

    public final static String COLUMN_APPINFO_CUBE_PORT = "cube_port";
    public final static String COLUMN_APPINFO_CUBE_LOCAL_NICKNAME = "cube_local_nickname";
    public final static String COLUMN_APPINFO_CUBE_LOCAL_ID = "cube_local_id";
    public final static String COLUMN_APPINFO_CUBE_LOCAL_PASSWORD = "cube_local_password";
    public final static String COLUMN_APPINFO_ALL_HEADER_FIELDS_COOKIE = "all_header_fields_cookie";
    public final static String COLUMN_APPINFO_ROUTER_SSID_PASSWORD = "router_ssid_password";
    //后加的
    public final static String COLUMN_APPINFO_CURRENT_SECURITY_STATUS = "current_security_status";
    public final static String COLUMN_APPINFO_CUBE_VOICE_RECOGNIZE = "cube_voice_recognize";
    public final static String COLUMN_APPINFO_CUBE_VERSION = "cube_version";
    public final static String COLUMN_APPINFO_LAST_READ_TIME = "last_read_time";
    public final static String COLUMN_APPINFO_ONLINE = "online";


    public static final String[] cubebase_confignames = {
            "CUBE_ID", "CUBE_PWD", "WIFI_SSID", "WIFI_PWD",
            "WIFI_ENC_TYPE", "WIFI_IP", "WIFI_MASK", "WIFI_GW",
            "LAN_IP", "LAN_MASK", "LAN_GW", "DNS_SERVER1",
            "DNS_SERVER2", "HNS_SERVER_IP", "CLOUD_SERV1", "NICK_NAME",
            "BACNET_ID", "CONFIG_VER"};
    public static final String[] cubebase_configvalues = {"88888888", "222222", "homesystem", "123456",
            "WEP2", "192.168.0.100", "255.255.255.0", "192.168.0.254",
            "172.16.1.100", "255.255.0.0", "172.16.1.254", "8.8.8.8",
            "6.6.6.6", "172.16.1.2", "https://www.", "My Cube",
            "123", "0"};

    public static final String[] default_roomname = {"Master Bedroom", "Second Bedroom", "Guest Bedroom", "Kitchen",
            "Dining Room", "Living Room", "Master Bathroom", "Second Bathroom"};


    //single instance Java Mode
    private static ConfigCubeDatabaseHelper instance = null;
    public Context mContext = null;

    public static synchronized ConfigCubeDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigCubeDatabaseHelper(context);
        }
        return instance;
    }

    private ConfigCubeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // createPeripheralDeviceTable
        String sql = createPeripheralDeviceTable();
        db.execSQL(sql);

        //create cube table
        sql = createCubeTable();
        db.execSQL(sql);

        // createSparkLightingLoopTable
        sql = createSparkLightingLoopTable();
        db.execSQL(sql);

        // createVentilationLoopTable
        sql = createVentilationLoopTable();
        db.execSQL(sql);

        //这个表不一定有用 后面需要验证
        //create Cubebase table
        sql = createCubebaseTable();
        db.execSQL(sql);
        //should insert init record
//        initCubebaseTableRecord(db);

        //create cubebase configuration
        sql = createCubebaseConfigTable();
        db.execSQL(sql);

        // create RelayLoop Table
        sql = createRelayLoopTable();
        db.execSQL(sql);

        //createBacnetLoopTable
        sql = createBacnetLoopTable();
        db.execSQL(sql);

        //create WiredZone Loop Table
        sql = createWirdzoneLoopTable();
        db.execSQL(sql);

        //create wired 315M 433M looptable
        sql = createWireless315m433mLoopTable();
        db.execSQL(sql);

        //create Scenario Settings Table
        sql = createScenarioSettingsTable();
        db.execSQL(sql);

        //create Scenario IO ids Tables
        sql = createScenarioIdsTable();
        db.execSQL(sql);
        initScenarioSettingNormalTableRecord(db);


        // create Ir Info Table
        sql = createIrInfoTable();
        db.execSQL(sql);

        //create Ir Loop Table
        sql = createIrLoopTable();
        db.execSQL(sql);

        //create Ir Code Table
        sql = createIrCodeTable();
        db.execSQL(sql);

        //create back audio device table
        sql = createBackaudioDeviceTable();
        db.execSQL(sql);

        //create back audio loop table
        sql = createBackaudioLoopTable();
        db.execSQL(sql);

        //create 485 loop table
        sql = create485LoopTable();
        db.execSQL(sql);

        //create ipc stream info table
        sql = createIpcStreamInfoTable();
        db.execSQL(sql);

        //create ipvdp info Table
        sql = createIpvdpInfoTable();
        db.execSQL(sql);

        //create ipvdp zone loop table
        sql = createIpvdpZoneLoopTable();
        db.execSQL(sql);

        //create scenario trigger table
        sql = createScenarioTriggerTable();
        db.execSQL(sql);

        //create condition table
        sql = createConditionTable();
        db.execSQL(sql);

        //create trigger scenario table
        sql = createTriggerScenarioTable();
        db.execSQL(sql);

        //create trigger device table
        sql = createTriggerDeviceTable();
        db.execSQL(sql);

        //ios中没有下面2个表 待验证
        //create mutex rule table-－－MutexRule  MutexDevice
        sql = createMutexRuleTable();
        db.execSQL(sql);

        //create mutex device map table
        sql = createMutexDeviceMapTable();
        db.execSQL(sql);

        //create schedule rule table
        sql = createScheduleRuleTable();
        db.execSQL(sql);

        //create schedule scenario table
        sql = createScheduleScenarioTable();
        db.execSQL(sql);

        //create schedule device table
        sql = createScheduleDeviceTable();
        db.execSQL(sql);


        //create environment table

        sql = createEnvironmentTable();
        db.execSQL(sql);

        //App info
        sql = createAppInfoTable();
        db.execSQL(sql);

        //room
        sql = createRoomTable();
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //清空所有表的内容
    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        //cube
        clearTableWithName(TABLE_CUBE, db);

        //cube base config
        clearTableWithName(TABLE_CUBEBASECONFIG, db);

        //backAudio
        clearTableWithName(TABLE_BACKAUDIO_DEVICE, db);

        //backAudioLoop
        clearTableWithName(TABLE_BACKAUDIO_LOOP, db);

        //Periphera
        clearTableWithName(TABLE_PERIPHERALDEVICE, db);

        //SparkLighting
        clearTableWithName(TABLE_SPARKLIGNTINGLOOP, db);

        //ventilation
        clearTableWithName(TABLE_VENTILATIONLOOP, db);

        //WIFi relay
        clearTableWithName(TABLE_RELAYLOOP, db);

        //ipvdp zone
        clearTableWithName(TABLE_IPVDPZONELOOP, db);

        //wired zone
        clearTableWithName(TABLE_WIRDEDZONELOOP, db);

        //315_433M
        clearTableWithName(TABLE_WIRELESS315M433MLOOP, db);

        //Ip camera
        clearTableWithName(TABLE_IPCSTREAM_INFO, db);

        //bacnet
        clearTableWithName(TABLE_BACNETLOOP, db);

        //485
        clearTableWithName(TABLE_485_LOOP, db);

        //IR info
        clearTableWithName(TABLE_IR_INFO, db);

        //IR Loop
        clearTableWithName(TABLE_IR_LOOP, db);

        //IR Code
        clearTableWithName(TABLE_IR_CODE, db);

        //scenario setting
        clearTableWithName(TABLE_SCENARIOSETTINGS, db);

        //schedule
        clearTableWithName(TABLE_SCHEDULE_RULE, db);

        //schedule scenario
        clearTableWithName(TABLE_SCHEDULE_SCENARIO, db);

        //schedule devices
        clearTableWithName(TABLE_SCHEDULE_DEVICE, db);

        //rule
        clearTableWithName(TABLE_SCENARIO_TRIGGER, db);

        //rule condition
        clearTableWithName(TABLE_CONDITION, db);

        //rule action scenario
        clearTableWithName(TABLE_TRIGGER_SCENARIO, db);

        //rule device
        clearTableWithName(TABLE_TRIGGER_DEVICE, db);

        //room
        clearTableWithName(TABLE_ROOM, db);

        initScenarioSettingNormalTableRecord(db);

        db.close();

    }

    private void clearTableWithName(String tableName, SQLiteDatabase db) {

        String sql = "delete from " + tableName + " where 1=1";
        Loger.print(TAG, sql, Thread.currentThread());
        db.execSQL(sql);
    }

    //private method
    private String createCubeTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CUBE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_CUBE_DEVICEID + " INTEGER, "//device id
                + COLUMN_CUBE_DEVICE_SERIAL + " nvarchar(128), " // device serial
                + COLUMN_CUBE_DEVICEINFO_SERIALNUMBER + " nvarchar(128), " // info - serial number
                + COLUMN_CUBE_DEVICEINFO_FIRMWAREVERSION + " nvarchar(128), " // info - firmware version
                + COLUMN_CUBE_DEVICEINFO_APPLICATIONVERSION + " nvarchar(128), " // Application version
                + COLUMN_CUBE_DEVICEINFO_MACADDRESS + " nvarchar(128), " // Mac 地址
                + COLUMN_CUBE_DEVICEINFO_ALIASNAME + " nvarchar(128) " // 别名
                + ") ";
    }


    private String createPeripheralDeviceTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_PERIPHERALDEVICE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PERIPHERALDEVICE_PRIMARYID + " INTEGER, "//primary Id
                + COLUMN_PERIPHERALDEVICE_TYPE + " INTEGER, " // type
                + COLUMN_PERIPHERALDEVICE_NAME + " nvarchar(128), " // name
                + COLUMN_PERIPHERALDEVICE_IP + " nvarchar(128), " // ip_addr
                + COLUMN_PERIPHERALDEVICE_MAC + " nvarchar(128), " // mac_addr
                + COLUMN_PERIPHERALDEVICE_PORT + " INTEGER, " // port
                + COLUMN_PERIPHERALDEVICE_ISCONFIG + " INTEGER, " // isConfig
                + COLUMN_PERIPHERALDEVICE_ISONLINE + " INTEGER, " // isOnline
                + COLUMN_PERIPHERALDEVICE_BACNETID + " INTEGER, " // brandid
                + COLUMN_PERIPHERALDEVICE_BRANDNAME + " nvarchar(128), " // brandname
                + COLUMN_PERIPHERALDEVICE_MASKID + " INTEGER, " // maskid
                + COLUMN_PERIPHERALDEVICE_VERSION + " nvarchar(128)" // version
                + ") ";
    }

    private String createSparkLightingLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SPARKLIGNTINGLOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, " //primary id
                + COLUMN_DEVICEID + " INTEGER, " // dev_id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, " // room_name
                + COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID + " INTEGER, " // sub_dev_id
                + COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE + " nvarchar(128), " // sub_dev_type
                + COLUMN_LOOPTYPE + " INTEGER, " // loop_type
                + COLUMN_LOOPID + " INTEGER, " // loop_id
                + COLUMN_IS_ENABLE + " INTEGER" //is_enable
                + ") ";
    }

    private String createVentilationLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_VENTILATIONLOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, " //primary id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, " // room_name
                + COLUMN_VENTILATION_CONTROLTYPE + " nvarchar(128), "
                + COLUMN_VENTILATION_POWER + " nvarchar(128), "
                + COLUMN_VENTILATION_FANSPEED + " nvarchar(128), "
                + COLUMN_VENTILATION_CYCLETYPE + " nvarchar(128), "
                + COLUMN_VENTILATION_HUMIDITY + " nvarchar(128), "
                + COLUMN_VENTILATION_DEHUMIDITY + " nvarchar(128)"
                + ") ";
    }

    private String createCubebaseTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CUBEBASE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_CUBEBASE_CONFIGNAME + " nvarchar(128), " // conf_name
                + COLUMN_CUBEBASE_CONFIGVALUE + " nvarchar(128)" // conf_value
                + ") ";
    }

    private String createCubebaseConfigTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CUBEBASECONFIG + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, " //primary id
                + COLUMN_CUBEBASE_CONFIGNAME + " nvarchar(128), " // conf_name
                + COLUMN_CUBEBASE_CONFIGVALUE + " nvarchar(128)" // conf_value
                + ") ";
    }

//    private void initCubebaseTableRecord(SQLiteDatabase db) {
//        db.beginTransaction();//开始事务
//        try {
//            for (int i = 0; i < cubebase_confignames.length; i++) {
//                ContentValues values = new ContentValuesFactory()
//                        .put(COLUMN_CUBEBASE_CONFIGNAME, cubebase_confignames[i])
//                        .put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE, cubebase_configvalues[i]).getValues();
//                db.insert(ConfigCubeDatabaseHelper.TABLE_CUBEBASE, null, values);
//            }
//            db.setTransactionSuccessful();//由事务的标志决定是提交事务，还是回滚事务
//        } catch (Exception e) {
//            Loger.print(TAG, "ssd:initCubebaseTableRecord()--exception" + e.toString(), Thread.currentThread());
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
//        }
//    }


    private String createRelayLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_RELAYLOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " // dev_id
                + COLUMN_LOOPID + " INTEGER, " //relay_id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, "
                + COLUMN_REALYLOOP_TRIGGERTIME + " INTEGER" // trigger_time
                + ") ";
    }

    private String createBacnetLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BACNETLOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " // dev_id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, "
                + COLUMN_LOOPID + " INTEGER, " // loop_id
                + COLUMN_BACNET_SUBGATEWAYID + " INTEGER" // subgateway_id
                + ") ";
    }

    private String createWirdzoneLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_WIRDEDZONELOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " // dev_id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, "
                + COLUMN_LOOPID + " INTEGER, " // loop_id
                + COLUMN_ZONETYPE + " nvarchar(128), " // zonetype
                + COLUMN_ALARMTYPE + " nvarchar(128), " // alarmtype
                + COLUMN_ALARMTIMER + " INTEGER, " // alarmtimer
                + COLUMN_IS_ENABLE + " INTEGER" // isenable()
                + ") ";
    }

    private String createWireless315m433mLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_WIRELESS315M433MLOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " // dev_id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, "
                + COLUMN_LOOPID + " INTEGER, " // loop_id
                + COLUMN_ZONETYPE + " nvarchar(128), " // zonetype
                + COLUMN_ALARMTYPE + " nvarchar(128), " // alarmtype
                + COLUMN_ALARMTIMER + " INTEGER, " // alarmtimer
                + COLUMN_IS_ENABLE + " INTEGER, " // isenable()
                + COLUMN_WIREDLESS315433DEVICEID + " INTEGER, " // deviceid
                + COLUMN_WIREDLESS315433SERIALNO + " nvarchar(128), " // serialnumber
                + COLUMN_WIREDLESS315433DEVICETYPE + " nvarchar(128), " // devicetype
                + COLUMN_LOOPTYPE + " INTEGER" // looptype
                + ") ";
    }

    private String createScenarioSettingsTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCENARIOSETTINGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_SCENARIO_ID + " INTEGER, " // scenario_id
                + COLUMN_SCENARIO_NAME + " nvarchar(128), " // scenario_name
                + COLUMN_SCENARIO_MODULETYPE + " INTEGER, " // moduletype
                + COLUMN_DEVICEID + " INTEGER, " // dev_id,   device loop的primaryid
                + COLUMN_SCENARIO_ACTIONINFO + " nvarchar(1024), " // action
                + COLUMN_SCENARIO_IS_ARM + " INTEGER, " // isarm
                + COLUMN_SCENARIO_IMAGENAME + " nvarchar(128), " // action
                + COLUMN_SCENARIO_CLICKCOUNT + " INTEGER" // click count
                + ") ";
    }

    private String createScenarioIdsTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCENARIOIDS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_SCENARIO_ID + " INTEGER, " // scenario_id
                + COLUMN_SCENARIO_NAME + " nvarchar(128), " // scenario_name
                + COLUMN_SCENARIO_IMAGENAME + " nvarchar(128)" // imagename
                + ") ";
    }

    private void initScenarioSettingNormalTableRecord(SQLiteDatabase db) {
        db.beginTransaction();//开始事务
        try {
            for (int i = CommonData.SCENARIO_ID_HOME; i <= CommonData.SCENARIO_ID_DISARMALL; i++) {
                ContentValues values = new ContentValuesFactory()
                        .put(COLUMN_SCENARIO_ID, i)
                        .put(COLUMN_SCENARIO_NAME, Util.getScenarioNameByScenarioId(i))
                        .put(COLUMN_SCENARIO_IMAGENAME, "").getValues();
                db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null, values);
            }
            db.setTransactionSuccessful();//由事务的标志决定是提交事务，还是回滚事务
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
        }
    }

    private String createIrInfoTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_IR_INFO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_DEVICEID + " INTEGER, " //
                + COLUMN_IR_INFO_TYPE + " nvarchar(128), "
                + COLUMN_IR_INFO_NAME + " nvarchar(128), "
                + COLUMN_IR_INFO_LOCK + " INTEGER, "
                + COLUMN_IR_INFO_PASSWORD + " nvarchar(128), "
                + COLUMN_IR_INFO_ID + " INTEGER, "
                + COLUMN_IR_INFO_SUBDEV + " INTEGER, "
                + COLUMN_IR_INFO_KEY + " nvarchar(128)"
                + ") ";
    }

    private String createIrLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_IR_LOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " //
                + COLUMN_LOOPNAME + " nvarchar(128), "
                + COLUMN_ROOM_ID + " INTEGER, "
                + COLUMN_IR_LOOP_TYPE + " nvarchar(128)" //
                + ") ";
    }

    private String createIrCodeTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_IR_CODE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_IR_CODE_LOOPID + " INTEGER, " // _id
                + COLUMN_IR_CODE_NAME + " nvarchar(128), " //
                + COLUMN_IR_CODE_IMAGENAME + " nvarchar(128), " //
                + COLUMN_IR_CODE_DATA1 + " nvarchar(1024), "
                + COLUMN_IR_CODE_DATA2 + " nvarchar(1024)"
                + ") ";
    }

    private String createBackaudioDeviceTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BACKAUDIO_DEVICE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER + " nvarchar(10), "
                + COLUMN_BACKAUDIO_DEVICE_NAME + " nvarchar(128), "
                + COLUMN_BACKAUDIO_DEVICE_MACHINETYPE + " INTEGER, "
                + COLUMN_BACKAUDIO_DEVICE_LOOPNUMBER + " INTEGER, "
                + COLUMN_BACKAUDIO_DEVICE_ISONLINE + " INTEGER"
                + ") ";
    }

    private String createBackaudioLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BACKAUDIO_LOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " //
                + COLUMN_LOOPID + " INTEGER, " //
                + COLUMN_LOOPNAME + " nvarchar(128), "
                + COLUMN_ROOM_ID + " INTEGER"
                + ") ";
    }

    private String create485LoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_485_LOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " //
                + COLUMN_485_BRANDNAME + " nvarchar(32), " //
                + COLUMN_485_PORTID + " INTEGER, " //
                + COLUMN_LOOPID + " INTEGER, " //
                + COLUMN_LOOPTYPE + " nvarchar(32), " //
                + COLUMN_485_SLAVEADDR + " INTEGER, " //
                + COLUMN_LOOPNAME + " nvarchar(128), "
                + COLUMN_ROOM_ID + " INTEGER"
                + ") ";
    }

    private String createIpcStreamInfoTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_IPCSTREAM_INFO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " //
                + COLUMN_IPCSTREAN_INFO_TYPE + " nvarchar(128), "
                + COLUMN_IPCSTREAN_INFO_MAIN_STREAM + " nvarchar(128), "
                + COLUMN_IPCSTREAN_INFO_SUB_STREAM + " nvarchar(128), "
                + COLUMN_IPCSTREAN_INFO_STREAM_PORT + " INTEGER, "
                + COLUMN_IPCSTREAN_INFO_USER + " nvarchar(32), "
                + COLUMN_IPCSTREAN_INFO_PASSWORD + " nvarchar(32), "
                + COLUMN_ROOM_ID + " INTEGER"
                + ") ";
    }

    private String createIpvdpInfoTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_IPVDP_INFO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_DEVICEID + " INTEGER, " //
                + COLUMN_IPVDP_INFO_DEVICEID + " INTEGER, "
                + COLUMN_IPVDP_INFO_HNSSERVERADDR + " nvarchar(16)"
                + ") ";
    }

    private String createIpvdpZoneLoopTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_IPVDPZONELOOP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_DEVICEID + " INTEGER, " // dev_id
                + COLUMN_LOOPNAME + " nvarchar(128), " // loop_name
                + COLUMN_ROOM_ID + " INTEGER, " // room_name
                + COLUMN_LOOPID + " INTEGER, " // loop_id
                + COLUMN_ZONETYPE + " nvarchar(128), " // zonetype
                + COLUMN_ALARMTYPE + " nvarchar(128), " // alarmtype
                + COLUMN_ALARMTIMER + " INTEGER, " // alarmtimer
                + COLUMN_IS_ENABLE + " INTEGER" // isenable()
                + ") ";
    }

    private String createScenarioTriggerTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCENARIO_TRIGGER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_SCENARIOTRIGGER_SWITCHSTATUS + " nvarchar(32), "
                + COLUMN_SCENARIOTRIGGER_DELAYTIME + " INTEGER, " //
                + COLUMN_SCENARIOTRIGGER_AVAIBLETIME + " nvarchar(256), "
                + COLUMN_SCENARIOTRIGGER_TYPE + " nvarchar(64), "
                + COLUMN_SCENARIOTRIGGER_NAME + " nvarchar(128), "
                + COLUMN_SCENARIOTRIGGER_DESCRIPTION + " nvarchar(128)"
                + ") ";
    }


    private String createConditionTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CONDITION + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_CONDITION_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_CONDITION_TRIGGERID + " INTEGER, " // triggerid
                + COLUMN_CONDITION_ACTIONINFO + " nvarchar(1024), "
                + COLUMN_PRIMARYID + " INTEGER, " // primaryid
                + COLUMN_MODULETYPE + " INTEGER" // moduletype
                + ") ";
    }


    private String createTriggerScenarioTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_TRIGGER_SCENARIO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_TRIGGERSCNARIO_TRIGGERID + " INTEGER, " // triggerid
                + COLUMN_TRIGGERSCNARIO_ACTIONINFO + " nvarchar(1024)"
                + ") ";
    }

    private String createTriggerDeviceTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_TRIGGER_DEVICE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_TRIGGERDEVICE_PRIMARYID + " INTEGER, " // triggerid
                + COLUMN_TRIGGERDEVICE_TRIGGERID + " INTEGER, " // triggerid
                + COLUMN_TRIGGERDEVICE_ACTIONINFO + " nvarchar(1024), "
                + COLUMN_PRIMARYID + " INTEGER, " // primaryid
                + COLUMN_MODULETYPE + " INTEGER" // moduletype
                + ") ";
    }

    private String createMutexRuleTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_MUTEX_RULE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_MUTEXRULE_SWITCHSTATUS + " nvarchar(32), "
                + COLUMN_MUTEXRULE_NAME + " nvarchar(64), "
                + COLUMN_MUTEXRULE_DESCRIPTION + " nvarchar(128)"
                + ") ";
    }

    private String createMutexDeviceMapTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_MUTEX_DEVICE_MAP + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_MUTEXDEVICEMAP_MUTEXID + " INTEGER, " // mutexid
                + COLUMN_PRIMARYID + " INTEGER, " // primaryid
                + COLUMN_MODULETYPE + " INTEGER" // moduletype
                + ") ";
    }

    private String createScheduleRuleTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE_RULE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_SCHEDULERULE_SWITCHSTATUS + " nvarchar(32), "
                + COLUMN_SCHEDULERULE_AVAIBLETIME + " nvarchar(512), "
                + COLUMN_SCHEDULERULE_NAME + " nvarchar(128), "
                + COLUMN_SCHEDULERULE_DESCRIPTION + " nvarchar(128)"
                + ") ";
    }

    private String createScheduleScenarioTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE_SCENARIO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_SCHEDULESCENARIO_RULEID + " INTEGER, " // triggerid
                + COLUMN_SCHEDULESCENARIO_ACTIONINFO + " nvarchar(1024)"
                + ") ";
    }

    private String createScheduleDeviceTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE_DEVICE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_SCHEDULEDEVICE_ID + " INTEGER, " // triggerid
                + COLUMN_SCHEDULEDEVICE_RULEID + " INTEGER, " // triggerid
                + COLUMN_SCHEDULEDEVICE_ACTIONINFO + " nvarchar(1024), "
                + COLUMN_PRIMARYID + " INTEGER, " // primaryid
                + COLUMN_MODULETYPE + " INTEGER" // moduletype
                + ") ";
    }

    private String createRoomTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ROOM + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_PRIMARYID + " INTEGER, "//primaryId
                + COLUMN_ROOMNAME + " nvarchar(128), " // roomname
                + COLUMN_ROOM_IMAGENAME + " nvarchar(128)"//imagename
                + ") ";
    }

    private void initRoomTableRecord(SQLiteDatabase db) {
        db.beginTransaction();//开始事务
        try {
            for (int i = 0; i < default_roomname.length; i++) {
                ContentValues values = new ContentValuesFactory()
                        .put(COLUMN_ROOMNAME, default_roomname[i])
                        .put(COLUMN_ROOM_IMAGENAME, default_roomname[i].replace(" ", "_").toLowerCase()).getValues();
                db.insert(ConfigCubeDatabaseHelper.TABLE_ROOM, null, values);
            }
            db.setTransactionSuccessful();//由事务的标志决定是提交事务，还是回滚事务
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
        }
    }

    private String createEnvironmentTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ENVIRONMENT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_ENVIRONMENT_ROOMID + " INTEGER, " // triggerid
                + COLUMN_MODULETYPE + " INTEGER, "//moduletype
                + COLUMN_ENVIRONMENT_SUBGATEWAYID + " INTEGER, " // loopid
                + COLUMN_LOOPID + " INTEGER, " // loopid
                + COLUMN_ENVIRONMENT_PARAMTYPE + " nvarchar(128)" // paramtype
                + ") ";
    }

    private String createAppInfoTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_APPINFO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // _id
                + COLUMN_APPINFO_DEVICETOKEN + " nvarchar(128), "
                + COLUMN_APPINFO_VERSION + " nvarchar(128), "
                + COLUMN_APPINFO_DATABASE_VERSION + " nvarchar(128), "
                + COLUMN_APPINFO_CUBE_LOCATION + " nvarchar(128), "
                + COLUMN_APPINFO_PHONE_PREFIX + " nvarchar(128), "
                + COLUMN_APPINFO_USERNAME + " nvarchar(128), "
                + COLUMN_APPINFO_PASSWORD + " nvarchar(128),"
                + COLUMN_APPINFO_NICKNAME + " nvarchar(128), "
                + COLUMN_APPINFO_PHONEID + " INTEGER,"
                + COLUMN_APPINFO_DEVICEID + " INTEGER,"
                + COLUMN_APPINFO_USER_IMAGE_PATH + " nvarchar(128), "
                + COLUMN_APPINFO_CURRENT_SCENARIO_ID + " INTEGER,"
                + COLUMN_APPINFO_CUBE_IP + " nvarchar(128), "
                + COLUMN_APPINFO_CUBE_MAC + " nvarchar(128), "
                + COLUMN_APPINFO_CUBE_PORT + " INTEGER,"
                + COLUMN_APPINFO_CUBE_LOCAL_NICKNAME + " nvarchar(128), "
                + COLUMN_APPINFO_CUBE_LOCAL_ID + " nvarchar(128), "
                + COLUMN_APPINFO_CUBE_LOCAL_PASSWORD + " nvarchar(128), "
                + COLUMN_APPINFO_ALL_HEADER_FIELDS_COOKIE + " nvarchar(128), "
                + COLUMN_APPINFO_ROUTER_SSID_PASSWORD + " nvarchar(128), "
                + COLUMN_APPINFO_CURRENT_SECURITY_STATUS + " INTEGER, "
                + COLUMN_APPINFO_CUBE_VOICE_RECOGNIZE + " INTEGER, "
                + COLUMN_APPINFO_ONLINE + " INTEGER, "
                + COLUMN_APPINFO_LAST_READ_TIME + " nvarchar(128), "
                + COLUMN_APPINFO_CUBE_VERSION + " nvarchar(128)"
                + ") ";
    }

    public static String insertWithOnConflict(String table, String nullColumnHack,
                                              ContentValues initialValues) {

        StringBuilder sql = new StringBuilder();
        StringBuilder sql2 = new StringBuilder();
        sql.append("INSERT");
        sql.append(" INTO ");
        sql.append(table);
        sql.append(" ( ");

        Object[] bindArgs = null;
        int size = (initialValues != null && initialValues.size() > 0)
                ? initialValues.size() : 0;
        if (size > 0) {
            bindArgs = new Object[size];
            int i = 0;
            for (String colName : initialValues.keySet()) {
                sql.append((i > 0) ? "," : "");
                sql.append(colName);
                sql2.append((i > 0) ? ("," + initialValues.get(colName)) : "" + initialValues.get(colName));
                bindArgs[i++] = initialValues.get(colName);
            }
            sql.append(" ) ");
            sql.append(" VALUES ( ");
            sql.append(sql2.toString());
//                for (i = 0; i < size; i++) {
//                    sql.append((i > 0) ? ",?" : "?");
//                }
        } else {
            sql.append(nullColumnHack + " ) VALUES (NULL");
        }
        sql.append(" );");
        return sql.toString();
    }
}
