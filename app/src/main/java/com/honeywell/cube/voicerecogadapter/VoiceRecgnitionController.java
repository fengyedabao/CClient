package com.honeywell.cube.voicerecogadapter;

import android.content.Context;

import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.Loger.Loger;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/7/25. 09:45
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 用于实现语音是别的业务处理
 */
public class VoiceRecgnitionController {
    private static final String TAG = VoiceRecgnitionController.class.getSimpleName();

    /**
     * 用于处理语音识别返回的数据,判断是否是我们需要的，同时进行数据处理和响应
     *
     * @param context
     * @param string
     */
    public static ScenarioLoop manageVoiceControlWithResult(Context context, String string) {
        Loger.print(TAG, "ssd receive voice recgnize result + " + string, Thread.currentThread());
        string = string.replace(",", "");
        string = string.replace("，", "");
        //获取Scenario loops
        ArrayList<ScenarioLoop> loops = ScenarioController.getScenarioList(context);
        if (loops == null || loops.size() == 0) {
            Loger.print(TAG, "ssd manage voice control loops is null", Thread.currentThread());
            return null;
        }
        for (int i = 0; i < loops.size(); i++) {
            ScenarioLoop loop = loops.get(i);
            String name = CommonUtils.transferNumToChinese(loop.mScenarioName);
            if (loop.mScenarioName.equalsIgnoreCase(string) || ChineseSpelling.judgeIsMatch(string, name)) {
                return loop;
            }
        }
        return null;
    }
}
