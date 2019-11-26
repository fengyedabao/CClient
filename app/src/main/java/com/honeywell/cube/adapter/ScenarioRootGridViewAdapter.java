package com.honeywell.cube.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.honeywell.cube.R;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.lib.utils.ResourceUtil;

import java.util.ArrayList;
import java.util.List;


public class ScenarioRootGridViewAdapter extends IconTextGridAdapter {
    private static final String TAG = ScenarioRootGridViewAdapter.class.getSimpleName();
    private ArrayList<ScenarioLoop> scenarioLoops;
    private Context mContext;
    private int mMaxItems;
    private int curScenarioID = 0;

    public void setScenarioLoops(ArrayList<ScenarioLoop> loops) {
        scenarioLoops = initData(loops);
    }

    public void setCurScenarioID(int scenarioID) {
        this.curScenarioID = scenarioID;
    }

    /**
     * 传入loops
     *
     * @param context
     * @param loops
     */
    public ScenarioRootGridViewAdapter(Context context, ArrayList<ScenarioLoop> loops, boolean hadDivider, int maxItems) {
        super(new ArrayList<ItemBean>(), hadDivider);
        mContext = context;
        scenarioLoops = initData(loops);
        mMaxItems = maxItems;
    }

    private ArrayList<ScenarioLoop> initData(ArrayList<ScenarioLoop> loops) {
        ArrayList<ScenarioLoop> temp = loops;
        final int size = temp.size();
        ScenarioLoop trans;
        //对loops 进行排序，按照Scenario ID
//        for (int i = 0; i < size; i++) {
//            for (int j = i; j < size; j++) {
//                if (temp.get(j).mScenarioId < temp.get(i).mScenarioId) {
//                    trans = temp.get(i);
//                    temp.set(i, temp.get(j));
//                    temp.set(j, trans);
//                }
//            }
//        }
        List<ItemBean> itemList = new ArrayList<ItemBean>();
        for (int i = 0; i < size; i++) {
            trans = temp.get(i);
            itemList.add(new ItemBean(ResourceUtil.getResIdFromName(mContext, trans.mImageName), null, trans.mScenarioName, "", null));
        }
        setItemList(itemList);

        return temp;
    }

    @Override
    public int getCount() {
        return mMaxItems > 0 ? Math.min(mMaxItems, scenarioLoops.size()) : scenarioLoops.size();
    }

    @Override
    public ScenarioLoop getItem(int position) {
        return scenarioLoops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getScenarioId(int postion) {
        return scenarioLoops.get(postion).mScenarioId;
    }

    public void deleteItem(int position) {
        scenarioLoops.remove(position);
        updateState(0);

    }

    public void updateState(int position) {
        curScenarioID = scenarioLoops.get(position).mScenarioId;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        final ItemHolder holder = (ItemHolder) convertView.getTag();
        ScenarioLoop loop = scenarioLoops.get(position);
        if (curScenarioID == loop.mScenarioId) {
            holder.icon.setImageResource(DeviceManager.transferImageStrToInt(loop.mImageName, true));
            holder.text.setTextColor(mContext.getResources().getColor(R.color.senario_item_selector));
        } else {
            holder.icon.setImageResource(DeviceManager.transferImageStrToInt(loop.mImageName, false));
            holder.text.setTextColor(mContext.getResources().getColor(R.color.senario_item_normal));
        }
        //set icon
        holder.text.setText(loop.mScenarioName);
        return convertView;
    }


}
