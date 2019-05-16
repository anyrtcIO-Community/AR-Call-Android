package org.ar.arp2p.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.ar.arp2p.DeveloperInfo;
import org.ar.arp2p.P2PApplication;
import org.ar.arp2p.R;
import org.ar.arp2p.utils.SharePrefUtil;
import org.ar.rtp2pcall.ARP2PKit;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvCall, tvId, tvGo;
    private String mUserid = "";
    private EditText etId;
    private ImageView icon;
    private ARP2PKit mP2PKit;
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
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
    // 如果你开通了推送服务 可以开启推送
    private void init() {
//        JPushInterface.init(getApplicationContext());
//        JPushInterface.setAlias(getApplicationContext(), 1, mUserid);
//        HashSet<String> set = new HashSet<String>();
//        set.add(mUserid);
//        JPushInterface.setTags(getApplicationContext(), 1, set);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go_pre: {
                if (AndPermission.hasPermissions(MainActivity.this,Permission.RECORD_AUDIO,Permission.CAMERA)){
                    goCall();
                }else {
                    AndPermission.with(MainActivity.this).runtime().permission(Permission.CAMERA, Permission.RECORD_AUDIO).onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            Toast.makeText(MainActivity.this,"请开启音视频权限",Toast.LENGTH_SHORT).show();
                        }
                    }).onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            goCall();
                        }
                    }).start();
                }

            }
                break;
            case R.id.tv_call: {
                Uri uri = Uri.parse("tel:021-65650071");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
                break;
            default: {

            }
        }
    }

    private void goCall() {
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
        SharePrefUtil.putString(DeveloperInfo.USERID,mUserid);
        mP2PKit.turnOn(mUserid);
        startAnimActivity(PreCallActivity.class);
        finishAnimActivity();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        P2PApplication.the().setmCallback(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
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
