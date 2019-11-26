package com.honeywell.cube.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.RoomEditActivity;
import com.honeywell.cube.adapter.CheckTextAdapter;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.ScanController;
import com.honeywell.cube.controllers.UIItem.ScanUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.controllers.menus.MenuModuleController;
import com.honeywell.cube.controllers.menus.MenuRuleController;
import com.honeywell.cube.controllers.menus.MenuScheduleController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milton on 16/6/20.
 */
public class DeviceHelper {
    public static List<BottomDialog.ItemBean> sRoomList;
    public static List<BottomDialog.ItemBean> sAlarmTypeList;
    public static List<BottomDialog.ItemBean> sZoneTypeList;
    public static List<BottomDialog.ItemBean> sLoopTypeList;
    public static List<BottomDialog.ItemBean> sBrandNameList;
    public static List<BottomDialog.ItemBean> sPortList;
    public static List<BottomDialog.ItemBean> sIpcTypeList;

    public static List<BottomDialog.ItemBean> sBacnetTypeList;
    public static List<BottomDialog.ItemBean> sACTemperatureList;
    public static List<BottomDialog.ItemBean> sACModeTypeList;
    public static List<BottomDialog.ItemBean> sTaskList;
    public static List<BottomDialog.ItemBean> sConditionList;
    public static List<BottomDialog.ItemBean> sRoomEnvironmentTypeList;
    public static List<BottomDialog.ItemBean> sRoomEnvironmentConditionList;
    public static List<BottomDialog.ItemBean> sAirQualityList;
    public static List<BottomDialog.ItemBean> sPM2_5List;
    public static List<BottomDialog.ItemBean> sTemperatureList;
    public static List<BottomDialog.ItemBean> sHumidityList;

