package org.anyrtc.arp2p.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gyf.barlibrary.ImmersionBar;

import org.anyrtc.arp2p.P2PApplication;
import org.anyrtc.arp2p.utils.SharePrefUtil;
import org.ar.rtp2pcall.ARP2PCallMode;
import org.ar.rtp2pcall.ARP2PEvent;
import org.ar.rtp2pcall.ARP2PKit;

/**
 * Created by Skyline on 2016/5/24.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected ImmersionBar mImmersionBar;
    protected ARP2PKit mRTP2PCallKit;
    protected boolean isCallActivity = false;
    protected boolean turnOn;
    public static   boolean isOnline = false;

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
        P2PApplication.the().getmP2pKit().setP2PEvent(mP2PCallHelper);
        mRTP2PCallKit = P2PApplication.the().getmP2pKit();
        this.initView(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


    public void startAnimActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void finishAnimActivity() {
        finish();
    }

    public void startAnimActivity(Class<?> cls, String key, boolean value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);


    @Override
    protected void onResume() {
        super.onResume();
        P2PApplication.the().setmCallback(mP2PCallHelper);
    }


    ARP2PEvent mP2PCallHelper = new ARP2PEvent() {
        @Override
        public void onConnected() {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseActivity.this.onConnected();
                    BaseActivity.this.isOnline=true;
                    Log.d("p2pDemo","onConnected");
                }
            });

        }

        @Override
        public void onDisconnect(final int nErrCode) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseActivity.this.onDisconnect(nErrCode);
                    BaseActivity.this.isOnline=false;
                    Log.d("p2pDemo","onDisconnect code" +nErrCode);
                }
            });

        }

        @Override
        public void onRTCMakeCall(final String strPeerUserId, final ARP2PCallMode nCallMode, String s1) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCMakeCall strPeerUserId" +strPeerUserId);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", SharePrefUtil.getString("userid"));
                    bundle.putString("callid", strPeerUserId);
                    bundle.putBoolean("p2p_push", true);
                    bundle.putInt("p2p_mode", nCallMode.level);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(BaseActivity.this, P2PCallActivity.class);
                    startActivity(intent);
                }
            });
        }


        @Override
        public void onRTCAcceptCall(final String strPeerUserId) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCAcceptCall strPeerUserId" +strPeerUserId);
                    BaseActivity.this.onRTCAcceptCall(strPeerUserId);
                }
            });

        }

        @Override
        public void onRTCRejectCall(final String strPeerUserId, final int nErrCode) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCRejectCall strPeerUserId" +strPeerUserId);
                    BaseActivity.this.onRTCRejectCall(strPeerUserId, nErrCode);
                }
            });

        }

        @Override
        public void onRTCEndCall(final String strPeerUserId, final int nErrCode) {
//            mHandler.sendEmptyMessageDelayed(0, 2000);
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCEndCall strPeerUserId" +strPeerUserId+"nErrCode"+nErrCode);
                    BaseActivity.this.onRTCEndCall(strPeerUserId, nErrCode);
                }
            });

        }

        @Override
        public void onRTCSwithToAudioMode() {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCSwithToAudioMode ");
                    BaseActivity.this.onRTCSwithToAudioMode();
                }
            });

        }

        @Override
        public void onRTCUserMessage(final String strPeerUserId, final String strMessage) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseActivity.this.onRTCUserMessage(strPeerUserId, strMessage);
                }
            });

        }

        @Override
        public void onRTCOpenRemoteVideoRender(final String strDevId) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCOpenVideoRender strDevId"+strDevId);
                    BaseActivity.this.onRTCOpenVideoRender(strDevId);
                }
            });

        }

        @Override
        public void onRTCCloseRemoteVideoRender(final String strDevId) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("p2pDemo","onRTCCloseVideoRender strDevId"+strDevId);
                    BaseActivity.this.onRTCCloseVideoRender(strDevId);
                }
            });
        }

    };

    public abstract void onConnected();

    /**
     */
    public abstract void onDisconnect(int nErrCode);

    /**
     * @param strPeerUserId
     * @param nCallMode
     * @param strUserData
     */
    public abstract void onRTCMakeCall(String strPeerUserId, int nCallMode, String strUserData);

    /**
     * @param strPeerUserId
     */
    public abstract void onRTCAcceptCall(String strPeerUserId);

    /**
     * @param strPeerUserId
     * @param nErrCode
     */
    public abstract void onRTCRejectCall(String strPeerUserId, int nErrCode);

    /**
     * @param strPeerUserId
     * @param nErrCode
     */
    public abstract void onRTCEndCall(String strPeerUserId, int nErrCode);

    /**
     */
    public abstract void onRTCSwithToAudioMode();

    /**
     * @param strPeerUserId
     * @param strMessage
     */
    public abstract void onRTCUserMessage(String strPeerUserId, String strMessage);

    /**
     * onRTCOpenVideoRender
     *
     * @param strDevId
     */
    public abstract void onRTCOpenVideoRender(String strDevId);

    /**
     * onRTCCloseVideoRender
     *
     * @param strDevId
     */
    public abstract void onRTCCloseVideoRender(String strDevId);

}
