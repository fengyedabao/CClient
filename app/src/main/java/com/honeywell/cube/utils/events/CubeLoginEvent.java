package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/5/27. 13:26
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeLoginEvent extends CubeEvents {

    public CubeEvents.CubeLoginEventType type;
    public boolean success; //记录事件是否成功或者失败
    public Object item;//如果需要，则在这个数据里携带需要的参数，成功了，可能是需要显示的字段，失败了用它携带对应的错误信息

    /**
     * 构建LoginEvent事件
     * @param type－－－－event 类型
     * @param state－－－ 事件状态，成功或者失败
     * @param item－－－可选，如果需要就传递需要的参数
     */
    public CubeLoginEvent(CubeEvents.CubeLoginEventType type, boolean state, Object item)
    {
        this.type = type;
        this.success = state;
        this.item = item;
    }

}
