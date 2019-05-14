package org.anyrtc.rtp2pcall;

/**
 * Created by Eric on 2016/11/8.
 */
@Deprecated
public interface RTP2PCallHelper {
    /**
     * Join P2P OK
     */
    public void onConnected();

    /**
     * P2P Disconnect
     */
    public void onDisconnect(int nErrCode);

    /**
     * 收到呼叫请求
     * @param strPeerUserId
     * @param nCallMode
     * @param strUserData
     */
    public void onRTCMakeCall(String strPeerUserId, int nCallMode, String strUserData);

    /**
     * 收到对方接受呼叫请求
     * @param strPeerUserId
     */
    public void onRTCAcceptCall(String strPeerUserId);

    /**
     * 收到拒绝呼叫请求
     * @param strPeerUserId
     * @param nErrCode
     */
    public void onRTCRejectCall(String strPeerUserId, int nErrCode);

    /**
     * P2P通话结束回调
     * @param strPeerUserId
     * @param nErrCode
     */
    public void onRTCEndCall(String strPeerUserId, int nErrCode);

    /**
     * 切换至语音呼叫回调
     */
    public void onRTCSwithToAudioMode();

    /**
     * 收到消息回调
     * @param strPeerUserId
     * @param strMessage
     */
    public void onRTCUserMessage(String strPeerUserId, String strMessage);

    /**
     * onRTCOpenVideoRender
     *
     * @param strDevId
     */
    public void onRTCOpenVideoRender(String strDevId);

    /**
     * onRTCCloseVideoRender
     *
     * @param strDevId
     */
    public void onRTCCloseVideoRender(String strDevId);
}
