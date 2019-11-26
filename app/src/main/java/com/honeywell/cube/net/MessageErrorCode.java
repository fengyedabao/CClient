package com.honeywell.cube.net;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.Loger.Loger;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by H157925 on 16/5/1. 15:00
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个文件定义了网络请求中的错误代码
 */
public class MessageErrorCode {

    /**
     * Http 错误类型
     */
    private static final String ERROR_TYPE = "errorType";

    private static final String ERROR_TYPE_INVALID_MESSAGE_FORMART = "InvalidMessageFormat";//数据格式错误
    private static final String ERROR_TYPE_INVALID_LANGUAGE = "InvalidLanguage";//语言错误
    private static final String ERROR_TYPE_INVALID_PHONE_NUMBER = "InvalidPhoneNumber";//手机号不正确
    private static final String ERROR_TYPE_INVALID_EMAIL = "InvalidEmail";//邮箱不正确
    private static final String ERROR_TYPE_VCODE_TOO_FREQUENT = "VCodeTooFrequent";//验证码请求过于频繁
    private static final String ERROR_TYPE_VCODE_FAILED = "VCodeFailed";//验证码请求失败

    private static final String ERROR_TYPE_INVALID_NAME = "InvalidName";//用户名不符合要求
    private static final String ERROR_TYPE_INVALID_PWD = "InvalidPassword";//密码不符合要求
    private static final String ERROR_TYPE_PHONE_REGISTERED = "PhoneNumberAlreadyRegistered";//手机号已注册
    private static final String ERROR_TYPE_INVALID_PHONE_VCODE = "InvalidPhoneVCode";//手机验证码错误
    private static final String ERROR_TYPE_EMAIL_REGISTERED = "EmailAlreadyRegistered";//邮箱已注册
    private static final String ERROR_TYPE_INVALID_EMAIL_VCODE = "InvalidEmailVCode";//邮箱验证码错误

    private static final String ERROR_TYPE_LOGIN_FAILED = "LoginFailed";//登录失败
    private static final String ERROR_TYPE_INVALID_PHONE_UUID = "InvalidPhoneUuid";//手机唯一识别码无效
    private static final String ERROR_TYPE_INVALID_PHONE_TYPE = "InvalidPhoneType";//手机类型无效
    private static final String ERROR_TYPE_INVALID_PUSH_VENDOR = "InvalidPushVendor";//推送码无效
    private static final String ERROR_TYPE_INVALID_PUSH_ID = "InvalidPushId";//推送ID无效

    private static final String ERROR_TYPE_PWD_ERROR = "wrong password";//密码错误

    private static final String ERROR_TYPE_BIND_PWD_ERROR = "PasswordError";//绑定密码错误
    private static final String ERROR_TYPE_CAN_NOT_BIND = "CannotBind";//绑定密码错误


    // general error codes
    public static final int MESSAGE_ERROR_CODE_OK = 0;
    public static final int MESSAGE_ERROR_CODE_INVALID_PARAM = 1;
    public static final int MESSAGE_ERROR_CODE_TIMEOUT = 2;


    //auto
    public static final int MESSAGE_ERROR_CODE_AUTH_AUTHED = 100;//已经认证
    public static final int MESSAGE_ERROR_CODE_AUTH_WRONGPWD = 101;//用户名或者密码错误
    public static final int MESSAGE_ERROR_CODE_AUTH_REQUIRED = 102;//需要认证


    //SparkLighting specified error
    public static final int MESSAGE_ERROR_CODE_NO_LOOP = 200;// No the loop
    public static final int MESSAGE_ERROR_CODE_ERRORPARAM = 201;// Error param.
    public static final int MESSAGE_ERROR_CODE_EXIST_LOOP = 202;// The loop has already exist.
    public static final int MESSAGE_ERROR_CODE_EXIST_DEVICE = MESSAGE_ERROR_CODE_EXIST_LOOP;
    public static final int MESSAGE_ERROR_CODE_OPERATION_FAIL = 203;//  This operation failed.
    public static final int MESSAGE_ERROR_CODE_PWDERROR = 204;//   pwd error
    public static final int MESSAGE_ERROR_CODE_NOMMCCARD = 205;//   no mmc card
    public static final int MESSAGE_ERROR_CODE_LOOPHASBEENREF = 206;// loop has been ref by such as scenario,triggermrule and so on.
    public static final int MESSAGE_ERROR_CODE_LOOPEXCEEDCOUNTS = 207;//   loop,trigger,schedule tule has been exceed max counts
    public static final int MESSAGE_ERROR_CODE_NOTHISPARAMDEVICE = 208;//   config trigger rule,this room do not has  device for param


