package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/8/5. 11:25
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeBaseConfig implements Parcelable {
    public int primaryid = -1;
    public String conf_value = "";
    public String conf_name = "";

    public CubeBaseConfig() {
    }

    public CubeBaseConfig(int primaryid, String conf_value,
                          String conf_name) {
        this.primaryid = primaryid;
        this.conf_value = conf_value;
        this.conf_name = conf_name;
    }

    public CubeBaseConfig(Parcel parcel) {
        primaryid = parcel.readInt();
        conf_value = parcel.readString();
        conf_name = parcel.readString();
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
        parcel.writeInt(primaryid);
        parcel.writeString(conf_value);
        parcel.writeString(conf_name);
    }

    public static final Parcelable.Creator<CubeBaseConfig> CREATOR = new Creator<CubeBaseConfig>() {
        public CubeBaseConfig createFromParcel(Parcel source) {
            return new CubeBaseConfig(source);
        }

        public CubeBaseConfig[] newArray(int size) {
            return new CubeBaseConfig[size];
        }
    };
}
