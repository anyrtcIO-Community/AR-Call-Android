package org.anyrtc.arp2p.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.anyrtc.arp2p.P2PApplication;
import org.anyrtc.arp2p.R;
import org.anyrtc.arp2p.model.CallRecord;
import org.anyrtc.arp2p.widgets.CircleImageView;
import org.anyrtc.arp2p.widgets.RTCVideoView;
import org.anyrtc.common.utils.AnyRTCAudioManager;
import org.anyrtc.rtp2pcall.AnyRTCP2PEngine;
import org.ar.rtp2pcall.ARP2PCallMode;
import org.ar.rtp2pcall.ARP2PKit;
import org.webrtc.VideoRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class P2PCallActivity extends BaseActivity implements Chronometer.OnChronometerTickListener {
    private boolean misCalling = false;
    public boolean IS_CALLING = false;

    private RTCVideoView mVideoView;
    private String mUserId, mCallid;
    //是否是被呼叫
    private boolean mIsCalled;
    private ARP2PCallMode mP2PModel;
    private MediaPlayer player;
    private Chronometer chronometer;
    private SimpleDateFormat sdf;
    private long startTime;
    private String DisPlayTime = "00:00";
    private AnyRTCAudioManager rtcAudioManager;
    private Button btnChangeMode;
    private ImageButton btn_camare, ibtn_accept, ibtn_audio, ibtn_hang_up, ibtn_video, ibtn_voice;
    private TextView tvUserid, tvType, tvTime, tvstate;
    private ImageView ivBg;
    private View space;
    private CircleImageView iv_icon;
    private ARP2PKit mP2PKit;
    private int state = 1;

    private CallRecord callRecord = new CallRecord();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ibtn_audio.getVisibility() == View.GONE) {
                mRTP2PCallKit.endCall(mCallid);
                finishAnimActivity();
            } else {
                if (misCalling) {
                    mRTP2PCallKit.endCall(mCallid);
                }
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 停止播放铃音
         */
        stopRing();
        if (mRTP2PCallKit != null && null != mVideoView) {
            mVideoView.OnRtcRemoveLocalRender();
            mVideoView.OnRtcRemoveRemoteRender(mCallid);
            mRTP2PCallKit.endCall(mCallid);
        }
        mVideoView = null;
        mAudioRunnable = null;
//        mp2Phandler.removeCallbacksAndMessages(null);
        if (chronometer != null) {
            chronometer.stop();
        }
        IS_CALLING = false;
        if (misCalling) {
            callRecord.setTime(DisPlayTime);
        } else {
            callRecord.setTime("00:00");
        }
        callRecord.setMode(mP2PModel.level);
        P2PApplication.the().getmDBDao().Add(callRecord);
        misCalling = false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_p2pcall;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        isCallActivity = true;
        space = findViewById(R.id.view_space);
        mImmersionBar.titleBar(space).init();
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        btn_camare = (ImageButton) findViewById(R.id.btn_camare);
        ibtn_accept = (ImageButton) findViewById(R.id.ibtn_accept);
        ibtn_audio = (ImageButton) findViewById(R.id.ibtn_audio);
        ibtn_hang_up = (ImageButton) findViewById(R.id.ibtn_hang_up);
        ibtn_video = (ImageButton) findViewById(R.id.ibtn_video);
        tvUserid = (TextView) findViewById(R.id.tv_userid);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ivBg = (ImageView) findViewById(R.id.iv_background);
        iv_icon = (CircleImageView) findViewById(R.id.iv_icon);
        ibtn_voice = (ImageButton) findViewById(R.id.ibtn_voice);
        tvstate = (TextView) findViewById(R.id.tv_state);
        btnChangeMode = (Button) findViewById(R.id.btn_change_audio);
        ibtn_voice.setSelected(true);
        chronometer.setOnChronometerTickListener(this);
        sdf = new SimpleDateFormat("mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        startTime = System.currentTimeMillis();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        rtcAudioManager = AnyRTCAudioManager.create(getApplicationContext(), mAudioRunnable);
        rtcAudioManager.init();
        rtcAudioManager.setAudioDevice(AnyRTCAudioManager.AudioDevice.SPEAKER_PHONE);

        mUserId = getIntent().getExtras().getString("userid");
        mCallid = getIntent().getExtras().getString("callid");
        int p2p_mode = getIntent().getExtras().getInt("p2p_mode");
        mIsCalled = getIntent().getExtras().getBoolean("p2p_push");
        Log.d("p2pCallBack misPush", mIsCalled + "");
        tvUserid.setText(mCallid + "");
        if (mIsCalled) {
            startRing();
        }
        if (p2p_mode == 0) {
            //视频呼叫
            mP2PModel = ARP2PCallMode.video;
            if (mIsCalled) {
                beCalledByOther_Video();
            } else {
                callOther_Video();
            }
        } else if (p2p_mode == 1) {
            //视频PRO呼叫（被呼叫者可以先看到呼叫者的视频）
            mP2PModel = ARP2PCallMode.video_pro;
            if (mIsCalled) {
                beCalledByOther_Video_pre();
            } else {
                callOther_Video();
            }
        } else if (p2p_mode == 2) {
            //音频呼叫
            mP2PModel = ARP2PCallMode.audio;
            if (mIsCalled) {
                beCalledByOther_Audio();
            } else {
                callOther_Audio();
            }

        } else if (p2p_mode == 3) {
            mP2PModel = ARP2PCallMode.monitor;
            if (mIsCalled) {
                beCalledByOther_Watch();
            } else {
                callOther_Video();
            }
        }
        mP2PKit = P2PApplication.the().getmP2pKit();

        if (mVideoView == null) {
            mVideoView = new RTCVideoView((RelativeLayout) findViewById(R.id.rl_rtc_videos), this, AnyRTCP2PEngine.Inst().Egl());
        }
        if (mP2PModel != ARP2PCallMode.audio) {
            VideoRenderer render = mVideoView.OnRtcOpenLocalRender();
            mP2PKit.setLocalVideoCapturer(render.GetRenderPointer());
        }
        if (mIsCalled) {

        } else {
            if (mRTP2PCallKit != null) {
                mRTP2PCallKit.makeCall(mCallid, mP2PModel, "{userid: " + mUserId + "}");
            }
            callRecord.setState(3);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        //获取当前时间
        Date curDate = new Date(System.currentTimeMillis());
        String date = formatter.format(curDate);
        callRecord.setData(date);
        callRecord.setId(mCallid);
    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if
        // AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private Runnable mAudioRunnable = new Runnable() {
        @Override
        public void run() {
            onAudioManagerChangedState();
        }
    };

    public void OnBtnClicked(View btn) {
        switch (btn.getId()) {
            case R.id.ibtn_hang_up:
                if (mRTP2PCallKit == null) {
                    return;
                }

                if (misCalling) {
                    //正在通话
                    mRTP2PCallKit.endCall(mCallid);
                    finishAnimActivity();
                } else {
                    //还未打电话    可分为主动打 （挂断）  被呼叫（拒绝）
                    if (mIsCalled) {
                        mRTP2PCallKit.rejectCall(mCallid);
                        stopRing();
                        finishAnimActivity();
                        state = 2;
                        callRecord.setState(state);
                    } else {
                        mRTP2PCallKit.endCall(mCallid);
                        finishAnimActivity();
                    }
                }
                break;
            case R.id.ibtn_accept:
                state = 0;
                callRecord.setState(state);
                mRTP2PCallKit.accpetCall(mCallid);
                if (mP2PModel == ARP2PCallMode.audio) {
                    startAudioCall();
                } else if (mP2PModel == ARP2PCallMode.video) {
                    startVideoCall();
                } else if (mP2PModel == ARP2PCallMode.video_pro) {
                    mVideoView.preView2Normol();
                    startVideoCall();
                } else if (mP2PModel == ARP2PCallMode.monitor) {
                    startWatchCall();
                }
                break;
            case R.id.ibtn_audio:
                if (ibtn_audio.isSelected()) {
                    ibtn_audio.setSelected(false);
                    mRTP2PCallKit.setLocalAudioEnable(true);
                } else {
                    ibtn_audio.setSelected(true);
                    mRTP2PCallKit.setLocalAudioEnable(false);
                }
                break;
            case R.id.ibtn_video:
                if (ibtn_video.isSelected()) {
                    mRTP2PCallKit.setLocalVideoEnable(true);
                    ibtn_video.setSelected(false);
                } else {
                    ibtn_video.setSelected(true);
                    mRTP2PCallKit.setLocalVideoEnable(false);
                }
                break;
            case R.id.btn_camare:
                mRTP2PCallKit.switchCamera();
                if (btn_camare.isSelected()) {
                    btn_camare.setSelected(false);
                } else {
                    btn_camare.setSelected(true);
                }
                break;
            case R.id.ibtn_voice:
                if (ibtn_voice.isSelected()) {
                    setSpeakerOn(false);
                    ibtn_voice.setSelected(false);
                } else {
                    ibtn_voice.setSelected(true);
                    setSpeakerOn(true);
                }
                break;
            case R.id.btn_change_audio:
                if (mRTP2PCallKit != null) {
                    mRTP2PCallKit.swtichToAudioMode();
                    startAudioCall();
                    mP2PModel = ARP2PCallMode.audio;
                    mVideoView.OnRtcRemoveLocalRender();
                }
                break;
            default: {

            }
        }
    }

    /**
     * 播放铃音
     */
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
        tvTime.setText(DisPlayTime);
    }


    public void callOther_Video() {//视频呼叫别人
        tvType.setText("等待对方接听");
        if (chronometer != null) {
            chronometer.start();
        }
    }

    public void beCalledByOther_Video() {//被别人视频频呼叫
        tvType.setText("视频来电");
        ibtn_accept.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.GONE);
    }

    public void beCalledByOther_Video_pre() {//被别人优先视频频呼叫
        tvType.setText("优先视频来电");
        iv_icon.setVisibility(View.GONE);
        ivBg.setVisibility(View.GONE);
        ibtn_accept.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.GONE);
    }

    public void callOther_Audio() {//音频呼叫别人
        tvType.setText("等待对方接听");
        if (chronometer != null) {
            chronometer.start();
        }
    }

    public void beCalledByOther_Audio() {//被别人音频呼叫
        tvType.setText("音频来电");
        iv_icon.setVisibility(View.VISIBLE);
        ivBg.setVisibility(View.VISIBLE);
        ibtn_accept.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.GONE);
    }

    public void beCalledByOther_Watch() {//被别人监测模式呼叫
        tvType.setText("监看模式来电");
        ibtn_accept.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.GONE);
    }

    public void startAudioCall() {//开始音频通话
        misCalling = true;
        stopRing();
        iv_icon.setVisibility(View.VISIBLE);
        ivBg.setVisibility(View.VISIBLE);
        btnChangeMode.setVisibility(View.GONE);
        tvTime.setVisibility(View.VISIBLE);
        tvType.setVisibility(View.VISIBLE);
        tvType.setText("正在通话中...");
        startTime = System.currentTimeMillis();
        btn_camare.setVisibility(View.GONE);
        chronometer.start();
        ibtn_accept.setVisibility(View.GONE);
        ibtn_voice.setVisibility(View.VISIBLE);
        ibtn_video.setVisibility(View.GONE);
        ibtn_audio.setVisibility(View.VISIBLE);
    }

    //开始视频通话
    public void startVideoCall() {
        misCalling = true;
        stopRing();
        if (mIsCalled) {
            btnChangeMode.setVisibility(View.GONE);
        } else {
            if (mP2PModel == ARP2PCallMode.monitor) {
                btnChangeMode.setVisibility(View.GONE);
            } else {
                btnChangeMode.setVisibility(View.VISIBLE);
            }
        }
        tvUserid.setVisibility(View.GONE);
        btn_camare.setVisibility(View.VISIBLE);
        iv_icon.setVisibility(View.GONE);
        ivBg.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvType.setVisibility(View.GONE);
        ibtn_accept.setVisibility(View.GONE);
        ibtn_audio.setVisibility(View.VISIBLE);
        ibtn_video.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis();
        if (chronometer != null) {
            chronometer.start();
        }
    }

    //开始监看通话
    public void startWatchCall() {
        if (mIsCalled) {
            mVideoView.removeLocalRenderBg();
        }
        misCalling = true;
        stopRing();
        if (mIsCalled) {
            ibtn_audio.setVisibility(View.VISIBLE);
            ibtn_video.setVisibility(View.VISIBLE);
        } else {
            ibtn_audio.setVisibility(View.GONE);
            ibtn_video.setVisibility(View.GONE);
        }
        btnChangeMode.setVisibility(View.GONE);
        tvUserid.setVisibility(View.GONE);
        btn_camare.setVisibility(View.GONE);
        iv_icon.setVisibility(View.GONE);
        ivBg.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvType.setVisibility(View.GONE);
        ibtn_accept.setVisibility(View.GONE);
        startTime = System.currentTimeMillis();
        if (chronometer != null) {
            chronometer.start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        IS_CALLING = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * p2p连接成功
     */
    @Override
    public void onConnected() {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("p2pCallBack", "OnRTCOpenVideoRender OnConnected 连接成功");
                if (tvstate != null) {
                    tvstate.setText("连接成功");
                }
            }
        });
    }

    /**
     * p2p断开连接
     *
     * @param nErrCode
     */

    @Override
    public void onDisconnect(final int nErrCode) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "onDisconnect OnRTCOpenVideoRender 连接断开 Code=" + nErrCode);
                if (nErrCode == 0) {
                    Toast.makeText(P2PCallActivity.this, "连接断开", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(P2PCallActivity.this, "" + nErrCode, Toast.LENGTH_LONG).show();
                }
                mRTP2PCallKit.stopCapturer();
                finishAnimActivity();
            }
        });
    }

    /**
     * 其他人呼叫回掉
     *
     * @param strPeerUserId 呼叫人ID
     * @param nCallMode     呼叫模式
     * @param strUserData   呼叫人自定义数据
     */
    @Override
    public void onRTCMakeCall(final String strPeerUserId, final int nCallMode, final String strUserData) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCMakeCall OnRTCOpenVideoRender strPeerUserId=" + strPeerUserId + "nCallMode=" + nCallMode + "strUserData=" + strUserData);
            }
        });
    }

    /**
     * 被呼叫人接收你的呼叫
     *
     * @param strPeerUserId 被呼叫人ID
     */
    @Override
    public void onRTCAcceptCall(final String strPeerUserId) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCAcceptCall OnRTCOpenVideoRender strPeerUserId=" + strPeerUserId);
                if (mP2PModel == ARP2PCallMode.audio) {
                    startAudioCall();
                } else if (mP2PModel == ARP2PCallMode.monitor) {
                    startWatchCall();
                } else {
                    startVideoCall();
                }
            }
        });
    }

    /**
     * 被呼叫人拒绝你的呼叫请求
     *
     * @param strPeerUserId 被呼叫人ID
     * @param errCode       状态码
     */
    @Override
    public void onRTCRejectCall(final String strPeerUserId, int errCode) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCRejectCall OnRTCOpenVideoRender strPeerUserId=" + strPeerUserId);
                Toast.makeText(P2PCallActivity.this, "对方已拒绝！", Toast.LENGTH_LONG).show();
                mRTP2PCallKit.stopCapturer();
                finish();
            }
        });

    }

    /**
     * 通话结束
     *
     * @param strPeerUserId 对方ID
     * @param nErrCode      状态码
     */
    @Override
    public void onRTCEndCall(final String strPeerUserId, final int nErrCode) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
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
                Toast.makeText(P2PCallActivity.this, notification, Toast.LENGTH_LONG).show();
                mRTP2PCallKit.stopCapturer();
                finishAnimActivity();
            }
        });
    }

    /**
     * 对方由视频切换至音频模式
     */
    @Override
    public void onRTCSwithToAudioMode() {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCOpenVideoRender onRTCSwithToAudioMode");
                startAudioCall();
                mP2PModel = ARP2PCallMode.audio;
                mRTP2PCallKit.setRTCRemoteVideoRender(mCallid, 0);
                mVideoView.OnRtcRemoveLocalRender();
                mVideoView.OnRtcRemoveRemoteRender(mCallid);
            }
        });
    }

    /**
     * 收到对方消息
     *
     * @param strPeerUserId
     * @param strMessage
     */
    @Override
    public void onRTCUserMessage(String strPeerUserId, String strMessage) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "onRTCUserMessage");
            }
        });
    }

    /**
     * 对方视频即将显示
     *
     * @param strDevId
     */
    @Override
    public void onRTCOpenVideoRender(final String strDevId) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCOpenVideoRender OnRTCOpenVideoRender=" + strDevId);
                VideoRenderer render = null;
                if (mP2PModel == ARP2PCallMode.video) {
                    render = mVideoView.OnRtcOpenRemoteRender(strDevId);
                } else if (mP2PModel == ARP2PCallMode.video_pro) {
                    if (mIsCalled) {
                        render = mVideoView.OnRtcOpenPreViewRender(strDevId);
                    } else {
                        render = mVideoView.OnRtcOpenRemoteRender(strDevId);
                    }
                } else if (mP2PModel == ARP2PCallMode.monitor) {
                    render = mVideoView.OnRtcOpenRemoteRender(strDevId);
                }
                if (null != render) {
                    mRTP2PCallKit.setRTCRemoteVideoRender(strDevId, render.GetRenderPointer());
                }
            }
        });
    }

    /**
     * 对方视频关闭
     *
     * @param strDevId
     */
    @Override
    public void onRTCCloseVideoRender(final String strDevId) {
        P2PCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("p2pCallBack", "OnRTCCloseVideoRender strDevId=" + strDevId);
                if (null != mRTP2PCallKit) {
                    mRTP2PCallKit.setRTCRemoteVideoRender(strDevId, 0);
                    mVideoView.OnRtcRemoveRemoteRender(strDevId);
                }
                finish();
            }
        });
    }

    public void setSpeakerOn(boolean bOpen) {
        if (rtcAudioManager != null) {
            if (bOpen) {
                rtcAudioManager.setAudioDevice(AnyRTCAudioManager.AudioDevice.SPEAKER_PHONE);
            } else {
                rtcAudioManager.setAudioDevice(AnyRTCAudioManager.AudioDevice.EARPIECE);
            }
        }
    }


}
