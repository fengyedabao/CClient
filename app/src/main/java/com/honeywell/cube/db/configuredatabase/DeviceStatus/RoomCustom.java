package com.honeywell.cube.db.configuredatabase.DeviceStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/8/5. 10:27
 * Email:Shodong.Sun@honeywell.com
 */
public class RoomCustom implements Parcelable {
    public int pm2_5 = -1;
    public int co_2 = -1;
    public int temperature = -1;
    public int humidity = -1;

    public RoomCustom(){
    }

    public RoomCustom(Parcel parcel) {
        pm2_5 = parcel.readInt();
        co_2 = parcel.readInt();
        temperature = parcel.readInt();
        humidity = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(pm2_5);
        parcel.writeInt(co_2);
        parcel.writeInt(temperature);
        parcel.writeInt(humidity);
    }

    public static Parcelable.Creator<RoomCustom> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<RoomCustom> CREATOR = new Creator<RoomCustom>() {
        public RoomCustom createFromParcel(Parcel source) {
            return new RoomCustom(source);
        }

        public RoomCustom[] newArray(int size) {
            return new RoomCustom[size];
        }

    };

}
