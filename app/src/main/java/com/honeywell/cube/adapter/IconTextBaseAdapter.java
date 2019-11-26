
package com.honeywell.cube.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;

import java.util.List;


public abstract class IconTextBaseAdapter extends BaseAdapter {
    List<ItemBean> mItemList;

    private LayoutInflater mInflater;

    public IconTextBaseAdapter(List<ItemBean> list) {
        mItemList = list;
    }

    public abstract int getItemLayout();

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

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
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        holder.icon.setImageResource(mItemList.get(position).getIconId());
        holder.text.setText(mItemList.get(position).getText());
        return convertView;
    }

    public List<ItemBean> getItemList() {
        return mItemList;
    }

    public void setItemList(List<ItemBean> itemList) {
        this.mItemList = itemList;
    }

    protected static class ItemHolder {
        public ImageView icon;
        public TextView text;
    }

    public static class ItemBean {
        public int mIconId;
        public Drawable mIcon;
        public String mText;
        public String mType;
        public Object mObject;

        public ItemBean(int iconId, Drawable icon, String text, String type, Object object) {
            mIconId = iconId;
            mIcon = icon;
            mText = text;
            mType = type;
            mObject = object;
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

        public String getType() {
            return mType;
        }

        public void setType(String type) {
            this.mType = type;
        }
    }

}
