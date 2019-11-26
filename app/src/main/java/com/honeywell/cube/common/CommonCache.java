package com.honeywell.cube.common;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.RoomManager;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/6/22. 09:31
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个类做一些预缓存的工作
 */
public class CommonCache {
    public static final String TAG = CommonCache.class.getSimpleName();
    private static ArrayList<String> roomList = null;
    private static ArrayList<RoomLoop> roomListLoop = null;
    private static ArrayList<Integer> roomListLoopId = null;
    public static String sP2PUUID;

    public static ArrayList<RoomLoop> getRoomListLoop(Context context) {
        if (roomListLoop == null) {
            CommonCache.updateRooomList(context);
        }
        return roomListLoop;
    }

    public static ArrayList<String> getRoomNameList(Context context) {
        if (roomList == null) {
            CommonCache.updateRooomList(context);
        }
        return roomList;
    }

    public static ArrayList<Integer> getRoomIdList(Context context) {
        if (roomListLoopId == null) {
            CommonCache.updateRooomList(context);
        }
        return roomListLoopId;
    }

    public static ArrayList<RoomLoop> getRoomLoopList(Context context) {
        if (roomListLoop == null) {
            CommonCache.updateRooomList(context);
        }
        return roomListLoop;
    }


    /**
     * 获取编辑页面 Room列表
     *
     * @param context
     * @return
     */
    public synchronized static void updateRooomList(Context context) {
        Loger.print(TAG, "ssd update room List", Thread.currentThread());
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.menu_device_edit_add));
        ArrayList<Integer> returnValueId = new ArrayList<>();
        returnValueId.add(-1);
        roomListLoop = (ArrayList<RoomLoop>) RoomLoopFunc.getInstance(context).getRoomLoopAllList();
        if (roomListLoop.size() > 0) {
            for (RoomLoop loop : roomListLoop) {
                String roomStr = RoomManager.checkDefaultNameWithProtocolName(context, loop.mRoomName);
                returnValue.add(roomStr);
                returnValueId.add(loop.mPrimaryId);
            }
        } else {
            returnValue.add("");
            returnValueId.add(-1);
        }
        roomList = returnValue;
        roomListLoopId = returnValueId;
    }
}
