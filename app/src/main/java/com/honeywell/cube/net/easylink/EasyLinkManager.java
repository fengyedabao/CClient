package com.honeywell.cube.net.easylink;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.honeywell.cube.R;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.Loger.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;

import cn.com.broadlink.blnetwork.BLNetwork;

/**
 * Created by H157925 on 16/6/22. 13:20
 * Email:Shodong.Sun@honeywell.com
 * Easy link 管理类
 * 包含内容：easy link，group listener, broad link
 */
public class EasyLinkManager {
    private static final String TAG = EasyLinkManager.class.getSimpleName();
    public static Context mContext = null;//不能为空值
    private static EasyLinkManager manager = null;

    /**
     * easy lick
     */
    private multiUdpSendTask mSendTask = null;//发送广播 easy link

    /**
     * 发现Cube
     */
    //参数列表
    public static final String FIND_IP = "IP";
    public static final String FIND_MAC = "MAC";
    private static final String FIND_MAF = "MAF";
    private static final String FIND_TYPE = "Type";
    private static final String FIND_RAND = "RAND";
    private static final String FIND_VER = "VER";

    private multiUdpRecvTask mRecvTask = null;//监听固定端口Ip，解析配置结果
    public static volatile boolean isStopGroupReceiver = false;//用于停止组播监听线程的字段
    public static ArrayList<JSONObject> findCubeList = new ArrayList<>();//用于存储发现的Cube

    /**
     * Broad Link
     */
    public static BLNetwork mBlNetwork;
    public static String licenseValue =
            "IDqOTOuVhMNQz8XWEc2wqmrjuYeTDGtBlMkm6AT1mmKKNLTrl45x" +
                    "4KzHGywehG/TzmSMIDnemvSlaNMSyYceBTJnNVQ10LKQ9sNzVIBX21r87yx+quE=";


    private EasyLinkManager(Context context) {
        mContext = context;
    }

    /**
     * 单例
     *
     * @param context
     * @return
     */
    public static EasyLinkManager newInstance(Context context) {
        if (manager == null) {
            if (context == null) {
                Loger.print(TAG, "ssd easy link new instance context is null", Thread.currentThread());
            } else {
                manager = new EasyLinkManager(context);
            }
        }
        return manager;
    }

    /**
     * 开始配置 Broad Link
     *
     * @param ssid
     * @param pwd
     */
    public void startConfigBroadLink(String ssid, String pwd) {
        Loger.print(TAG, "ssd start config broad link", Thread.currentThread());
        initBroadLink();
        broadLinkConfig(ssid, pwd);
    }

    /**
     * 停止配置Broad Link
     */
    public void stopConfigBroadLink() {
        cancelConfig();
    }

