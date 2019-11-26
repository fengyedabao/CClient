package com.honeywell.cube.ipc.nativeapi;


import com.honeywell.cube.ipc.GLFrameRenderer;
import com.honeywell.cube.ipc.P2PCallBack;
import com.honeywell.cube.ipc.VideoInfo;
import com.honeywell.lib.utils.LogUtil;

import java.net.InetAddress;
import java.nio.ByteBuffer;


/**
 * Created by alwin on 15-9-16.
 */
public class P2PConn {
    private final static String TAG = P2PConn.class.getSimpleName()+" IPCTAG";

    static {
        System.loadLibrary("p2plib");
    }

    private static String mConnUUID = null;
    private static boolean bInited = false;
    private static int mInitCnt = 0;
    // 使用jni的direct buffer提高传数据效率，另外，jni参数传数据有大小限制。
    //private static ByteBuffer directBuf;
    private static volatile P2PCallBack mConnCallBack = null;
    private static volatile P2PCallBack mIpcCallBack = null;
    private static volatile P2PCallBack mIpVdpCallBack = null;

    // native function
    private static native String initConn(String path, Object Buf);

    private static native int unInitConn();

    private static native int setupConnById(String ssId);

    private static native int setupConnByIp(int ip, int port);

    private static native int disConnById(String ssId);

    private static native int sendDataById(byte[] data, int dataLen, String ssId);

    private static native int sendDataByIp(byte[] data, int dataLen, int ip/*String ip,*/);

    private static native int sendCallDataVById(byte[] data, int dataLen, String ssId);

    private static native int sendCallDataAById(byte[] data, int dataLen, String ssId);

    ///////     call by jni, soft dec, yuv data      ////////
    static public void yuv_callback_directbuf(int width, int height) {
        if (0 == width || 0 == height) {
            LogUtil.e(TAG, "------P2PConn-------->   width=" + width + ", height=" + height);
            return;
        }
        if (null != mRender) {
            //Log.d(TAG, "wXh="+width+"X"+height);
            //Log.d(TAG, "wXh="+picWidth+"X"+picHeight);
            if (picWidth != width || picHeight != height) {
                mRender.update(width, height);
                picWidth = width;
                picHeight = height;
                //re-new data buf
                dataY = null;
                dataU = null;
                dataV = null;
            }
            byte src[] = directBuf.array();
            if (null == dataY) {
                dataY = new byte[picWidth * picHeight];
            }
            if (null == dataU) {
                dataU = new byte[picWidth * picHeight / 4];
            }
            if (null == dataV) {
                dataV = new byte[picWidth * picHeight / 4];
            }
            System.arraycopy(src, 0, dataY, 0, picWidth * picHeight);
            System.arraycopy(src, picWidth * picHeight, dataU, 0, picWidth * picHeight / 4);
            System.arraycopy(src, picWidth * picHeight + picWidth * picHeight / 4,
                    dataV, 0, picWidth * picHeight / 4);
            mRender.update(dataY, dataU, dataV);
        }
    }

    // data call back, IPC
    public static void dataCallBack(byte[] data, int dataLen, String uuid/*int ssId, int timeStp*/) {
        LogUtil.e("TAG", "------P2PConn-------->  dataCallBack  ");
        if (null != mIpcCallBack) {
            mIpcCallBack.dataCallBack(data, dataLen, uuid);
        }
    }

    // 通话数据回调
    public static void dataCallBackCallV(byte[] data, int dataLen, String uuid/*int ssId, int timeStp*/) {
        LogUtil.e("TAG", "------P2PConn-------->  dataCallBackCallV  ");
        if (null != mIpVdpCallBack) {
            mIpVdpCallBack.dataCallBackCallV(data, dataLen, uuid);
        }
    }

    public static void dataCallBackCallA(byte[] data, int dataLen, String uuid/*int ssId, int timeStp*/) {
        LogUtil.e("TAG", "------P2PConn-------->  dataCallBackCallA  ");
        if (null != mIpVdpCallBack) {
            mIpVdpCallBack.dataCallBackCallA(data, dataLen, uuid);
        }
    }

    public static void updateIPCVideoInfo(byte[] csd_info, int csd_info_length, int video_width, int video_height) {
        LogUtil.d(TAG, "------P2PConn-------->  updateIPCVideoInfo  ipc info:" + video_width + "x" + video_height);
        if (null != mIpcCallBack) {
            VideoInfo info = new VideoInfo();
            info.mCsdInfo = csd_info;
            info.mWidth = video_width;
            info.mHeight = video_height;
            mIpcCallBack.updateVideoInfo(info);
        }
    }

