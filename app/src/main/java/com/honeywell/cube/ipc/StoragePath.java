package com.honeywell.cube.ipc;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.honeywell.cube.R;
import com.honeywell.lib.utils.DebugUtil;
import com.honeywell.lib.utils.FileUtil;

/**
 * Created by milton on 16/6/21.
 */
public class StoragePath {
    private static final String TAG = "StoragePath";
    private static String CD_S_SdcardPath = "";
    private static String CD_S_SdcardPathAbsolute = "";
    private static final String CT_S_Sdcard_Sign_Storage_emulated = "storage/emulated/";
    private static final String CT_S_Sdcard_Sign_Storage_sdcard = "storage/sdcard";
    private static final String ExternalSdCard = "/external_sd";
    private static final String SDCARDDIR = "IPC";

//    public static String path() {
//        //check if path can access
//        File file = new File(getSdcardPath());
//        if (!file.exists()) {
//            Log.d("StoragePath Log", "Path:" + getSdcardPath() + ", not exist");
//            //try /sdcard/
//            File sdpath = new File("/sdcard/");
//            if (!sdpath.exists()) {
//                Log.d("StoragePath Log", "/sdcard/ not exist");
//                //then maybe we should use /data/data/app/
//            }
//            return "/sdcard/";
//        }
//        return getSdcardPath() + "/";
//    }

    public static Bitmap getImagePathFromSD2(Context context, String imagePath) {

        return BitmapFactory.decodeFile(FileUtil.generateAbsoluteFilePath(context, imagePath));
    }


    /*
     * ====================================================================
     * Method Name :getDbPathFromSD
     * Description : Get file path from the SD card
     * Referenced Global Variables :
     * Parameters :String
     * Returns :String
     * ====================================================================
     */
//    public static String getFilePathFromSD(String FilePath) {
//        return path() + FilePath;
//    }

//    public static String getSdcardPath() {
//        File externalSD = new File(Environment.getExternalStorageDirectory().getParent() + ExternalSdCard);
//        if (externalSD.exists() && externalSD.isDirectory() && externalSD.canRead()) {
//            CD_S_SdcardPath = Environment.getExternalStorageDirectory().getParent() + ExternalSdCard;
//        } else {
//            CD_S_SdcardPath = Environment.getExternalStorageDirectory().getPath();
//        }
//        return checkAndReplaceEmulatedPath(CD_S_SdcardPath);
//    }

//    public static String getAbsoluteSdcardPath() {
//        CD_S_SdcardPathAbsolute = Environment.getExternalStorageDirectory().getAbsolutePath();
//        return checkAndReplaceEmulatedPath(CD_S_SdcardPathAbsolute);
//    }

//    public static String checkAndReplaceEmulatedPath(String strSrc) {
//        Pattern p = Pattern.compile("/?storage/emulated/\\d{1,2}");//some use /storage/emulated/legacy
//        Matcher m = p.matcher(strSrc);
//        if (m.find()) {
//            strSrc = strSrc.replace(CT_S_Sdcard_Sign_Storage_emulated, CT_S_Sdcard_Sign_Storage_sdcard);
//        }
//        return strSrc;
//    }

    public static boolean checkAndCreateFolder(Context context, String strFolder) {
        String fullPath = FileUtil.generateAbsoluteFilePath(context, strFolder);
//        if (strFolder.startsWith("/"))
//            fullPath = getSdcardPath() + strFolder;
//        else
//            fullPath = getSdcardPath() + "/" + strFolder;
//        Log.d("StoragePath Log", fullPath);
        File file = new File(fullPath);
        if (!file.exists()) {
            Log.d("StoragePath Log", "Create file path.");
            if (file.mkdirs()) {
                Log.d("StoragePath Log", "Create OK!");
                return true;
            } else {
                Log.d("StoragePath Log", "Create Failed!");
                return false;
            }
        }
        return true;
    }

    // check p2p conf dir
    public static String checkP2PDir(Context ctx) {
        String configDir = FileUtil.generateAbsoluteFilePath(ctx, "");
        Log.d(TAG, "sdcard:" + configDir);
        StoragePath.checkAndCreateFolder(ctx, Constants.APPSDCARDDIR + "/" + SDCARDDIR);
        configDir = configDir + Constants.APPSDCARDDIR + "/" + SDCARDDIR;
        String configFile = configDir + "/Configuration.ini";
        File conffile = new File(configFile);
        if(conffile.exists()){
            conffile.delete();
        }
        if (!conffile.exists()) {
            // 不存在，copy过去
            try {
                conffile.createNewFile();
                InputStream input = ctx.getResources().openRawResource(DebugUtil.DEBUG?R.raw.p2pconfig_debug:R.raw.p2pconfig);
                FileOutputStream out = new FileOutputStream(conffile);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                input.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configDir;
    }
    //其他有用信
    //getDataDirectory()：用户数据目录。
    //getDownloadCacheDirectory()：下载缓存内容目录。
    //getExternalStorageDirectory()：主要的外部存储目录。
}
