package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LocationAnalysisDto {
    private Long startAt;
    private Long resultGridCode;
    private String relations;
    private String devices;
    private Double averageLongitude;
    private Double averageLatitude;
    private Long countTotal;
    private Long count0;
    private Long count1;
    private Long count2;
    private Long count3;
    private Long count4;
    private Long count5;
    private Long count6;
    private Long count7;
    private Long count8;
    private Long count9;
    private Long count10;
    private Long count11;
    private Long count12;
    private Long count13;
    private Long count14;
    private Long count15;
    private Long count16;
    private Long count17;
    private Long count18;
    private Long count19;
    private Long count20;
    private Long count21;
    private Long count22;
    private Long count23;
    private Long count24;
    private Long count25;
    private Long count26;
    private Long count27;
    private Long count28;
    private Long count29;
    private Long count30;
    private Long count31;

    public long countHour0To6() {
        return count0 + count1 + count2 + count3 + count4 + count5;
    }

    public long countHour6To12() {
        return count6 + count7 + count8 + count9 + count10 + count11;
    }

    public long countHour12To18() {
        return count12 + count13 + count14 + count15 + count16 + count17;
    }

    public long countHour18To24() {
        return count18 + count19 + count20 + count21 + count22 + count23;
    }

    public long countDay1To5() {
        return count1 + count2 + count3 + count4 + count5;
    }

    public long countDay6To10() {
        return count6 + count7 + count8 + count9 + count10;
    }

    public long countDay11To15() {
        return count11 + count12 + count13 + count14 + count15;
    }

    public long countDay16To20() {
        return count16 + count17 + count18 + count19 + count20;
    }

    public long countDay21To25() {
        return count21 + count22 + count23 + count24 + count25;
    }

    public long countDay26To31() {
        return count26 + count27 + count28 + count29 + count30 + count31;
    }
}
