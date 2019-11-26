package com.honeywell.cube.controllers.UIItem;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/6/16. 09:10
 * Email:Shodong.Sun@honeywell.com
 */
public class IPCameraInfo implements Parcelable {
    public int video_width = 0;//视频宽度
    public int video_height = 0;//视频高度

    //call 页面参数
    public String call_type = "";
    public String call_name = "";
    public String call_session_id = "";

    public IPCameraInfo() {
    }

    public IPCameraInfo(Parcel parcel) {
        video_width = parcel.readInt();
        video_height = parcel.readInt();

        call_type = parcel.readString();
        call_name = parcel.readString();
        call_session_id = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(video_width);
        dest.writeInt(video_height);
        dest.writeString(call_type);
        dest.writeString(call_name);
        dest.writeString(call_session_id);
    }

    @Override
    public String toString() {
        return "IPCameraInfo [video_width = " + video_width
                + ", video_height = " + video_height
                + ", call_type = " + call_type
                + ", call_name = " + call_name
                + ", call_session_id = " + call_session_id
                + " ]";
    }

    public static final Parcelable.Creator<IPCameraInfo> CREATOR = new Creator<IPCameraInfo>() {
        public IPCameraInfo createFromParcel(Parcel source) {
            return new IPCameraInfo(source);
        }

        public IPCameraInfo[] newArray(int size) {
            return new IPCameraInfo[size];
        }

    };
}
