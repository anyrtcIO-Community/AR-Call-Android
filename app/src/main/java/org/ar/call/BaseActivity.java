package org.ar.call;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gyf.barlibrary.ImmersionBar;

import org.ar.arcall.ARCallEvent;
import org.ar.arcall.ARCallKit;
import org.ar.arcall.ARCallMode;
import org.ar.arcall.ARMeetZoomMode;
import org.ar.common.enums.ARNetQuality;

/**
 * Created by liuxiaozhong on 2019/4/11.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected ImmersionBar mImmersionBar;
    protected ARCallKit arCallKit;
    protected String selfPhone;
    protected Handler TimerHandler=new Handler();;
    protected boolean canCall = true;

    protected Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            canCall=true;
        }
    };
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
        arCallKit = CallApplication.get().getArCallKit();
        CallApplication.get().setArCallEvent(arCallEvent);
        selfPhone = SpUtil.getString(Contants.PHONE);
        this.initView(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void startActivity(Class<?> cls, String key, boolean value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);


    ARCallEvent arCallEvent = new ARCallEvent() {
        @Override
        public void onConnected() {
            Log.d("ARCALL", "onConnected");
            BaseActivity.this.onConnected();
        }

        @Override
        public void onDisconnect(int i) {
            Log.d("ARCALL", "onDisconnect code=" + i);
            BaseActivity.this.onDisconnect(i);
        }

        @Override
        public void onRTCJoinRoomOk(String s) {

        }



        @Override
        public void onRTCMakeCall(final String strPeerUserId, final ARCallMode arCallMode, final String strUserData, String strExtend) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("ARCALL", "onRTCMakeCall "+ "strPeerUserId=" + strPeerUserId);

                    Bundle bundle = new Bundle();
                    bundle.putString(Contants.CALL_ID, strPeerUserId);//呼叫人号码
                    bundle.putBoolean(Contants.IS_CALLED, true);//是否被呼叫
                    bundle.putInt(Contants.CALL_MODE, arCallMode.level);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(bundle);
                    intent.setClass(BaseActivity.this, CallActivity.class);
                    startActivity(intent);
                }
            });
        }


        @Override
        public void onRTCAcceptCall(String s) {
            Log.d("ARCALL", "onRTCAcceptCall s=" + s);
            BaseActivity.this.onRTCAcceptCall(s);
        }

        @Override
        public void onRTCRejectCall(String s, int i) {
            Log.d("ARCALL", "onRTCRejectCall s=" + s);
            BaseActivity.this.onRTCRejectCall(s, i);
        }

        @Override
        public void onRTCEndCall(String s, int i) {
            Log.d("ARCALL", "onRTCEndCall s=" + s);
            BaseActivity.this.onRTCEndCall(s, i);
        }

        @Override
        public void onRTCSipSupport(boolean b, boolean b1, boolean b2) {
            BaseActivity.this.onRTCSipSupport(b, b1, b2);
        }

        @Override
        public void onRTCSwithToAudioMode() {
            BaseActivity.this.onRTCSwithToAudioMode();
        }

        @Override
        public void onRTCUserMessage(String s, String s1) {
            BaseActivity.this.onRTCUserMessage(s, s1);
        }

        @Override
        public void onRTCOpenRemoteVideoRender(String s, String s1, String s2) {
            Log.d("ARCALL", "onRTCOpenVideoRender s=" + s);
            BaseActivity.this.onRTCOpenRemoteVideoRender(s, s1, s2);
        }

        @Override
        public void onRTCCloseRemoteVideoRender(String s, String s1) {
            Log.d("ARCALL", "onRTCCloseVideoRender s=" + s + "s1" + s1);
            BaseActivity.this.onRTCCloseRemoteVideoRender(s, s1);
        }

        @Override
        public void onRTCOpenRemoteAudioTrack(String s, String s1) {
            Log.d("ARCALL", "onRTCOpenAudioTrack s=" + s + "s1" + s1);
            BaseActivity.this.onRTCOpenRemoteAudioTrack(s, s1);
        }

        @Override
        public void onRTCCloseRemoteAudioTrack(String s) {
            Log.d("ARCALL", "onRTCCloseAudioTrack s=" + s);
            BaseActivity.this.onRTCCloseRemoteAudioTrack(s);
        }

        @Override
        public void onRTCRemoteAudioActive(String s, int i, int i1) {
            BaseActivity.this.onRTCRemoteAudioActive(s, i, i1);
        }

        @Override
        public void onRTCLocalAudioActive(int i, int i1) {
            BaseActivity.this.onRTCLocalAudioActive( i, i1);
        }

        @Override
        public void onRTCRemoteNetworkStatus(String s, int i, int i1, ARNetQuality arNetQuality) {
            BaseActivity.this.onRTCRemoteNetworkStatus(s, i, i1,arNetQuality);
        }

        @Override
        public void onRTCLocalNetworkStatus(int i, int i1, ARNetQuality arNetQuality) {
            BaseActivity.this.onRTCLocalNetworkStatus( i, i1,arNetQuality);
        }

        @Override
        public void onRTCZoomPageInfo(ARMeetZoomMode arMeetZoomMode, int i, int i1, int i2, int i3, int i4) {
            BaseActivity.this.onRTCZoomPageInfo(arMeetZoomMode, i, i1, i2, i3,i4);
        }

        @Override
        public void onRTCUserCome(String userId, String videoId ,String userData) {
            Log.d("ARCALL", "onRTCUserCome videoId=" + videoId+"userId="+userId);
        }

        @Override
        public void onRTCUserOut(String userId, String videoId) {
            Log.d("ARCALL", "onRTCUserOut videoId=" + videoId+"userId="+userId);
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


    };

    public abstract void onConnected();

    public abstract void onDisconnect(int nErrCode);

    public abstract void onRTCAcceptCall(String strPeerUserId);

    public abstract void onRTCRejectCall(String strPeerUserId, int errCode);

    public abstract void onRTCEndCall(String strPeerUserId, int errCode);

    public abstract void onRTCSipSupport(boolean bPstn, boolean bExtension, boolean bNull);

    public abstract void onRTCSwithToAudioMode();

    public abstract void onRTCUserMessage(String strPeerUserId, String strMessage);
    public abstract void onRTCOpenRemoteVideoRender(String userId, String vidRenderId, String userData);

    public abstract void onRTCCloseRemoteVideoRender(String userId, String vidRenderId);

    public abstract void onRTCOpenRemoteAudioTrack(String userId, String userData);

    public abstract void onRTCCloseRemoteAudioTrack(String userId);

    public abstract void onRTCRemoteAudioActive(String userId, int level, int time);

    public abstract void onRTCLocalAudioActive(int level, int time);

    public abstract void onRTCRemoteNetworkStatus(String userId, int netSpeed, int packetLost, ARNetQuality netQuality);

    public abstract void onRTCLocalNetworkStatus(int netSpeed, int packetLost, ARNetQuality netQuality);

    public abstract void onRTCZoomPageInfo(ARMeetZoomMode arMeetZoomMode, int nAllPages, int nCurPage, int nAllRender, int nScrnBeginIdx, int nNum);


    public abstract void onRTCUserCTIStatus(int nQueueNum);

    public abstract void onRTCClerkCTIStatus(int nQueueNum, int nAllClerk, int nWorkingClerk);

    public abstract void onRTCAVStatus(String strRTCPeerId, boolean bAudio, boolean bVideo);
}
