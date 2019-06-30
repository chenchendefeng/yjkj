package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "device_time_statistic")
@Getter
@Setter
@ToString
@IdClass(StreamStatisticGroupPK.class)
public class DeviceTimeStatistic {
    @Id
    private String src;
    @Id
    private String code;
    private Integer threshold;
    private Integer qualify;
    private Integer average;

    public String srcAndCode(){
        return src + "|" + code;
    }
}
