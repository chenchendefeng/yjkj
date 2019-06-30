package com.jiayi.platform.judge.entity.mysql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : weichengke
 * @date : 2019-04-18 17:41
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "query_history")
public class QueryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "request_type")
    private String requestType;
    @Column(name = "request_parameter")
    private String requestParameter;
    private String md5;
    @Column(name = "result_count")
    private Long resultCount;
    private Integer status;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
