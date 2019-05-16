package org.ar.arp2p.model;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ar.arp2p.R;


/**
 * Created by liuxiaozhong on 2017/11/9.
 */

public class Call_Record_Adapter extends BaseQuickAdapter<CallRecord,BaseViewHolder> {
    public Call_Record_Adapter() {
        super(R.layout.item_call_records);
    }

    @Override
    protected void convert(BaseViewHolder helper, CallRecord item) {
        helper.setText(R.id.tv_time,item.getTime());
        helper.setText(R.id.tv_date,item.getData());
        String mode="";
        String state="";
        if (item.getMode()==0){
            mode="视频通话";
        }else if (item.getMode()==1){
            mode="视频优先通话";
        }else if (item.getMode()==2){
            mode="音频通话";
        }

        if (item.getState()==0){
            helper.setTextColor(R.id.tv_userid, Color.parseColor("#000000"));
            state="已接通话";
        }else if (item.getState()==1){
            helper.setTextColor(R.id.tv_userid, Color.parseColor("#FF0000"));
            state="未接通话";
        }else if (item.getState()==2){
            helper.setTextColor(R.id.tv_userid, Color.parseColor("#000000"));
            state="拒接通话";
        }else if (item.getState()==3){
            helper.setTextColor(R.id.tv_userid, Color.parseColor("#000000"));
            state="已拨通话";
        }
        helper.setText(R.id.tv_userid,item.getUserid());
        helper.setText(R.id.tv_mode,mode+" - "+state);
        helper.setText(R.id.tv_time,"时长 "+item.getTime());
    }
}
