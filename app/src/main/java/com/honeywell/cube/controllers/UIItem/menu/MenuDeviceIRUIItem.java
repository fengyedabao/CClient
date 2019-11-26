package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/7/4. 11:12
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuDeviceIRUIItem implements Parcelable {

    /**
     * 第一级页面
     */
    public String IR_name = "";
    public int IR_image = -1;
    public String IR_type = "";

    /**
     * 二级界面
     */
    public String IR_room_name = "";//房间名称
    public int IR_room_id = -1;//房间Id;

    public String IR_Module_name = "";//模块名称
    public PeripheralDevice IR_module_device = null;//模块

    /**
     * 第三级界面
     */
    public IrLoop IR_loop = null;

    /**
     * 三级页面 Custom
     */
    public ArrayList<MenuDeviceIRIconItem> IR_iconItems = null;

    /**
     * 三级页面 Air controller
     */
    public int IR_AC_temperature = 15;
    public String IR_AC_mode = "";
    public String IR_AC_name = "";

    /**
     * 第四级页面 添加IRCodes 数组
     * 收到Event事件后，将获取到的Map添加到这个数组里，后期用于添加对应的设备
     */
    public ArrayList<MenuIRCode> IR_Code_List = new ArrayList<>();

    public void addMenuCodeList(MenuIRCode code, boolean isAC) {
        if (code == null) return;
        if (isAC) {
            IR_Code_List.add(code);
            return;
        }
        if (IR_Code_List.size() == 0) {
            IR_Code_List.add(code);
        } else {
            for (MenuIRCode irCode : IR_Code_List) {
                if (irCode.equalsIrCode(code)) {
                    irCode = code;
                    return;
                }
            }
            IR_Code_List.add(code);
        }
    }


    public MenuDeviceIRUIItem() {
    }

    public MenuDeviceIRUIItem(Parcel parcel) {
        IR_name = parcel.readString();
        IR_image = parcel.readInt();
        IR_type = parcel.readString();
        IR_room_name = parcel.readString();
        IR_room_id = parcel.readInt();
        IR_Module_name = parcel.readString();
        IR_module_device = parcel.readParcelable(PeripheralDevice.class.getClassLoader());
        IR_loop = parcel.readParcelable(IrLoop.class.getClassLoader());
        IR_iconItems = new ArrayList<>();
        parcel.readTypedList(IR_iconItems, MenuDeviceIRIconItem.CREATOR);
        IR_AC_temperature = parcel.readInt();
        IR_AC_mode = parcel.readString();
        IR_AC_name = parcel.readString();
        IR_Code_List = new ArrayList<>();
        parcel.readTypedList(IR_Code_List, MenuIRCode.CREATOR);
    }

    @Override
    public String toString() {
        return "menuDeviceIr Uiitem :[ "
                + " IR_name : " + IR_name
                + " IR_image : " + IR_image
                + " IR_type : " + IR_type
                + " IR_room_name : " + IR_room_name
                + " IR_room_id : " + IR_room_id
                + " IR_Module_name : " + IR_Module_name
                + " IR_module_device : " + IR_module_device.toString()
                + " IR_loop : " + IR_loop.toString()
                + " IR_AC_temperature : " + IR_AC_temperature
                + " IR_AC_mode : " + IR_AC_mode
                + " IR_AC_name : " + IR_AC_name
                + " ] .";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(IR_name);
        parcel.writeInt(IR_image);
        parcel.writeString(IR_type);
        parcel.writeString(IR_room_name);
        parcel.writeInt(IR_room_id);
        parcel.writeString(IR_Module_name);
        parcel.writeParcelable(IR_module_device, flags);
        parcel.writeParcelable(IR_loop, flags);
        parcel.writeTypedList(IR_iconItems);
        parcel.writeInt(IR_AC_temperature);
        parcel.writeString(IR_AC_mode);
        parcel.writeString(IR_AC_name);
        parcel.writeTypedList(IR_Code_List);
    }

    public static Parcelable.Creator<MenuDeviceIRUIItem> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<MenuDeviceIRUIItem> CREATOR = new Creator<MenuDeviceIRUIItem>() {
        public MenuDeviceIRUIItem createFromParcel(Parcel source) {
            return new MenuDeviceIRUIItem(source);
        }

        public MenuDeviceIRUIItem[] newArray(int size) {
            return new MenuDeviceIRUIItem[size];
        }
    };
}
