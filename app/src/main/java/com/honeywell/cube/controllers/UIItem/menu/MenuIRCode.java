package com.honeywell.cube.controllers.UIItem.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/7/19. 10:05
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuIRCode implements Parcelable {

    public String name = "";
    public String imagename = "";
    public ArrayList<String> wifiirdata = new ArrayList<>();

    public MenuIRCode() {
    }

    public MenuIRCode(Parcel parcel) {
        name = parcel.readString();
        imagename = parcel.readString();
        wifiirdata = new ArrayList<>();
        parcel.readStringList(wifiirdata);
    }

    public boolean equalsIrCode(MenuIRCode code) {
        if (this.imagename.equalsIgnoreCase(code.imagename)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(imagename);
        parcel.writeStringList(wifiirdata);
    }

    public static Parcelable.Creator<MenuIRCode> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<MenuIRCode> CREATOR = new Creator<MenuIRCode>() {
        public MenuIRCode createFromParcel(Parcel source) {
            return new MenuIRCode(source);
        }

        public MenuIRCode[] newArray(int size) {
            return new MenuIRCode[size];
        }
    };
}
