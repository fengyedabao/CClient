package com.honeywell.cube.db.configuredatabase.DeviceStatus;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/5/14. 17:12
 * Email:Shodong.Sun@honeywell.com
 */
public class BackAudioCustom implements Parcelable {
    public String power = "";//0n/off
    public String mute = "";//on/off
    public String singlecycle = "";// on/off
    public String playstatus = "";//play/pause
    public String source = "";//mp3/phone
    public int volume = 0;
    public String songname = "";

    public int allplaytime = 0;
    public String allplaytimeStr = "";
    public int playtime = 0;
    public String playTimeStr = "";
    public String setPlayTimeDateString = "";

    public BackAudioCustom() {
    }

    public BackAudioCustom(Parcel parcel) {
        power = parcel.readString();
        mute = parcel.readString();
        singlecycle = parcel.readString();
        playstatus = parcel.readString();
        source = parcel.readString();
        volume = parcel.readInt();
        songname = parcel.readString();
        allplaytime = parcel.readInt();
        allplaytimeStr = parcel.readString();
        playtime = parcel.readInt();
        playTimeStr = parcel.readString();
        setPlayTimeDateString = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(power);
        parcel.writeString(mute);
        parcel.writeString(singlecycle);
        parcel.writeString(playstatus);
        parcel.writeString(source);
        parcel.writeInt(volume);
        parcel.writeString(songname);
        parcel.writeInt(allplaytime);
        parcel.writeString(allplaytimeStr);
        parcel.writeInt(playtime);
        parcel.writeString(playTimeStr);
        parcel.writeString(setPlayTimeDateString);
    }

    public static Parcelable.Creator<BackAudioCustom> getCreator() {
        return CREATOR;
    }

    public static final Parcelable.Creator<BackAudioCustom> CREATOR = new Creator<BackAudioCustom>() {
        public BackAudioCustom createFromParcel(Parcel source) {
            return new BackAudioCustom(source);
        }

        public BackAudioCustom[] newArray(int size) {
            return new BackAudioCustom[size];
        }

    };
}
