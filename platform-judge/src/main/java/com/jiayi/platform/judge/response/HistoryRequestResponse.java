package com.jiayi.platform.judge.response;

import com.jiayi.platform.judge.entity.mysql.RequestHistoryInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class HistoryRequestResponse {
    private String requestType;
    private Date requestDate;
    private String requestParameter;
    private Long userId;
    private String requestRemark;
    private Long id;
    private Long resultCount;
    private int status;


    public HistoryRequestResponse(RequestHistoryInfo requestHistoryInfo) {
        super();
        this.requestType = requestHistoryInfo.getRequestType();
        this.requestDate = requestHistoryInfo.getRequestDate();
        this.requestParameter = requestHistoryInfo.getRequestParameter();
        this.userId = requestHistoryInfo.getUser().getId();
        this.requestRemark = requestHistoryInfo.getRequestRemark();
        this.id = requestHistoryInfo.getId();
        this.resultCount = requestHistoryInfo.getResultCount();
        this.status = requestHistoryInfo.getStatus();
    }
}