    //security center service error code
    public static final int MESSAGE_ERROR_CODE_SECURITY_ZONE_ABNORMAL = 300;// zone abnormal when arm
    public static final int MESSAGE_ERROR_CODE_SECURITY_PWD_INVALID = 301;// pwd wrong when disarm
    public static final int MESSAGE_ERROR_CODE_SECURITY_NOSUCH_SCENARIO = 302;// can't find specified scenario

    //update error code
    public static final int MESSAGE_ERROR_CODE_UPGRADE_UNKOWN = 500;// unknown error
    public static final int MESSAGE_ERROR_CODE_UPGRADE_FILE_ER = 501;// file error, ex. checksum wrong
    public static final int MESSAGE_ERROR_CODE_UPGRADE_FLASH_ER = 502;// flash error
    public static final int MESSAGE_ERROR_CODE_UPGRADE_HW_ER = 503;// hardware error
    public static final int MESSAGE_ERROR_CODE_UPGRADE_NO_VER = 504;//  no new version
    public static final int MESSAGE_ERROR_CODE_UPGRADE_DOING = 508;// doing upgrade
    public static final int MESSAGE_ERROR_CODE_UPGRADE_FINISH = 509;// upgrade finished
    public static final int MESSAGE_ERROR_CODE_UPGRADE_REBOOT = 510;// reboot for upgrade
    public static final int MESSAGE_ERROR_CODE_RECOVERY_REBOOT = 511;// reboot for recovery
    public static final int MESSAGE_ERROR_CODE_RECOVERY_OK = 512;// recovery ok
    public static final int MESSAGE_ERROR_CODE_RECOVERY_FAIL = 513;// recovery failed
    public static final int MESSAGE_ERROR_CODE_BACKUP_OK = 514;// backup OK
    public static final int MESSAGE_ERROR_CODE_BACKUP_FAILED = 515;// backup failed


    // IPC error code
    public static final int MESSAGE_ERROR_CODE_IPC_MAX_STREAM = 600;// reach max support
    public static final int MESSAGE_ERROR_CODE_IPC_PLAY_ERR = 601;// reach max support

    // room info query errors
    public static final int MESSAGE_ERROR_CODE_NO_ROOM_DEVICEVALUE = 700;// unknown error


