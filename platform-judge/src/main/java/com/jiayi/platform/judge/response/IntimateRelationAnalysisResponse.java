package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IntimateRelationAnalysisResponse {
    private String objectTypeName;
    private String objectValue;
    private Integer matchDays;
    private Long matchTracks;
    private String desc = "";
}
