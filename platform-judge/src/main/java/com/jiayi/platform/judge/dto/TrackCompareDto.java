package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@Setter
@ToString
public class TrackCompareDto {
    private String srcObjectValue;
    private Long srcRecordAt;
    private Long srcDeviceId;
    private Integer srcLongitude;
    private Integer srcLatitude;
    private String desObjectValue;
    private Long desRecordAt;
    private Long desDeviceId;
    private Integer desLongitude;
    private Integer desLatitude;

    public TrackCompareDto() {
    }

    public TrackCompareDto(String srcObjectValue, Long srcRecordAt, Long srcDeviceId, Integer srcLongitude, Integer srcLatitude,
                           String desObjectValue, Long desRecordAt, Long desDeviceId, Integer desLongitude, Integer desLatitude) {
        this.srcObjectValue = srcObjectValue;
        this.srcRecordAt = srcRecordAt;
        this.srcDeviceId = srcDeviceId;
        this.srcLongitude = srcLongitude;
        this.srcLatitude = srcLatitude;
        this.desObjectValue = desObjectValue;
        this.desRecordAt = desRecordAt;
        this.desDeviceId = desDeviceId;
        this.desLongitude = desLongitude;
        this.desLatitude = desLatitude;
    }

    public TrackCompareDto(TrackCompareInfo srcInfo, TrackCompareInfo desInfo) {
        if (srcInfo != null) {
            this.srcObjectValue = srcInfo.getObjectValue();
            this.srcRecordAt = srcInfo.getRecordAt();
            this.srcDeviceId = srcInfo.getDeviceId();
            this.srcLongitude = srcInfo.getLongitude();
            this.srcLatitude = srcInfo.getLatitude();
        }
        if (desInfo != null) {
            this.desObjectValue = desInfo.getObjectValue();
            this.desRecordAt = desInfo.getRecordAt();
            this.desDeviceId = desInfo.getDeviceId();
            this.desLongitude = desInfo.getLongitude();
            this.desLatitude = desInfo.getLatitude();
        }
    }

    public TrackCompareDto(Pair<TrackCompareInfo, TrackCompareInfo> pairs) {
        this.srcObjectValue = pairs.getLeft().getObjectValue();
        this.srcRecordAt = pairs.getLeft().getRecordAt();
        this.srcDeviceId = pairs.getLeft().getDeviceId();
        this.srcLongitude = pairs.getLeft().getLongitude();
        this.srcLatitude = pairs.getLeft().getLatitude();
        this.desObjectValue = pairs.getRight().getObjectValue();
        this.desRecordAt = pairs.getRight().getRecordAt();
        this.desDeviceId = pairs.getRight().getDeviceId();
        this.desLongitude = pairs.getRight().getLongitude();
        this.desLatitude = pairs.getRight().getLatitude();
    }
}
