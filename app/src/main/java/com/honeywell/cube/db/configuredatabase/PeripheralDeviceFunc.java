package com.honeywell.cube.db.configuredatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.utils.Loger.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 14:52
 * Email:Shodong.Sun@honeywell.com
 */
public class PeripheralDeviceFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public PeripheralDeviceFunc(ConfigCubeDatabaseHelper instance) {
        this.dbHelper = instance;
    }

    //增加
    public synchronized long addPeripheralDevice(PeripheralDevice device) throws SQLException {
        if (null == device) {
            return -1;
        }
        return addPeripheralDevice(device.mPrimaryID, device.mType, device.mName, device.mIpAddr, device.mMacAddr, device.mPort,
                device.mIsConfig, device.mIsOnline,
                device.mBacnetId, device.mBrandName, device.mMaskId, device.mVersion);
    }

    public synchronized long addPeripheralDevice(PeripheralDevice device, SQLiteDatabase db) throws SQLException {
        if (null == device) {
            return -1;
        }
        return addPeripheralDevice(db, device.mPrimaryID, device.mType, device.mName, device.mIpAddr, device.mMacAddr, device.mPort,
                device.mIsConfig, device.mIsOnline,
                device.mBacnetId, device.mBrandName, device.mMaskId, device.mVersion);
    }

    public synchronized long addPeripheralDevice(int primaryID, int type, String name, String ip_addr, String mac_addr, int port, int isConfig, int isOnline, int bacnetId, String brandName, int maskId, String version) {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID, primaryID)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_TYPE, type)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_NAME, name)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_IP, ip_addr)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MAC, mac_addr)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PORT, port)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISCONFIG, isConfig)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISONLINE, isOnline)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BACNETID, bacnetId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BRANDNAME, brandName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_VERSION, version)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MASKID, maskId).getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized long addPeripheralDevice(SQLiteDatabase db, int primaryID, int type, String name, String ip_addr, String mac_addr, int port, int isConfig, int isOnline, int bacnetId, String brandName, int maskId, String version) {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID, primaryID)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_TYPE, type)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_NAME, name)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_IP, ip_addr)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MAC, mac_addr)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PORT, port)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISCONFIG, isConfig)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISONLINE, isOnline)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BACNETID, bacnetId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BRANDNAME, brandName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_VERSION, version)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MASKID, maskId).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized int deletePeripheralDeviceByPrimaryId(long primaryId) {
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        try {
            PeripheralDevice device = getPeripheralDeviceByPrimaryId(primaryId);
            if (null == device) {
                return num;
            }
//            deleteCorrespondingLoop(primaryId,device.mType);
            //先删除loop，在删除外围表
            num = db.delete(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE,
                    ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});

            if (num > 0) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
//        if (num > 0) {
//            db.endTransaction();
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    /**
     * 清空表
     */
    public void clearPeripheraDevices() {
        String sql = "delete from " + ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE + " where 1=1";
        dbHelper.getWritableDatabase().execSQL(sql);
    }

    public PeripheralDevice getPeripheralDeviceByMaskId(int maskId) {
        if (maskId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MASKID + "=?", new String[]{String.valueOf(maskId)}, null, null, null, null);
        PeripheralDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
            break;
        }
        cursor.close();
        return device;
    }

    public PeripheralDevice getPeripheralDeviceByPrimaryId(long devId) {
        if (devId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID + "=?", new String[]{String.valueOf(devId)}, null, null, null, null);
        PeripheralDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
        }
        cursor.close();
        return device;
    }

    //
