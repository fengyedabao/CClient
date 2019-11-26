package com.honeywell.cube.ipc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.honeywell.cube.ipc.nativeapi.P2PConn;
import com.honeywell.lib.utils.LogUtil;


public class AudioProcess implements Runnable {

    private final static int Sample_Rate = 8000;//8000;
    private final static int ChannelRecordConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private final static int ChannelPlayConfiguration = AudioFormat.CHANNEL_OUT_MONO;
    private final static int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final String TAG = "AudioProcess";
    private AudioRecord mAudioRecord = null;
    private AudioTrack mAudioTrack = null;
    private volatile boolean mStoped = false;
    private int mRecBufferSize = 0;
    private int mPlayBufferSize = 0;
    private Thread mPlayThread = null, mRecordThread = null;
    private static final int MAX_QUEUE_SIZE = 200;
    public BlockingQueue<byte[]> mPlayQueue = new LinkedBlockingQueue<byte[]>(MAX_QUEUE_SIZE);
    private String mP2PUUID = null;
    private Context mContext = null;
    private File audioFile;

    //IPVDP only support 160 bytes per package
    private byte[] mRestData = null;
    private final static int BYTESENDLEN = 160;

    public AudioProcess(Context context, String p2pUUid) {
        mP2PUUID = p2pUUid;
        mContext = context;
        //在这里我们创建一个文件，用于保存录制内容
        File fpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/files/");
        fpath.mkdirs();//创建文件夹
        //创建临时文件,注意这里的格式为.pcm
//					audioFile = File.createTempFile("recording", ".pcm", fpath);
        audioFile = new File(fpath, "recording.pcm");
        if (audioFile.exists()) {
            audioFile.delete();
        }
//				if(!audioFile.exists()){
        try {
            audioFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//				}
        mRestData = new byte[0];
    }

    public void startPhoneRecordAndPlay() throws Exception {
        LogUtil.e(TAG, "-------12345initAudioHardware() begin-");
        initAudioHardware();
        LogUtil.e(TAG, "------12345initAudioHardware() end-----");
        mStoped = false;
        //start record Thread
        mRecordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    startPhoneMicRecord();
                    LogUtil.e(TAG, "12345startPhoneMicRecord()------");
                } catch (Exception e) {
                    LogUtil.e(TAG, "12345Exception e ----- " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        mRecordThread.start();
        //start playing
        mPlayThread = new Thread(this);
        mPlayThread.start();
        LogUtil.e(TAG, "12345mPlayThread = new Thread(this);");
    }

    private void initAudioHardware() throws Exception {
        mRecBufferSize = AudioRecord.getMinBufferSize(Sample_Rate,
                ChannelRecordConfiguration, AUDIOENCODING);

        LogUtil.e(TAG, " -------- 12345initAudioHardware() ---------mRecBufferSize =  " + mRecBufferSize);
//		 mRecBufferSize = 320;


        mPlayBufferSize = AudioTrack.getMinBufferSize(Sample_Rate,
                ChannelPlayConfiguration, AUDIOENCODING);
        //recBufferSize = 4 * 1024;// 4k bytes
        //playBufferSize = 4 * 1024;
        LogUtil.e(TAG, " -------- 12345initAudioHardware() ---------mPlayBufferSize =  " + mPlayBufferSize);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, Sample_Rate,
                ChannelRecordConfiguration, AUDIOENCODING, mRecBufferSize);
        LogUtil.e(TAG, " -------- 12345initAudioHardware() ---------1111");
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Sample_Rate,
                ChannelPlayConfiguration, AUDIOENCODING, mPlayBufferSize,
                AudioTrack.MODE_STREAM);
        LogUtil.e(TAG, " -------- 12345initAudioHardware() ---------2222");
        mAudioTrack.setStereoVolume(1.0f, 1.0f);
        LogUtil.e(TAG, " -------- 12345initAudioHardware() ---------33333");
    }

    @Override
    public void run() {
        try {
            startPhoneSpkPlay();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startPhoneMicRecord() throws Exception {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));
        mAudioRecord.startRecording();

        int restLength = 0;
        while ((!Thread.interrupted()) && !mStoped) {
            if (mStoped) {
                break;
            }
            byte[] compressedVoice = new byte[mRecBufferSize];
            int num = mAudioRecord.read(compressedVoice, 0, mRecBufferSize);
            Log.i(TAG, " 12345dos.write(compressedVoice, 0, num) mRecBufferSize=" + mRecBufferSize + " ,num=" + num + ",1111111111" + ",compressedVoice = " + compressedVoice);
            dos.write(compressedVoice, 0, num);

            Log.i(TAG, "12345startPhoneMicRecord() record before encode: num=" + num + ",1111111111");


            byte[] writePCMData = new byte[num / 2];
            int length = 0;
            for (int f = 0; f < num / 2; f++) {
                int a = compressedVoice[f * 2];
                int b = compressedVoice[f * 2 + 1];
                int ab = (b << 8) + a;
                byte data = s16_to_alaw(ab);
                writePCMData[length] = data;
                length++;
            }
            //TODO
            //start write p2p audio data!
            //curCallLink.getOutputStream().write(writePCMData, 0, length);
            Log.i(TAG, "12345startPhoneMicRecord() record length=" + length + ",22222");


//			P2PConn.sendCallByteDataAById(writePCMData, length/2, mP2PUUID);
//			
//			byte[] dataRest = new byte[length - length/2];
//			System.arraycopy(writePCMData, length/2, dataRest, 0, length - length/2);
//			P2PConn.sendCallByteDataAById(dataRest, dataRest.length, mP2PUUID);

            int totalLength = restLength + length;
            byte[] totalData = new byte[totalLength];
            if (restLength > 0) {
                System.arraycopy(mRestData, 0, totalData, 0, restLength);
            }
            System.arraycopy(writePCMData, 0, totalData, restLength, length);

            int count = totalLength / BYTESENDLEN;
            Log.i(TAG, "12345sendCallByteDataAById count =" + count + " , mP2PUUID = " + mP2PUUID);
            for (int i = 0; i < count; i++) {
                byte[] sendData = new byte[BYTESENDLEN];
                System.arraycopy(totalData, i * BYTESENDLEN, sendData, 0, BYTESENDLEN);
                if (!TextUtils.isEmpty(mP2PUUID)) {
                    P2PConn.sendCallByteDataAById(sendData, BYTESENDLEN, mP2PUUID);
                    Log.i(TAG, "12345    P2PConn.sendCallByteDataAById(sendData, BYTESENDLEN, mP2PUUID);");
                }
            }
            restLength = (totalLength - count * BYTESENDLEN);
            mRestData = new byte[restLength];
            System.arraycopy(totalData, count * BYTESENDLEN, mRestData, 0, restLength);

        }
        dos.close();
    }

