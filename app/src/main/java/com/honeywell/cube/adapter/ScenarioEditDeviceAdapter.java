
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SeekBar;
import android.widget.TextView;


import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.widgets.HeadListViewCompat;
import com.honeywell.lib.widgets.SlideView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScenarioEditDeviceAdapter extends BaseAdapter implements SectionIndexer,
        HeadListViewCompat.HeaderAdapter, OnScrollListener {
    public static final int TYPE_EXPANDABLE = 0;
    public static final int TYPE_SIMPLE = 1;
    public static final int TYPE_SECTION = 2;
    public static final int TYPE_JUMP = 3;
    public static final int TYPE_EXPANDABLE_BACKAUDIO = 4;
    private List<Integer> mPositions;
    private List<String> mSections;

    public ArrayList<UIItems> mDataList;

    private LayoutInflater mInflater;
    private Dialog mDialog;
    protected SlideView mLastSlideViewWithStatusOn;
    protected Context mContext;

    private ArrayList<UIItems> mDeleteItems = new ArrayList<>();

    public ScenarioEditDeviceAdapter(Context context, ArrayList<UIItems> list, Dialog dialog) {
        mInflater = LayoutInflater.from(context);
        mDialog = dialog;
        mDataList = list;
        mContext = context;
        initDateHead();
    }

    public int getItemLayout(int position) {
        int layoutId = -1;
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_SECTION) {
            layoutId = R.layout.list_scenario_edit_device_section;
        } else if (itemType == TYPE_EXPANDABLE) {
            layoutId = R.layout.list_scenario_edit_device_expandable;
        } else if (itemType == TYPE_SIMPLE) {
            layoutId = R.layout.list_scenario_edit_device_simple;
        } else if (itemType == TYPE_JUMP) {
            layoutId = R.layout.list_scenario_edit_device_jump;
        }
        return layoutId;
    }

    public int getSlideViewLayout() {
        return R.layout.layout_slide_view_delete;
    }

    public ItemHolder initItemHolder(SlideView slideView, int position) {
//        slideView.setOnSlideListener(null);
        final int type = getItemViewType(position);
        ItemHolder holder = new ItemHolder(slideView);
        return holder;
//        if(type==TYPE_SECTION){
//
//        }

    }

    public void initView(final ItemHolder holder, int position) {
        final UIItems item = mDataList.get(position);
        final int type = getItemViewType(position);
        holder.mSlideView.setSlidable(!(type == TYPE_SECTION));
        if (type == TYPE_SECTION) {
            holder.mSection.setText(mContext.getString(Constants.DEVICE_TYPE_MAP.get((String) item.object)));
        } else {
            holder.mText.setText(((BasicLoop) item.object).mLoopName);
            if (type == TYPE_SIMPLE) {
                updateSimpeUI(holder, item);
                holder.cbSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateSimpleValue(item, ((CheckBox) v).isChecked());
                    }
                });
            } else if (type == TYPE_EXPANDABLE) {
                updateExpandableUI(holder, item);
                holder.cbSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        updateSimpleValue(item, ((CheckBox) v).isChecked());
                        updateExpandableValue(item, holder);
                    }
                });
                holder.sbLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateExpandableValue(item, holder);
                    }
                });
            }
        }

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


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        Log.e("alinmi2", "loopType = " + mDataList.get(position).looptype + " , deviceType = " + mDataList.get(position).deviceType);
        ItemHolder holder;
        SlideView slideView = (SlideView) convertView;
        if (null == slideView) {
            View itemView = mInflater.inflate(getItemLayout(position), null);
            slideView = new SlideView(parent.getContext(), getSlideViewLayout());
            slideView.setContentView(itemView);
            holder = initItemHolder(slideView, position);

            slideView.setTag(holder);
            slideView.setOnSlideListener(new SlideView.OnSlideListener() {
                @Override
                public void onSlide(View view, int status) {
                    if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
                        mLastSlideViewWithStatusOn.shrink();
                    }

                    if (status == SLIDE_STATUS_ON) {
                        mLastSlideViewWithStatusOn = (SlideView) view;
                    }
                }
            });
        } else {
            holder = (ItemHolder) slideView.getTag();
        }
        if (holder.mIconDelete != null) {
            holder.mIconDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(position);
                }
            });
        }
        if (holder.mIconEdit != null) {
            holder.mIconEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit(position);
                }
            });
        }
        slideView.shrink();

        initView(holder, position);
        return slideView;
    }

    public ArrayList<UIItems> getDataList() {
        return mDataList;
    }

    public ArrayList<UIItems> getSelectedList() {
        ArrayList<UIItems> selectedItems = new ArrayList<>();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final UIItems item = mDataList.get(i);
                if (item.type != ModelEnum.UI_TYPE_TITLE) {
                    selectedItems.add(item);
                }
            }
        }
        return selectedItems;
    }

    public void setDataList(ArrayList<UIItems> dataList) {
        this.mDataList = dataList;
    }

    public ArrayList<UIItems> getDeleteItems() {
        return mDeleteItems;
    }

    public void showDialog() {
        if (mDialog != null) {//&& !mDialog.isShowing()
            mDialog.show();
        }
    }

    protected void delete(int position) {
        final UIItems item = mDataList.get(position);
        mDeleteItems.add(item);
        mDataList.remove(position);

        final int size = mDataList.size();
        int pos = 0;
        if (size == 1) {
            mDataList.remove(0);
        } else {
            for (int i = 0; i < size; i++) {
                if (mDataList.get(i).deviceType.equals(item.deviceType)) {
                    pos = i;
                    break;
                }
            }
            if (pos == size - 1 || (!mDataList.get(pos + 1).deviceType.equals(item.deviceType))) {
                mDataList.remove(pos);
            }

        }
        initDateHead();
        notifyDataSetChanged();
    }

    protected void edit(int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return getItemType(mDataList.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    private void initDateHead() {
        mSections = new ArrayList<>();
        mPositions = new ArrayList<>();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final UIItems item = mDataList.get(i);
                if (getItemType(item) == TYPE_SECTION) {
                    mSections.add(mContext.getString(Constants.DEVICE_TYPE_MAP.get(item.object)));
                    mPositions.add(i);
                }
            }
        }
    }

    private int getItemType(UIItems item) {
        if (item.type == ModelEnum.UI_TYPE_TITLE) {
            return TYPE_SECTION;
        } else if (isSimpleType(item)) {
            return TYPE_SIMPLE;
        } else if (isExpandableType(item)) {
            return TYPE_EXPANDABLE;
        } else if (isJumpType(item)) {
            return TYPE_JUMP;
        }
//        else if (ModelEnum.LOOP_BACKAUDIO.equals(item.looptype)) {
//            return TYPE_EXPANDABLE_BACKAUDIO;
//        }
        else {
            return TYPE_SIMPLE;
        }

    }

    private boolean checkSparkLightingExpandable(SparkLightingLoop loop) {
//        Log.e("alinmi2", "loop.mLoopType = " + loop.mLoopType);
        if (loop.mLoopType == ModelEnum.LOOP_TYPE_SWITCH_INT ||
                loop.mLoopType == ModelEnum.LOOP_TYPE_CURTAIN_INT ||
                loop.mLoopType == ModelEnum.LOOP_TYPE_RELAY_INT) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isSimpleType(UIItems item) {
        final String loopType = item.looptype;
        if (ModelEnum.WIRELESS_315_433.equals(loopType) ||
                ModelEnum.LOOP_RELAY.equals(loopType)) {
            return true;
        } else if (ModelEnum.SPARKLIGHTING.equals(loopType)) {
            return !checkSparkLightingExpandable((SparkLightingLoop) item.object);
        } else {
            return false;
        }
    }


    private void updateSimpleValue(UIItems item, boolean isChecked) {
        final String loopType = item.looptype;
        if (ModelEnum.WIRELESS_315_433.equals(loopType)) {
            Wireless315M433MLoop loop = (Wireless315M433MLoop) item.object;
            ((Wireless315M433MLoop) item.object).customStatus.status = isChecked;
        } else if (ModelEnum.LOOP_RELAY.equals(loopType)) {
            ((RelayLoop) item.object).customStatus.status = isChecked;
        } else if (ModelEnum.SPARKLIGHTING.equals(loopType)) {
            ((SparkLightingLoop) item.object).customStatus.status = isChecked;
        }
    }

    private void updateSimpeUI(ItemHolder holder, UIItems item) {
        final String loopType = item.looptype;
        boolean isChecked = false;
        if (ModelEnum.WIRELESS_315_433.equals(loopType)) {
            isChecked = ((Wireless315M433MLoop) item.object).customStatus.status;
        } else if (ModelEnum.LOOP_RELAY.equals(loopType)) {
            isChecked = ((RelayLoop) item.object).customStatus.status;
        } else if (ModelEnum.SPARKLIGHTING.equals(loopType)) {
            isChecked = ((SparkLightingLoop) item.object).customStatus.status;
        }
        holder.cbSwitch.setChecked(isChecked);
    }

    private boolean isJumpType(UIItems item) {
        final String loopType = item.looptype;
        if (ModelEnum.LOOP_IR.equals(loopType) ||
                ModelEnum.LOOP_IR_DVD.equals(loopType) ||
                ModelEnum.LOOP_IR_TV.equals(loopType) ||
                ModelEnum.LOOP_IR_STB.equals(loopType) ||
                ModelEnum.LOOP_IR_AC.equals(loopType) ||
                ModelEnum.LOOP_IR_CUSTOM.equals(loopType) ||
                ModelEnum.LOOP_VENTILATION.equals(loopType) ||
                ModelEnum.LOOP_BACNET.equals(loopType) ||
                ModelEnum.WIFI_485.equals(loopType)
                ) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isExpandableType(UIItems item) {
        final String loopType = item.looptype;
        if (ModelEnum.LOOP_BACKAUDIO.equals(loopType)) {
            return true;
        } else if (ModelEnum.SPARKLIGHTING.equals(loopType)) {
            return checkSparkLightingExpandable((SparkLightingLoop) item.object);
        } else {
            return false;
        }
    }

    private void updateExpandableValue(UIItems item, ItemHolder holder) {
        final String loopType = item.looptype;
        if (ModelEnum.LOOP_BACKAUDIO.equals(loopType)) {
            BackaudioLoop loop = (BackaudioLoop) item.object;
            loop.customModel.playstatus = holder.cbSwitch.isChecked() ? "play" : "pause";
            loop.customModel.volume = holder.sbLight.getProgress();
        } else if (ModelEnum.SPARKLIGHTING.equals(loopType)) {
            SparkLightingLoop loop = (SparkLightingLoop) item.object;
            loop.customStatus.openClosePercent = holder.sbLight.getProgress();
            loop.customStatus.status = holder.cbSwitch.isChecked();
        }
    }

    private void updateExpandableUI(ItemHolder holder, UIItems item) {
        final String loopType = item.looptype;
        if (ModelEnum.LOOP_BACKAUDIO.equals(loopType)) {
            BackaudioLoop loop = (BackaudioLoop) item.object;
            holder.mVolume.setVisibility(View.VISIBLE);
            holder.sbLight.setProgress(loop.customModel.volume);
            holder.sbLight.setMax(31);

            holder.cbSwitch.setChecked("play".equalsIgnoreCase(loop.customModel.playstatus));
        } else if (ModelEnum.SPARKLIGHTING.equals(loopType)) {
            SparkLightingLoop loop = (SparkLightingLoop) item.object;
            holder.mVolume.setVisibility(View.GONE);
            holder.sbLight.setProgress(loop.customStatus.openClosePercent);
            holder.sbLight.setMax(100);
            holder.cbSwitch.setChecked(loop.customStatus.status);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof HeadListViewCompat) {
            ((HeadListViewCompat) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public int getHeaderState(int position) {
        int realPosition = position;
        if (realPosition < 0 || position >= getCount()) {
            return HEADER_GONE;
        }
        int section = getSectionForPosition(realPosition);
        int nextSectionPosition = getPositionForSection(section + 1);
        if (nextSectionPosition != -1 && realPosition == nextSectionPosition - 1) {
            return HEADER_PUSHED_UP;
        }
        return HEADER_VISIBLE;
    }

    @Override
    public void configureHeader(View header, int position, int alpha) {
        int realPosition = position;
        int section = getSectionForPosition(realPosition);
        String title = (String) getSections()[section];
        ((TextView) header.findViewById(R.id.tv_section)).setText(title);

    }

    @Override
    public Object[] getSections() {
        return mSections.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
            return -1;
        }
        return mPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position < 0 || position >= getCount()) {
            return -1;
        }
        int index = Arrays.binarySearch(mPositions.toArray(), position);
        return index >= 0 ? index : -index - 2;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    protected static class ItemHolder {
        protected ImageView mIconDelete;
        protected ImageView mIconEdit;
        public ImageView ivExpandable;
        public SeekBar sbLight;
        public CheckBox cbSwitch;
        public TextView mSection;
        public CheckBox mSelect;
        public TextView mText;
        public SlideView mSlideView;
        public ImageView mVolume;

        public ItemHolder(SlideView view) {
            mIconDelete = (ImageView) view.findViewById(R.id.iv_delete);
            mIconEdit = (ImageView) view.findViewById(R.id.iv_edit);
            ivExpandable = (ImageView) view.findViewById(R.id.iv_expandable);
            sbLight = (SeekBar) view.findViewById(R.id.sb_light);
            cbSwitch = (CheckBox) view.findViewById(R.id.cb_switch);
            mSection = (TextView) view.findViewById(R.id.tv_section);
            mText = (TextView) view.findViewById(R.id.tv_name);
            mSelect = (CheckBox) view.findViewById(R.id.cb_selected);
            mVolume = (ImageView) view.findViewById(R.id.iv_volume);
            mSlideView = view;
        }
    }

}
