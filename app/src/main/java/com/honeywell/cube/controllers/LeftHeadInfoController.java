package com.honeywell.cube.controllers;

/**
 * Created by shushunsakai on 16/7/1.
 */
public class LeftHeadInfoController {

    public static final int HEAD_ICON_CHANGE = 0;
    public static final int OUT_SIDE_INFO_CHANGE = 1;
    public static final int IN_SIDE_INFO_CHANGE = 2;


    public static LeftHeadInfoController mController;

    public LeftHeadInfoController() {
    }

    public static LeftHeadInfoController getInstance() {

        if (mController == null) {
            mController = new LeftHeadInfoController();
        }

        return mController;
    }

//    private void



}
