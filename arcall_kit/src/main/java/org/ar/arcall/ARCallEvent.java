package org.ar.arcall;

import org.ar.common.enums.ARNetQuality;

/**
 *
 * @author liuxiaozhong
 * @date 2019/1/16
 */
public abstract class ARCallEvent {
    public abstract void onConnected();

    public abstract void onDisconnect(int code);

    public abstract void onRTCMakeCall(String meetId, String userId, ARCallMode callMode, String userData, String extend);

    public abstract void onRTCAcceptCall(String userId);

    public abstract void onRTCRejectCall(String userId, int code);

    public abstract void onRTCEndCall(String userId, int code);

    public abstract void onRTCSipSupport(boolean bPstn, boolean bExtension, boolean bNull);

    public abstract void onRTCSwithToAudioMode();

    public abstract void onRTCUserMessage(String userId, String message);

    public abstract void onRTCOpenRemoteVideoRender(String userId, String vidRenderId, String userData);

    public abstract void onRTCCloseRemoteVideoRender(String userId, String vidRenderId);

    public abstract void onRTCOpenRemoteAudioTrack(String userId, String userData);

    public abstract void onRTCCloseRemoteAudioTrack(String userId);

    public abstract void onRTCRemoteAudioActive(String userId, int level, int time);

    public abstract void onRTCLocalAudioActive(int level, int time);

    public abstract void onRTCRemoteNetworkStatus(String userId, int netSpeed, int packetLost, ARNetQuality netQuality);

    public abstract void onRTCLocalNetworkStatus(int netSpeed, int packetLost, ARNetQuality netQuality);

    public abstract void onRTCZoomPageInfo(ARMeetZoomMode zoomMode, int allPages, int curPage, int allRender, int scrnBeginIdx, int num);

    public abstract void onRTCUserCome(String userId, String vidRenderId, String userData);

    public abstract void onRTCUserOut(String userId, String vidRenderId);

    public abstract void onRTCUserCTIStatus(int nQueueNum);

    public abstract void onRTCClerkCTIStatus(int nQueueNum, int nAllClerk, int nWorkingClerk);

    public abstract void onRTCAVStatus(String strRTCPeerId, boolean bAudio, boolean bVideo);
}