    public static void addObject2Intent(Intent intent, String key, Object object) {
        Bundle bundle = new Bundle();
        if (object instanceof SparkLightingLoop) {
            SparkLightingLoop loop = (SparkLightingLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof Wireless315M433MLoop) {
            Wireless315M433MLoop loop = (Wireless315M433MLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof RelayLoop) {
            RelayLoop loop = (RelayLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof WiredZoneLoop) {
            WiredZoneLoop loop = (WiredZoneLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof IpvdpZoneLoop) {
            IpvdpZoneLoop loop = (IpvdpZoneLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof BacnetLoop) {
            BacnetLoop loop = (BacnetLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof Wifi485Loop) {
            Wifi485Loop loop = (Wifi485Loop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof BackaudioLoop) {
            BackaudioLoop loop = (BackaudioLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof IpcStreamInfo) {
            IpcStreamInfo info = (IpcStreamInfo) object;
            bundle.putParcelable(key, info);
        } else if (object instanceof IrLoop) {
            IrLoop loop = (IrLoop) object;
            bundle.putParcelable(key, loop);
        } else if (object instanceof MenuDeviceIRUIItem) {
            MenuDeviceIRUIItem o = (MenuDeviceIRUIItem) object;
            bundle.putParcelable(key, o);
        } else if (object instanceof ScanUIItem) {
            ScanUIItem o = (ScanUIItem) object;
            bundle.putParcelable(key, o);
        } else if (object instanceof VentilationLoop) {
            VentilationLoop o = (VentilationLoop) object;
            bundle.putParcelable(key, o);
        } else if (object instanceof MenuScheduleDeviceObject) {
            MenuScheduleDeviceObject o = (MenuScheduleDeviceObject) object;
            bundle.putParcelable(key, o);
        }
        intent.putExtras(bundle);
    }

    public static List<BottomDialog.ItemBean> getRoomList(Context context) {
//        if (sRoomList == null) {
//            sRoomList = new ArrayList<>();
        List<BottomDialog.ItemBean> roomList = new ArrayList<>();
        ArrayList<String> list = DeviceController.getLoopDetailRoom(context);
        ArrayList<Integer> listId = DeviceController.getLoopDetailRoomId(context);
        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                roomList.add(new BottomDialog.ItemBean(list.get(i), listId.get(i)));
            }
        }
        return roomList;
//        return sRoomList;
    }

    public static List<BottomDialog.ItemBean> getModuleList(Context context, int type) {
        List<BottomDialog.ItemBean> dataList = new ArrayList<>();
        ArrayList<MenuDeviceUIItem> list = MenuDeviceController.getAddModuleList(context, type);
        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                dataList.add(new BottomDialog.ItemBean(list.get(i).deviceName, list.get(i).peripheraDevice));
            }
        }
        return dataList;
    }

    public static List<BottomDialog.ItemBean> getBackaudioModuleList(Context context, int type) {
        List<BottomDialog.ItemBean> dataList = new ArrayList<>();
        ArrayList<MenuDeviceUIItem> list = MenuDeviceController.getAddModuleList(context, type);
        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                dataList.add(new BottomDialog.ItemBean(list.get(i).deviceName, list.get(i).backaudioDevice));
            }
        }
        return dataList;
    }

    public static List<BottomDialog.ItemBean> getAlarmTypeList(Context context) {
        if (sAlarmTypeList == null) {
            sAlarmTypeList = generateStringDataList(MenuDeviceController.getZoneAlarmList(context));
        }
        return sAlarmTypeList;
    }

    public static List<BottomDialog.ItemBean> getZoneTypeList(Context context) {
        if (sZoneTypeList == null) {
            sZoneTypeList = generateStringDataList(MenuDeviceController.getZoneTypeList(context));
        }
        return sZoneTypeList;
    }

    public static List<BottomDialog.ItemBean> getLoopTypeList(Context context) {
        if (sLoopTypeList == null) {
            sLoopTypeList = generateStringDataList(MenuDeviceController.getWifi485LooptypeList(context));
        }
        return sLoopTypeList;
    }

    public static List<BottomDialog.ItemBean> getBrandNameList(Context context) {
        if (sBrandNameList == null) {
            sBrandNameList = generateStringDataList(MenuDeviceController.getWifi485BranchNameList(context));
        }
        return sBrandNameList;
    }

    public static List<BottomDialog.ItemBean> getPortList(Context context) {
        if (sPortList == null) {
            sPortList = new ArrayList<>();
            sPortList.add(new BottomDialog.ItemBean("1", null));
            sPortList.add(new BottomDialog.ItemBean("2", null));
        }
        return sPortList;
    }

    public static List<BottomDialog.ItemBean> getIpcTypeList(Context context) {
        if (sIpcTypeList == null) {
            sIpcTypeList = generateStringDataList(MenuDeviceController.getIpcTypeList(context));
        }
        return sIpcTypeList;
    }

    public static List<BottomDialog.ItemBean> getBacnetTypeList(Context context) {
        if (sBacnetTypeList == null) {
            sBacnetTypeList = generateStringDataList(MenuModuleController.getBacnetTypeList(context));
        }
        return sBacnetTypeList;
    }

    public static List<BottomDialog.ItemBean> getACTemperatureList(Context context) {
        if (sACTemperatureList == null) {
            sACTemperatureList = new ArrayList<>();
            for (int i = 15; i < 35; i++) {
                sACTemperatureList.add(new BottomDialog.ItemBean(i + " ℃", i));
            }
        }
        return sACTemperatureList;
    }

    public static List<BottomDialog.ItemBean> getACModeTypeList(Context context) {
        if (sACModeTypeList == null) {
            sACModeTypeList = generateStringDataList(MenuDeviceController.getIR_AC_modeList(context));
        }
        return sACModeTypeList;
    }

    public static List<BottomDialog.ItemBean> getTaskList(Context context) {
        if (sTaskList == null) {
            sTaskList = new ArrayList<>();
            sTaskList.add(new BottomDialog.ItemBean(context.getString(R.string.main_tabbar_text_scenario), null));
            sTaskList.add(new BottomDialog.ItemBean(context.getString(R.string.main_tabbar_text_device), null));
        }
        return sTaskList;
    }

    public static List<BottomDialog.ItemBean> getConditionList(Context context) {
        if (sConditionList == null) {
            sConditionList = new ArrayList<>();
            sConditionList.add(new BottomDialog.ItemBean(context.getString(R.string.device_type_zone), null));
            sConditionList.add(new BottomDialog.ItemBean(context.getString(R.string.room_environment), null));
        }
        return sConditionList;
    }

    public static List<BottomDialog.ItemBean> getRoomEnvironmentTypeList(Context context) {
        if (sRoomEnvironmentTypeList == null) {
            sRoomEnvironmentTypeList = generateStringDataList(MenuRuleController.getRuleRoomTypeList(context));
        }
        return sRoomEnvironmentTypeList;
    }

    public static List<BottomDialog.ItemBean> getRoomEnvironmentConditionList(Context context) {
        if (sRoomEnvironmentConditionList == null) {
            sRoomEnvironmentConditionList = generateStringDataList(MenuRuleController.getRuleRoomTriggerList(context));
        }
        return sRoomEnvironmentConditionList;
    }

    public static List<BottomDialog.ItemBean> getAirQualityList(Context context) {
        if (sAirQualityList == null) {
            sAirQualityList = generateStringDataList(MenuRuleController.getRuleRoomValue(context, context.getString(R.string.rule_room_type_AirQ)));
        }
        return sAirQualityList;
    }

    public static List<BottomDialog.ItemBean> getPM25List(Context context) {
        if (sPM2_5List == null) {
            sPM2_5List = new ArrayList<>();
            sPM2_5List.add(new BottomDialog.ItemBean("80", 80));
            sPM2_5List.add(new BottomDialog.ItemBean("300", 300));
        }
        return sPM2_5List;
    }

    public static List<BottomDialog.ItemBean> getTemperatureList(Context context) {
        if (sTemperatureList == null) {
            sTemperatureList = new ArrayList<>();
            sTemperatureList.add(new BottomDialog.ItemBean("10", 10));
            sTemperatureList.add(new BottomDialog.ItemBean("40", 40));
        }
        return sTemperatureList;
    }

    public static List<BottomDialog.ItemBean> getHumidityList(Context context) {
        if (sHumidityList == null) {
            sHumidityList = new ArrayList<>();
            sHumidityList.add(new BottomDialog.ItemBean("10", 10));
            sHumidityList.add(new BottomDialog.ItemBean("80", 80));
        }
        return sHumidityList;
    }

    public static void initRoom(final SelectItem room) {
        if (room != null) {
            List<BottomDialog.ItemBean> list = DeviceHelper.getRoomList(room.getContext());
            room.setDataList(list);
            room.setName(R.string.room);
            room.setContent(list.size() > 1 ? list.get(1).mText : "");
            room.setOnItemClickListener(new SelectItem.OnItemClickListener() {
                @Override
                public void itemClick(View view, int position) {
                    if (position == 0) {
                        Intent intent = new Intent(room.getContext(), RoomEditActivity.class);
                        intent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_ADD);
                        room.getContext().startActivity(intent);
                    } else {
                        room.setContent(position, true);
                    }
                }
            });
            room.setOnCustomClickListener(new SelectItem.OnCustomClickListener() {
                @Override
                public void click(View view) {
                    List<BottomDialog.ItemBean> list = DeviceHelper.getRoomList(room.getContext());
                    room.setDataList(list);
                }
            });
        }
    }

    public static void initModuleData(final SelectItem module, final int type) {
        List<BottomDialog.ItemBean> list = DeviceHelper.getModuleList(module.getContext(), type);
        module.setDataList(list);
        module.setContent(list.size() > 1 ? list.get(1).mText : "");
        module.setOnCustomClickListener(new SelectItem.OnCustomClickListener() {
            @Override
            public void click(View view) {
                List<BottomDialog.ItemBean> list = DeviceHelper.getModuleList(module.getContext(), type);
                module.setDataList(list);
            }
        });
    }

    public static List<BottomDialog.ItemBean> getSensorType(Context context) {
        return generateStringDataList(ScanController.get433SensorType(context));
    }

    public static List<BottomDialog.ItemBean> generateStringDataList(ArrayList<String> list) {
        List<BottomDialog.ItemBean> dataList = new ArrayList<>();
        if (list != null) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                dataList.add(new BottomDialog.ItemBean(list.get(i), null));
            }
        }
        return dataList;
    }
}
