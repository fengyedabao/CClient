
package com.honeywell.cube.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;

import java.util.List;


public class IconTextSwitchListAdapter extends BaseAdapter {
    List<ItemBean> mDataList;

    private LayoutInflater mInflater;

    public IconTextSwitchListAdapter(List<ItemBean> list) {
        mDataList = list;
    }

    public int getItemLayout() {
        return R.layout.list_icon_text_switch;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    //然后重写getView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            holder = new ItemHolder();
            convertView = mInflater.inflate(getItemLayout(), null);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.text = (TextView) convertView.findViewById(R.id.tv_text);
//            holder.slideSwitch=(SlideSwitch)convertView.findViewById(R.id.slide_switch);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.slide_switch);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        holder.icon.setImageResource(mDataList.get(position).getIconId());
        holder.text.setText(mDataList.get(position).getText());
//        holder.slideSwitch.setState(mDataList.get(position).isSwitch());
        holder.checkBox.setChecked(false);
        return convertView;
    }

    public List<ItemBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<ItemBean> dataList) {
        this.mDataList = dataList;
    }

    protected static class ItemHolder {
        public ImageView icon;
        public TextView text;
        //       public SlideSwitch slideSwitch;
//        public Switch slideSwitch;
        public CheckBox checkBox;
    }

    public static class ItemBean {
        int mIconId;
        Drawable mIcon;
        String mText;
        boolean bSwitch;

        public ItemBean(int iconId, Drawable icon, String text, boolean slideSwitch) {
            mIconId = iconId;
            mIcon = icon;
            mText = text;
            bSwitch = slideSwitch;

        }

        public int getIconId() {
            return mIconId;
        }

        public void setIconId(int iconId) {
            this.mIconId = iconId;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public void setIcon(Drawable icon) {
            this.mIcon = icon;
        }

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            this.mText = text;
        }

        public boolean isSwitch() {
            return bSwitch;
        }

        public void setSwitch(boolean bSwitch) {
            this.bSwitch = bSwitch;
        }
    }

}
