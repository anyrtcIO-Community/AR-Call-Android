package org.anyrtc.p2p.activity;

import org.anyrtc.rtp2pcall.RTP2PCallHelper;

/**
 * Created by liuxiaozhong on 2017/11/10.
 */

public abstract class P2PListener implements RTP2PCallHelper{
    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnect(int nErrCode) {
    }

    @Override
    public void onRTCMakeCall(final String strPeerUserId, final int nCallMode, String strUserData) {
    }

    @Override
    public void onRTCAcceptCall(String strPeerUserId) {

    }

    @Override
    public void onRTCRejectCall(String strPeerUserId, int errCode) {

    }

    @Override
    public void onRTCEndCall(String strPeerUserId, int errCode) {
    }

    @Override
    public void onRTCSwithToAudioMode() {

    }

    @Override
    public void onRTCUserMessage(String strPeerUserId, String strMessage) {

    }

    @Override
    public void onRTCOpenVideoRender(String strDevId) {

    }

    @Override
    public void onRTCCloseVideoRender(String strDevId) {

    }
}
