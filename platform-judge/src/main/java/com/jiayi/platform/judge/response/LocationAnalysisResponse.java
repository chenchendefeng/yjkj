package com.jiayi.platform.judge.response;

import com.jiayi.platform.judge.dto.LocationAnalysisDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class LocationAnalysisResponse {
    private Integer id;// 编号
    private String address = "";
    private Integer deviceCount;// 设备数
    private Long numberCount;// 总统计数
    private double longitude;
    private double latitude;
    private Long gridCode;
    private List<Long> timeCount;// 各时间段统计数

    public void setDailyTimeCount(LocationAnalysisDto dto) {
        List<Long> timeCount = new ArrayList<>();
        timeCount.add(dto.getCount0());
        timeCount.add(dto.getCount1());
        timeCount.add(dto.getCount2());
        timeCount.add(dto.getCount3());
        timeCount.add(dto.getCount4());
        timeCount.add(dto.getCount5());
        timeCount.add(dto.getCount6());
        timeCount.add(dto.getCount7());
        timeCount.add(dto.getCount8());
        timeCount.add(dto.getCount9());
        timeCount.add(dto.getCount10());
        timeCount.add(dto.getCount11());
        timeCount.add(dto.getCount12());
        timeCount.add(dto.getCount13());
        timeCount.add(dto.getCount14());
        timeCount.add(dto.getCount15());
        timeCount.add(dto.getCount16());
        timeCount.add(dto.getCount17());
        timeCount.add(dto.getCount18());
        timeCount.add(dto.getCount19());
        timeCount.add(dto.getCount20());
        timeCount.add(dto.getCount21());
        timeCount.add(dto.getCount22());
        timeCount.add(dto.getCount23());
        this.timeCount = timeCount;
    }

    public void setWeeklyTimeCount(LocationAnalysisDto dto) {
        List<Long> timeCount = new ArrayList<>();
        timeCount.add(dto.getCount1());
        timeCount.add(dto.getCount2());
        timeCount.add(dto.getCount3());
        timeCount.add(dto.getCount4());
        timeCount.add(dto.getCount5());
        timeCount.add(dto.getCount6());
        timeCount.add(dto.getCount0());
        this.timeCount = timeCount;
    }

    public void setMonthlyTimeCount(LocationAnalysisDto dto) {
        List<Long> timeCount = new ArrayList<>();
        timeCount.add(dto.getCount1());
        timeCount.add(dto.getCount2());
        timeCount.add(dto.getCount3());
        timeCount.add(dto.getCount4());
        timeCount.add(dto.getCount5());
        timeCount.add(dto.getCount6());
        timeCount.add(dto.getCount7());
        timeCount.add(dto.getCount8());
        timeCount.add(dto.getCount9());
        timeCount.add(dto.getCount10());
        timeCount.add(dto.getCount11());
        timeCount.add(dto.getCount12());
        timeCount.add(dto.getCount13());
        timeCount.add(dto.getCount14());
        timeCount.add(dto.getCount15());
        timeCount.add(dto.getCount16());
        timeCount.add(dto.getCount17());
        timeCount.add(dto.getCount18());
        timeCount.add(dto.getCount19());
        timeCount.add(dto.getCount20());
        timeCount.add(dto.getCount21());
        timeCount.add(dto.getCount22());
        timeCount.add(dto.getCount23());
        timeCount.add(dto.getCount24());
        timeCount.add(dto.getCount25());
        timeCount.add(dto.getCount26());
        timeCount.add(dto.getCount27());
        timeCount.add(dto.getCount28());
        timeCount.add(dto.getCount29());
        timeCount.add(dto.getCount30());
        timeCount.add(dto.getCount31());
        this.timeCount = timeCount;
    }
}
