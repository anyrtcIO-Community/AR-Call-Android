package org.ar.arcall;

/**
 * Created by Eric on 2016/11/8.
 */

public interface ARCallHelper {
    public void onConnected();

    public void onDisconnect(int nErrCode);

    public void onRTCMakeCall(String strMeetId, String strPeerUserId, int nCallMode, String strUserData, String strExtend);

    public void onRTCAcceptCall(String strPeerUserId);

    public void onRTCRejectCall(String strPeerUserId, int errCode);

    public void onRTCEndCall(String strPeerUserId, int errCode);

    public void onRTCSipSupport(boolean bPstn, boolean bExtension, boolean bNull);

    public void onRTCSwithToAudioMode();

    public void onRTCUserMessage(String strPeerUserId, String strMessage);

    public void onRTCOpenVideoRender(String strPeerUserId, String strVidRenderId, String strUserData);

    public void onRTCCloseVideoRender(String strPeerUserId, String strVidRenderId);

    public void onRTCOpenAudioTrack(String strPeerUserId, String strUserData);

    public void onRTCCloseAudioTrack(String strPeerUserId);

    public void onRTCAudioLevel(String strPeerUserId, int nLevel, int nShowTime);

    public void onRTCNetworkStatus(String strPeerUserId, int nNetSpeed, int nPacketLost);

    public void onRTCZoomPageInfo(int nZoomMode, int nAllPages, int nCurPage, int nAllRender, int nScrnBeginIdx, int nNum);

    public void onRTCUserCome(String userId, String vidRenderId, String userData);

    public void onRTCUserOut(String userId, String vidRenderId);

    public void onRTCUserCTIStatus(int nQueueNum);

    public void onRTCClerkCTIStatus(int nQueueNum, int nAllClerk, int nWorkingClerk);

    public void onRTCAVStatus(String strRTCPeerId, boolean bAudio, boolean bVideo);
}
