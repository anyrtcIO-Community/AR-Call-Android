package org.ar.arcall;

public class ARQueueOption {
    public ARCallMode callMode;
    public String userData;
    public int level;
    public String area;
    public String business;

    public ARQueueOption(ARCallMode callMode, String userData, int level, String area, String business) {
        this.callMode = callMode;
        this.userData = userData;
        this.level = level;
        this.area = area;
        this.business = business;
    }
}
