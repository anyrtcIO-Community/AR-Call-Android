package org.ar.rtp2pcall;

/**
 * Created by Eric on 2016/11/8.
 */

public interface ARP2PHelper {

    public void onConnected();

    public void onDisconnect(int nErrCode);

    public void onRTCMakeCall(String strPeerUserId, int nCallMode, String strUserData);

    public void onRTCAcceptCall(String strPeerUserId);

    public void onRTCRejectCall(String strPeerUserId, int errCode);

    public void onRTCEndCall(String strPeerUserId, int errCode);

    public void onRTCSwithToAudioMode();

    public void onRTCUserMessage(String strPeerUserId, String strMessage);

    public void onRTCOpenVideoRender(String strDevId);

    public void onRTCCloseVideoRender(String strDevId);
}
