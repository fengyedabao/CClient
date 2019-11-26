package com.honeywell.cube.ipc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.content.Context;
import android.content.Intent;

import com.honeywell.lib.utils.LogUtil;

public class CubeNetConnect {
    protected static final String TAG = CubeNetConnect.class.getSimpleName() + " IPCTAG";
    //心跳检测的检测时间
    private static final int HEARTBEATCHECKTIMER = 15 * 1000;////15 * 1000;//700 * 1000;//
    //	心跳发送的轮询时间
    private static final int HEARTBEATSENDTIMER = 10 * 1000;//	//10 * 1000;//600 * 1000;//
    private static final int REQUESTTIMER = 30 * 1000;//
    private static final int MAX_QUEUE_SIZE = 200; // 最大缓存200个配置消息(JSON)
    private static final String SOCKETCLOSEREASON_HEARTBEAT = "heartbeat";
    private static final String SOCKETCLOSEREASON_READIOEXCEPTION = "read ioexception";
    private static final String SOCKETCLOSEREASON_WRITEIOEXCEPTION = "write ioexception";
    public static final String SOCKETCLOSEREASON_SERVICEDESTORY = "service destory";
    private static final String HEARTBEAT_REQUEST_ID = "11122233344455";
    //private static final int MAXTCPLENGTH = 100000;
    private static final int MAXTCPLENGTH = 15 * 1024;

    //property
    public Socket mSocket = null;
    public String mIpAddr = null;
    public boolean mInit = false;
    public int mPort = Constants.DEFAULT_PORT;
    public BlockingQueue<ControlCommandInfo> mCommandQueue = new LinkedBlockingQueue<ControlCommandInfo>(MAX_QUEUE_SIZE);
    public BlockingQueue<String> mRequestIdQueue = new LinkedBlockingQueue<String>(MAX_QUEUE_SIZE);
    private volatile boolean mRun = true;
    public Context mContext = null;
    private BufferedInputStream mInputStream = null;
    private BufferedOutputStream mBos = null;
    public SocketCloseCallback mCallback = null;
    private Timer mHeartBeatTimer = new Timer();
    private Timer mRequestTimer = new Timer();

    private HeartTimeOutTask mHeartTimeOutTask = null;
    private RequestTimerOutTask mRequestTimeoutTask = null;
    private HeartSendTask mHeartSendTask = null;

    //	private Object mLock = new Object();
    public ReadThread mReadThread = null;
    public WriteThread mWriteThread = null;
    private int mRegisterCount = 0;
    private static final int REGISTER_MAX_COUNT = 3;

    private int mReadPostion = 0;
    private int mWritePosion = 0;

    @Override
    public String toString() {
        return "CubeNetConnect [mIpAddr=" + mIpAddr
                + ", mInit=" + mInit
                + ", mPort=" + mPort + "]";
    }

    public CubeNetConnect(Socket socket, String ipAddr, int port, Context context) throws IOException {
        mSocket = socket;
        mIpAddr = ipAddr;
        mPort = port;
        mContext = context;
        mInputStream = new BufferedInputStream(mSocket.getInputStream());
        mBos = new BufferedOutputStream(mSocket.getOutputStream());
    }

