package org.ar.arcall;

/**
 *
 * @author liuxiaozhong
 * @date 2019/1/16
 */
public enum ARCallMode {
    /**
     * Default - VIDEO 视频呼叫
     */
    video(0),
    /**
     * VIDEO PRO 视频呼叫Pro模式: 被呼叫方可先看到对方视频
     */
    video_pro(1),
    /**
     * AUDIO 音频呼叫
     */
    audio(2),
    /**
     * VIDEO MONITOR  视频监看模式,此模式被叫端只能是Android
     */
    monitor(3),
    /**
     * 会议邀请，可以同时邀请多人
     */
    meet_invite(10),
    /**
     * 呼叫中心音频
     */
    call_cit_audio(20),
    /**
     * 呼叫中心视频
     */
    call_cit_video(21);

    public final int level;

    ARCallMode(int level) {
        this.level = level;
    }

    public static ARCallMode getObject(int value) {
        for (int i = 0; i < ARCallMode.values().length; i++) {
            if (ARCallMode.values()[i].level == value) {
                return ARCallMode.values()[i];
            }
        }
        return ARCallMode.values()[0];
    }
}
