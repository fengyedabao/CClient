package com.honeywell.cube.ipc;

/**
 * Created by e562450 on 11/23/2015.
 */
public class VideoInfo {
    public byte [] mCsdInfo;
    public int mWidth;
    public int mHeight;
    public VideoInfo() {
        mCsdInfo = null;
        mWidth = 0;
        mHeight = 0;
    }
    public void reset() {
        mCsdInfo = null;
        mWidth = 0;
        mHeight = 0;
    }
}
