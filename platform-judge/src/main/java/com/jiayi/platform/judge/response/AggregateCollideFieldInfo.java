package com.jiayi.platform.judge.response;

import com.jiayi.platform.judge.entity.mysql.CollisionResultField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AggregateCollideFieldInfo {
    private Long id;
    private String fieldDesc;
    private String fieldType;
    private String fieldName;
    private String requestType;

    public AggregateCollideFieldInfo(CollisionResultField fields) {
        this.id = fields.getId();
        this.fieldDesc = fields.getFieldDesc();
        this.fieldType = fields.getFieldType();
        this.fieldName = fields.getFieldName();
        this.requestType = fields.getRequestType();
    }
}
