package org.ar.arcall;

/**
 * @author Skyline
 */

public enum ARMeetZoomMode {
    /**
     * 多屏模式
     */
    normal(0),
    /**
     * 语音激励模式
     */
    single(1),
    /**
     * 驾驶模式
     */
    drive(2);

    public final int type;

    private ARMeetZoomMode(int type) {
        this.type = type;
    }

    public static ARMeetZoomMode getObject(int value) {
        for (int i = 0; i < ARMeetZoomMode.values().length; i++) {
            if (ARMeetZoomMode.values()[i].type == value) {
                return ARMeetZoomMode.values()[i];
            }
        }
        return ARMeetZoomMode.values()[0];
    }
}
