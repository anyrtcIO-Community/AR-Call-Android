package org.anyrtc.p2p;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.anyrtc.p2p.utils.DBDao;
import org.anyrtc.p2p.utils.SharePrefUtil;
import org.anyrtc.rtp2pcall.AnyRTCP2PEngine;
import org.anyrtc.rtp2pcall.RTP2PCallKit;

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
    private RTP2PCallKit mP2pKit;
    private String mUserid="";


    public P2PApplication() {
        mInstance = this;
        mP2pKit = new RTP2PCallKit();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SharePrefUtil.init(this);
        registerActivityLifecycleCallbacks(this);

        //初始化P2P引擎并设置开发者信息  开发者信息可去anyrtc.io官网注册获取
        AnyRTCP2PEngine.Inst().initEngineWithAnyrtcInfo(getApplicationContext(),"", "", "", "");
        //配置私有云  没有可不填写
//        AnyRTCP2PEngine.Inst().ConfigServerForPriCloud("", 0);
        mDBDao=new DBDao(this);
    }

    public DBDao getmDBDao() {
        return mDBDao;
    }


    public RTP2PCallKit getmP2pKit() {
        return mP2pKit;
    }

    public String getmUserid() {
        return mUserid;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

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
}
