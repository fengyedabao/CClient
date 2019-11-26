package com.honeywell.cube.controllers.UIItem.menu;

import android.graphics.AvoidXfermode;
import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/6/19. 09:52
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 模块首页显示参数
 */
public class MenuModuleUIItem implements Parcelable {
    //UI
    public String title = "";//名称
    public String version = "";//版本
    public String type = "";//类型
    public String ipAddr = "";//IP
    public boolean state = false;//状态，是否在线
    //spark lighting
    public int sub_gateway_id = 1;//子网 ID
    //bacnet
    public String bacnet_type = "";
    public int cube_bacnet_id = 1;
    public int bacnet_device_id = 1;
    //ipvdp
    public String hns_ip = "";
    public int ipvdp_roomId = 0;
    public String ipvdp_roomName = "";
    public ArrayList<MenuAddModuleIpvdpListObj> ipvdpListObjs = new ArrayList<>();

    public String deviceType = "";//用于区分moduleObject的类型 是peripheradevice 还是 BackAudiodevice
    public Object moduleObject = null;

    public MenuModuleUIItem() {
    }

    public MenuModuleUIItem(Parcel parcel) {
        title = parcel.readString();
        version = parcel.readString();
        type = parcel.readString();
        ipAddr = parcel.readString();
        state = parcel.readInt() == 1 ? true : false;
        sub_gateway_id = parcel.readInt();
        bacnet_type = parcel.readString();
        cube_bacnet_id = parcel.readInt();
        bacnet_device_id = parcel.readInt();
        hns_ip = parcel.readString();
        deviceType = parcel.readString();
        if (ModelEnum.MAIN_MODULE.equalsIgnoreCase(deviceType)) {
            moduleObject = parcel.readParcelable(PeripheralDevice.class.getClassLoader());
        } else if (ModelEnum.MAIN_BACKAUDIO.equalsIgnoreCase(deviceType)) {
            moduleObject = parcel.readParcelable(BackaudioDevice.class.getClassLoader());
        }
    }


    @Override
    public String toString() {
        return " ";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(version);
        parcel.writeString(type);
        parcel.writeString(ipAddr);
        parcel.writeInt(state ? 1 : 0);
        parcel.writeInt(sub_gateway_id);
        parcel.writeString(bacnet_type);
        parcel.writeInt(cube_bacnet_id);
        parcel.writeInt(bacnet_device_id);
        parcel.writeString(hns_ip);
        parcel.writeString(deviceType);

        if (ModelEnum.MAIN_MODULE.equalsIgnoreCase(deviceType)) {
            parcel.writeParcelable((PeripheralDevice) moduleObject, flags);
        } else if (ModelEnum.MAIN_BACKAUDIO.equalsIgnoreCase(deviceType)) {
            parcel.writeParcelable((BackaudioDevice) moduleObject, flags);
        }
    }

    public static Parcelable.Creator<MenuModuleUIItem> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<MenuModuleUIItem> CREATOR = new Creator<MenuModuleUIItem>() {
        public MenuModuleUIItem createFromParcel(Parcel source) {
            return new MenuModuleUIItem(source);
        }

        public MenuModuleUIItem[] newArray(int size) {
            return new MenuModuleUIItem[size];
        }
    };

}
