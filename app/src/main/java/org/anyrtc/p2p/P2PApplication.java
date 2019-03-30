package org.anyrtc.p2p;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import org.anyrtc.common.enums.ARVideoCommon;
import org.anyrtc.p2p.utils.DBDao;
import org.anyrtc.p2p.utils.SharePrefUtil;
import org.anyrtc.rtp2pcall.ARP2PCallMode;
import org.anyrtc.rtp2pcall.ARP2PEngine;
import org.anyrtc.rtp2pcall.ARP2PEvent;
import org.anyrtc.rtp2pcall.ARP2PKit;
import org.anyrtc.rtp2pcall.ARP2POption;

/**
 * Created by Skyline on 2017/10/28.
 */

public class P2PApplication extends Application implements Application.ActivityLifecycleCallbacks{
    private static P2PApplication mInstance;
    private int activityCounter=0;
    private DBDao mDBDao;
    public static P2PApplication the() {
        if(null == mInstance) {
            mInstance = new P2PApplication();
        }
        return mInstance;
    }
    private ARP2PKit mP2pKit;
    private String mUserid="";

    private ARP2PEvent mCallbackHelper;

    public void setmCallback(ARP2PEvent mCallback) {
        this.mCallbackHelper = mCallback;
    }


    public P2PApplication() {
        mInstance = this;
        ARP2POption arp2POption = new ARP2POption();
        arp2POption.setVideoProfile(ARVideoCommon.ARVideoProfile.ARVideoProfile720x960);
        arp2POption.setDefaultFrontCamera(true);
        mP2pKit = new ARP2PKit();
        mP2pKit.setP2PEvent(mRTP2PCallHelper);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SharePrefUtil.init(this);
        registerActivityLifecycleCallbacks(this);

        //初始化P2P引擎并设置开发者信息  开发者信息可去anyrtc.io官网注册获取
        ARP2PEngine.Inst().initEngineWithAnyrtcInfo(getApplicationContext(), "",
                "", "",
                "");
        //配置私有云  没有可不填写
//        AnyRTCP2PEngine.Inst().ConfigServerForPriCloud("", 0);
        mDBDao=new DBDao(this);
    }

    public DBDao getmDBDao() {
        return mDBDao;
    }


    public ARP2PKit getmP2pKit() {
        return mP2pKit;
    }

    public String getmUserid() {
        return mUserid;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onActivityStarted(Activity activity) {
        activityCounter++;
        //如果mFinalCount ==1，说明是从后台到前台

        if (activityCounter == 1){
            Log.d("activity","前台");
            if(mP2pKit.isTurnOff()) {
                String userid = SharePrefUtil.getString("userid");
                if (!TextUtils.isEmpty(userid)) {
                    mP2pKit.turnOn(userid);
                }
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCounter--;
        //如果mFinalCount ==0，说明是前台到后台

        if (activityCounter == 0){
            Log.d("activity","后台");
            //说明从前台回到了后台
            mP2pKit.turnOff();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private ARP2PEvent mRTP2PCallHelper = new ARP2PEvent() {
        @Override
        public void onConnected() {
            if(null != mCallbackHelper) {
                mCallbackHelper.onConnected();
            }
        }

        @Override
        public void onDisconnect(int nErrCode) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onDisconnect(nErrCode);
            }
        }

        @Override
        public void onRTCMakeCall(String s, ARP2PCallMode arp2PCallMode, String s1) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCMakeCall(s, arp2PCallMode, s1);
            }
        }


        @Override
        public void onRTCAcceptCall(String strPeerUserId) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCAcceptCall(strPeerUserId);
            }
        }

        @Override
        public void onRTCRejectCall(String strPeerUserId, int nErrCode) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCRejectCall(strPeerUserId, nErrCode);
            }
        }

        @Override
        public void onRTCEndCall(String strPeerUserId, int nErrCode) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCEndCall(strPeerUserId, nErrCode);
            }
        }

        @Override
        public void onRTCSwithToAudioMode() {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCSwithToAudioMode();
            }
        }

        @Override
        public void onRTCUserMessage(String strPeerUserId, String strMessage) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCUserMessage(strPeerUserId, strMessage);
            }
        }

        @Override
        public void onRTCOpenRemoteVideoRender(String strDevId) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCOpenRemoteVideoRender(strDevId);
            }
        }

        @Override
        public void onRTCCloseRemoteVideoRender(String strDevId) {
            if(null != mCallbackHelper) {
                mCallbackHelper.onRTCCloseRemoteVideoRender(strDevId);
            }
        }

    };
}
