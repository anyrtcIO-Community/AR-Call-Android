package org.ar.arcall;

public class ARClertOption {
    public int level;
    public String area;
    public String business;


    /**
     *
     * @param level 等级：0~: 0等级最大，值越大，等级越小
     * @param area 区域设定;如果不设定，该频道下的所有客服都能为其服务
     * @param business 交易类型
     */
    public ARClertOption(int level, String area, String business) {
        this.level = level;
        this.area = area;
        this.business = business;
    }
}
