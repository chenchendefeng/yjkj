package com.jiayi.platform.judge.entity.mysql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户查询请求的操作记录
 * @author : weichengke
 * @date : 2019-04-18 17:51
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "request_history")
public class RequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "case_id")
    private String caseId;
    @Column(name = "request_date")
    private Date requestDate;
    @Column(name = "query_id")
    private Long queryId; //查询结果queryHistory
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "request_remark")
    private String requestRemark;
    private Boolean valid;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
    @Column(name = "two_collision")
    private Boolean twoCollision;
    @Column(name = "result_name")
    private String resultName;
}
