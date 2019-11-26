package com.honeywell.cube.controllers.UIItem.menu;

/**
 * Created by H157925 on 16/8/5. 09:23
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuAccountUIItem {
    public String loginName_up = "";//上方名称
    public String loginName_down = "";//下方名称
    public int userImageName = -1;//用户图标名称
    public int loginImageName = -1;//登录图标名称

    /**
     * cube 信息部分
     */
    public String cube_version = "";//Cube 版本
    public String cube_ip = "";//cube ip
    public String cube_mac = "";//cube mac
    public String cube_hns = "";//hns
    /**
     * 物业网络部分
     */
    public String ethip = "";//ip
    public String ethmask = "";//mask
    public String ethgw = "";//router

    public MenuAccountUIItem() {
    }

}