    public void parseCubeMessageBodyInfo(String jsonStr, int length) {
	    LogUtil.e(TAG, "-------CubeNetConnect ---- parseCubeMessageBodyInfo receives cube message:" + toString()+ jsonStr+",,,1111111");

        try {
            JSONTokener jsonParser = new JSONTokener(jsonStr);
            JSONObject head = (JSONObject) jsonParser.nextValue();
            String strAction = new String(head.getString(Constants.JSON_COMMAND_ACTION));
            String strSubaction = new String(head.getString(Constants.JSON_COMMAND_SUBACTION));
            Intent intent = new Intent();
            byte[] byteArray = jsonStr.getBytes();
            boolean bSendBoradcast = false;
            //报警或者是事件上报
            if (strAction.equals(Constants.JSON_COMMAND_ACTION_EVENT)) {
                bSendBoradcast = true;
                intent.setAction(Constants.ACTION_DEV_ALARM);
                resetHeartBeatTimeoutTask();
                if (strSubaction.equals(Constants.JSON_COMMAND_SUBACTION_CALL)) {
                    String callmsg = head.getString(Constants.JSON_COMMAND_CALLMSG);
                    LogUtil.i(TAG, "-------CubeNetConnect ---- parseCubeMessageBodyInfo() 111111111111");
                    if (callmsg.equals(Constants.CALL_MSG_INCOMING_CALL)) {
//                        Intent inCallIntent = new Intent(mContext, IpcCallScreenActivity.class);
//                        inCallIntent.putExtra(Constants.EXTRA_DATA_CALLMSG, NetUtil.parseCallMsg(head));
//                        inCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivity(inCallIntent);
                        LogUtil.i(TAG, "-------CubeNetConnect ---- parseCubeMessageBodyInfo() 2222222222222");
                        bSendBoradcast = false;
                        return;
                    }
                }
            } else if (strAction.equals(Constants.JSON_COMMAND_ACTION_RESPONSE)) {
                //unknown or other response

                if (!strSubaction.equals(Constants.JSON_COMMAND_SUBACTION_UNKNOWN)) {
                    //注册
                    if (strSubaction.equals(Constants.JSON_COMMAND_SUBACTION_REGISTER)) {
                        int errorCode = head.getInt(Constants.JSON_COMMAND_ERRORCODE);
                        if (errorCode == Constants.MESSAGE_ERROR_CODE_OK) {
                            //注册OK
                            //1.开始心跳
                            resetHeartBeatSendTask();
                            resetHeartBeatTimeoutTask();

                            //2。向服务器获取所有设备的配置
                            startGetCubeConfig();
                            bSendBoradcast = true;
                            intent.setAction(Constants.ACTION_LOG_CUBE_SUCCESS);
                        } else if (mRegisterCount < REGISTER_MAX_COUNT) {
                            startReigsterTask();
                            mRegisterCount++;
                        }
                    } else if (strSubaction.equals(Constants.JSON_COMMAND_SUBACTION_HEARTBEAT)) {
                        resetHeartBeatTimeoutTask();
                    } else if (strSubaction.equals(Constants.JSON_COMMAND_SUBACTION_GETDEVCONF)) {
                        LogUtil.e(TAG, "-------CubeNetConnect ----   start parse!!!!!!!!!!!!!!!!!!!1111111");
                        resetHeartBeatTimeoutTask();
                        parseCubebaseDatabase(head);
                        LogUtil.e(TAG, "-------CubeNetConnect ----   end parse!!!!!!!!!!!!!!!!!!!2222222222222222");
                    }
                    //请求响应
                    else {
                        bSendBoradcast = true;
                        intent.setAction(Constants.ACTION_DEV_CONTROL);
                        String msgId = mRequestIdQueue.poll();
                        if (!CommonUtils.ISNULL(msgId)) {
                            intent.putExtra(Constants.EXTRA_DATA_REQUESTID, msgId);
                        } else {
                            bSendBoradcast = false;
                        }
                        LogUtil.d(TAG, "-------CubeNetConnect ----  response requestId=" + msgId + toString());
                        if (null != mRequestTimeoutTask) {
                            mRequestTimeoutTask.cancel();
                            mRequestTimeoutTask = null;
                        }
                        /*synchronized (mLock) {
                            mLock.notifyAll();
						}*/
                        resetHeartBeatTimeoutTask();
                    }
                }
            }
            if (bSendBoradcast && null != byteArray && byteArray.length > 0) {
                intent.putExtra(Constants.EXTRA_DATA_FROM, Constants.EXTRA_DATA_FROM_CUBEBASE);
                intent.putExtra(Constants.EXTRA_DATA_LEN, length);
                intent.putExtra(Constants.EXTRA_DATA_NAME, byteArray);
                if (mContext != null) {
                    mContext.sendBroadcast(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseCubebaseDatabase(JSONObject head) throws JSONException {
//        if (null == head) {
//            return;
//        }
//        //first up local database
//        String cubVersion = head.getString(Constants.JSON_COMMAND_VERSION);
//        long cubVerLong = Long.parseLong(cubVersion);
//        CubebaseFuc func = new CubebaseFuc(ConfigDatabaseHelper.getInstance(mContext));
//        long localversion = func.getCubebaseDbVersion();
//        if (localversion >= cubVerLong) {
//            return;
//        }
//
//        JSONArray jsonArray = head.getJSONArray(Constants.JSON_COMMAND_CONFIGDATA);
//        if (jsonArray == null) {
//            return;
//        }
//        int jsonLenth = jsonArray.length();
//        for (int i = 0; i < jsonLenth; i++) {
//            JSONObject jObject = jsonArray.getJSONObject(i);
//            //parse peripheraldevice
//            if (jObject.has(Constants.PERIPHERALDEVICE)) {
//                JSONArray peripheraldeviceArray = jObject.getJSONArray(Constants.PERIPHERALDEVICE);
//                Log.e("alinmi", "peripheraldeviceArray = " + peripheraldeviceArray);
//                NetUtil.parsePeriphaldeviceObject(mContext, peripheraldeviceArray);
//            }
//            //update ipcstreaminfo
//            else if (jObject.has(Constants.IPCSTREAMINFO)) {
//                JSONArray ipcStreamInfoArray = jObject.getJSONArray(Constants.IPCSTREAMINFO);
//                NetUtil.parseIpcInfoObject(mContext, ipcStreamInfoArray);
//            }
//            //update sparklighting loop
//            else if (jObject.has(Constants.SPARKLIGHTINGLOOP)) {
//                JSONArray sparklightingLoopArray = jObject.getJSONArray(Constants.SPARKLIGHTINGLOOP);
//                NetUtil.parseSparklightingLoopObject(mContext, sparklightingLoopArray);
//            }
//            //update relay loop
//            else if (jObject.has(Constants.RELAYLOOP)) {
//                JSONArray relayLoopArray = jObject.getJSONArray(Constants.RELAYLOOP);
//                NetUtil.parseRelayLoopObject(mContext, relayLoopArray);
//            }
//            //update wiredzone loop
//            else if (jObject.has(Constants.WIREDZONELOOP)) {
//                JSONArray relayLoopArray = jObject.getJSONArray(Constants.WIREDZONELOOP);
//                NetUtil.parseWiredzoneLoopObject(mContext, relayLoopArray);
//            }
//        }
//
//        //最终版本号一cube为准
//        func.updateConfigVer(cubVersion);


    }

    private void startGetCubeConfig() {
        //获取数据库版本号
//        ConfigDatabaseHelper instance = ConfigDatabaseHelper.getInstance(mContext);
//        long version = new CubebaseFuc(instance).getCubebaseDbVersion();
////		instance.clearAndCreate(instance.getWritableDatabase());
//        try {
//            LogUtil.d(TAG, "-------CubeNetConnect ----   startGetCubeConfig() 1111");
//            JSONObject getconfigRequest = new JSONObject();
//            getconfigRequest.put(Constants.JSON_COMMAND_MESSAGEID, HEARTBEAT_REQUEST_ID);
//            getconfigRequest.put(Constants.JSON_COMMAND_ACTION, Constants.JSON_COMMAND_ACTION_REQUEST);
//            getconfigRequest.put(Constants.JSON_COMMAND_SUBACTION, Constants.JSON_COMMAND_SUBACTION_GETDEVCONF);
//            getconfigRequest.put(Constants.JSON_COMMAND_MODULETYPE, "cube");
//            getconfigRequest.put(Constants.JSON_COMMAND_VERSION, version);
//            byte[] bytes = getconfigRequest.toString().getBytes();
//            byte[] data = NetUtil.appendSendingBytes(bytes, bytes.length);
//            mCommandQueue.put(new ControlCommandInfo(HEARTBEAT_REQUEST_ID, data, true));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    class ReadThread extends Thread {
        private ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();

        public void run() {
            int bodyLength = 0;
            int iread = 0;
            int isEncrypt = 0;
            final int oneReadByteLen = 2 * 1024;
            int positon = 0;
            while (mRun) {
                // 接收从服务端发送过来的数据
                byte buffer[] = new byte[oneReadByteLen];
//	        	byte buffer[] = new byte[1024];
//	        	byte buffer[]  = null;
                if (!mInit) {
                    mRun = false;
                    mInit = false;
                    LogUtil.d(TAG, "-------CubeNetConnect ----  ReadThread  ---->     IP:" + mIpAddr + ",bDelDeviceStop,ReadThread  stop!");
                    break;
                }
                try {
                    if (null != mInputStream) {
                        iread = mInputStream.read(buffer);
//	            		buffer = CommonUtils.inputStreamToBytes(mInputStream);
//	            		if(null != buffer){
//	            			iread = buffer.length;
//	            		}     

//	            		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	            		int lengh = 0;
//	            		while((lengh = mInputStream.read(buffer, 0, buffer.length)) > 0){
//	            			bos.write(buffer, 0, lengh);
//	    				}
//	            		buffer = bos.toByteArray();
//	            		iread = buffer.length;
                    } else {
                        LogUtil.d(TAG, "-------CubeNetConnect ----  ReadThread  ---->   read thread input stream null!!!!!!!!!!!!");
                    }
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (IOException e) {
                    //close the socket
                    e.printStackTrace();
                    LogUtil.e(TAG, "-------CubeNetConnect ----  ReadThread  ---->   IP:" + mIpAddr + ",read exception!");
                    break;
                }
//	            Log.d(TAG, "Read cube socket successfully, bytes len:" + iread + CubeNetConnect.this.toString());          
                if (buffer != null && iread > 0) {
                    try {
                        mByteArrayOutputStream.write(buffer, 0, iread);
                        String tmpStr = new String(buffer, 0, iread);
                        LogUtil.d(TAG, "-------CubeNetConnect ----  ReadThread  ---->   this time receive origin:" + tmpStr + ",,,,,,111111");
                        byte[] totalBytes = mByteArrayOutputStream.toByteArray();
                        int totalLength = totalBytes.length;
                        String nowString = new String(totalBytes);
                        LogUtil.e(TAG, "-------CubeNetConnect ----  ReadThread  ---->   total nowString:" + nowString + ",,,,,,22222");
                        int dataStartOffset = nowString.indexOf(NetUtil.FIXSTRING_MOBILE);

                        if (dataStartOffset >= 0 && ((totalLength - dataStartOffset) >= NetUtil.HEADERLENGTH_MOBILE)) {
                            //3.获取头
                            byte[] headBuffer = new byte[5];
                            System.arraycopy(totalBytes, dataStartOffset + NetUtil.FIXEDLENGTH_MOBILE, headBuffer, 0, 5);
                            isEncrypt = (byte) headBuffer[0];
                            bodyLength = (((int) headBuffer[1]) << 24 & 0xff000000)
                                    + (((int) headBuffer[2]) << 16 & 0xff0000)
                                    + (((int) headBuffer[3]) << 8 & 0xff00)
                                    + (int) (headBuffer[4] & 0xff);
                            LogUtil.i(TAG, "-------CubeNetConnect ----  ReadThread  ---->   receive bodylength:" + bodyLength + ",,,3333");
                            if ((totalLength - dataStartOffset - NetUtil.HEADERLENGTH_MOBILE) >= bodyLength) {
                                if (bodyLength >= 2000) {
                                    LogUtil.i(TAG, "-------CubeNetConnect ----  ReadThread  ---->   should stop !");
                                }
                                byte[] jsonByte = new byte[bodyLength];
                                int bodyStartOffset = dataStartOffset + NetUtil.HEADERLENGTH_MOBILE;
                                System.arraycopy(totalBytes, bodyStartOffset, jsonByte, 0, bodyLength);
                                mByteArrayOutputStream.reset();
                                if ((totalLength - dataStartOffset - NetUtil.HEADERLENGTH_MOBILE) > bodyLength) {
                                    int remainLength = totalLength - bodyStartOffset - bodyLength;
                                    byte[] remainBytes = new byte[remainLength];
                                    System.arraycopy(totalBytes, bodyStartOffset + bodyLength, remainBytes, 0, remainLength);
                                    mByteArrayOutputStream.write(remainBytes);
                                }
                                String oneReceiveStr = new String(jsonByte);

//	                    Log.d(TAG, "receive tmpStr:"+tmpStr+"\n\r");
                                if (null != oneReceiveStr) {
                                    LogUtil.e(TAG, "-------CubeNetConnect ----  ReadThread  ---->   oneReceiveStr:" + oneReceiveStr + ",,,444444");
                                    if (bodyLength > 3 * 1024) {
                                        LogUtil.e(TAG, oneReceiveStr.substring(3 * 1024) + ",,5555555");
                                    }
                                    if (isEncrypt == NetUtil.WITHCHACHAENCRYPT) {
                                        parseCubeMessageBodyInfo(oneReceiveStr, bodyLength);
                                    } else {
                                        //parse json ,judge heart response or normal control response accdording to subaction
                                        parseCubeMessageBodyInfo(oneReceiveStr, bodyLength);
                                    }
                                }
                            } else {
                                LogUtil.e(TAG, "-------CubeNetConnect ----  ReadThread  ---->   should continually received ,,,66666");
                                resetHeartBeatTimeoutTask();
                                continue;
                            }
                        } else {
                            resetHeartBeatTimeoutTask();
                            LogUtil.e(TAG, "-------CubeNetConnect ----  ReadThread  ---->   no fixed header 777!!");
                            continue;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                } else {
                    LogUtil.e(TAG, "-------CubeNetConnect ----  ReadThread  ---->   IP:" + mIpAddr + ",read length error,-1!");
                    break;
                }
            }
            closeSocket(SOCKETCLOSEREASON_READIOEXCEPTION);
        }
    }

    class WriteThread extends Thread {
        public void run() {
            while (mRun) {
                //write request
                try {
                    ControlCommandInfo commandInfo = mCommandQueue.take();
                    //heartbeat 不是request，在收到回复后不用 send broadcast
                    if (!mInit) {
                        mRun = false;
                        mInit = false;
                        LogUtil.e(TAG, "-------CubeNetConnect ----  WriteThread  ---->   IP:" + mIpAddr + ",mInit,WriteThread  stop!");
                        break;
                    }
                    if (HEARTBEAT_REQUEST_ID != commandInfo.mMsgId) {
                        mRequestIdQueue.offer(commandInfo.mMsgId);
                    }
                    byte[] data = commandInfo.mJsonData;
                    if (null == mBos) {
                        LogUtil.e(TAG, "-------CubeNetConnect ----  WriteThread  ---->   mBos == NULL mrun=!!!" + mRun);
                        continue;
                    }
                    if (null != data) {
                        try {
                            mBos.write(data);
                            mBos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtil.e(TAG, "-------CubeNetConnect ----  WriteThread  ---->   IP:" + mIpAddr + ",WriteThread exception!");
                            break;
                        }
                    } else {
                        continue;
                    }

                    LogUtil.d(TAG, "-------CubeNetConnect ----  WriteThread  ---->   WriteThread::::::send request to cube:" + commandInfo + CubeNetConnect.this.toString());
                    // 等待回复
                    if (commandInfo.mMsgId != HEARTBEAT_REQUEST_ID && commandInfo.mIsRequest) {
                        mRequestTimeoutTask = new RequestTimerOutTask();
                        if (null == mRequestTimer) {
                            mRequestTimer = new Timer();
                        } else {
                            mRequestTimer.schedule(mRequestTimeoutTask, REQUESTTIMER);
                        }
                        /*synchronized (mLock) {
                            try {
								mLock.wait();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}*/
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "-------CubeNetConnect ----  WriteThread  ---->   IP:" + mIpAddr + ",WriteThread:InterruptedException");
                }
            }
            closeSocket(SOCKETCLOSEREASON_WRITEIOEXCEPTION);
        }
    }

    class RequestTimerOutTask extends TimerTask {
        @Override
        public void run() {
             /*synchronized (mLock) {
                 mLock.notifyAll();
			 }*/
            if (!mRequestIdQueue.isEmpty()) {
                String processReq = mRequestIdQueue.poll();
                String logString = CubeNetConnect.this.toString() + ",mRequestTimer processReq " + processReq + ",has been Timeout!!";
                LogUtil.e(TAG, "-------CubeNetConnect ----  RequestTimerOutTask  ---->   " + logString);
            }
        }
    }

    public void startConnect() {
        LogUtil.e(TAG, "-------CubeNetConnect -------->   startConnect() ");
        mReadThread = new ReadThread();
        mWriteThread = new WriteThread();
        mRun = true;
        mInit = true;
        mReadThread.start();
        mWriteThread.start();

        //start register
        startReigsterTask();
        mRegisterCount++;
//		resetHeartBeatSendTask();	
//		resetHeartBeatTimeoutTask();
    }

    private void startReigsterTask() {
        try {
            LogUtil.e(TAG, "-------CubeNetConnect -------->   startReigsterTask() 1111");
            JSONObject registerRequest = new JSONObject();
            registerRequest.put(Constants.JSON_COMMAND_MESSAGEID, HEARTBEAT_REQUEST_ID);
            registerRequest.put(Constants.JSON_COMMAND_ACTION, Constants.JSON_COMMAND_ACTION_REQUEST);
            registerRequest.put(Constants.JSON_COMMAND_SUBACTION, Constants.JSON_COMMAND_SUBACTION_REGISTER);
            registerRequest.put(Constants.JSON_COMMAND_CUBEID, "88888888");
            registerRequest.put(Constants.JSON_COMMAND_CUBEPWD, "222222");
            byte[] bytes = registerRequest.toString().getBytes();
            byte[] data = NetUtil.appendSendingBytes(bytes, bytes.length);
            mCommandQueue.put(new ControlCommandInfo(HEARTBEAT_REQUEST_ID, data, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送Heartbeat的TimerTask
    private void resetHeartBeatSendTask() {
        if (mHeartSendTask != null) {
            mHeartSendTask.cancel();
            mHeartSendTask = null;
        }
        mHeartSendTask = new HeartSendTask();
        new Timer().schedule(mHeartSendTask, HEARTBEATSENDTIMER, HEARTBEATSENDTIMER);
    }

    //超时轮训的TimerTask
    private void resetHeartBeatTimeoutTask() {
        if (null != mHeartTimeOutTask) {
            mHeartTimeOutTask.cancel();
            mHeartTimeOutTask = null;
        }
        mHeartTimeOutTask = new HeartTimeOutTask();
        mHeartBeatTimer.schedule(mHeartTimeOutTask, HEARTBEATCHECKTIMER);
    }

    class HeartSendTask extends TimerTask {
        @Override
        public void run() {
            try {
                LogUtil.e(TAG, "-------CubeNetConnect -----HeartSendTask --->   sendHeartBeat() 1111");
                JSONObject heartRequest = new JSONObject();
                heartRequest.put(Constants.JSON_COMMAND_MESSAGEID, HEARTBEAT_REQUEST_ID);
                heartRequest.put(Constants.JSON_COMMAND_ACTION, Constants.JSON_COMMAND_ACTION_REQUEST);
                heartRequest.put(Constants.JSON_COMMAND_SUBACTION, Constants.JSON_COMMAND_SUBACTION_HEARTBEAT);
                byte[] bytes = heartRequest.toString().getBytes();

                byte[] data = NetUtil.appendSendingBytes(bytes, bytes.length);
                mCommandQueue.put(new ControlCommandInfo(HEARTBEAT_REQUEST_ID, data, true));
                if (mSocket != null && (!mSocket.isConnected() || mSocket.isClosed())) {
                    this.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HeartTimeOutTask extends TimerTask {
        @Override
        public void run() {
            closeSocket(SOCKETCLOSEREASON_HEARTBEAT);
        }
    }

    public void closeSocket(String reason) {
        try {
            if (!mRun || !mInit) {
                LogUtil.e(TAG, "-------CubeNetConnect --------> closeSocket  IP:" + mIpAddr + ",thread has been exit::mRun=" + mRun + ",mInit=" + mInit);
//				return;
            }

            LogUtil.e(TAG, "-------CubeNetConnect -------->   IP:" + mIpAddr + ",NetError Occored reason:" + reason + ",socket closed!,please Check Module is OK??");
            /*synchronized (mLock) {
                mRun = false;
				mInit = false;
			} */
            mReadThread.interrupt();
            mWriteThread.interrupt();

            if (null != mInputStream) {
                mInputStream.close();
                mInputStream = null;
            }

            if (null != mBos) {
                mBos.close();
                mBos = null;
            }
            if (null != mHeartTimeOutTask) {
                mHeartTimeOutTask.cancel();
                mHeartTimeOutTask = null;
            }
            if (null != mHeartSendTask) {
                mHeartSendTask.cancel();
                mHeartSendTask = null;
            }
            if (null != mRequestTimeoutTask) {
                mRequestTimeoutTask.cancel();
                mRequestTimeoutTask = null;
            }
            if (null != mHeartBeatTimer) {
                mHeartBeatTimer.cancel();
                mHeartBeatTimer = null;
            }
            if (null != mRequestTimer) {
                mRequestTimer.cancel();
                mRequestTimer = null;
            }
            if (null != mSocket && !mSocket.isClosed()) {
                mSocket.close();
            }
            //清除request queue！
            mRequestIdQueue.clear();
            mCommandQueue.clear();
            if (!reason.equals(SOCKETCLOSEREASON_SERVICEDESTORY)) {
                if (null != mCallback) {
                    mCallback.removeCubeNet(mIpAddr);
                }
            } else {
                mCallback = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void writeLogToCenter(int level, String logString) {
//        LogUtil.i(TAG, "level:" + logString);
////	    	try {
////				if(WifiModuleService.mIaidlLogService != null){
////					WifiModuleService.mIaidlLogService.writeLog(level, TAG, Constants.FILE_(), Constants.LINE_(),logString);
////				}
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
//    }
}
