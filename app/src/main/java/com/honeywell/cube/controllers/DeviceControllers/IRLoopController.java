package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;
import android.util.Log;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuIRCode;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCodeFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/20. 15:54
 * Email:Shodong.Sun@honeywell.com
 */
public class IRLoopController {
    private static final String TAG = IRLoopController.class.getSimpleName();

    /**
     * 通过 loop 来获取对应的IRCode
     *
     * @param context
     * @param loop
     * @return
     */
    public static List<IrCode> getIRCodeArray(Context context, IrLoop loop) {
        return new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrCodeByLoopId((int) loop.mLoopSelfPrimaryId);
    }

    /**
     * 右上角列表 直接使用 Irloop 中的 loopName
     *
     * @param context
     * @param name    Device Controller 首页的name
     * @return
     */
    public static ArrayList<IrLoop> getIrLoopWithLoopType(Context context, String name) {
        ArrayList<Object> loops = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, name);
        ArrayList<IrLoop> returnValue = new ArrayList<>();
        if (ModelEnum.MAIN_IR_DVD.equalsIgnoreCase(name) ||
                ModelEnum.MAIN_IR_STB.equalsIgnoreCase(name) ||
                ModelEnum.MAIN_IR_TELEVISION.equalsIgnoreCase(name) ||
                ModelEnum.MAIN_IR_CUSTOMIZE.equalsIgnoreCase(name) ||
                ModelEnum.MAIN_IR_AC.equalsIgnoreCase(name)) {
            for (int i = 0; i < loops.size(); i++) {
                returnValue.add((IrLoop) loops.get(i));
            }
        }
        return returnValue;
    }

    /**
     * Device 界面  获取自定义界面的数据
     *
     * @param context
     */
    public static ArrayList<MenuDeviceIRIconItem> getCustomIrIcons(Context context, IrLoop loop) {
        ArrayList<MenuDeviceIRIconItem> arrayList = new ArrayList<>();
        if (loop == null) {
            Loger.print(TAG, "ssd get custom ir icons loop is null", Thread.currentThread());
            return arrayList;
        }
        //遥控器码
        ArrayList<IrCode> codesList = (ArrayList<IrCode>) new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrCodeByLoopId((int) loop.mLoopSelfPrimaryId);

        if (codesList.size() == 0) {
            //当前Custom没有学习任何ICon
            Loger.print(TAG, "ssd get custom ir icons 没有学习任何Icon", Thread.currentThread());
            return arrayList;
        } else {
            for (int i = 0; i < codesList.size(); i++) {
                IrCode code = codesList.get(i);
                MenuDeviceIRIconItem uiItem = new MenuDeviceIRIconItem();
                uiItem.IR_icon_name = DeviceManager.getNameWithProtocol(code.mName);
                uiItem.IR_icon_imageName = code.mImageName;
                DeviceManager.transferIRIconImage(DeviceManager.getImageNameWithprotocol(code.mImageName), uiItem);
                uiItem.IR_icon_enable = true;
                uiItem.IR_iconCode = code;
                arrayList.add(uiItem);
            }
            return arrayList;
        }
    }

    /**
     * Menu 界面和 Device 界面 获取各个界面对应的icon--上界面 4个按钮
     * DVD STB TV
     *
     * @param context
     * @param ir_type--在添加界面传入，在Device界面不传 赋值为null
     * @param loop--在添加界面                  loop可以传入null,或者在添加过程中生成的loop， 在Device界面，loop需要传入界面中获取的loop列表的最后一个
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> getIRViewUpIconItems(Context context, String ir_type, IrLoop loop) {
        ArrayList<IrCode> codes = null;
        if (loop != null && !"".equalsIgnoreCase(loop.mLoopType)) {
            codes = (ArrayList<IrCode>) IRLoopController.getIRCodeArray(context, loop);
        }
        if (ir_type == null) {
            ir_type = loop.mLoopType;
        } else {
            ir_type = DeviceManager.getIrProtocolFromName(ir_type);
        }
        if ("".equalsIgnoreCase(ir_type)) {
            Loger.print(TAG, "ssd get Ir view up icon type is null", Thread.currentThread());
            return new ArrayList<>();
        }
        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        ArrayList<String> imagenames = new ArrayList<>();
        if (ModelEnum.IR_TYPE_DVD_S.equalsIgnoreCase(ir_type) || ModelEnum.IR_TYPE_STB_S.equalsIgnoreCase(ir_type)) {
            //DVD界面和STB界面
            imagenames.add("ir_control_power.png");
            imagenames.add("ir_control_back.png");
            imagenames.add("ir_control_menu.png");
            imagenames.add("ir_control_home.png");

        } else if (ModelEnum.IR_TYPE_TV_S.equalsIgnoreCase(ir_type)) {
            //TV界面
            imagenames.add("ir_control_power.png");
            imagenames.add("ir_control_keypad01.png");
            imagenames.add("ir_control_menu.png");
            imagenames.add("ir_control_home.png");
        }

        if (imagenames.size() == 0) return returnValue;
        for (int i = 0; i < imagenames.size(); i++) {
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            String image = imagenames.get(i);
            item.IR_icon_name = null;
            item.IR_icon_imageName = DeviceManager.get_IR_ProtocolImageNameWithImageName(image);
            DeviceManager.transferIRIconImage(image, item);
            returnValue.add(item);
        }
        if (codes == null) {
            //获取学习界面参数
            return returnValue;
        }
        return updateIconList(returnValue, codes);//返回更新状态后的数据
    }

    /**
     * Menu 界面和 Device 界面 获取TV对应的icon--Key board
     * TV
     *
     * @param context
     * @param loop--在添加界面 loop可以传入null,或者在添加过程中生成的loop， 在Device界面，loop需要传入界面中获取的loop列表的最后一个
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> getIR_TV_keyboard(Context context, IrLoop loop) {
        ArrayList<IrCode> codes = null;
        if (loop != null && !"".equalsIgnoreCase(loop.mLoopType)) {
            codes = (ArrayList<IrCode>) IRLoopController.getIRCodeArray(context, loop);
        }
        ArrayList<String> imagenames = new ArrayList<>();
        imagenames.add("ir_control_1.png");
        imagenames.add("ir_control_2.png");
        imagenames.add("ir_control_3.png");
        imagenames.add("ir_control_4.png");
        imagenames.add("ir_control_5.png");
        imagenames.add("ir_control_6.png");
        imagenames.add("ir_control_7.png");
        imagenames.add("ir_control_8.png");
        imagenames.add("ir_control_9.png");
        imagenames.add("ir_control_asterisk.png");
        imagenames.add("ir_control_0.png");
        imagenames.add("ir_control_ok.png");

        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        for (int i = 0; i < imagenames.size(); i++) {
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            String image = imagenames.get(i);
            item.IR_icon_name = null;
            item.IR_icon_imageName = DeviceManager.get_IR_ProtocolImageNameWithImageName(image);
            DeviceManager.transferIRIconImage(image, item);
            returnValue.add(item);
        }
        if (codes == null) {
            //返回未更新状态的数据
            return returnValue;
        }
        return updateIconList(returnValue, codes);
    }

    /**
     * 获取中间板块 上下左右键
     *
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> getIRViewTVMiddleKeyboard(Context context, IrLoop loop) {
        ArrayList<IrCode> codes = null;
        if (loop != null && !"".equalsIgnoreCase(loop.mLoopType)) {
            codes = (ArrayList<IrCode>) IRLoopController.getIRCodeArray(context, loop);
        }
        ArrayList<String> imagenames = new ArrayList<>();
        imagenames.add("ir_control_up.png");
        imagenames.add("ir_control_left.png");
        imagenames.add("ir_control_tap.png");
        imagenames.add("ir_control_right.png");
        imagenames.add("ir_control_down.png");

        ArrayList<String> names = new ArrayList<>();
        names.add(context.getString(R.string.up));
        names.add(context.getString(R.string.left));
        names.add(context.getString(R.string.click));
        names.add(context.getString(R.string.right));
        names.add(context.getString(R.string.down));

        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        for (int i = 0; i < imagenames.size(); i++) {
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            String image = imagenames.get(i);
            item.IR_icon_name = names.get(i);
            item.IR_icon_imageName = DeviceManager.get_IR_ProtocolImageNameWithImageName(image);
            DeviceManager.transferTVKeyboardImageName(image, item);
            returnValue.add(item);
        }

        if (codes == null) {
            //返回未更新状态的数据
            return returnValue;
        }
        return updateIconList(returnValue, codes);
    }


    /**
     * Menu 界面和 Device 界面 获取DVD TV STB中底端的Button
     * DVD STB TV
     *
     * @param context
     * @param ir_type--在添加界面传入，在Device界面不传 赋值为null
     * @param loop--在添加界面  loop可以传入null,或者在添加过程中生成的loop， 在Device界面，loop需要传入界面中获取的loop列表的最后一个
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> getIRViewBottomIconItems(Context context, String ir_type, IrLoop loop) {
        ArrayList<IrCode> codes = null;
        if (loop != null && !"".equalsIgnoreCase(loop.mLoopType)) {
            codes = (ArrayList<IrCode>) IRLoopController.getIRCodeArray(context, loop);
        }
        if (ir_type == null) {
            ir_type = loop.mLoopType;
        } else {
            ir_type = DeviceManager.getIrProtocolFromName(ir_type);
        }
        if ("".equalsIgnoreCase(ir_type)) {
            Loger.print(TAG, "ssd get Ir view down icon type is null", Thread.currentThread());
            return new ArrayList<>();
        }


        ArrayList<String> imagenames = new ArrayList<>();
        if (ModelEnum.IR_TYPE_STB_S.equalsIgnoreCase(ir_type)) {
            imagenames.add("ir_control_volume_down.png");
            imagenames.add("ir_control_volume_up.png");
            imagenames.add("");
            imagenames.add("");
            imagenames.add("");
            imagenames.add("");
            imagenames.add("");
            imagenames.add("");
        } else if (ModelEnum.IR_TYPE_TV_S.equalsIgnoreCase(ir_type)) {
            imagenames.add("ir_control_channel_up.png");
            imagenames.add("ir_control_volume_up.png");
            imagenames.add("ir_control_mute.png");
            imagenames.add("ir_control_change.png");
            imagenames.add("ir_control_channel_down.png");
            imagenames.add("ir_control_volume_down.png");
            imagenames.add("");
            imagenames.add("ir_control_back.png");
        } else if (ModelEnum.IR_TYPE_DVD_S.equalsIgnoreCase(ir_type)) {
            imagenames.add("ir_control_volume_down.png");
            imagenames.add("ir_control_volume_up.png");
            imagenames.add("ir_control_mute.png");
            imagenames.add("ir_control_change.png");
            imagenames.add("ir_control_fast_reverse.png");
            imagenames.add("ir_control_play.png");
            imagenames.add("ir_control_fast_forward.png");
            imagenames.add("ir_control_dvd_out.png");
        }

        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        for (int i = 0; i < imagenames.size(); i++) {
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            String image = imagenames.get(i);
            item.IR_icon_name = null;
            item.IR_icon_imageName = DeviceManager.get_IR_ProtocolImageNameWithImageName(image);
            DeviceManager.transferIRIconImage(image, item);
            returnValue.add(item);
        }
        if (codes == null) {
            return returnValue;
        }
        return updateIconList(returnValue, codes);
    }

    /**
     * 更新 Icon 状态
     *
     * @param iconItems
     * @param codes
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> updateIconList(ArrayList<MenuDeviceIRIconItem> iconItems, ArrayList<IrCode> codes) {
        for (int i = 0; i < codes.size(); i++) {
            IrCode code = codes.get(i);
            for (int j = 0; j < iconItems.size(); j++) {
                MenuDeviceIRIconItem item = iconItems.get(j);
                if (item.IR_icon_imageName.equalsIgnoreCase(code.mImageName)) {
                    item.IR_iconCode = code;
                    item.IR_icon_enable = true;
                }
            }
        }
        return iconItems;
    }


    /**
     * 发送IR命令
     *
     * @param context
     * @param loop    -- 对应的IRLoop
     */
    public static void sendIRMessage(Context context, IrLoop loop, MenuDeviceIRIconItem item) {
        if (loop == null) {
            Loger.print(TAG, "ssd sendIRMessage loop is null", Thread.currentThread());
            return;
        }

        IrCode code = item.IR_iconCode;
        String imageName = item.IR_icon_imageName;
        //获取Periphera
        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);
        if (device == null) {
            Loger.print(TAG, "ssd sendIRMessage PeripheralDevice is null", Thread.currentThread());
            return;
        }
        //是否学习过
        if (code == null) {
            //发送通知 该按键未学习
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IR_NOT_STUDY, null));
            return;
        }

        ArrayList<Object> iRData = new ArrayList<>();
        //查询code
        Map<String, Object> map01 = new HashMap<>();
        map01.put("data", code.mData1);

        Map<String, Object> map02 = new HashMap<>();
        map02.put("data", code.mData2);
        iRData.add(map01);
        iRData.add(map02);


        //发送命令
        String message = MessageManager.getInstance(context).sendIRCode(imageName, device.mMacAddr, iRData);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);

    }

    /**
     * 发送空调红外数据
     *
     * @param context
     * @param code
     */
    public static void sendIRAirController(Context context, IrCode code) {
        if (code == null) {
            Loger.print(TAG, "ssd send ir ac code is null", Thread.currentThread());
            return;
        }
        IrLoop loop = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrLoopByPrimaryId(code.mLoopId);
        if (loop == null) {
            Loger.print(TAG, "ssd send ir loop is null", Thread.currentThread());
        }
        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

        if (device == null) {
            Loger.print(TAG, "ssd send ir device is null", Thread.currentThread());
        }

        String imageName = "custom";
        ArrayList<Object> codes = new ArrayList<>();
        Map<String, Object> item01 = new HashMap<>();
        item01.put("data", code.mData1);
        codes.add(item01);
        Map<String, Object> item02 = new HashMap<>();
        item02.put("data", code.mData2);
        codes.add(item02);

        //发送命令
        String message = MessageManager.getInstance(context).sendIRCode(imageName, device.mMacAddr, codes);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 处理编辑后返回的数据
     *
     * @param context
     * @param body
     * @throws JSONException
     */
    public static void handleIRLoopConfigDeviceWithBody(Context context, JSONObject body) throws JSONException {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configtype = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configtype)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_IR, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_IR, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }
        }

        IrLoopFunc func = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        IrCodeFunc codeFunc = new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configtype)) {
            //add module
            PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong(CommonData.JSON_COMMAND_PRIMARYID));
            long dev_id = 0;
            if (moduleDevice != null) {
                dev_id = moduleDevice.mPrimaryID;
            }

            IrLoop loop = new IrLoop();
            loop.mLoopSelfPrimaryId = body.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
            loop.mModulePrimaryId = dev_id;
            loop.mLoopName = body.optString(CommonData.JSON_COMMAND_ALIAS);
            loop.mRoomId = body.optInt(CommonData.JSON_COMMAND_ROOMID);
            loop.mLoopId = body.optInt(CommonData.JSON_COMMAND_LOOPID);
            loop.mLoopType = body.optString(CommonData.JSON_COMMAND_TYPE);
            func.addIrLoop(loop);

            //红外码
            JSONArray codeArray = body.optJSONArray("codeloopmap");
            if (codeArray != null && codeArray.length() != 0) {
                for (int i = 0; i < codeArray.length(); i++) {
                    JSONObject object = (JSONObject) codeArray.get(i);
                    IrCode code = new IrCode();
                    //这个部分和iOS有一点不一致 iOS中获取——ID是和mloopId一致
                    code.mId = object.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                    code.mLoopId = body.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                    code.mName = object.optString(CommonData.JSON_COMMAND_NAME);
                    code.mImageName = object.optString(CommonData.JSON_COMMAND_IMAGENAME);

                    JSONArray dataArra = object.getJSONArray(CommonData.JSON_COMMAND_WIFIIRDATA);
                    if (dataArra.length() == 2) {
                        code.mData1 = ((JSONObject) dataArra.get(0)).getString(CommonData.JSON_COMMAND_DATA);
                        code.mData2 = ((JSONObject) dataArra.get(1)).getString(CommonData.JSON_COMMAND_DATA);
                    }
                    new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context)).addIrCode(code);
                }
            }
        } else if ("delete".equalsIgnoreCase(configtype)) {
            //删除loop
            func.deleteIrLoopByPrimaryId(body.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            //删除code
            codeFunc.deleteIrCodeByLoopId(body.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_IR, context.getString(R.string.operation_success_tip)));
            return;
        } else if ("modify".equalsIgnoreCase(configtype)) {
            IrLoop loop = func.getIrLoop(body.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            loop.mLoopName = body.optString(CommonData.JSON_COMMAND_ALIAS);
            loop.mRoomId = body.optInt(CommonData.JSON_COMMAND_ROOMID);

            func.updateIrLoop(loop);
        }

        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_IR, context.getString(R.string.operation_success_tip)));
    }

    /**
     * 处理学习Ir code
     *
     * @param context
     * @param body
     */
    public static void handleStudyIRDeviceWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IR_STUDY, false, ModelEnum.DEVICE_TYPE_IR_CUSTOMIZE, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }
        Loger.print(TAG, "ssd  *******************  study ir code success", Thread.currentThread());
        //学码
        if ("study".equalsIgnoreCase(body.optString("ircommand"))) {
            ArrayList<String> list = new ArrayList<>();
            JSONArray array = body.optJSONArray("wifiirdata");
            if (array == null || array.length() == 0) {
                Loger.print(TAG, "ssd study ir wifi ir data is null", Thread.currentThread());
            } else {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    if (object != null) {
                        String data = object.optString("data");
                        list.add(data);
                    }
                }
            }
            MenuIRCode code = new MenuIRCode();
            code.name = body.optString("name");
            code.imagename = body.optString("imagename");
            code.wifiirdata = list;
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IR_STUDY, true, code, context.getString(R.string.operation_success_tip)));
        }
    }

    /**
     * 发送控制 直接返回成功 失败
     *
     * @param context
     * @param body
     */
    public static void handleSendIrWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IR_SEND, false, ModelEnum.DEVICE_TYPE_IR_CUSTOMIZE, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IR_SEND, true, ModelEnum.DEVICE_TYPE_IR_CUSTOMIZE, "操作成功"));
    }
}
