package org.ar.rtp2pcall;

/**
 * @author liuxiaozhong
 * @date 2019/1/16
 */
public abstract class ARP2PEvent {

    public abstract void onConnected();

    public abstract void onDisconnect(int code);

    public abstract void onRTCMakeCall(String userId, ARP2PCallMode callMode, String userData);

    public abstract void onRTCAcceptCall(String userId);

    public abstract void onRTCRejectCall(String userId, int code);

    public abstract void onRTCEndCall(String userId, int code);

    public abstract void onRTCSwithToAudioMode();

    public abstract void onRTCUserMessage(String userId, String message);

    public abstract void onRTCOpenRemoteVideoRender(String publishId);

    public abstract void onRTCCloseRemoteVideoRender(String publishId);
}
