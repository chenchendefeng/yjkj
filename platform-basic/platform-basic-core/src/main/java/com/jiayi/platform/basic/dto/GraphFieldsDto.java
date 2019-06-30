package com.jiayi.platform.basic.dto;

public class GraphFieldsDto {
    private Long time;
    private Long count;

    public GraphFieldsDto() {
    }

    public GraphFieldsDto(Long time, Long count) {
        this.time = time;
        this.count = count;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
