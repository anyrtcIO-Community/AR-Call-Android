package org.ar.call;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.ar.arcall.ARMeetZoomMode;
import org.ar.common.enums.ARNetQuality;

import java.util.List;
import java.util.regex.Pattern;

public class SetPhoneActivity extends BaseActivity {

    EditText etPhone;
    Button btnLogin;
    @Override
    public int getLayoutId() {
        return R.layout.activity_set_phone;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mImmersionBar.keyboardEnable(true).statusBarDarkFont(true,0.2f).init();
        btnLogin=findViewById(R.id.btn_sure);
        etPhone=findViewById(R.id.et_phone);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPhone.getText().toString().isEmpty()){
                    Toast.makeText(SetPhoneActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkPhone(etPhone.getText().toString())){
                    Toast.makeText(SetPhoneActivity.this,"请输入正确格式的手机号",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (AndPermission.hasPermissions(SetPhoneActivity.this, Permission.RECORD_AUDIO,Permission.CAMERA,Permission.WRITE_EXTERNAL_STORAGE,Permission.READ_EXTERNAL_STORAGE)){
                    SpUtil.putString(Contants.PHONE,etPhone.getText().toString().trim());
                    startActivity(MainActivity.class);
                    finish();
                }else {
                    AndPermission.with(SetPhoneActivity.this).runtime().permission(Permission.RECORD_AUDIO, Permission.CAMERA,Permission.WRITE_EXTERNAL_STORAGE,Permission.READ_EXTERNAL_STORAGE).onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            SpUtil.putString(Contants.PHONE, etPhone.getText().toString().trim());
                            startActivity(MainActivity.class);
                            finish();

                        }
                    }).onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            Toast.makeText(SetPhoneActivity.this, "请授予相机、录音、文件读写权限", Toast.LENGTH_SHORT).show();
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnect(int nErrCode) {

    }


    @Override
    public void onRTCAcceptCall(String strPeerUserId) {

    }

    @Override
    public void onRTCRejectCall(String strPeerUserId, int errCode) {

    }

    @Override
    public void onRTCEndCall(String strPeerUserId, int errCode) {

    }

    @Override
    public void onRTCSipSupport(boolean bPstn, boolean bExtension, boolean bNull) {

    }

    @Override
    public void onRTCSwithToAudioMode() {

    }

    @Override
    public void onRTCUserMessage(String strPeerUserId, String strMessage) {

    }

    @Override
    public void onRTCOpenRemoteVideoRender(String userId, String vidRenderId, String userData) {

    }

    @Override
    public void onRTCCloseRemoteVideoRender(String userId, String vidRenderId) {

    }

    @Override
    public void onRTCOpenRemoteAudioTrack(String userId, String userData) {

    }

    @Override
    public void onRTCCloseRemoteAudioTrack(String userId) {

    }

    @Override
    public void onRTCRemoteAudioActive(String userId, int level, int time) {

    }

    @Override
    public void onRTCLocalAudioActive(int level, int time) {

    }

    @Override
    public void onRTCRemoteNetworkStatus(String userId, int netSpeed, int packetLost, ARNetQuality netQuality) {

    }

    @Override
    public void onRTCLocalNetworkStatus(int netSpeed, int packetLost, ARNetQuality netQuality) {

    }

    @Override
    public void onRTCZoomPageInfo(ARMeetZoomMode arMeetZoomMode, int nAllPages, int nCurPage, int nAllRender, int nScrnBeginIdx, int nNum) {

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


    public  boolean checkPhone(String phone) {
        String regex = "^13\\d{9}$|^17[8,0,2,1,5,6,3,4,7]\\d{8}$|^18\\d{9}$|^15[1,2,3,5,6,7,8,9,0]\\d{8}$|^14[9,7,8,5,6]\\d{8}$|^19[8,9]\\d{8}$|^9\\d{8}$|^166\\d{8}$\n";
        return Pattern.matches(regex, phone);
    }
}
