package org.ar.call;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ar.arcall.ARCallEngine;
import org.ar.arcall.ARCallKit;
import org.ar.arcall.ARCallMode;
import org.ar.arcall.ARMeetZoomMode;
import org.ar.arcall.ARUserOption;
import org.ar.common.enums.ARNetQuality;
import org.ar.common.utils.AR_AudioManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CallActivity extends BaseActivity implements Chronometer.OnChronometerTickListener{

    private ImageView ivBackGround,iv_icon;
    private RelativeLayout rlVideo,rl_log_layout;

    private FrameLayout flAccept,flAudio,flVideo,flVoice;
    private LinearLayout llAllBtnGroup,ll_right_btns;
    private ImageButton btn_snap,btn_camera,btn_log;

    private TextView tv_phone,tvStateTime;

    TextView tvAccept,tvAudio,tvVideo,tvHangUp,tvVoice;
    Chronometer chronometer;

    private MediaPlayer player;
    public boolean IS_CALLING = false;
    private String callId;
    private boolean isCalled=false;
    private int callMode;
    private String selfId;
    private ARCallKit arCallKit;
    private ARVideoView arVideoView;
    RecyclerView rvLogList;
    LogAdapter logAdapter;

    private SimpleDateFormat sdf;
    private long startTime;
    private String DisPlayTime = "00:00";

    private AR_AudioManager rtcAudioManager;
    @Override
    public int getLayoutId() {
        return R.layout.activity_call;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        arCallKit= CallApplication.get().getArCallKit();
        selfId= SpUtil.getString(Contants.PHONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        iv_icon=findViewById(R.id.iv_icon);
        flAccept=findViewById(R.id.fl_accept);
        flAudio=findViewById(R.id.fl_audio);
        flVideo=findViewById(R.id.fl_video);
        flVoice=findViewById(R.id.fl_voice);

        rl_log_layout = findViewById(R.id.rl_log_layout);
        rvLogList = findViewById(R.id.rv_log);
        rvLogList.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new LogAdapter();
        rvLogList.setAdapter(logAdapter);

        ivBackGround=findViewById(R.id.iv_background);
        rlVideo=findViewById(R.id.rl_rtc_videos);
        llAllBtnGroup=findViewById(R.id.ll_all_btn_group);
        ll_right_btns=findViewById(R.id.ll_right_btns);
        btn_camera=findViewById(R.id.btn_camera);
        btn_log=findViewById(R.id.btn_log);
        btn_snap=findViewById(R.id.btn_snap);
        tv_phone=findViewById(R.id.tv_phone);
        tvStateTime=findViewById(R.id.tv_state_time);
        tvAccept=findViewById(R.id.ibtn_accept);
        tvAudio=findViewById(R.id.ibtn_audio);
        tvVideo=findViewById(R.id.ibtn_video);
        tvVoice=findViewById(R.id.ibtn_voice);
        tvVoice.setSelected(true);
        tvHangUp=findViewById(R.id.ibtn_hang_up);
        chronometer=findViewById(R.id.chronometer);
        chronometer.setOnChronometerTickListener(this);
        arVideoView = new ARVideoView(rlVideo, ARCallEngine.Inst().Egl(),this,false);
        arVideoView.setVideoViewLayout(false, Gravity.CENTER,LinearLayout.HORIZONTAL);
        sdf = new SimpleDateFormat("mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));


        rtcAudioManager = AR_AudioManager.create(getApplicationContext(), mAudioRunnable);
//        rtcAudioManager.setProximitySensorChange(false);
        rtcAudioManager.init();
        rtcAudioManager.setAudioDevice(AR_AudioManager.AudioDevice.SPEAKER_PHONE);
        callId=getIntent().getStringExtra(Contants.CALL_ID);
        isCalled=getIntent().getBooleanExtra(Contants.IS_CALLED,true);
        callMode=getIntent().getIntExtra(Contants.CALL_MODE,0);
        tv_phone.setText(callId);


        if (callMode== ARCallMode.audio.level){
            tvStateTime.setText(isCalled ? "音频来电" : "音频呼叫");
        }else if (callMode==ARCallMode.video.level){
            tvStateTime.setText(isCalled ? "视频来电" : "视频呼叫");
        }else if (callMode==ARCallMode.video_pro.level){
            tvStateTime.setText(isCalled ? "视频优先来电" : "视频优先呼叫");
        }
        if (isCalled){//如果是被呼叫
            startRing();
            if (callMode==ARCallMode.video_pro.level) {
                iv_icon.setVisibility(View.GONE);
            }
            flAccept.setVisibility(View.VISIBLE);
        }else {//主动呼叫
            arCallKit.makeCallUser(callId,new ARUserOption(ARCallMode.getObject(callMode)));
            if (callMode==ARCallMode.video_pro.level){//视频优先呼叫 先打开本地摄像头
                arCallKit.setLocalVideoCapturer(arVideoView.openLocalVideoRender().GetRenderPointer());
                rlVideo.setVisibility(View.GONE);
                arCallKit.setLocalAudioEnable(false);
            }
        }


    }

    public String getUserData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nickName","哈哈哈哈");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    private Runnable mAudioRunnable = new Runnable() {
        @Override
        public void run() {//自动切换听筒扬声器
//            onAudioManagerChangedState();
        }
    };

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if
        // AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }
    public void START_AUDIO_CALL(){
        stopRing();
        startTime = System.currentTimeMillis();
        chronometer.start();
        IS_CALLING = true;
        flAccept.setVisibility(View.GONE);
        flAudio.setVisibility(View.VISIBLE);
        flVoice.setVisibility(View.VISIBLE);
        ll_right_btns.setVisibility(View.VISIBLE);
        btn_snap.setVisibility(View.GONE);
        btn_camera.setVisibility(View.GONE);
    }

    public void START_VIDEO_CALL(){
        stopRing();
        IS_CALLING = true;
        arCallKit.setLocalVideoCapturer(arVideoView.openLocalVideoRender().GetRenderPointer());
        ivBackGround.setVisibility(View.GONE);
        llAllBtnGroup.setVisibility(View.GONE);
        flAccept.setVisibility(View.GONE);
        flAudio.setVisibility(View.VISIBLE);
        flVideo.setVisibility(View.VISIBLE);
        ll_right_btns.setVisibility(View.VISIBLE);
    }

    public void START_VIDEO_PRO_CALL(){
        stopRing();
        IS_CALLING = true;
        llAllBtnGroup.setVisibility(View.GONE);
        flAccept.setVisibility(View.GONE);
        flAudio.setVisibility(View.VISIBLE);
        flVideo.setVisibility(View.VISIBLE);
        ll_right_btns.setVisibility(View.VISIBLE);
        if (isCalled){
            arCallKit.setLocalVideoCapturer(arVideoView.openLocalVideoRender().GetRenderPointer());
        }else {
            arCallKit.setLocalAudioEnable(true);
            rlVideo.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnect(int nErrCode) {

    }




    @Override
    public void onRTCAcceptCall(final String strPeerUserId) {
        CallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAdapter.addData("回调：onRTCAcceptCall peerId:"+strPeerUserId);
                if (callMode == ARCallMode.audio.level){
                    START_AUDIO_CALL();
                }else if (callMode == ARCallMode.video.level){
                    START_VIDEO_CALL();
                }else if (callMode==ARCallMode.video_pro.level){
                    START_VIDEO_PRO_CALL();
                }
            }
        });
    }

    @Override
    public void onRTCRejectCall(String strPeerUserId, int errCode) {
        CallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CallActivity.this, "对方已拒绝！", Toast.LENGTH_LONG).show();
                if (!isCalled&&callMode==ARCallMode.video_pro.level) {
                    arCallKit.stopCapturer();
                }
                finish();
            }
        });

    }

    @Override
    public void onRTCEndCall(final String strPeerUserId, final int nErrCode) {
        CallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCEndCall OnRTCOpenVideoRender strPeerUserId=" + strPeerUserId + "errCode=" + nErrCode);
                stopRing();
                String notification = "对方已挂断！";
                if (nErrCode == 800) {
                    notification = "对方正忙！";
                } else if (nErrCode == 801) {

                    notification = "对方不在线！";
                } else if (nErrCode == 802) {
                    notification = "不能呼叫自己！";

                } else if (nErrCode == 803) {
                    notification = "通话中对方意外掉线！";

                } else if (nErrCode == 804) {
                    notification = "对方异常导致(如：重复登录帐号将此前的帐号踢出)！";

                } else if (nErrCode == 805) {
                    notification = "呼叫超时！";
                }
                Toast.makeText(CallActivity.this, notification, Toast.LENGTH_LONG).show();
                arCallKit.stopCapturer();
                finish();
            }
        });
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
    public void onRTCOpenRemoteVideoRender(final String userId, final String strVidRenderId, String userData) {
        CallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAdapter.addData("回调：onRTCOpenRemoteVideoRender userId:"+userId);
                if (isCalled){
                    if (callMode==ARCallMode.video_pro.level){
                        if (arVideoView.getLocalVideoRender()==null) {//这个主要判断 视频优先模式时  远程图像还未显示 就接受打开本地 导致远程图像显示在上方（预览位置）
                            arCallKit.setRTCRemoteVideoRender(strVidRenderId, arVideoView.openProVideoRender(strVidRenderId).GetRenderPointer());
                        }else {
                            arCallKit.setRTCRemoteVideoRender(strVidRenderId, arVideoView.openRemoteVideoRender(strVidRenderId).GetRenderPointer());
                        }
                    }else {
                        arCallKit.setRTCRemoteVideoRender(strVidRenderId,arVideoView.openRemoteVideoRender(strVidRenderId).GetRenderPointer());
                    }
                }else {
                    arCallKit.setRTCRemoteVideoRender(strVidRenderId,arVideoView.openRemoteVideoRender(strVidRenderId).GetRenderPointer());
                }
            }
        });
    }

    @Override
    public void onRTCCloseRemoteVideoRender(final String userId, final String strVidRenderId) {
        CallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAdapter.addData("回调：onRTCCloseRemoteVideoRender userId:"+userId);
                arVideoView.removeRemoteRender(strVidRenderId);
            }
        });
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


    public void OnBtnClicked(View view) {
        switch (view.getId()){
            case R.id.ibtn_hang_up:
                if (IS_CALLING){
                    arCallKit.stopCapturer();
                    arCallKit.endCall(callId);
                    finish();
                }else {
                    if (isCalled){
                        arCallKit.rejectCall(callId);
                        stopRing();
                        finish();
                    }else {

                        if (callMode==ARCallMode.video_pro.level){
                            arCallKit.stopCapturer();
                        }
                        arCallKit.endCall(callId);
                        stopRing();
                        finish();
                    }
                }
                break;
            case R.id.ibtn_accept:
                arCallKit.accpetCall(callId);
                if (callMode==ARCallMode.audio.level){
                    START_AUDIO_CALL();
                }else if (callMode==ARCallMode.video.level){
                    START_VIDEO_CALL();
                }else if (callMode==ARCallMode.video_pro.level){
                    START_VIDEO_PRO_CALL();
                }
                break;
            case R.id.ibtn_audio:
                if (tvAudio.isSelected()) {
                    tvAudio.setSelected(false);
                    arCallKit.setLocalAudioEnable(true);
                } else {
                    tvAudio.setSelected(true);
                    arCallKit.setLocalAudioEnable(false);
                }
                break;
            case R.id.ibtn_video:
                if (tvVideo.isSelected()) {
                    arCallKit.setLocalVideoEnable(true);
                    tvVideo.setSelected(false);
                } else {
                    tvVideo.setSelected(true);
                    arCallKit.setLocalVideoEnable(false);
                }
                break;
            case R.id.ibtn_voice:
                if (tvVoice.isSelected()) {
                    setSpeakerOn(false);
                    tvVoice.setSelected(false);
                } else {
                    setSpeakerOn(true);
                    tvVoice.setSelected(true);
                }
                break;
            case R.id.btn_snap:
                //仅作演示保存本地像图片
                arVideoView.saveLocalPicture();
                break;
            case R.id.btn_camera:
                arCallKit.switchCamera();
                break;
            case R.id.btn_log:
                rl_log_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.ibtn_close_log:
                rl_log_layout.setVisibility(View.GONE);
                break;
        }
    }
    public void setSpeakerOn(boolean bOpen) {
        if (rtcAudioManager != null) {
            if (bOpen) {
                rtcAudioManager.setAudioDevice(AR_AudioManager.AudioDevice.SPEAKER_PHONE);
            } else {
                rtcAudioManager.setAudioDevice(AR_AudioManager.AudioDevice.EARPIECE);
            }
        }
    }
    private void startRing() {
        try {
            player = MediaPlayer.create(this, R.raw.video_request);
            //循环播放
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放铃音
     */
    private void stopRing() {
        try {
            if (null != player) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        long tp = System.currentTimeMillis();
        DisPlayTime = sdf.format(new Date(tp - startTime));
        tvStateTime.setText(DisPlayTime);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRing();
        if (chronometer != null) {
            chronometer.stop();
        }
        if (arCallKit != null && null != arVideoView) {
            arVideoView.removeLocalVideoRender();
            arVideoView.removeRemoteRender(callId);
            arCallKit.endCall(callId);
        }
        if (rtcAudioManager!=null){
            rtcAudioManager.close();
            mAudioRunnable=null;
        }
    }
}