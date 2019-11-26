package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;

/**
 * Created by H157925 on 16/7/4. 13:04
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuDeviceIRIconItem implements Parcelable {
    public String IR_icon_name = "";//icon 名称 UI
    public String IR_icon_imageName = "";//icon image name  protocol

    public String ventilation_type = "";//新风中使用 用于区分新风的类型
    public String ventilation_type_value = "";//新风中使用 新风对应的值

    public int IR_icon_imageId = -1;//icon image
    public int IR_icon_imageSelectId = -1;//第四级页面选择后显示的数据

    public boolean IR_icon_select = false;//是否选中

    public boolean IR_icon_enable = false;//是否可点击

    public IrCode IR_iconCode = null;

    public MenuDeviceIRIconItem() {
    }


    /**
     * 进行比较
     *
     * @param uiitem
     * @return
     */
    public boolean compareIrImageName(MenuDeviceIRIconItem uiitem) {
        if (!this.IR_icon_imageName.startsWith(ModelEnum.DEVICE_IR_ADD_CUSTOMIZE) || !uiitem.IR_icon_imageName.startsWith(ModelEnum.DEVICE_IR_ADD_CUSTOMIZE)) {
            if (this.IR_icon_imageName.equalsIgnoreCase(uiitem.IR_icon_imageName)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (this.IR_icon_name.equalsIgnoreCase(uiitem.IR_icon_name)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public MenuDeviceIRIconItem(String IR_icon_name, String IR_icon_imageName, int IR_icon_imageId, int IR_icon_imageSelectId, boolean IR_icon_select) {
        this.IR_icon_name = IR_icon_name;
        this.IR_icon_imageId = IR_icon_imageId;
        this.IR_icon_imageName = IR_icon_imageName;
        this.IR_icon_imageSelectId = IR_icon_imageSelectId;
        this.IR_icon_select = IR_icon_select;
    }

    public MenuDeviceIRIconItem(Parcel parcel) {
        IR_icon_name = parcel.readString();
        IR_icon_imageName = parcel.readString();
        ventilation_type = parcel.readString();
        ventilation_type_value = parcel.readString();
        IR_icon_imageId = parcel.readInt();
        IR_icon_imageSelectId = parcel.readInt();
        IR_icon_select = parcel.readInt() == 1 ? true : false;
        IR_icon_enable = parcel.readInt() == 1 ? true : false;
        IR_iconCode = parcel.readParcelable(IrCode.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "menuDevice Ir icon item Uiitem :[ "
                + " IR_icon_name : " + IR_icon_name
                + " IR_icon_imageName : " + IR_icon_imageName
                + " IR_icon_image Id : " + IR_icon_imageId
                + " IR_icon_imageSelect Id : " + IR_icon_imageSelectId
                + " IR_icon_select : " + IR_icon_select
                + " IR_icon_enable : " + IR_icon_enable
                + " ] .";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(IR_icon_name);
        parcel.writeString(IR_icon_imageName);
        parcel.writeString(ventilation_type);
        parcel.writeString(ventilation_type_value);
        parcel.writeInt(IR_icon_imageId);
        parcel.writeInt(IR_icon_imageSelectId);
        parcel.writeInt(IR_icon_select ? 1 : 0);
        parcel.writeInt(IR_icon_enable ? 1 : 0);
        parcel.writeParcelable(IR_iconCode, flags);
    }

    public static Parcelable.Creator<MenuDeviceIRIconItem> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<MenuDeviceIRIconItem> CREATOR = new Creator<MenuDeviceIRIconItem>() {
        public MenuDeviceIRIconItem createFromParcel(Parcel source) {
            return new MenuDeviceIRIconItem(source);
        }

        public MenuDeviceIRIconItem[] newArray(int size) {
            return new MenuDeviceIRIconItem[size];
        }
    };
}