    //broad link 初始化
    private void initBroadLink() {
        mBlNetwork = BLNetwork.getInstanceBLNetwork(mContext);
        JSONObject initJsonObjectIn = new JSONObject();
        JSONObject initJsonObjectOut = null;
        try {
            initJsonObjectIn.put("api_id", 1);
            initJsonObjectIn.put("command", "network_init");
            initJsonObjectIn.put("license", licenseValue);
            String string = initJsonObjectIn.toString();
            String initOut = mBlNetwork.requestDispatch(string);
            initJsonObjectOut = new JSONObject(initOut);
            String retMsg = initJsonObjectOut.getString("msg");
            Loger.print(TAG, "ssd init BroadLink result : " + retMsg, Thread.currentThread());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //配置Broad link
    private void broadLinkConfig(String ssid, String password) {
        long sTime = System.currentTimeMillis();
        JSONObject in = new JSONObject();
        try {
            in.put("api_id", 10000);
            in.put("command", "smart_config");
            in.put("ssid", ssid);
            in.put("password", password);
            in.put("broadlinkv2", 1);//选填，默认是二代模块
            in.put("dst", "192.168.31.1");
            String string = in.toString();
            Log.d(TAG, "out = " + string);
            String outString = mBlNetwork.requestDispatch(string);
            Loger.print(TAG, "ssd result : " + outString, Thread.currentThread());
            long eTime = System.currentTimeMillis();
            Log.i(TAG, "time:" + (eTime - sTime));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //取消配置 Broad Link
    private void cancelConfig() {
        long sTime = System.currentTimeMillis();
        JSONObject in = new JSONObject();
        try {
            if (mBlNetwork != null) {
                in.put("api_id", 10001);
                in.put("command", "cancel_easyconfig");
                String string = in.toString();
                String outString = mBlNetwork.requestDispatch(string);
                Loger.print(TAG, "ssd ret:" + outString, Thread.currentThread());
                long eTime = System.currentTimeMillis();
                Log.i(TAG, "time:" + (eTime - sTime));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 启动组播监听
     */
    public void startGroupListener() {
        if (mRecvTask != null) {
            mRecvTask.cancel(true);
            mRecvTask = null;
        }
        if (!CommonUtils.isConnectNetwork(mContext)) {
            Loger.print(TAG, "ssd start group listener 当前网络未连接", Thread.currentThread());
            return;
        }
        Loger.print(TAG, "ssd start group listener", Thread.currentThread());
        mRecvTask = new multiUdpRecvTask();
        mRecvTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        isStopGroupReceiver = false;
    }

    /**
     * 启动 Easy Link
     * 只需要 mSendTask
     *
     * @param password
     */
    public void startEasyLink(String password) {
        Loger.print(TAG, "ssd start easy link", Thread.currentThread());
        if (password == null) {
            Loger.print(TAG, "ssd start easy link parameters is null", Thread.currentThread());
            return;
        }

        if (mSendTask != null && !mSendTask.isCancelled()) {
            mSendTask.cancel(true);
            mSendTask = null;
        }
        if (!CommonUtils.isConnectNetwork(mContext)) {
            Loger.print(TAG, "ssd start easy link 当前网络未连接", Thread.currentThread());
            return;
        }
        mSendTask = new multiUdpSendTask();
        mSendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, password);
    }

    /**
     * 停止 easy link
     */
    public void stopEasyLink() {
        Loger.print(TAG, "ssd stop easy link", Thread.currentThread());
        if (mSendTask != null && !mSendTask.isCancelled()) {
            mSendTask.cancel(true);
        }
        mSendTask = null;
    }

    /**
     * 停止 组播 监听
     */
    public void stopGroupListener() {
        Loger.print(TAG, "ssd stop group listener", Thread.currentThread());
        if (mRecvTask != null && !mRecvTask.isCancelled()) {
            mRecvTask.cancel(true);
        }
        isStopGroupReceiver = true;
        mRecvTask = null;
    }


    /**
     * easy link 线程 发送密码线程
     */
    private class multiUdpSendTask extends AsyncTask<String, Integer, Void> {

        private MulticastSocket socket = null;
        private static final String aesPassword = "1234567890123456";

        @Override
        protected void onPreExecute() {
            Loger.print(TAG, "ssd send task is running", Thread.currentThread());
        }

        /**
         * @param first
         * @param second
         * @param third
         * @return
         */
        private boolean send24BitByMultiUdp(byte first, byte second, byte third, int length) {
            /* 239.0.0.0～239.255.255.255为本地管理组播地址，仅在特定的本地范围内有效 */
            //String destAddressHex = "239.0.0.1";
            byte[] da = {(byte) 239, 0, 0, 1};
            da[0] = (byte) 239;
            da[1] = first;
            da[2] = second;
            da[3] = third;
            length++;/* It's very important. */

            Inet4Address destAddress = null;
            try {
                destAddress = (Inet4Address) Inet4Address.getByAddress(da);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            }

            if (!destAddress.isMulticastAddress()) {
                Loger.print(TAG, "ssd send task ip : " + destAddress.toString(), Thread.currentThread());
                destAddress = null;
                return false;
            }
            try {
                if (socket == null) {
                    socket = new MulticastSocket();
                    socket.setTimeToLive(4);
                    //socket.setSendBufferSize(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            }
            if (socket != null) {

                try {
                    byte[] msg = new byte[length];
                    DatagramPacket dp = new DatagramPacket(msg, msg.length, destAddress, NetConstant.EASY_LINK_SEND_GROUP);
                    socket.send(dp);
                    //Log.e("udp", "send24BitByMultiUdp " + msg.length + " " + destAddress.toString());
                    dp = null;
                    msg = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    socket.close();
                    socket = null;
                    return false;
                }
                destAddress = null;
            }
            return true;
        }

        private byte[] getChacha20Key() {
            /* this is chacha20 key. chacha20 key = md5(source key) */
            return new byte[]{(byte) 0xC6, (byte) 0xD0, (byte) 0xFD, (byte) 0xE4, (byte) 0x6F, (byte) 0x20, (byte) 0x3D, (byte) 0x6F, (byte) 0x4D, (byte) 0x27, (byte) 0x41, (byte) 0x9B, (byte) 0x23, (byte) 0x83, (byte) 0x38, (byte) 0xEC};
        }

        private byte[] byteArrayResize(byte[] input) {
            if (input == null) {
                return null;
            }
            int length = (input.length % 3 == 0) ?
                    (input.length) :
                    (input.length - (input.length % 3) + 3);
            byte[] output = new byte[length];
            System.arraycopy(input, 0, output, 0, input.length);
            SecureRandom sr = new SecureRandom(input);
            switch (length - input.length) {
                case 0:
                    break;
                case 1:
                    output[input.length] = (byte) (sr.nextInt() & 0xFF);
                    sr = null;
                    break;
                case 2:
                    output[input.length] = (byte) (sr.nextInt() & 0xFF);
                    output[input.length + 1] = (byte) (sr.nextInt() & 0xFF);
                    sr = null;
                    break;
                default:
                    break;
            }
            return output;
        }

        private byte[] encrypt_chacha20(String passphase) {
            byte[] password = passphase.getBytes();
            byte[] plaintext = new byte[password.length + 3];

            //length
            plaintext[0] = (byte) (password.length + 2);
            //Nonce
            do {
                //plaintext[1] = (byte)System.currentTimeMillis();
                plaintext[1] = 0x55;
            } while (plaintext[1] == 0 || plaintext[1] == -1);
            //crc8
            plaintext[2] = 0;
            //password
            System.arraycopy(password, 0, plaintext, 3, password.length);

            //calc crc8
            plaintext[2] = Crc8PolynomialD5.getCrc8(plaintext, 0, plaintext.length);
            //calc chacha20

            return plaintext;
        }

        /**
         * background task
         *
         * @param params
         */
        @Override
        protected Void doInBackground(String... params) {
            WifiManager.MulticastLock lock = ((WifiManager) mContext.
                    getSystemService(Context.WIFI_SERVICE)).
                    createMulticastLock(mContext.getResources().getString(R.string.app_name) + "send");
            byte[] passwdData = encrypt_chacha20(params[0]);
            if (passwdData == null) {
                Log.e("wifi", "doInBackground passwdData fail");
                return null;
            }
            //decryptAES128(passwdData);//for test
            passwdData = byteArrayResize(passwdData);
            boolean exceptionFlag = false;
            lock.acquire();
            if (lock.isHeld()) {
                //共计60
                long endTime = System.currentTimeMillis() + 60 * 1000;
                while (System.currentTimeMillis() < endTime) {
                    if (isCancelled() || exceptionFlag) {
                        break;
                    }
                    //发送前导码
                    for (int j = 0; j < 4; j++) {
                        if (isCancelled() || exceptionFlag) {
                            break;
                        }
                        if (!send24BitByMultiUdp((byte) 0x7e, (byte) 0x6e, (byte) 0x6e, j)) {
                            exceptionFlag = true;
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            exceptionFlag = true;
                        }
                    }
                    /*
                    if (!send24BitByMultiUdp((byte) 0x6e, (byte) 0x6e, (byte) 0x6e, passedLength)) {
                        exceptionFlag = true;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        exceptionFlag = true;
                    }
                    */

                    //发送密钥 500ms
                    for (int i = 0; i < passwdData.length; i += 3) {
                        if (isCancelled() || exceptionFlag) {
                            break;
                        }
                        if (!send24BitByMultiUdp((byte) (0x40 + i / 3), passwdData[i], passwdData[i + 1], (passwdData[i + 2]) & 0xFF)) {
                            exceptionFlag = true;
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            exceptionFlag = true;
                        }
                    }
                }
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            lock.release();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mContext == null) {
                mSendTask = null;
                return;
            }
            mSendTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mContext == null) {
                mSendTask = null;
                return;
            }
            mSendTask = null;
        }
    }

    private class multiUdpRecvTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            WifiManager.MulticastLock lock = ((WifiManager) mContext.
                    getSystemService(Context.WIFI_SERVICE)).
                    createMulticastLock(mContext.getResources().getString(R.string.app_name) + "recv");
            lock.acquire();
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket dp = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            MulticastSocket ms;
            InetAddress receiveAddress;

            Loger.print(TAG, "ssd start group listener", Thread.currentThread());
            try {
                ms = new MulticastSocket(NetConstant.EASY_LINK_GROUP_LISTENER_PORT);
                receiveAddress = InetAddress.getByName("239.102.103.104");
                ms.joinGroup(receiveAddress);
            } catch (IOException e) {
                e.printStackTrace();
                lock.release();
                return null;
            }
            while (!isStopGroupReceiver) {
                try {
                    ms.receive(dp);
                    byte[] data = dp.getData();
                    int port = dp.getPort();
                    String dataStr = new String(data);
                    String resultStr = dataStr.substring(dataStr.lastIndexOf("{"), dataStr.lastIndexOf("}")) + "}";
                    JSONObject jsonObject = new JSONObject(resultStr);
                    if (jsonObject != null) {
                        //解析JSONS
                        String type = jsonObject.optString(FIND_TYPE);
                        if (type.indexOf("CUBE") != -1 || type.indexOf("cube") != -1) {
                            Loger.print(TAG, "ssd find cube--receive port : " + port + "; data : " + jsonObject, Thread.currentThread());
                            boolean haveAdd = false;
                            for (int i = 0; i < findCubeList.size(); i++) {
                                JSONObject jsonObject1 = findCubeList.get(i);
                                if (jsonObject1.optString(FIND_MAC).equalsIgnoreCase(jsonObject.optString(FIND_MAC))) {
                                    haveAdd = true;
                                    break;
                                }
                            }
                            if (!haveAdd) {
                                findCubeList.add(jsonObject);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            try {
                ms.leaveGroup(receiveAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            lock.release();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (mContext == null) {
                return;
            }
        }

        @Override
        protected void onCancelled() {
            if (mContext == null) {
                mRecvTask = null;
                return;
            }
            mRecvTask = null;
        }
    }
}
