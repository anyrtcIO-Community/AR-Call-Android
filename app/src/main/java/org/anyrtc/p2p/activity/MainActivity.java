package org.anyrtc.p2p.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.anyrtc.p2p.LocalBroadcastManager;
import org.anyrtc.p2p.P2PApplication;
import org.anyrtc.p2p.R;
import org.anyrtc.p2p.utils.ExampleUtil;
import org.anyrtc.p2p.utils.SharePrefUtil;
import org.anyrtc.rtp2pcall.RTP2PCallKit;

public class MainActivity extends BaseActivity implements View.OnClickListener,BaseActivity.ConnListener {


    TextView tvCall, tvId, tvGo;
    private String mUserid = "";
    private RTP2PCallKit mP2PKit;
    private EditText etId;
    ImageView icon;
    public static boolean isTurnOn;
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        registerMessageReceiver();
        mImmersionBar.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        tvCall = (TextView) findViewById(R.id.tv_call);
        tvGo = (TextView) findViewById(R.id.tv_go_pre);
        tvId = (TextView) findViewById(R.id.tv_userid);
        etId = (EditText) findViewById(R.id.et_id);
        icon = (ImageView) findViewById(R.id.icon);
        tvCall.setOnClickListener(this);
        tvGo.setOnClickListener(this);
        mP2PKit = P2PApplication.the().getmP2pKit();
        setConnListener(this);
        mUserid = SharePrefUtil.getString("userid");
        DealUserId();

    }

    private void DealUserId() {
        if (!TextUtils.isEmpty(mUserid)) {
            tvId.setText(mUserid + "");
            mP2PKit.turnOn(mUserid);
            init();
        } else {
            tvId.setText(  "");
            etId.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
            Back();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go_pre:
                if (!TextUtils.isEmpty(mUserid)) {
                    startAnimActivity(PreCallActivity.class);
                    return;
                }
                if (TextUtils.isEmpty(etId.getText().toString().trim())){
                    Toast.makeText(this,"请输入手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                mUserid=etId.getText().toString().trim();
                SharePrefUtil.putString("userid",mUserid);
                mP2PKit.turnOn(mUserid);
                startAnimActivity(PreCallActivity.class);
                break;
            case R.id.tv_call:
                Uri uri = Uri.parse("tel:021-65650071");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    public void ConnSuccess() {
        icon.setImageResource(R.drawable.img_on_line);
    }

    @Override
    public void ConnOff() {
        icon.setImageResource(R.drawable.img_off_line);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    // 如果你开通了推送服务 可以开启推送
    private void init() {
//        JPushInterface.init(getApplicationContext());
//        JPushInterface.setAlias(getApplicationContext(), 1, mUserid);
//        HashSet<String> set = new HashSet<String>();
//        set.add(mUserid);
//        JPushInterface.setTags(getApplicationContext(), 1, set);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void Back() {
        mUserid= SharePrefUtil.getString("userid");
        if (!TextUtils.isEmpty(mUserid)) {
            tvId.setText(mUserid + "");
            etId.setVisibility(View.GONE);
            if (isTurnOn){
                icon.setImageResource(R.drawable.img_on_line);
                icon.requestLayout();
            }
        } else {
            tvId.setText("");
            etId.setVisibility(View.VISIBLE);
        }

    }




}