    public static String transferErrorCode(Context context, int errorCode) {
        String errorStr = "未知错误";
        if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_INVALID_PARAM) {
            //参数错误
            errorStr = context.getString(R.string.error_invalid_param);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_TIMEOUT) {
            //请求超时
            errorStr = context.getString(R.string.error_request_timeout);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_AUTH_AUTHED) {
            //已经认证了
            errorStr = context.getString(R.string.error_already_authed);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_AUTH_WRONGPWD) {
            //用户名，密码验证失败
            errorStr = context.getString(R.string.error_password_wrong);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_AUTH_AUTHED) {
            //已经认证了
            errorStr = context.getString(R.string.error_already_authed);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_AUTH_REQUIRED) {
            //需要认证
            LoginController.getInstance(context).logout(context);
            errorStr = context.getString(R.string.error_need_auth);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_NO_LOOP) {
            //没有此回路
            errorStr = context.getString(R.string.error_no_the_loop);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_ERRORPARAM) {
            //操作失败
            errorStr = context.getString(R.string.error_invalid_param);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_EXIST_LOOP) {
            //已经存在此回路
            errorStr = context.getString(R.string.error_loop_exited);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_OPERATION_FAIL) {
            //操作失败
            errorStr = context.getString(R.string.error_operation_failed);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_PWDERROR) {
            //修改密码时，密码错误
            errorStr = context.getString(R.string.error_password_wrong);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_NOMMCCARD) {
            //no mmc 卡
            errorStr = context.getString(R.string.error_no_mcc_card);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_LOOPHASBEENREF) {
            //loop已经被引用
            errorStr = context.getString(R.string.error_loop_has_been_ref);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_LOOPEXCEEDCOUNTS) {
            //loop,trigger,schedule tule has been exceed max counts
            errorStr = context.getString(R.string.error_reach_limit);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_NOTHISPARAMDEVICE) {
            //config trigger rule,this room do not has  device for param
            errorStr = context.getString(R.string.error_room_not_add_this_device);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_SECURITY_ZONE_ABNORMAL) {
            // 布防时，防区异常
            errorStr = context.getString(R.string.error_zone_abnormal);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_SECURITY_PWD_INVALID) {
            // 撤防时，密码错误
            errorStr = context.getString(R.string.error_wrong_password);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_SECURITY_NOSUCH_SCENARIO) {
            // 找不到要操作的场景
            errorStr = context.getString(R.string.error_can_not_find_scenario);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_UNKOWN) {
            // 升级，未知错误
            errorStr = context.getString(R.string.error_upgrade_unknown_error);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_FILE_ER) {
            // 升级，文件错误
            errorStr = context.getString(R.string.error_upgrade_file_error);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_FLASH_ER) {
            // 升级，闪存错误
            errorStr = context.getString(R.string.error_upgrade_flash_error);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_HW_ER) {
            // 升级，硬件错误
            errorStr = context.getString(R.string.error_upgrade_hardware_error);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_NO_VER) {
            // 升级，没有新版本
            errorStr = context.getString(R.string.error_upgrade_no_new_version);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_DOING) {
            // 升级，正在升级
            errorStr = context.getString(R.string.error_upgrade_doing);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_UPGRADE_REBOOT) {
            // 升级，重启
            errorStr = context.getString(R.string.error_upgrade_done_reboot);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_RECOVERY_REBOOT) {
            //开启登陆
            LoginController.getInstance(context).startLogin();
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_IPC_MAX_STREAM) {
            //  IPC，超过支持数量
            errorStr = context.getString(R.string.error_ipc_reach_max_support);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_IPC_PLAY_ERR) {
            // IPC，播放错误
            errorStr = context.getString(R.string.error_ipc_play_error);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_NO_ROOM_DEVICEVALUE) {
            // 房间没有此类型设备
            errorStr = context.getString(R.string.error_room_no_this_type_device);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_RECOVERY_OK) {
            errorStr = context.getString(R.string.restore_backup_success);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_RECOVERY_FAIL) {
            errorStr = context.getString(R.string.restore_backup_failed);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_BACKUP_OK) {
            errorStr = context.getString(R.string.backup_success);
        } else if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_BACKUP_FAILED) {
            errorStr = context.getString(R.string.backup_failed);
        } else {
            errorStr = context.getString(R.string.error_unknown);
        }
        return errorStr;
    }

    /**
     * 用于解析Http请求错误后错误信息的处理
     *
     * @param context
     * @param bytes
     * @param headers
     * @return
     */
    public static String transferHttpErrorCode(Context context, byte[] bytes, Header[] headers) {
        if (bytes == null || bytes.length == 0) {
            return context.getString(R.string.error_unknown);
        }
        for (int i = 0; i < headers.length; i++) {
            Loger.print("ssd test", "*********** name : " + headers[i].getName() + "  value : " + headers[i].getValue(), Thread.currentThread());
        }
        try {
            String message = new String(bytes);
            JSONObject object = new JSONObject(message);
            Loger.print("ssd test", "************* http failed message : " + message, Thread.currentThread());
            if (object == null) {
                return context.getString(R.string.error_unknown);
            }
            String errorType = null;
            if (object.has(ERROR_TYPE)) {
                errorType = object.optString(ERROR_TYPE);
            }
            if (errorType == null) {
                return context.getString(R.string.error_unknown);
            }
            if (ERROR_TYPE_INVALID_MESSAGE_FORMART.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_message_formart);
            } else if (ERROR_TYPE_INVALID_LANGUAGE.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_language);
            } else if (ERROR_TYPE_INVALID_PHONE_NUMBER.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_phone_number);
            } else if (ERROR_TYPE_INVALID_EMAIL.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_email);
            } else if (ERROR_TYPE_VCODE_TOO_FREQUENT.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_vcode_too_frequent);
            } else if (ERROR_TYPE_VCODE_FAILED.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_vcode_failed);
            } else if (ERROR_TYPE_INVALID_NAME.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_name);
            } else if (ERROR_TYPE_INVALID_PWD.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_pwd);
            } else if (ERROR_TYPE_PHONE_REGISTERED.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_phone_registered);
            } else if (ERROR_TYPE_INVALID_PHONE_VCODE.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invalid_phone_vcode);
            } else if (ERROR_TYPE_EMAIL_REGISTERED.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_email_registered);
            } else if (ERROR_TYPE_INVALID_EMAIL_VCODE.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invali_email_vcode);
            } else if (ERROR_TYPE_LOGIN_FAILED.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_login_failed);
            } else if (ERROR_TYPE_INVALID_PHONE_UUID.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invali_phone_uuid);
            } else if (ERROR_TYPE_INVALID_PHONE_TYPE.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invali_phone_type);
            } else if (ERROR_TYPE_INVALID_PUSH_VENDOR.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invali_push_vendor);
            } else if (ERROR_TYPE_INVALID_PUSH_ID.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invali_push_id);
            } else if (ERROR_TYPE_PWD_ERROR.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_invali_pwd_wrong);
            } else if (ERROR_TYPE_BIND_PWD_ERROR.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_bind_pwd_error);
            } else if (ERROR_TYPE_CAN_NOT_BIND.equalsIgnoreCase(errorType)) {
                return context.getString(R.string.error_can_not_bind);
            } else {
                return context.getString(R.string.error_unknown);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getString(R.string.error_unknown);
    }

}
