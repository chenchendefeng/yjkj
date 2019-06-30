package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : weichengke
 * @date : 2019-04-20 15:04
 */
@Getter
@Setter
@ToString
public class LineCollisionDto {
    private String objectValue;
    private Integer matchCount;
    private Integer uniqueDevCount;
    private Long minHappenAt;
    private Long maxHappenAt;
    private Long fromDeviceId;
    private Long toDeviceId;
    private String imsiImei = "";
}
