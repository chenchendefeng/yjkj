package com.jiayi.platform.repo.minerepo.enums;

/**
 * 数据挖掘库详情来源
 */
public enum MiningRepoSourceEnum {
    /**
     * 挖掘
     */
    MINING(0),
    /**
     * 手动添加
     */
    MANUAL(1),
    /**
     * 导入
     */
    IMPORT(2)
    ;

    private int code;

    MiningRepoSourceEnum (int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
