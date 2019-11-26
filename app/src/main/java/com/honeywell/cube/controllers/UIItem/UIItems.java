package com.honeywell.cube.controllers.UIItem;

import android.os.Parcel;
import android.os.Parcelable;

import com.honeywell.cube.db.configuredatabase.BasicLoop;

/**
 * Created by H157925 on 16/6/3. 16:19
 * Email:Shodong.Sun@honeywell.com
 */
public class UIItems implements Parcelable {
    public int type;
    public Object object;
    public String deviceType;
    /**
     * device
     */
    public boolean isSelcet = false;
    public String looptype;

    /**
     * device object
     *
     * @param type
     * @param object
     * @param deviceType
     */
    public UIItems(int type, Object object, String deviceType, String looptype, boolean isSelect) {
        this.type = type;
        this.object = object;
        this.deviceType = deviceType;
        this.isSelcet = isSelect;
        this.looptype = looptype;
    }


    public UIItems() {
        super();
    }

    //通过Parsel初始化
    public UIItems(Parcel parcel) {
        this.type = parcel.readInt();
        this.object = parcel.readParcelable(BasicLoop.class.getClassLoader());
        this.looptype = parcel.readString();
        this.deviceType = parcel.readString();
        this.isSelcet = parcel.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(type);
        parcel.writeParcelable((BasicLoop) object, flags);
        parcel.writeString(this.looptype);
        parcel.writeString(this.deviceType);
        parcel.writeInt(isSelcet ? 1 : 0);
    }

    public static final Parcelable.Creator<UIItems> CREATOR = new Creator<UIItems>() {
        public UIItems createFromParcel(Parcel source) {
            return new UIItems(source);
        }

        public UIItems[] newArray(int size) {
            return new UIItems[size];
        }

    };
}
