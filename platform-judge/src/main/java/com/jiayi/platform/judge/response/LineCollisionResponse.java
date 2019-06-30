package com.jiayi.platform.judge.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:17
 */
@Getter
@Setter
@ToString
public class LineCollisionResponse {
    private String objectId;
    private String objectValue;
    private Integer matchCount;
    private Integer uniqueDevCount;
    private Long beginDate;
    private Long endDate;
    private String beginAddress = "";
    private String endAddress = "";
    private String desc;
    @ApiModelProperty(value = "一键搜索按钮显示状态（之前是电子档案）")
    private boolean eRecord;
    private String imsiImei = "";
}
