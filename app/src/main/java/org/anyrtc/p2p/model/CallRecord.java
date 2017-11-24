package org.anyrtc.p2p.model;

/**
 * Created by liuxiaozhong on 2017/11/9.
 */

public class CallRecord {

    /**
     * id : 110
     * time : 00:24
     * data : 2017.1.1
     * mode : 0 video  1  video_pre 2 audio
     * state : 0 已接通 1 未接通 2 拒绝接通  3  已经拨打
     */

    private String userid;
    private String time;
    private String data;
    private int mode;
    private int state=1;


    public String getUserid() {
        return userid;
    }

    public void setId(String userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
