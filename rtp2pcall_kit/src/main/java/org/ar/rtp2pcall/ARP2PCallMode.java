package org.ar.rtp2pcall;

/**
 * Created by liuxiaozhong on 2019/1/16.
 */
public enum ARP2PCallMode {
    /**
     * Default - VIDEO
     */
    video(0),//视频呼叫
    /**
     * VIDEO PRO
     */
    video_pro(1),// 视频呼叫Pro模式: 被呼叫方可先看到对方视频
    /**
     * AUDIO
     */
    audio(2), //    音频呼叫
    /**
     * VIDEO MONITOR  视频监看模式,此模式被叫端只能是Android
     */
    monitor(3);// 视频监看模式,此模式被叫端只能是Android

    public final int level;

    ARP2PCallMode(int level) {
        this.level = level;
    }
}
