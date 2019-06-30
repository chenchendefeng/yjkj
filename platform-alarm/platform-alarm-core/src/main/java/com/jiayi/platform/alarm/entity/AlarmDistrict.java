package com.jiayi.platform.alarm.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bkyj_alarm_district")
@Getter
@Setter
@ToString
public class AlarmDistrict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer type;
    @Column(name = "map_region")
    private String mapRegion;
    @Column(name = "ex_info")
    private String exInfo;
    private Integer district;
    @Column(name = "obj_type")
    private String objType;
    @Column(name = "start_time")
    @Temporal(TemporalType.DATE)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.DATE)
    private Date endTime;
    @Column(name = "be_long_valid")
    private Integer beLongValid;
    @Column(name = "user_id")
    private String userId;
    private String remark;
    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

    private Integer status;
}
