
package com.honeywell.cube.utils;

import android.text.InputType;

import com.honeywell.cube.R;
import com.honeywell.cube.widgets.EditTextItem;

public class ViewUtil {
    public static void initIntegetEditItem(EditTextItem item) {
        if (item != null) {
            initIntegetEditItem(item, R.string.loop_id_hint, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

    public static void initIntegetEditItem(EditTextItem item, int hint, int inputType) {
        if (item != null) {
            item.setHint(hint);
            item.getEditName().setInputType(inputType);
        }
    }

    public static void initIPAddrEditItem(EditTextItem item) {
        if (item != null) {
            initIPAddrEditItem(item, R.string.ip_addr_hint);
        }
    }

    public static void initIPAddrEditItem(EditTextItem item, int hint) {
        if (item != null) {
            item.setHint(hint);
        }
    }
}
