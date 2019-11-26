package com.honeywell.cube.controllers.UIItem;

import com.honeywell.cube.db.configuredatabase.PeripheralDevice;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/7/14. 22:32
 * Email:Shodong.Sun@honeywell.com
 */
public class ScanMaiaUIItem {
    public String type = "";
    public String model_num = "";
    public String id = "";
    public int loopCount = 0;

    public String mainDeviceName = "";
    public PeripheralDevice mainDevice = null;

    public ArrayList<ScanMaiaLoopObject> deviceloops = new ArrayList<>();

    public ScanMaiaUIItem() {
    }
}
