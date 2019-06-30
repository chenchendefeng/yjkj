package com.jiayi.platform.alarm.entity;

import com.jiayi.platform.caseinfo.entity.CaseInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bkyj_alarm_msg")
@Getter
@Setter
@ToString
public class AlarmMsgBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "case_id")
    private CaseInfo caseId;
    private Long strategyId;
    private String alarmSuspect;
    private String objectType;
    private String objectValue;
    private Date alarmTime;
    private Long deviceId;
    private Integer alarmType;
    private String assembleGroupId;
    private Integer isDistrict;
    private int status;
    private int sendStatus;
    private Date createAt;
    private Date updateAt;
}
