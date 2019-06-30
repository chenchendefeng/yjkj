package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiFeatureAnalysisResponse {
    private String objectTypeName;
    private String objectValue;
    private Integer matchDays;
    private Double matchCount;
    private Long matchTracks;
    private String desc = "";
}
