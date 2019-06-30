package com.jiayi.platform.judge.entity.mysql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jiayi.platform.judge.enums.JudgeStatus;
import com.jiayi.platform.judge.enums.RequestType;
import com.jiayi.platform.security.core.entity.UserBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "request_history")
public class RequestHistoryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id")
    private UserBean user;
    @ManyToOne
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "query_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private QueryHistory queryHistory;
    @Column(name = "case_id")
    private Integer caseId;
    @Column(name = "result_name")
    private String resultName;
    @Column(name = "request_remark")
    private String requestRemark;
    @Column(name = "request_date")
    private Date requestDate;
    private Short valid;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
    @Column(name = "two_collision")
    private Short twoCollision;
    @Transient
    private String validDate;

    public String getRequestType() {
        return queryHistory.getRequestType();
    }

    public void setRequestType(String requestType) {
        queryHistory.setRequestType(requestType);
    }

    public String getRequestParameter() {
        return queryHistory.getRequestParameter();
    }

    public void setRequestParameter(String requestParameter) {
        queryHistory.setRequestParameter(requestParameter);
    }

    public Long getResultCount() {
        return queryHistory.getResultCount();
    }

    public Integer getStatus() {
        return queryHistory.getStatus();
    }

    public String getStatusDesc() {
        return JudgeStatus.getStatusByCode(queryHistory.getStatus()).message();
    }

    public String getTypeDesc() {
        return RequestType.getRequestType(queryHistory.getRequestType()).description();
    }

    public String getSecondDataSource() {
        StringBuilder sb = new StringBuilder();
        if ("collision_aggregate".equals(queryHistory.getRequestType())) {
            try {
                String json = queryHistory.getRequestParameter();
                JSONObject jsonObject = JSONObject.parseObject(json);//new JSONObject(json);
                String type = null;
                type = jsonObject.getString("aggregateType");
                JSONArray arr = jsonObject.getJSONArray("requestTmpls");
                String source1 = arr.getJSONObject(0).getString("resultSetName");
                String source2 = arr.getJSONObject(1).getString("resultSetName");
                sb.append(source1).append("与").append(source2);
                if ("intersect".equals(type)) {
                    sb.append("交集");
                }
                if ("union".equals(type)) {
                    sb.append("并集");
                }
                if ("subtract".equals(type)) {
                    sb.append("差集");
                }
                if ("xor".equals(type)) {
                    sb.append("异或集");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
