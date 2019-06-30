package com.jiayi.platform.common.enums;

/**
 * @author : weichengke
 * @date : 2019-04-10 10:11
 */
public enum CollectCategory {
    TRACK("track"), AUDIT("audit"), HEARTBEAT("heartbeat"), UNKNOWN("unknown");

    String code;

    CollectCategory(String code){
        this.code = code;
    }

    public String code() {
        return code;
    }
}
