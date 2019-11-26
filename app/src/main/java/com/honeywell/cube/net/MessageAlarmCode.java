package com.honeywell.cube.net;

import android.content.Context;

import com.honeywell.cube.R;

/**
 * Created by H157925 on 16/7/11. 21:43
 * Email:Shodong.Sun@honeywell.com
 */
public class MessageAlarmCode {
    public static final int ALARM_CODE_UNDEFINED = 0; // 未定义错误

    // medical alarms
    public static final int ALARM_CODE_MEDICALAID = 1100; // 医疗救护
    public static final int ALARM_CODE_EMERGENCY = 1101; // 紧急求助

    // fire alarms
    public static final int ALARM_CODE_FIRE = 1110; // 火灾报警

    // panic alarms
    public static final int ALARM_CODE_PANIC = 1120; // 挟持
    public static final int ALARM_CODE_DURESS = 1121; // 挟持
    public static final int ALARM_CODE_DURESS_ACCESS = 1123; // 挟持
    public static final int ALARM_CODE_DURESS_EGRESS = 1124; // 挟持

    // burglar alarms
    public static final int ALARM_CODE_INTRUSION = 1130; // 小偷入侵
    public static final int ALARM_CODE_TEMPER = 1131; // 撬锁
    public static final int ALARM_CODE_PWDWRONG = 1132; // 密码错误

    // General Alarm
    public static final int ALARM_CODE_LOWBATTERY = 1140; // 低电量
    public static final int ALARM_CODE_DOOROPEN = 1141; // 门未关
    public static final int ALARM_CODE_ZONETROUBLE = 1142; // 设备故障

    // 24 Hour Non-Burglary
    public static final int ALARM_CODE_GAS = 1151; // 煤气泄漏

    //Supervisory
    public static final int ALARM_CODE_PM2P5 = 1200; // PM 2.5超标

    public static String getAlarmStringFromMessageID(Context context, int messageId) {
        String alarmStr = "";
        if (messageId == ALARM_CODE_UNDEFINED) {
            alarmStr = context.getString(R.string.alarm_unknown);
        } else if (messageId == ALARM_CODE_MEDICALAID) {
            alarmStr = context.getString(R.string.alarm_medicalaid);
        } else if (messageId == ALARM_CODE_EMERGENCY) {
            alarmStr = context.getString(R.string.alarm_emergency);
        } else if (messageId == ALARM_CODE_FIRE) {
            alarmStr = context.getString(R.string.alarm_fire);
        } else if (messageId == ALARM_CODE_PANIC) {
            alarmStr = context.getString(R.string.alarm_panic);
        } else if (messageId == ALARM_CODE_DURESS) {
            alarmStr = context.getString(R.string.alarm_panic);
        } else if (messageId == ALARM_CODE_DURESS_ACCESS) {
            alarmStr = context.getString(R.string.alarm_panic);
        } else if (messageId == ALARM_CODE_DURESS_EGRESS) {
            alarmStr = context.getString(R.string.alarm_panic);
        } else if (messageId == ALARM_CODE_INTRUSION) {
            alarmStr = context.getString(R.string.alarm_code_intrusion);
        } else if (messageId == ALARM_CODE_TEMPER) {
            alarmStr = context.getString(R.string.alarm_code_temper);
        } else if (messageId == ALARM_CODE_PWDWRONG) {
            alarmStr = context.getString(R.string.alarm_code_pwdwrong);
        } else if (messageId == ALARM_CODE_LOWBATTERY) {
            alarmStr = context.getString(R.string.alarm_code_lowbattery);
        } else if (messageId == ALARM_CODE_DOOROPEN) {
            alarmStr = context.getString(R.string.alarm_code_dooropen);
        } else if (messageId == ALARM_CODE_ZONETROUBLE) {
            alarmStr = context.getString(R.string.alarm_code_zonetrouble);
        } else if (messageId == ALARM_CODE_GAS) {
            alarmStr = context.getString(R.string.alarm_code_gas);
        } else if (messageId == ALARM_CODE_PM2P5) {
            alarmStr = context.getString(R.string.alarm_code_pm2p5);
        }
        return alarmStr;
    }
}
