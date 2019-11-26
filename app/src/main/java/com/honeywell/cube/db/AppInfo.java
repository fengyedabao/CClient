package com.honeywell.cube.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by H157925 on 16/4/27. 15:49
 * Email:Shodong.Sun@honeywell.com
 */
public class AppInfo implements Parcelable {
    //系统
    public String deviceToken = "";
    public String version = ""; //App 版本
    public String database_version = "";//数据库版本
    public String router_ssid_password = "";
    public String all_header_fields_cookie = "";

    // 用户
    public String phone_prefix = "";
    public String username = "";
    public String password = "";
    public String nickname = "";
    public int phoneId = -1;
    public int deviceId = -1;

    // Cube
    public String cube_location = "";// city
    public String user_image_path = "";
    public int current_scenario_id;
    public String cube_ip = "";
    public String cube_mac = "";//新增
    public int cube_port = -1;
    public String cube_local_nickname = "";
    public String cube_local_id = "";
    public String cube_local_password = "";

    //后加的
    public int current_security_status = 0;//当前安全状态
    public int cube_voice_recognize = 0;//语音识别
    public String cube_version = "";//Cube 版本

    public String last_read_time = "";
    public int online = -1;

    public AppInfo() {
    }

    public AppInfo(Parcel parcel) {
        database_version = parcel.readString();
        cube_location = parcel.readString();
        deviceToken = parcel.readString();
        version = parcel.readString();
        phone_prefix = parcel.readString();
        username = parcel.readString();
        password = parcel.readString();
        nickname = parcel.readString();
        phoneId = parcel.readInt();
        deviceId = parcel.readInt();
        user_image_path = parcel.readString();
        current_scenario_id = parcel.readInt();
        cube_ip = parcel.readString();
        cube_port = parcel.readInt();
        cube_local_nickname = parcel.readString();
        cube_local_id = parcel.readString();
        cube_local_password = parcel.readString();
        all_header_fields_cookie = parcel.readString();
        router_ssid_password = parcel.readString();

        current_security_status = parcel.readInt();
        cube_voice_recognize = parcel.readInt();
        cube_version = parcel.readString();

        last_read_time = parcel.readString();
        online = parcel.readInt();
    }

    public AppInfo(String deviceToken, String database_version, String cubeLocation, String version, String phone_prefix,
                   String username, String password, String nickname, int phoneId,
                   int deviceId, String user_image_path, int current_scenario_id,
                   String cube_ip, int cube_port, String cube_local_nickname,
                   String cube_local_id, String cube_local_password,
                   String all_header_fields_cookie, String router_ssid_password,
                   int current_security_status, int cube_voice_recognize, String cube_version, String last_read_time, int online
    ) {
        super();
        this.database_version = database_version;
        this.cube_location = cubeLocation;
        this.deviceToken = deviceToken;
        this.version = version;
        this.phone_prefix = phone_prefix;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.phoneId = phoneId;
        this.deviceId = deviceId;
        this.user_image_path = user_image_path;
        this.current_scenario_id = current_scenario_id;
        this.cube_ip = cube_ip;
        this.cube_port = cube_port;
        this.cube_local_nickname = cube_local_nickname;
        this.cube_local_id = cube_local_id;
        this.cube_local_password = cube_local_password;
        this.all_header_fields_cookie = all_header_fields_cookie;
        this.router_ssid_password = router_ssid_password;

        this.current_security_status = current_security_status;
        this.cube_voice_recognize = cube_voice_recognize;
        this.cube_version = cube_version;
        this.last_read_time = last_read_time;
        this.online = online;
    }

    public static Parcelable.Creator<AppInfo> getCreator() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "AppInfo [deviceToken=" + deviceToken + ", version=" + version
                + ", phone_prefix=" + phone_prefix + ", username=" + username
                + ", password=" + password + ", nickname=" + nickname
                + ", phoneId=" + phoneId + ", deviceId=" + deviceId
                + ", user_image_path=" + user_image_path + ", current_scenario_id=" + current_scenario_id
                + ", database_version=" + database_version + ", cube_location=" + cube_location
                + ", cube_ip=" + cube_ip + ", cube_port=" + cube_port
                + ", cube_local_nickname=" + cube_local_nickname + ", cube_local_id=" + cube_local_id
                + ", cube_local_password=" + cube_local_password + ", all_header_fields_cookie=" + all_header_fields_cookie
                + ", router_ssid_password=" + router_ssid_password
                + ", current_security_status=" + current_security_status + ", cube_voice_recognize=" + cube_voice_recognize
                + ", last_read_time=" + last_read_time + ", online=" + online
                + ", cube_version=" + cube_version + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(database_version);
        parcel.writeString(cube_location);
        parcel.writeString(deviceToken);
        parcel.writeString(version);
        parcel.writeString(phone_prefix);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(nickname);
        parcel.writeInt(phoneId);
        parcel.writeInt(deviceId);
        parcel.writeString(user_image_path);
        parcel.writeInt(current_scenario_id);
        parcel.writeString(cube_ip);
        parcel.writeInt(cube_port);

        parcel.writeString(cube_local_nickname);
        parcel.writeString(cube_local_id);
        parcel.writeString(cube_local_password);
        parcel.writeString(all_header_fields_cookie);
        parcel.writeString(router_ssid_password);

        parcel.writeInt(current_security_status);
        parcel.writeInt(cube_voice_recognize);
        parcel.writeString(cube_version);

        parcel.writeString(last_read_time);
        parcel.writeInt(online);
    }

    public static final Parcelable.Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}
