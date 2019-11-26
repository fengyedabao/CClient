package com.honeywell.cube.net.http;

import android.content.Context;

import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.Loger.Loger;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by H157925 on 16/4/29. 14:28
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 重写Responder 进行cookie的存储
 */
public class MyHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = "MyHttpResponseHandler";
    public static final int RESPONCE_CODE_SUCESS = 200;
    public static final int RESPONCE_CODE_ERROR_IN_REQUEST = 400;//请求中有错误
    public static final int RESPONCE_CODE_ERROR_LOGIN = 401;//当需要登陆时返回
    public static final int RESPONCE_CODE_ERROR_SERVER = 500;//服务器端出现错误


    private Context mContext;

    public MyHttpResponseHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        //读cookie 存储cookie
        setCookieText(headers);
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

    }

    /**
     * 设置用户的all_header_fields_cookie 参数，即存储设置的cookie
     */
    private void setCookieText(Header[] headers) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getName().equals(CommonData.JSON_HTTP_SET_USER_COOKIE)) {
                Loger.print(TAG, "ssd head :name " + headers[i].getName() + " value:" + headers[i].getValue(), Thread.currentThread());
                String value = headers[i].getValue();
                updateSqliteOfAppInfo(value);
            }
        }
    }

    /**
     * 处理错误信息
     */
    private void dealWithFail(int stateCode, Header[] headers, byte[] bytes, Throwable throwable) {
        Loger.print(TAG, "on Fail code :" + stateCode + " head: " + headers.toString() + " content :" + (new String(bytes)), Thread.currentThread());
        if (stateCode == 401) {
            //删除cookie 认证失败
            LoginController handler = LoginController.getInstance(mContext);
            handler.setLoginType(LoginController.LOGIN_TYPE_DISCONNECT);
            updateSqliteOfAppInfo("");

            //重新进行WebSocket 登陆
        }
    }

    /**
     * 更新用户信息
     */
    private void updateSqliteOfAppInfo(String cookie) {
        AppInfoFunc infoFunc = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(this.mContext));
        AppInfo info = AppInfoFunc.getCurrentUser(mContext);
        info.all_header_fields_cookie = cookie;
        infoFunc.updateAppInfoByUserName(info.username, info);
    }

}
