package org.anyrtc.p2p.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;

import org.anyrtc.p2p.P2PApplication;
import org.anyrtc.p2p.R;
import org.anyrtc.p2p.model.CallRecord;
import org.anyrtc.p2p.model.Call_Record_Adapter;

import java.util.List;

/**
 * Created by liuxiaozhong on 2017/11/8.
 */

public class CallRecordDialog extends DialogFragment{

    RecyclerView recyclerView;
    Call_Record_Adapter adapter;
    private ImmersionBar mImmersionBar;
    private View space;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog, container);
        ImmersionBar.with(this, getDialog()) .init();
        recyclerView= (RecyclerView) view.findViewById(R.id.rv_call_record);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new Call_Record_Adapter();
        recyclerView.setAdapter(adapter);
        List<CallRecord> list= P2PApplication.the().getmDBDao().GetCallRecordList();
        adapter.setNewData(list);
        space=view.findViewById(R.id.space);
        view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initImmersionBar();
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this, getDialog()).titleBar(space).statusBarDarkFont(true,0.2f);
        mImmersionBar.init();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }


}
