package com.jiayi.platform.judge.query;

import com.jiayi.platform.judge.enums.AreaTypeEnum;
import lombok.*;

@Getter
@Setter
@ToString
public class RectQuery extends BaseAreaQuery {
    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;

    public RectQuery() {
        type = AreaTypeEnum.RECT.name();
    }

    public RectQuery(double minLat, double maxLat, double minLng, double maxLng) {
        type = AreaTypeEnum.RECT.name();
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLng = minLng;
        this.maxLng = maxLng;
    }
}