//    //when delete peripheral device,CorrespondingLoop should be deleted!
//    private void deleteCorrespondingLoop(long devId,int moduleType) throws JSONException {
//        if(devId <= 0 || moduleType <= 0){
//            return;
//        }
//        int num = -1;
//        JSONObject jObject = null;
//        switch (moduleType) {
//            case CommonData.MODULE_TYPE_SPARKLIGHTING:
//                SparkLightingLoopFunc sFuc = new SparkLightingLoopFunc(dbHelper);
//                List<SparkLightingLoop> list = sFuc.getSparkLightingLoopByDevId(true, devId);
//                num = sFuc.deleteSparkLightingLoopByDevId(devId);
//                if(num > 0 && null != list && !list.isEmpty()){
//                    jObject = Util.generalJsonObject(CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, CommonData.CONFIG_TYPE_DEL);
//                    SparkLightingLoopConfig sparkLightingLoopConfig = SparkLightingLoopConfig.getInstance(dbHelper.mContext);
//                    JSONArray array = new JSONArray();
//                    sparkLightingLoopConfig.generateSparklightingLoopList(jObject, true, list,array);
//                    jObject.put(CommonData.JSON_COMMAND_CONFIGDATA, array);
//                    Util.sendZoneModify(jObject, dbHelper.mContext);
//                }
//                break;
//            case CommonData.MODULE_TYPE_BACNET:
//                BacnetLoopFunc bacnetLoopFuc = new BacnetLoopFunc(dbHelper);
//                num = bacnetLoopFuc.deleteBacnetLoopByDevId(devId);
//                break;
//            case CommonData.MODULE_TYPE_IPC:
//                new IpcStreamInfoFunc(dbHelper).deleteIpcStreamInfoByDevId(devId);
//                break;
//            case CommonData.MODULE_TYPE_WIFIIR:
//                //删除code，loop，info
//                //先删除info表记录
//                new IrInfoFunc(dbHelper).deleteIrInfoByDevId(devId);
//
//                //根据devid获取loop表的记录（一个ir 会挂很多个遥控器，所以后多个loop）
//                IrLoopFunc irLoopFuc =  new IrLoopFunc(dbHelper);
//                IrCodeFunc irCodeFuc = new IrCodeFunc(dbHelper);
//                List<IrLoop> irLoops = irLoopFuc.getIrLoopListByDevId(devId);
//                //根据loopid删除code
//                if(null != irLoops){
//                    for(IrLoop irLoop:irLoops){
//                        irCodeFuc.deleteIrCodeByLoopId(irLoop.mLoopId);
//                    }
//                }
//                //根据devid删除loop
//                irLoopFuc.deleteIrLoopByDevId(devId);
//                break;
//            case CommonData.MODULE_TYPE_WIFIAIR:
//                break;
//            case CommonData.MODULE_TYPE_WIFIRELAY:
//                RelayLoopFuc relayLoop = new RelayLoopFuc(dbHelper);
//                num = relayLoop.deleteRelayLoopByDevId(devId);
//                break;
//            case CommonData.MODULE_TYPE_WIFI485:
//                new Wifi485LoopFuc(dbHelper).deleteWifi485LoopByDevId(devId);
//                break;
//            case CommonData.MODULE_TYPE_WIREDZONE:
//                WiredZoneLoopFuc wiredZoneLoopFuc = new WiredZoneLoopFuc(dbHelper);
//                List<WiredZoneLoop> wiredZoneLoops =  wiredZoneLoopFuc.getWiredZoneLoopListByDevId(devId);
//                num = wiredZoneLoopFuc.deleteWiredZoneLoopByDevId(devId);
//                if(num > 0 && null != wiredZoneLoops && !wiredZoneLoops.isEmpty()){
//                    jObject = Util.generalJsonObject(CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, CommonData.CONFIG_TYPE_DEL);
//                    WiredZoneLoopConfig wiredZoneLoopConfig = WiredZoneLoopConfig.getInstance(dbHelper.mContext);
//                    JSONArray array = new JSONArray();
//                    wiredZoneLoopConfig.generateWiredZoneLoopList(jObject, wiredZoneLoops,array);
//                    jObject.put(CommonData.JSON_COMMAND_CONFIGDATA, array);
//                    Util.sendZoneModify(jObject, dbHelper.mContext);
//                }
//                break;
//            case CommonData.MODULE_TYPE_WIFI315M433M:
//                Wireless315M433MLoopFuc wireless315m433mLoopFuc = new Wireless315M433MLoopFuc(dbHelper);
//                List<Wireless315M433MLoop> wireless315m433mLoops =  wireless315m433mLoopFuc.getWireless315M433MLoopListByDevId(devId);
//                num = wireless315m433mLoopFuc.deleteWireless315M433MLoopByDevId(devId);
//                if(num > 0 && !wireless315m433mLoops.isEmpty()){
//                    jObject = Util.generalJsonObject(CommonData.JSON_COMMAND_MODULETYPE_315N433, CommonData.CONFIG_TYPE_DEL);
//                    Wireless315M433MLoopConfig wireless315m433mLoopConfig = Wireless315M433MLoopConfig.getInstance(dbHelper.mContext);
//                    JSONArray array = new JSONArray();
//                    wireless315m433mLoopConfig.generateWireless315M433MLoopList(jObject, wireless315m433mLoops,array);
//                    jObject.put(CommonData.JSON_COMMAND_CONFIGDATA, array);
//                    Util.sendZoneModify(jObject, dbHelper.mContext);
//                }
//                break;
//            case CommonData.MODULE_TYPE_IPVDP:
//                new IpvdpInfoFuc(dbHelper).deleteIpvdpInfoByDevId(devId);
//                IpvdpZoneLoopFuc ipvdpZoneLoopFuc = new IpvdpZoneLoopFuc(dbHelper);
//                List<IpvdpZoneLoop> ipvdpZoneLoops =  ipvdpZoneLoopFuc.getIpvdpZoneLoopListByDevId(devId);
//                num = ipvdpZoneLoopFuc.deleteIpvdpZoneLoopByDevId(devId);
//                if(num > 0 && null != ipvdpZoneLoops && !ipvdpZoneLoops.isEmpty()){
//                    jObject = Util.generalJsonObject(CommonData.JSON_COMMAND_MODULETYPE_IPVDP, CommonData.CONFIG_TYPE_DEL);
//                    IpvdpZoneLoopConfig ipvdpZoneLoopConfig = IpvdpZoneLoopConfig.getInstance(dbHelper.mContext);
//                    JSONArray array = new JSONArray();
//                    ipvdpZoneLoopConfig.generateIpvdpZoneLoopListInternal(jObject, ipvdpZoneLoops, array);
//                    jObject.put(CommonData.JSON_COMMAND_CONFIGDATA, array);
//                    Util.sendZoneModify(jObject, dbHelper.mContext);
//                }
//                break;
//            default:
//                break;
//        }
//    }
    //更新所有的离线状态
    public synchronized int updateAllPeripheralDeviceDisOnline() {
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISONLINE, CommonData.NOTONLINE);
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //修改(更新),参数ip_addr或mac_addr
    public synchronized int updateIpAddressByMacAddr(String ipAddr, String macAddr) {
        if (CommonUtils.ISNULL(ipAddr) || CommonUtils.ISNULL(macAddr)) {
            return -1;
        }

        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!CommonUtils.ISNULL(ipAddr)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_IP, ipAddr);
        }
        try {
            if (!CommonUtils.ISNULL(macAddr)) {
                num = db.update(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, values,
                        ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MAC + "=?",
                        new String[]{macAddr});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    public synchronized int updatePeripheralDeviceByPrimaryId(int primaryId, PeripheralDevice device) {
        if (null == device || primaryId <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!CommonUtils.ISNULL(device.mName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_NAME, device.mName);
        }
        if (device.mIsOnline == CommonData.ONLINE || device.mIsOnline == CommonData.NOTONLINE) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISONLINE, device.mIsOnline);
        }
        //对于update来讲，就是更新为用户已经添加的状态
        values.put(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISCONFIG, CommonData.HASCONFIG);

        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, values,
                    ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //查询(所有列表)
    public synchronized List<PeripheralDevice> getPeripheralDeviceAllList() {
        List<PeripheralDevice> peripheralDevices = new ArrayList<PeripheralDevice>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);

        while (cursor.moveToNext()) {
            PeripheralDevice device = fillDefaultDevice(cursor);
            peripheralDevices.add(device);
        }
        cursor.close();
        return peripheralDevices;
    }

    //查询 by Ip
    public synchronized PeripheralDevice getPeripheralDeviceByIp(String ipAddr) {
        if (null == ipAddr || "".equals(ipAddr)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE,
                null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_IP + "=?", new String[]{ipAddr}, null, null, null, null);
        PeripheralDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
        }
        cursor.close();
        return device;
    }

    //查询 primary id
    public synchronized PeripheralDevice getPeripheralByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE,
                null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID + "=?", new String[]{String.valueOf(primaryId)}, null, null, null, null);
        PeripheralDevice peripheralDevice = null;
        while (cursor.moveToNext()) {
            peripheralDevice = fillDefaultDevice(cursor);
            break;
        }
        cursor.close();
        return peripheralDevice;
    }

    //查询 by Mac
    public synchronized PeripheralDevice getPeripheralDeviceByMac(String macAddr) {
        if (null == macAddr || "".equals(macAddr)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MAC + "=?", new String[]{macAddr}, null, null, null, null);
        PeripheralDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
        }
        cursor.close();
        return device;
    }

    //查询,by Type
    public synchronized ArrayList<PeripheralDevice> getPeripheralDeviceByType(int type) {
        if (type < 1) {
            return null;
        }
        ArrayList<PeripheralDevice> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_TYPE + "=?", new String[]{String.valueOf(type)}, null, null, null, null);
        while (cursor.moveToNext()) {
            PeripheralDevice device = fillDefaultDevice(cursor);
            list.add(device);
        }
        cursor.close();
        return list;
    }

    private PeripheralDevice fillDefaultDevice(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        PeripheralDevice device = new PeripheralDevice();
        device.mPrimaryID = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PRIMARYID));
        device.mType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_TYPE));
        device.mName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_NAME));
        device.mIpAddr = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_IP));
        device.mMacAddr = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MAC));
        device.mPort = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_PORT));
        device.mIsConfig = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISCONFIG));
        device.mIsOnline = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_ISONLINE));
        device.mBacnetId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BACNETID));
        device.mBrandName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BRANDNAME));
        device.mMaskId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MASKID));
        device.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        device.mVersion = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_VERSION));
        return device;
    }

    //query dev_id for SparkingLoop Adding by ip or mac field
    public synchronized long getDeviceIdByIpOrMac(String ipAddr, String macAddr) {
        long dev_id = -1;
        if (CommonUtils.ISNULL(ipAddr) && CommonUtils.ISNULL(macAddr)) {
            return dev_id;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        if (!CommonUtils.ISNULL(ipAddr)) {
            cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                    ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_IP + "=?", new String[]{ipAddr}, null, null, null, null);
        } else {
            cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                    ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_MAC + "=?", new String[]{macAddr}, null, null, null, null);
        }
        while (cursor.moveToNext()) {
            dev_id = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
            break;
        }
        cursor.close();
        return dev_id;
    }

    public synchronized int getDeviceIdByBacnetId(int bacnetId) {
        int dev_id = -1;
        if (bacnetId <= 0) {
            return dev_id;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_PERIPHERALDEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PERIPHERALDEVICE_BACNETID + "=?",
                new String[]{String.valueOf(bacnetId)}, null, null, null, null);

        while (cursor.moveToNext()) {
            dev_id = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
            break;
        }
        cursor.close();
        return dev_id;
    }

    public List<PeripheralDevice> getNotConfigWifiModuleList() {
        List<PeripheralDevice> allPerDevice = getPeripheralDeviceAllList();
        List<PeripheralDevice> wifiModuleList = new ArrayList<PeripheralDevice>();
        if (null != allPerDevice) {
            for (int i = 0; i < allPerDevice.size(); ++i) {
                PeripheralDevice device = allPerDevice.get(i);
                if (CommonUtils.judgeIsWifiModule(device.mType)
                        //&& device.mMacAddr != null
                        && !CommonUtils.ISNULL(device.mIpAddr)
                        && device.mPort > 0 && device.mIsConfig == CommonData.NOTCONFIG) {
                    wifiModuleList.add(device);
                }
            }
        }
        return wifiModuleList;
    }

    public List<PeripheralDevice> getNotOnlineWifiModuleList() {
        List<PeripheralDevice> allPerDevice = getPeripheralDeviceAllList();
        List<PeripheralDevice> wifiModuleList = new ArrayList<PeripheralDevice>();
        if (null != allPerDevice) {
            for (int i = 0; i < allPerDevice.size(); ++i) {
                PeripheralDevice device = allPerDevice.get(i);
                if (CommonUtils.judgeIsWifiModule(device.mType)
                        //&& device.mMacAddr != null
                        && !CommonUtils.ISNULL(device.mIpAddr)
                        && device.mPort > 0 && device.mIsConfig == CommonData.HASCONFIG
                        && device.mIsOnline == CommonData.NOTONLINE) {
                    wifiModuleList.add(device);
                }
                /*else if(device.mType == CommonData.MODULE_TYPE_IPVDP){
                    wifiModuleList.add(device);
                }*/
            }
        }
        return wifiModuleList;
    }

    public List<PeripheralDevice> getWifiModuleList() {
        List<PeripheralDevice> allPerDevice = getPeripheralDeviceAllList();
        List<PeripheralDevice> wifiModuleList = new ArrayList<PeripheralDevice>();
        if (null != allPerDevice) {
            for (int i = 0; i < allPerDevice.size(); ++i) {
                PeripheralDevice device = allPerDevice.get(i);
                if (CommonUtils.judgeIsWifiModule(device.mType)
                        //&& device.mMacAddr != null
                        && !CommonUtils.ISNULL(device.mIpAddr)
                        && device.mPort > 0 && device.mIsConfig == CommonData.HASCONFIG) {
                    wifiModuleList.add(device);
                }
            }
        }
        return wifiModuleList;
    }

    public List<PeripheralDevice> getAllConfigWifiModuleList() {
        List<PeripheralDevice> allPerDevice = getPeripheralDeviceAllList();
        List<PeripheralDevice> wifiModuleList = new ArrayList<PeripheralDevice>();
        if (null != allPerDevice) {
            for (int i = 0; i < allPerDevice.size(); ++i) {
                PeripheralDevice device = allPerDevice.get(i);
                if (CommonUtils.judgeIsWifiModule(device.mType)
                        //&& device.mMacAddr != null
                        && !CommonUtils.ISNULL(device.mIpAddr)
                        && device.mPort > 0) {
                    wifiModuleList.add(device);
                }
            }
        }
        return wifiModuleList;
    }

    //获取最后一个module 类方法
    public synchronized static PeripheralDevice getLastDevice(Context context) {
        ArrayList<PeripheralDevice> devices = (ArrayList<PeripheralDevice>) (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralDeviceAllList();
        if (devices.size() == 0) {
            return null;
        }
        return devices.get(devices.size() - 1);
    }
}
