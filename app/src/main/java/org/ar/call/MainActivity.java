package org.ar.call;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ar.arcall.ARCallKit;
import org.ar.arcall.ARCallMode;
import org.ar.arcall.ARMeetZoomMode;
import org.ar.common.enums.ARNetQuality;


public class MainActivity extends BaseActivity {

    EditText etContent;
    TextView tvPhone, tvState;
    View view_space;
    private String selfPhone = "";
    private ARCallKit arCallKit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        arCallKit = CallApplication.get().getArCallKit();
        view_space = findViewById(R.id.view_space);
        mImmersionBar.statusBarView(view_space).statusBarColor(R.color.stateBar).statusBarDarkFont(true, 0.2f).init();
        etContent = findViewById(R.id.et_content);
        selfPhone = SpUtil.getString(Contants.PHONE);
        tvPhone = findViewById(R.id.tv_id);
        tvState = findViewById(R.id.tv_state);

        if (TextUtils.isEmpty(selfPhone)) {
            Toast.makeText(MainActivity.this, "未能获取到你的手机号", Toast.LENGTH_SHORT).show();
            startActivity(SetPhoneActivity.class);
            finish();
            return;
        }
        tvPhone.setText(selfPhone);
        if (arCallKit.isTurnOff()) {
            arCallKit.turnOn(selfPhone);
        }
            findViewById(R.id.btn_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOutDialog();
                }
            });
        }

        @Override
        public void onConnected () {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tvState != null) {
                        tvState.setText("在线");
                        tvState.setTextColor(Color.parseColor("#41C981"));
                    }
                }
            });
        }

        @Override
        public void onDisconnect ( int nErrCode){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tvState != null) {
                        tvState.setText("重连中...");
                        tvState.setTextColor(Color.parseColor("#999999"));

                    }
                }
            });
        }


        @Override
        public void onRTCAcceptCall (String strPeerUserId){

        }

        @Override
        public void onRTCRejectCall (String strPeerUserId,int errCode){

        }

        @Override
        public void onRTCEndCall (String strPeerUserId,int errCode){

        }

        @Override
        public void onRTCSipSupport ( boolean bPstn, boolean bExtension, boolean bNull){

        }

        @Override
        public void onRTCSwithToAudioMode () {

        }

        @Override
        public void onRTCUserMessage (String strPeerUserId, String strMessage){

        }

        @Override
        public void onRTCOpenRemoteVideoRender (String userId, String vidRenderId, String userData){

        }

        @Override
        public void onRTCCloseRemoteVideoRender (String userId, String vidRenderId){

        }

        @Override
        public void onRTCOpenRemoteAudioTrack (String userId, String userData){

        }

        @Override
        public void onRTCCloseRemoteAudioTrack (String userId){

        }


        @Override
        public void onRTCRemoteAudioActive (String userId,int level, int time){

        }

        @Override
        public void onRTCLocalAudioActive ( int level, int time){

        }

        @Override
        public void onRTCRemoteNetworkStatus (String userId,int netSpeed,
        int packetLost, ARNetQuality netQuality){

        }

        @Override
        public void onRTCLocalNetworkStatus ( int netSpeed, int packetLost, ARNetQuality netQuality)
        {

        }

        @Override
        public void onRTCZoomPageInfo (ARMeetZoomMode arMeetZoomMode,int nAllPages, int nCurPage,
        int nAllRender, int nScrnBeginIdx, int nNum){

        }

    @Override
    public void onRTCUserCTIStatus(int nQueueNum) {

    }

    @Override
    public void onRTCClerkCTIStatus(int nQueueNum, int nAllClerk, int nWorkingClerk) {

    }

    @Override
    public void onRTCAVStatus(String strRTCPeerId, boolean bAudio, boolean bVideo) {

    }


    public void btnOnclick (View view){
            if (etContent.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入要呼叫的号码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etContent.getText().toString().equals(selfPhone)) {
                Toast.makeText(MainActivity.this, "不能呼叫自己", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (view.getId()) {
                case R.id.btn_audio:
                    Call(ARCallMode.audio.level);
                    break;
                case R.id.btn_video:
                    Call(ARCallMode.video.level);
                    break;
                case R.id.btn_video_pro:
                    Call(ARCallMode.video_pro.level);
                    break;
            }
        }

        private void Call ( int callType){
            if (canCall) {
                Bundle bundle = new Bundle();
                bundle.putString(Contants.CALL_ID, etContent.getText().toString());//呼叫人号码
                bundle.putBoolean(Contants.IS_CALLED, false);//是否被呼叫
                bundle.putInt(Contants.CALL_MODE, callType);//呼叫类型
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, CallActivity.class);
                startActivity(intent);
                canCall = false;
                TimerHandler.postDelayed(timeRunnable, 3000);
            } else {
                Toast.makeText(this, "呼叫太频繁", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }


        public void showOutDialog () {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("确定要解绑当前手机号吗");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SpUtil.putString(Contants.PHONE, "");
                    arCallKit.turnOff();
                    Log.d("ARCALL", arCallKit.isTurnOff() + "");
                    startActivity(SetPhoneActivity.class);
                    finish();
                }
            });
            builder.show();
        }
    }
