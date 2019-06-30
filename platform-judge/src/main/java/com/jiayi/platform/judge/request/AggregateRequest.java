package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class AggregateRequest extends JudgeRequest {
    private List<AggregateRequestFieldInfo> requestTmpls;
    private String aggregateType;
    private String resultName;
    private String resultDesc;
    private String resultSetName;
//    private String requestType;
//    private boolean isReferTo;
//    private Long queryId;
//    private  MiningAggregateQuery miningAggregateQuery;
//    private  List<ResultFieldDto> resultFieldDtos;
//    private Integer dataType;//1：常用库导入二次碰撞
//    private Integer index=1;//动态字段第几个位置为collision_result_aggregate表的result_value值
}
