package org.ar.rtp2pcall;

import org.ar.common.enums.ARVideoCommon;

/**
 * Created by liuxiaozhong on 2019/1/16.
 */
public class ARP2POption {
    /**
     * 前置摄像头；默认：true（前置摄像头）
     */
    private boolean isDefaultFrontCamera = true;
    /**
     * anyRTC屏幕方向；默认：竖屏
     */
    private ARVideoCommon.ARVideoOrientation videoOrientation = ARVideoCommon.ARVideoOrientation.Portrait;
    /**
     * anyRTC视频清晰标准；默认：标清（AnyRTC_Video_SD）
     */
    private ARVideoCommon.ARVideoProfile videoProfile = ARVideoCommon.ARVideoProfile.ARVideoProfile360x640;

    private ARVideoCommon.ARVideoFrameRate videoFps = ARVideoCommon.ARVideoFrameRate.ARVideoFrameRateFps15;

    public ARP2POption() {
    }

    public void setOptionParams(boolean isDefaultFrontCamera, ARVideoCommon.ARVideoOrientation videoOrientation, ARVideoCommon.ARVideoProfile videoProfile, ARVideoCommon.ARVideoFrameRate videoFps) {
        this.isDefaultFrontCamera = isDefaultFrontCamera;
        this.videoOrientation = videoOrientation;
        this.videoProfile = videoProfile;
        this.videoFps = videoFps;
    }

    protected boolean isDefaultFrontCamera() {
        return isDefaultFrontCamera;
    }

    public void setDefaultFrontCamera(boolean defaultFrontCamera) {
        isDefaultFrontCamera = defaultFrontCamera;
    }

    protected ARVideoCommon.ARVideoOrientation getVideoOrientation() {
        return videoOrientation;
    }

    public void setVideoOrientation(ARVideoCommon.ARVideoOrientation videoOrientation) {
        this.videoOrientation = videoOrientation;
    }

    protected ARVideoCommon.ARVideoProfile getVideoProfile() {
        return videoProfile;
    }

    public void setVideoProfile(ARVideoCommon.ARVideoProfile videoProfile) {
        this.videoProfile = videoProfile;
    }

    protected ARVideoCommon.ARVideoFrameRate getVideoFps() {
        return videoFps;
    }

    public void setVideoFps(ARVideoCommon.ARVideoFrameRate videoFps) {
        this.videoFps = videoFps;
    }
}
