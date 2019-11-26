package com.honeywell.cube.ipc;

public class ControlCommandInfo {
    public String mMsgId = "";
    public byte[] mJsonData = null;
    boolean mIsRequest = true;

    @Override
    public String toString() {
        String retString = null;
        if (null != mJsonData) {
            retString = "ControlCommandInfo:mRequestId:" + mMsgId + "JsonData:" + new String(mJsonData);
        } else {
            retString = super.toString();
        }
        return retString;
    }

    public ControlCommandInfo(String mCommandId, byte[] jsonByte, boolean bRequest) {
        this.mMsgId = mCommandId;
        this.mJsonData = jsonByte;
        this.mIsRequest = bRequest;
    }

}
