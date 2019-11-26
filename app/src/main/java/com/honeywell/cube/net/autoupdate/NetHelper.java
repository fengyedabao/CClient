
package com.honeywell.cube.net.autoupdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.honeywell.lib.utils.LogUtil;

public class NetHelper {
    public static String httpStringGet(String url) throws Exception {
        return httpStringGet(url, "GBK");
    }


    public static String httpStringGet(String url, String enc) throws Exception {
        // This method for HttpConnection
        String page = "";
        BufferedReader bufferedReader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                    "android");

            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);

            HttpGet request = new HttpGet();
            request.setHeader("Content-Type", "text/plain; charset=GBK");
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent(), enc));

            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            //String NL = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            bufferedReader.close();
            page = stringBuffer.toString();
            return page;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtil.d("BBB", e.toString());
                }
            }
        }
    }

    public static boolean checkNetWorkStatus(Context context) {
        boolean result;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            if (netinfo != null) {
                if (netinfo.isConnected()) {
                    result = true;
                    LogUtil.i("NetStatus", "The net was connected");
                } else {
                    result = false;
                    LogUtil.i("NetStatus", "The net was bad!");

                }
            } else {
                result = false;
                LogUtil.i("NetStatus", "The net was bad!");
            }
        } catch (Exception e) {
            result = true;
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static boolean linktoserver(String url) {
        boolean result = false;
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                    "android");

            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 2000);

            HttpGet request = new HttpGet();
            request.setHeader("Content-Type", "text/plain; charset=utf-8");
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            int r = response.getStatusLine().getStatusCode();
            if (r == 200)
                result = true;

            else
                result = false;


        } catch (Exception e) {
            result = false;
        }
        return result;

    }


}
