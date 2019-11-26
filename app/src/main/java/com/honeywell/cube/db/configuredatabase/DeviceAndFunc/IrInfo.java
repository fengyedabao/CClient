package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import com.honeywell.cube.common.CommonData;

/**
 * Created by H157925 on 16/4/11. 11:35
 * Email:Shodong.Sun@honeywell.com
 */
public class IrInfo {
    public long mId = -1;
    public long mDevId = -1;
    public String mIrType = "";
    public String mIrName = "";
    public int mIrLock = -1;
    public String mIrPwd = CommonData.DEFAULTSECURITYPWD;
    public int mIrId = -1;
    public int mIrSubDevId = -1;
    public String mIrKey = "";


    public IrInfo() {
        super();
    }

    public IrInfo(String mIrType, String mIrName, int mIrLock, String mIrPwd,
                  int mIrId, int mIrSubDevId, String mIrKey) {
        super();
        this.mIrType = mIrType;
        this.mIrName = mIrName;
        this.mIrLock = mIrLock;
        this.mIrPwd = mIrPwd;
        this.mIrId = mIrId;
        this.mIrSubDevId = mIrSubDevId;
        this.mIrKey = mIrKey;
    }

    @Override
    public String toString() {
        return "IrInfo [mDevId=" + mDevId + ", mIrType=" + mIrType
                + ", mIrName=" + mIrName + ", mIrTLock=" + mIrLock
                + ", mIrPwd=" + mIrPwd + ", mIrId=" + mIrId + ", mIrSubDevId="
                + mIrSubDevId + ", mIrKey=" + mIrKey + "]";
    }


}
