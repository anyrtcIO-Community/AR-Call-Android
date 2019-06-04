package org.ar.call;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.ar.arcall.ARCallEngine;
import org.ar.arcall.ARCallEvent;
import org.ar.arcall.ARCallKit;
import org.ar.arcall.ARCallMode;
import org.ar.arcall.ARMeetZoomMode;
import org.ar.common.enums.ARNetQuality;


/**
 * Created by liuxiaozhong on 2019/4/11.
 */
public class CallApplication extends Application implements Application.ActivityLifecycleCallbacks{

    private static CallApplication application;

    private ARCallKit arCallKit;

    private ARCallEvent arCallEvent;

    private Activity curActivity;




    public void setArCallEvent(ARCallEvent arCallEvent) {
        this.arCallEvent = arCallEvent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SpUtil.init(this);
        registerActivityLifecycleCallbacks(this);
        //设置开发者信息  appID + token
        //配置私有云
        ARCallEngine.Inst().initEngineWithAppInfo(this, DeveloperInfo.APPID, DeveloperInfo.APPTOKEN);

    }

    public static CallApplication get() {
        if (null == application) {
            application = new CallApplication();
        }
        return application;
    }

    public CallApplication() {
        application = this;
        //获取ARCall配置类
//        ARCallOption option=ARCallEngine.Inst().getARCallOption();
        //实例化ARCall对象
        arCallKit = new ARCallKit();
        arCallKit.setArCallEvent(event);
    }

    public ARCallKit getArCallKit() {
        return arCallKit;
    }



    ARCallEvent event = new ARCallEvent() {
        @Override
        public void onConnected() {
            if (null != arCallEvent) {
                arCallEvent.onConnected();
            }
        }

        @Override
        public void onDisconnect(int nErrCode) {
            if (null != arCallEvent) {
                arCallEvent.onDisconnect(nErrCode);
                if (nErrCode==-1) {
                    curActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(curActivity, SetPhoneActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SpUtil.putString(Contants.PHONE, "");
                            Toast.makeText(curActivity,"你已在其他设备登陆",Toast.LENGTH_SHORT).show();
                            curActivity.startActivity(intent);
                            curActivity.finish();
                        }
                    });

                }

            }
        }


        @Override
        public void onRTCMakeCall(String strMeetId, String strPeerUserId, ARCallMode nCallMode, String strUserData, String strExtend) {
            if (curActivity instanceof CallActivity) {

            }else {
                if (null != arCallEvent) {
                    arCallEvent.onRTCMakeCall(strPeerUserId, strPeerUserId, nCallMode, strUserData, strExtend);
                }
            }
        }

        @Override
        public void onRTCAcceptCall(String strPeerUserId) {
            if (null != arCallEvent) {
                arCallEvent.onRTCAcceptCall(strPeerUserId);
            }
        }

        @Override
        public void onRTCRejectCall(String strPeerUserId, int errCode) {
            if (null != arCallEvent) {
                arCallEvent.onRTCRejectCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCEndCall(String strPeerUserId, int errCode) {
            if (null != arCallEvent) {
                arCallEvent.onRTCEndCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCSipSupport(boolean bPstn, boolean bExtension, boolean bNull) {
            if (null != arCallEvent) {
                arCallEvent.onRTCSipSupport(bPstn, bExtension, bNull);
            }
        }

        @Override
        public void onRTCSwithToAudioMode() {
            if (null != arCallEvent) {
                arCallEvent.onRTCSwithToAudioMode();
            }
        }

        @Override
        public void onRTCUserMessage(String strPeerUserId, String strMessage) {
            if (null != arCallEvent) {
                arCallEvent.onRTCUserMessage(strPeerUserId, strMessage);
            }
        }

        @Override
        public void onRTCOpenRemoteVideoRender(String strPeerUserId, String strVidRenderId, String strUserData) {
            if (null != arCallEvent) {
                arCallEvent.onRTCOpenRemoteVideoRender(strPeerUserId, strVidRenderId, strUserData);
            }
        }

        @Override
        public void onRTCCloseRemoteVideoRender(String strPeerUserId, String strVidRenderId) {
            if (null != arCallEvent) {
                arCallEvent.onRTCCloseRemoteVideoRender(strPeerUserId, strVidRenderId);
            }
        }

        @Override
        public void onRTCOpenRemoteAudioTrack(String strPeerUserId, String strUserData) {
            if (null != arCallEvent) {
                arCallEvent.onRTCOpenRemoteAudioTrack(strPeerUserId, strUserData);
            }
        }

        @Override
        public void onRTCCloseRemoteAudioTrack(String strPeerUserId) {
            if (null != arCallEvent) {
                arCallEvent.onRTCCloseRemoteAudioTrack(strPeerUserId);
            }
        }

        @Override
        public void onRTCRemoteAudioActive(String s, int i, int i1) {
            if (null != arCallEvent) {
                arCallEvent.onRTCRemoteAudioActive(s,i,i1);
            }
        }

        @Override
        public void onRTCLocalAudioActive(int i, int i1) {
            if (null != arCallEvent) {
                arCallEvent.onRTCLocalAudioActive(i,i1);
            }
        }

        @Override
        public void onRTCRemoteNetworkStatus(String s, int i, int i1, ARNetQuality arNetQuality) {
            if (null != arCallEvent) {
                arCallEvent.onRTCRemoteNetworkStatus(s,i,i1,arNetQuality);
            }
        }

        @Override
        public void onRTCLocalNetworkStatus(int i, int i1, ARNetQuality arNetQuality) {
            if (null != arCallEvent) {
                arCallEvent.onRTCLocalNetworkStatus(i,i1,arNetQuality);
            }
        }

        @Override
        public void onRTCZoomPageInfo(ARMeetZoomMode arMeetZoomMode, int nAllPages, int nCurPage, int nAllRender, int nScrnBeginIdx, int nNum) {
            if (null != arCallEvent) {
                arCallEvent.onRTCZoomPageInfo(arMeetZoomMode, nAllPages, nCurPage, nAllRender, nScrnBeginIdx, nNum);
            }
        }

        @Override
        public void onRTCUserCome(String s, String s1, String s2) {
            if (null != arCallEvent) {
                arCallEvent.onRTCUserCome(s, s1, s2);
            }
        }

        @Override
        public void onRTCUserOut(String s, String s1) {
            if (null != arCallEvent) {
                arCallEvent.onRTCUserOut(s, s1);
            }
        }

        @Override
        public void onRTCUserCTIStatus(int nQueueNum) {
            if (null!=arCallEvent){
                arCallEvent.onRTCUserCTIStatus(nQueueNum);
            }
        }

        @Override
        public void onRTCClerkCTIStatus(int nQueueNum, int nAllClerk, int nWorkingClerk) {
            if (null!=arCallEvent){
                arCallEvent.onRTCClerkCTIStatus(nQueueNum,nAllClerk,nWorkingClerk);
            }
        }

        @Override
        public void onRTCAVStatus(String strRTCPeerId, boolean bAudio, boolean bVideo) {
            if (null!=arCallEvent){
                arCallEvent.onRTCAVStatus(strRTCPeerId,bAudio,bVideo);
            }
        }

    };

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.curActivity=activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
