package com.honeywell.cube.net.autoupdate;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.honeywell.cube.R;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.lib.utils.ApplicationUtil;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ToastUtil;

public class UpdateManager {
    private static final String TAG = UpdateManager.class.getSimpleName();

    private static final String ANDROID_APP = "android_app";
    private static final String URL = "url";
    private static final String VERSION = "version";

    private static final int UPDATE_CHECKCOMPLETED = 1;
    private static final int UPDATE_DOWNLOADING = 2;
    private static final int UPDATE_DOWNLOAD_ERROR = 3;
    private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
    private static final int UPDATE_DOWNLOAD_CANCELED = 5;


    private Context mContext;

    private ProgressDialog updateProgressDialog;

    private String mCurrentVersion;//
    private String mNewVersion;

    private String mCheckUpdateUrl;
    private String mDownloadUrl;

    //    public static final String URI_UPDATE_TEXT = NetConstant.URI_ALL + "/autoupdate/update_version.txt";
//    public static final String URI_UPDATE_APK = NetConstant.URI_ALL + "/autoupdate/cube.apk";
    //	public static final String UPDATE_DOWNURL = "http://192.168.20.26:8081/test001_UpdateServer/update_test.apk";
//	public static final String UPDATE_CHECKURL = "http://192.168.20.26:8081/test001_UpdateServer/update_version.txt";
    private String updateApkName = "";

    private boolean showNoUpdate;
    private int progress;
    private Boolean mHasNewVersion;
    private Boolean canceled;

    // private String savefolder = Environment.getExternalStorageDirectory();
    private String savefolder = "";
    private String updateSaveName = "CubeApp.apk";

    private UpdateListener mUpdateListener;

    public UpdateManager(Context context, Boolean flag) {
        mContext = context;
        this.showNoUpdate = flag;
        savefolder = context.getFilesDir().toString();
        canceled = false;
        mCurrentVersion = ApplicationUtil.getCurrentVersionName(mContext);
        mCheckUpdateUrl = NetConstant.URI_ALL + "/upgrade/00100001/android_app/" + mCurrentVersion;
    }

    public boolean NetWorkStatus() {
        if (!NetHelper.checkNetWorkStatus(mContext)) {
            ToastUtil.showLong(mContext, R.string.no_net, true);
            return false;
        }
        return true;
    }

    public void checkUpdate() {
        checkUpdate(mCheckUpdateUrl);
    }

    public void checkUpdate(final String uri) {
        mHasNewVersion = false;
        new Thread() {
            @Override
            public void run() {
                LogUtil.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() uri = " + uri);
                try {
                    String verjson = NetHelper.httpStringGet(uri);
                    LogUtil.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>verjson  = " + verjson);
                    JSONObject result = new JSONObject(verjson);
                    JSONObject object = result.getJSONObject(ANDROID_APP);
                    mNewVersion = object.getString(VERSION);
                    mDownloadUrl = object.getString(URL);
                    LogUtil.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>mCurrentVersion  = " + mCurrentVersion + ", mNewVersion = " + mNewVersion + " , mDownloadUrl = " + mDownloadUrl);
                    mHasNewVersion = hasNewVersion();
                } catch (Exception e) {
                    LogUtil.e(TAG, "Exception " + e.getMessage(), true);
//                    mHasNewVersion = true;
//                    mNewVersionCode = 222222;
//                    mNewVersion = "新版本";
//                    updateInfo = "updateInfo";
//                    updateApkName = "updateApkName";
//                    updateSaveName = "updateSaveName";
                }
                updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
            }
        }.start();
    }

