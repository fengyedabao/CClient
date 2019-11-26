package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.controllers.UIItem.UIItems;

/**
 * Created by H157925 on 16/6/11. 12:40
 * Email:Shodong.Sun@honeywell.com
 * 用于组织loop
 */
public class MenuDeviceLoopObject {
    public String section = "";//回路头名称
    public String room = "";//房间
    public String name = "";//名称
    public int roomId = 0;
    public int loopid = 0;//loopid
    public String loopType = "";//loop类型 relay switch 如果是Relay，可以选择灯光/Relay
    public boolean enable = false;//是否启用
    public int delaytime = 0;//单位是秒
    public boolean isDelay = false;

    //zone
    public String zoneType = "";//防区类型
    public String alarmType = "";//报警类型

    //IPVDP
    public boolean isEdit = true;//是否可以编辑

    //Bacnet
    public int bacnet_device_id = 0;
    public int bacnet_loop_id = 0;

    //Wifi 485
    public String wifi485_branchName = "";//品牌名称
    public int wifi485_port_id = 1;//端口名称
    public String wifi485_slave_address = "";//子网地址
    public int wifi485_loop_id = 0;//回路 Id

    public MenuDeviceLoopObject() {
    }

//    public MenuDeviceLoopObject(String section, String room, String name, int loopid, String loopType, boolean enable) {
//        this.section = section;
//        this.room = room;
//        this.name = name;
//        this.loopid = loopid;
//        this.loopType = loopType;
//        this.enable = enable;
//    }
//
//    //通过Parsel初始化
//    public MenuDeviceLoopObject(Parcel parcel) {
//        this.section = parcel.readString();
//        this.room = parcel.readString();
//        this.name = parcel.readString();
//        this.loopid = parcel.readInt();
//        this.loopType = parcel.readString();
//        this.enable = parcel.readInt() == 1;
//        this.isDelay = parcel.readInt() == 1;
//        this.delaytime = parcel.readInt();
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//
//    @Override
//    public void writeToParcel(Parcel parcel, int flags) {
//        parcel.writeString(section);
//        parcel.writeString(room);
//        parcel.writeString(name);
//        parcel.writeInt(loopid);
//        parcel.writeString(loopType);
//        parcel.writeInt(enable ? 1 : 0);
//        parcel.writeInt(isDelay ? 1 : 0);
//        parcel.writeInt(delaytime);
//    }
//
//    public static final Parcelable.Creator<UIItems> CREATOR = new Creator<UIItems>() {
//        public UIItems createFromParcel(Parcel source) {
//            return new UIItems(source);
//        }
//
//        public UIItems[] newArray(int size) {
//            return new UIItems[size];
//        }
//
//    };
}
