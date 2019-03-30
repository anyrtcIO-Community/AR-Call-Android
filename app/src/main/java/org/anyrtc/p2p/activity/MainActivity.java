package org.anyrtc.p2p.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.anyrtc.p2p.LocalBroadcastManager;
import org.anyrtc.p2p.P2PApplication;
import org.anyrtc.p2p.R;
import org.anyrtc.p2p.utils.ExampleUtil;
import org.anyrtc.p2p.utils.SharePrefUtil;
import org.anyrtc.rtp2pcall.ARP2PKit;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    TextView tvCall, tvId, tvGo;
    private String mUserid = "";
    private ARP2PKit mP2PKit;
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
        mImmersionBar.keyboardEnable(true).init();
        tvCall = (TextView) findViewById(R.id.tv_call);
        tvGo = (TextView) findViewById(R.id.tv_go_pre);
        tvId = (TextView) findViewById(R.id.tv_userid);
        etId = (EditText) findViewById(R.id.et_id);
        icon = (ImageView) findViewById(R.id.icon);
        tvCall.setOnClickListener(this);
        tvGo.setOnClickListener(this);
        mP2PKit = P2PApplication.the().getmP2pKit();
        mUserid = SharePrefUtil.getString("userid");
        if (TextUtils.isEmpty(mUserid)){
            etId.setVisibility(View.VISIBLE);
        }else {
            mP2PKit.turnOn(mUserid);
            tvId.setText(mUserid + "");
            init();
        }

    }





    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go_pre:

                if (!TextUtils.isEmpty(mUserid)) {
                    startAnimActivity(PreCallActivity.class);
                    finishAnimActivity();
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
                finishAnimActivity();
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
    public void onConnected() {
        if (icon!=null) {
            icon.setImageResource(R.drawable.img_on_line);
        }
    }

    @Override
    public void onDisconnect(int nErrCode) {
        if (icon!=null) {
            icon.setImageResource(R.drawable.img_off_line);
        }
    }

    @Override
    public void onRTCMakeCall(String strPeerUserId, int nCallMode, String strUserData) {

    }

    @Override
    public void onRTCAcceptCall(String strPeerUserId) {

    }

    @Override
    public void onRTCRejectCall(String strPeerUserId, int nErrCode) {

    }

    @Override
    public void onRTCEndCall(String strPeerUserId, int nErrCode) {

    }

    @Override
    public void onRTCSwithToAudioMode() {

    }

    @Override
    public void onRTCUserMessage(String strPeerUserId, String strMessage) {

    }

    @Override
    public void onRTCOpenVideoRender(String strDevId) {

    }

    @Override
    public void onRTCCloseVideoRender(String strDevId) {

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