    private boolean hasNewVersion() {
        String[] currentVersion = mCurrentVersion.split("\\.");
        String[] newVersion = mNewVersion.split("\\.");
        if (currentVersion == null || newVersion == null || currentVersion.length != newVersion.length) {
            LogUtil.e(TAG, "version code's format is wrong", true);
        }
        try {
            for (int i = 0; i < currentVersion.length; i++) {
                if (Integer.parseInt(currentVersion[i]) < Integer.parseInt(newVersion[i])) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "parseInt error " + e.getMessage(), true);
        }
        return false;
    }

    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), updateSaveName)), "application/vnd.android.package-archive");
        ((Activity) mContext).startActivityForResult(intent, 6);
    }


    public void download(final String uri) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File ApkFile = new File(Environment.getExternalStorageDirectory(), updateSaveName);
                    if (ApkFile.exists()) {
                        ApkFile.delete();
                    }

                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[512];

                    do {

                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING));
                        if (numread <= 0) {
                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!canceled);
                    if (canceled) {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                    }
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    LogUtil.e(TAG, e.getMessage(), true);

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR, e.getMessage()));
                } catch (IOException e) {
                    LogUtil.e(TAG, e.getMessage(), true);

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR, e.getMessage()));
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage(), true);
                }

            }
        }.start();
    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_CHECKCOMPLETED:
                    checkUpdateCompleted(mHasNewVersion, mNewVersion, "");
                    break;
                case UPDATE_DOWNLOADING:
                    downloadProgressChanged(progress);
                    break;
                case UPDATE_DOWNLOAD_ERROR:
                    downloadCompleted(false, msg.obj.toString());
                    break;
                case UPDATE_DOWNLOAD_COMPLETED:
                    downloadCompleted(true, "");
                    break;
                case UPDATE_DOWNLOAD_CANCELED:
                    downloadCanceled();
                default:
                    break;
            }
        }
    };


    public void downloadProgressChanged(int progress) {
        if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
            updateProgressDialog.setProgress(progress);
        }

    }

    public void downloadCompleted(Boolean sucess, String errorMsg) {
        if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
            updateProgressDialog.dismiss();
        }
        if (sucess) {
            update();
        } else {
            new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getText(R.string.dialog_error_title))
                    .setMessage(mContext.getText(R.string.dialog_downfailed_msg))
                    .setPositiveButton(mContext.getText(R.string.dialog_downfailed_btndown)
                            , new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    download(mDownloadUrl);
                                }
                            })
                    .setNegativeButton(mContext.getText(R.string.dialog_downfailed_btnnext), null)
                    .show();
        }
    }

    public void downloadCanceled() {
        if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
            updateProgressDialog.dismiss();
        }
    }

    public void checkUpdateCompleted(Boolean hasUpdate, String mNewVersion, String updateInfo) {
        if (hasUpdate) {
            new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getText(R.string.dialog_update_title))
                    .setMessage(mContext.getText(R.string.dialog_update_msg)
                            + mNewVersion + System.getProperty("line.separator")
                            + updateInfo + System.getProperty("line.separator")
                            + mContext.getText(R.string.dialog_update_msg2))
                    .setPositiveButton(mContext.getText(R.string.dialog_update_btnupdate)
                            , new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    updateProgressDialog = new ProgressDialog(mContext);
                                    updateProgressDialog.setMessage(mContext.getText(R.string.dialog_downloading_msg));
                                    updateProgressDialog.setIndeterminate(false);
                                    updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    updateProgressDialog.setMax(100);
                                    updateProgressDialog.setProgress(0);
                                    updateProgressDialog.setCanceledOnTouchOutside(false);
                                    updateProgressDialog.show();
                                    download(mDownloadUrl);
                                    if (mUpdateListener != null) {
                                        mUpdateListener.update();
                                    }
                                }
                            })
                    .setNegativeButton(mContext.getText(R.string.dialog_update_btnnext), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mUpdateListener != null) {
                                mUpdateListener.updateNext();
                            }
                        }
                    })
                    .setCancelable(false)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    })
//                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//
//                        }
//                    })
                    .show();
        } else {
            if (mUpdateListener != null) {
                mUpdateListener.noUpdate();
            }
            if (showNoUpdate) {
                ToastUtil.showShort(mContext, R.string.is_latest_version, true);
            }
        }
    }

    public void setUpdateListener(UpdateListener listener) {
        mUpdateListener = listener;
    }

    public interface UpdateListener {
        public void noUpdate();

        public void updateNext();

        public void update();
    }
}