    public static void updateCallVideoInfo(byte[] csd_info, int csd_info_length, int video_width, int video_height) {
        LogUtil.d(TAG, "------P2PConn-------->  updateCallVideoInfo  call video info:" + video_width + "x" + video_height);
        if (null != mIpVdpCallBack) {
            VideoInfo info = new VideoInfo();
            info.mCsdInfo = csd_info;
            info.mWidth = video_width;
            info.mHeight = video_height;
            mIpVdpCallBack.updateVideoInfo(info);
        }
    }

    public static void connCallBack(int type, int status) {
        // type: 1->穿透, 2->服务器, 3->连接
        // status: 0->fail, 1-> ok
        LogUtil.i(TAG, "------P2PConn-------->  connCallBack type:" + type + ", status=" + status);
        if (null != mConnCallBack) {
            mConnCallBack.connCallBack(type, status);
        }
    }

    // 借口函数，供外部调用
    // 初始化lib
    public static String initConnLib(/*P2PCallBack callBack, */String path) {
        mInitCnt++;
        if (bInited) {
            return mConnUUID;
        }
        picWidth = 0;
        picHeight = 0;
        mRender = null;
        directBuf = ByteBuffer.allocateDirect(1920 * 1080 + 1920 * 1080 / 2);
        mConnUUID = initConn(path, directBuf);
        if (mConnUUID != null) {
            bInited = true;
            //mCallBack = callBack;

        }
        return mConnUUID;
    }

    // 释放lib内部资源
    public static int unInitConnLib() {
        mInitCnt--;
        if (mInitCnt < 1 && bInited) {
            bInited = false;
            mConnCallBack = null;
            mIpcCallBack = null;
            mIpVdpCallBack = null;
            mInitCnt = 0;
            mConnUUID = null;
            picWidth = 0;
            picHeight = 0;
            mRender = null;
            return unInitConn();
        }
        return 0;
    }

    // 通过uuid建立连接
    public static int createConnById(String ssId) {
        if (!bInited) {
            return -1;
        }
        return setupConnById(ssId);
    }

    // 通过ip地址建立连接
    public static int createConnByIp(String ip, int port) {
        if (!bInited) {
            return -1;
        }
        return setupConnByIp(bytesToInt(ipToBytesByInet(ip)), port);
    }

    // 断开连接
    public static int dropConnById(String ssId) {
        if (!bInited) {
            return -1;
        }
        return disConnById(ssId);
    }

    // 通过UUID，发送，需要事先建立连接。
    public static int sendByteDataById(byte[] data, int dataLen, String ssId) {
        if (!bInited) {
            return -1;
        }
        //Log.d("P2P", "sendByteDataById");
        return sendDataById(data, dataLen, ssId);
    }

    // 通过UUID，发送通话数据，视频，需要事先建立连接。
    public static int sendCallByteDataVById(byte[] data, int dataLen, String ssId) {
        if (!bInited) {
            return -1;
        }
        //Log.d("P2P", "sendByteDataById");
        return sendCallDataVById(data, dataLen, ssId);
    }

    // 通过UUID，发送通话数据，视频，需要事先建立连接。
    public static int sendCallByteDataAById(byte[] data, int dataLen, String ssId) {
        if (!bInited) {
            return -1;
        }
        //Log.d("P2P", "sendByteDataById");
        return sendCallDataAById(data, dataLen, ssId);
    }

    // 无需connect，直接发送
    public static int sendByteDataByIp(byte[] data, int dataLen, String ip) {
        if (!bInited) {
            return -1;
        }
        //Log.d("P2P", "sendByteDataByIp");
        return sendDataByIp(data, dataLen, bytesToInt(ipToBytesByInet(ip)));
    }

    // 获取uuid
    public static String getUUID() {
        return mConnUUID;
    }

    public static void setRender(GLFrameRenderer render) {
        mRender = render;
    }

    public static void setConnCallBack(P2PCallBack callback) {
        mConnCallBack = callback;
    }

    public static void setIpcCallBack(P2PCallBack callback) {
        mIpcCallBack = callback;
    }

    public static void setIpVdpCallBack(P2PCallBack callback) {
        mIpVdpCallBack = callback;
    }


    /**
     * 把IP地址转化为字节数组
     *
     * @param ipAddr
     * @return byte[]
     */
    public static byte[] ipToBytesByInet(String ipAddr) {
        try {
            return InetAddress.getByName(ipAddr).getAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }

    /**
     * 根据位运算把 byte[] -> int
     *
     * @param bytes
     * @return int
     */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[3] & 0xFF;
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }

    //////////////// for soft dec and gl display ///////////////
    static private ByteBuffer directBuf;
    static private byte[] dataY = null;
    static private byte[] dataU = null;
    static private byte[] dataV = null;
    static private int picWidth = 0;
    static private int picHeight = 0;
    static private GLFrameRenderer mRender = null;
}
