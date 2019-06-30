package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : qinxiaoni
 * @date : 2019-04-10 21:11
 */
@Entity
@Table(name = "device_time_stream_statistic")
@Getter
@Setter
@ToString
@IdClass(StreamStatisticGroupPK.class)
public class DeviceTimeStreamStatistic implements Serializable {
    @Id
    private String src;
    @Id
    private String code;
    @Column(name = "data_start_time")
    private Long dataStartTime;
    @Column(name = "data_end_time")
    private Long dataEndTime;
    @Column(name = "heartbeat_time")
    private Long heartbeatTime;
    @Column(name = "record_time")
    private Long recordTime;
    @Column(name = "ip_port")
    private String ipPort;
//    private Integer threshold;
//    private Integer qualify;// todo 合格状态：0不合格，1合格？
//    private Integer average;// 7天平均值
    private String softwareVersion;// 软件版本号
    private String firmwareVersion;//固件版本号
    private String repo;

    public String srcAndCode(){
        return src + "|" + code;
    }

    public DeviceTimeStreamStatistic() {
        this.dataStartTime = 0L;
        this.dataEndTime = 0L;
        this.heartbeatTime = 0L;
        this.recordTime = new Date().getTime();
    }
}
