package com.honeywell.cube.controllers.UIItem.notifi;

import org.json.JSONObject;

/**
 * Created by H157925 on 16/7/12. 11:10
 * Email:Shodong.Sun@honeywell.com
 */
public class NotifiUIItem {
    //UI
    public int image_id = -1;

    public int item_type = 0;//section 或者 cell
    public String title_day = "";
    public String title_month = "";

    public String title_cell = "";
    public String room_str = "";
    public String time_str = "";
    public boolean show_video_btn = false;
    public boolean unReaded = false;//是否已读

    public JSONObject CellDic = null;

    public NotifiUIItem() {
    }

    @Override
    public String toString() {
        return "notification item : [ "
                + " image_id : " + image_id
                + " item_type : " + item_type
                + " title_month : " + title_month
                + " title_day : " + title_day
                + " title_cell : " + title_cell
                + " room_str : " + room_str
                + " time_str : " + time_str
                + " show_video_btn : " + show_video_btn
                + " unReaded : " + unReaded
                + " ];";
    }
}
