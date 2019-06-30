package com.jiayi.platform.judge.response;

import com.jiayi.platform.judge.enums.ResultFieldEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QueryResultFieldInfo {
    private String id;
    private String text;
    private String type;

    public QueryResultFieldInfo(String id, String text, String type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

    public QueryResultFieldInfo(ResultFieldEnum resultField) {
        this.id = resultField.responseName();
        this.text = resultField.resultDesc();
        this.type = resultField.resultType();
    }
}