    byte s16_to_alaw(int pcm_val) {
        int mask = 0;
        int seg = 0;
        byte aval = 0;

        if (pcm_val >= 0) {
            mask = 0xD5;
        } else {
            mask = 0x55;
            pcm_val = -pcm_val;
            if (pcm_val > 0x7fff)
                pcm_val = 0x7fff;
        }

        if (pcm_val < 256)
            aval = (byte) (pcm_val >> 4);
        else {
            /* Convert the scaled magnitude to segment number. */
            seg = val_seg(pcm_val);
            aval = (byte) ((seg << 4) | ((pcm_val >> (seg + 3)) & 0x0f));
        }
        return (byte) (aval ^ mask);
    }

    int val_seg(int val) {
        int r = 0;
        val >>= 7;
        if ((val & 0xf0) > 0) {
            val >>= 4;
            r += 4;
        }
        if ((val & 0x0c) > 0) {
            val >>= 2;
            r += 2;
        }
        if ((val & 0x02) > 0)
            r += 1;
        return r;
    }

    private void startPhoneSpkPlay() throws Exception {
//		byte[] gsmdata = null;//new byte[mPlayBufferSize];
        int numBytesRead = 0;
//		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));

        mAudioTrack.play();
        LogUtil.e(TAG, "-------------------------startPhoneSpkPlay mStoped =  " + mStoped + " , Thread.interrupted() = " + Thread.interrupted());
        try {
            while ((!Thread.interrupted()) && !mStoped) {
                if (mStoped) {
                    break;
                }
                //TODO
                //from p2p call back read data
                //numBytesRead = curCallLink.getInputStream().read(gsmdata);
                if (mPlayQueue == null) {
                    LogUtil.e(TAG, "-------------------------mPlayQueue==null||mPlayQueue.size() ");
                    Thread.sleep(400);
                    continue;
                }
                byte[] gsmdata = mPlayQueue.take();
                LogUtil.e(TAG, "-------------------------mPlayQueue.take() gsmdata =  " + gsmdata);
//				if (numBytesRead == -1) {
//					Log.e(TAG, "startPhoneSPK exit!");
//					break;
//				}
                if (null != gsmdata) {
                    numBytesRead = gsmdata.length;
//					Log.e(TAG, "startPhoneSpkPlay numBytesRead:"+numBytesRead+",,,,,,");
                    if (numBytesRead >= 0) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        for (int f = 0; f < numBytesRead; f++) {
                            int pcm = alaw_to_s16(gsmdata[f]);
                            byte low = (byte) (pcm & 0xff);
                            byte high = (byte) ((pcm >> 8) & 0xff);
                            outputStream.write(low);
                            outputStream.write(high);
                        }
                        byte[] array = outputStream.toByteArray();
                        mAudioTrack.write(array, 0, array.length);
//						dos.write(array);
//						mAudioTrack.write(gsmdata, 0, gsmdata.length);
                    }
                }
            }
//			dos.close();
        } catch (Exception e) {
            Log.e(TAG, "startPhoneSpkPlay Exception e =" + e.getMessage(), e);
        }
    }

    private int alaw_to_s16(byte a_val) {
        int t = 0;
        int seg = 0;
        a_val ^= 0x55;
        t = a_val & 0x7f;
        if (t < 16)
            t = (t << 4) + 8;
        else {
            seg = (t >> 4) & 0x07;
            t = ((t & 0x0f) << 4) + 0x108;
            t <<= seg - 1;
        }
        return (((a_val & 0x80) > 0) ? t : -t);
    }

    public void stopPhoneRecordAndPlay() throws Exception {
        mStoped = true;
//		while ((null != mPlayThread && mPlayThread.isAlive()) || (null != mRecordThread && mRecordThread.isAlive())) {
//			Thread.sleep(100);
//		}
        // stop record
        if (null != mAudioRecord) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        // stop play
        if (null != mAudioTrack) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public void setUuid(String uuid) {
        mP2PUUID = uuid;
    }
}
