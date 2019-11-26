package com.honeywell.cube.ipc;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.honeywell.lib.utils.LogUtil;

public class TextureViewListener implements TextureView.SurfaceTextureListener {
    private static final String TAG = TextureViewListener.class.getSimpleName();
    private Surface videoSurface = null;
    private HVideoDecoder videoDecoder;

    public TextureViewListener() {
        this.videoDecoder = null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        LogUtil.e("alinmi23","---onSurfaceTextureAvailable");
        if (null == videoSurface) {
            videoSurface = new Surface(arg0);
            Log.d(TAG, "surface created");
            videoDecoder = new HVideoDecoder(videoSurface);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        Log.i(TAG, "onSurfaceTextureDestroyed");
        /*if(null != videoDecoder){
            videoDecoder.detachDisplay();
		}*/
        videoDecoder = null;
        videoSurface = null;
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {
        Log.i(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        LogUtil.e("alinmi23","---onSurfaceTextureUpdated");
        //Log.i(TAG, "onSurfaceTextureUpdated");//每一帧都会调用这个函数
    }

    public Surface getSurface() {
        return videoSurface;
    }

    public void stopDecoder() {
        videoDecoder.closeDecoder();
    }

    public HVideoDecoder getVideoDecoder() {
        return videoDecoder;
    }

}
