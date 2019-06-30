package com.jiayi.platform.alarm.entity;

import javax.persistence.*;

/**
 * @program: platform-common
 * @description: 实体
 * @author: Mr.liang
 * @create: 2018-11-26 15:14
 **/
//fixme 未使用到
@Entity
@Table(name = "t_obj_type")
public class ObjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(name = "audit_id")
//    private Long auditId;
    @Column(name = "topic_name")
    private String topicName;
    private String fields;
    private String condition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Long getAuditId() {
//        return auditId;
//    }
//
//    public void setAuditId(Long auditId) {
//        this.auditId = auditId;
//    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ObjectType{" +
                "id=" + id +
                ", topicName='" + topicName + '\'' +
                ", fields='" + fields + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }
}
