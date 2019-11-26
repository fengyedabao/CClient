package com.honeywell.cube.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.dialogs.WeekSelectDialog;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ResourceUtil;

import java.util.List;

/**
 * Created by milton on 16/6/7.
 */
public class WeekSelectItem extends RelativeLayout {

    private static String NEVER = "从不";
    private static String EVERYDAY = "每天";
    private static String WORKDAY = "工作日";
    private static String WEEKEND = "周末";
    private boolean[] mSelected = new boolean[]{false, false, false, false, false, false, false};
    private String[] mWeek = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private List<BottomDialog.ItemBean> mDataList;
    private WeekSelectDialog mSelectDialog;
    private TextView mName;
    private TextView mContent;


    public WeekSelectItem(Context context) {
        super(context);
    }

    public WeekSelectItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.widget_select_item, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mName = (TextView) findViewById(R.id.tv_name);
        mContent = (TextView) findViewById(R.id.tv_content);
        mWeek = ResourceUtil.getStringArray(getResources(), R.array.week);
        NEVER = getResources().getString(R.string.weekdays_never);
        EVERYDAY = getResources().getString(R.string.weekdays_everyday);
        WORKDAY = getResources().getString(R.string.weekdays_workday);
        WEEKEND = getResources().getString(R.string.weekdays_weekend);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
//                Intent intent = new Intent(getContext(), SelectWeekActivity.class);
//                getContext().startActivity(intent);
            }
        });
    }

    public WeekSelectItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setName(int resId) {
        if (mName == null) {
            mName = (TextView) findViewById(R.id.tv_name);
        }
        if (mName != null) {
            mName.setText(resId);
        }
    }

    public void setName(String name) {
        if (mName == null) {
            mName = (TextView) findViewById(R.id.tv_name);
        }
        if (mName != null) {
            mName.setText(name);
        }
    }

    public void setContent(boolean[] selected) {
        setContent(transfString(selected), false);
    }

    public void setContent(String name, boolean transf) {
        if (mContent == null) {
            mContent = (TextView) findViewById(R.id.tv_content);
        }
        if (mContent != null) {
            if (transf) {
                transfBoolean(name);
            }
            mContent.setText(name);
        }
    }

    public void setContent(String name) {
        setContent(name, true);
    }

    private boolean[] transfBoolean(String time) {
        mSelected = new boolean[]{false, false, false, false, false, false, false};
        if (EVERYDAY.equalsIgnoreCase(time)) {
            mSelected = new boolean[]{true, true, true, true, true, true, true};
        } else if (WORKDAY.equalsIgnoreCase(time)) {
            mSelected = new boolean[]{true, true, true, true, true, false, false};
        } else if (WEEKEND.equalsIgnoreCase(time)) {
            mSelected = new boolean[]{false, false, false, false, false, true, true};
        } else if (NEVER.equalsIgnoreCase(time)) {
            mSelected = new boolean[]{false, false, false, false, false, false, false};
        } else {
            if (!TextUtils.isEmpty(time) && time.contains(",")) {
                String[] result = time.split(",");
                final int size = result.length;
                for (int i = 0; i < size; i++) {
                    checkWeek(result[i], mSelected);
                }
            } else {
                checkWeek(time, mSelected);
            }
        }
        return mSelected;
    }

    private void checkWeek(String week, boolean[] selected) {
        if (mWeek[0].equals(week)) {
            selected[0] = true;
        } else if (mWeek[1].equals(week)) {
            selected[1] = true;
        } else if (mWeek[2].equals(week)) {
            selected[2] = true;
        } else if (mWeek[3].equals(week)) {
            selected[3] = true;
        } else if (mWeek[4].equals(week)) {
            selected[4] = true;
        } else if (mWeek[5].equals(week)) {
            selected[5] = true;
        } else if (mWeek[6].equals(week)) {
            selected[6] = true;
        }
    }

    private String transfString(boolean[] selected) {
        if (selected != null && selected.length == 7) {
            mSelected = selected;
            if (selected[0] && selected[1] && selected[2] && selected[3] && selected[4] && selected[5] && selected[6]) {
                return EVERYDAY;
            } else if (selected[0] && selected[1] && selected[2] && selected[3] && selected[4] && (!selected[5]) && (!selected[6])) {
                return WORKDAY;
            } else if ((!selected[0]) && (!selected[1]) && (!selected[2]) && (!selected[3]) && (!selected[4]) && selected[5] && selected[6]) {
                return WEEKEND;
            } else if (!(selected[0] || selected[1] || selected[2] || selected[3] || selected[4] || selected[5] || selected[6])) {
                return NEVER;
            } else {
                String result = (selected[0] ? "," + mWeek[0] : "") + (selected[1] ? "," + mWeek[1] : "") + (selected[2] ? "," + mWeek[2] : "") + (selected[3] ? "," + mWeek[3] : "") + (selected[4] ? "," + mWeek[4] : "") + (selected[5] ? "," + mWeek[5] : "") + (selected[6] ? "," + mWeek[6] : "");
                return result.replaceFirst(",", "");
            }
        } else {
            LogUtil.e("WeekSelectItem", "aaaaaaaaaaa ");
        }
        return "";
    }

    public TextView getContent() {
        return mContent;
    }

    public String getContentText() {
        return mContent == null ? "" : mContent.getText().toString();
    }


    public void setDataList(List<BottomDialog.ItemBean> list) {
        mDataList = list;
    }

    public List<BottomDialog.ItemBean> getDataList() {
        return mDataList;
    }


    private void showSelectDialog() {
        if (null == mSelectDialog) {
            mSelectDialog = new WeekSelectDialog(getContext());
            mSelectDialog.setViewCreateListener(new WeekSelectDialog.ViewCreateListener() {
                @Override
                public void initTitle(TextView title) {
                    title.setText(R.string.repeat);
                }

                @Override
                public void initRightIcon(ImageView right) {
                    right.setImageResource(R.mipmap.nav_done);
                    right.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setContent(((WeekSelectDialog.ListAdapter) mSelectDialog.getContent().getAdapter()).getDataList());
                            mSelectDialog.dismiss();
                        }
                    });
                }

                @Override
                public void initLeftIcon(ImageView left) {
                    left.setImageResource(R.mipmap.nav_back_normal);
                    left.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectDialog.dismiss();
                        }
                    });
                }

                @Override
                public void initContent(ListView content) {

                    content.setAdapter(new WeekSelectDialog.ListAdapter(getContext(), mSelected));
                }
            });
            mSelectDialog.show();
        } else {
            if (!mSelectDialog.isShowing()) {
                WeekSelectDialog.ListAdapter adapter = (WeekSelectDialog.ListAdapter) mSelectDialog.getContent().getAdapter();
                adapter.setDataList(mSelected);
                mSelectDialog.show();
            }
        }
    }
}
