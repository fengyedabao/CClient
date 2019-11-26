package com.honeywell.cube.ipc;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.view.Surface;

import com.honeywell.lib.utils.LogUtil;

public class HVideoDecoder {
    private final static String TAG = HVideoDecoder.class.getSimpleName();
    private final static String mimeType = "video/avc";
    private MediaCodec decoder;
    private MediaFormat media_format;
    private ByteBuffer[] inputBuffers;
    private BufferInfo info;
    private boolean decoderHasStarted = false;
    //for display
    public Surface videoWindow;
    //public SurfaceHolder disHolder = null;

    public boolean decodeSuccess = false;

    public HVideoDecoder(Surface videoWindow) {
        this.videoWindow = videoWindow;
        decoderHasStarted = false;
    }
    /*public void attachDispayHolder(SurfaceHolder videoWindow)
    {
		disHolder = videoWindow;
	}
	public void detachDisplay()
	{
		disHolder = null;
	}*/

    public void decoder_init(byte[] csd_info, int video_width, int video_height) {
        /* Init codec */
        /* 213 e4v */
        // byte[] csd_info = new byte[]{0, 0, 0, 1, 103, 66, -32, 30, -37, 2, -64, 73, 16, 0, 0, 0, 1, 104, -50, 48, -92, -128};
        /* byte[] csd_info = new byte[]{0,0,0,1,0x67, 0x42, (byte)0xe0, 0x2a, (byte)0xdb, 0x01, (byte)0xe0, 0x08, (byte)0x97, (byte)0x95,
                                     0,0,0,1, 0x68, (byte)0xce, 0x30, (byte)0xa4, (byte)0x80}; */
        LogUtil.d(TAG, "sps length =" + csd_info.length + " video width " + video_width + "video height " + video_height + " mimeType " + mimeType);
        ByteBuffer bb = ByteBuffer.wrap(csd_info);
        media_format = MediaFormat.createVideoFormat(mimeType, video_width, video_height);
        media_format.setByteBuffer("csd-0", bb);
        media_format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, video_width * video_height);
        media_format.setInteger("durationUs", 63446722);
        try {
            decoder = MediaCodec.createDecoderByType(mimeType);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //decoder = MediaCodec.createByCodecName("OMX.google.h264.decoder");
        LogUtil.d(TAG, "Use default video decoder");
        decoder.configure(media_format, videoWindow, null, 0);
        /*outputSurface = new CodecOutputSurface(video_width, video_height);
        decoder.configure(media_format, outputSurface.getSurface(), null, 0);*/
        decoder.start();
        inputBuffers = decoder.getInputBuffers();
        //outputBuffers = decoder.getOutputBuffers();
        info = new BufferInfo();
        decoderHasStarted = true;
        decodeSuccess = false;
    }

    public void onFrameProcess(byte[] stream_data, int stream_length, long time_stamp) {
        LogUtil.e("alinmi23", "onFrameProcess(byte[] stream_data, int stream_length, long time_stamp) decoderHasStarted = " + decoderHasStarted);
        if (decoderHasStarted) {
            //Log.d("MainActivity", "In main callback "+stream_data[0]+":"+stream_data[1]+":"+stream_data[2]+":"+stream_data[3]+":"+stream_data[4]);
            int inIndex = -1;
            try {
                inIndex = decoder.dequeueInputBuffer(1000);
            } catch (IllegalStateException e) {
                inIndex = -1;
                LogUtil.e(TAG, "IllegalStateException");
            }
            if (inIndex >= 0) {
                ByteBuffer buffer_in = inputBuffers[inIndex];
                buffer_in.clear();
                buffer_in.put(stream_data, 0, stream_length);
                /*
				Log.d("DecodeActivity", "InputBuffer:"+buffer.get(0) + " " + buffer.get(1) + " " +
						buffer.get(2) + " " + buffer.get(3) + " " + buffer.get(4) + " ");*/
                decoder.queueInputBuffer(inIndex, 0, stream_length, time_stamp, 0);
                LogUtil.e("alinmi23", "onFrameProcess  decoder.queueInputBuffer(inIndex, 0, stream_length, time_stamp, 0);");
            }
            try {
                int outIndex = decoder.dequeueOutputBuffer(info, 80000);
                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        LogUtil.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                        //outputBuffers = decoder.getOutputBuffers();
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        LogUtil.d(TAG, "New format " + decoder.getOutputFormat());
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        LogUtil.d(TAG, "dequeueOutputBuffer timed out!");
                        break;
                    default:
                        //Log.v("DecodeActivity", "We can't use this buffer but render it due to the API limit, " + buffer);
                        LogUtil.e("alinmi23", "decoder.releaseOutputBuffer(outIndex, true);");
                        if (!decodeSuccess) {
                            decodeSuccess = true;
                        }
                        decoder.releaseOutputBuffer(outIndex, true);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, " decoder.dequeueOutputBuffer Exception e = " + e.getMessage());
            }
        }
    }

    public void closeDecoder() {
        if (decoderHasStarted) {
            decoderHasStarted = false;
            try {
                decoder.stop();
                decoder.release();
            } catch (Exception e) {
                LogUtil.e(TAG, "closeDecoder Exception e " + e.getMessage());
            }
        }
    }

    public boolean decoderStarted() {
        return decoderHasStarted;
    }

    public boolean isDecodeSuccess() {
        return decodeSuccess;
    }
}
