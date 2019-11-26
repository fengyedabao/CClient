package com.honeywell.cube.ipc;

public interface P2PCallBack {
    public void dataCallBack(byte[] data, int dataLen, /*int ssId*/String uuid);

    // 通话回调，音视频不同
    public void dataCallBackCallV(byte[] data, int dataLen, /*int ssId*/String uuid);

    public void dataCallBackCallA(byte[] data, int dataLen, /*int ssId*/String uuid);

    public void updateVideoInfo(VideoInfo info);

    public void connCallBack(int type, int status);
}
