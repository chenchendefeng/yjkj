package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@ToString
public class MovementAnalysisDto {
    private Long gridCode;
    private Long minHappenAt;
    private Long maxHappenAt;
    private Double averageLongitude;
    private Double averageLatitude;
    private Double averageStayTime;
    private String relations;
    private String devices;
    private Long in0;
    private Long in1;
    private Long in2;
    private Long in3;
    private Long in4;
    private Long in5;
    private Long in6;
    private Long in7;
    private Long in8;
    private Long in9;
    private Long in10;
    private Long in11;
    private Long in12;
    private Long in13;
    private Long in14;
    private Long in15;
    private Long in16;
    private Long in17;
    private Long in18;
    private Long in19;
    private Long in20;
    private Long in21;
    private Long in22;
    private Long in23;
    private Long out0;
    private Long out1;
    private Long out2;
    private Long out3;
    private Long out4;
    private Long out5;
    private Long out6;
    private Long out7;
    private Long out8;
    private Long out9;
    private Long out10;
    private Long out11;
    private Long out12;
    private Long out13;
    private Long out14;
    private Long out15;
    private Long out16;
    private Long out17;
    private Long out18;
    private Long out19;
    private Long out20;
    private Long out21;
    private Long out22;
    private Long out23;
    private Long stay0;
    private Long stay6;
    private Long stay12;
    private Long stay18;

    public String getAllStayTime() {
        double totalStayTime = stay0 + stay6 + stay12 + stay18;
        return "00:00-05:59(" + Math.round(stay0 / totalStayTime * 100) + "%); " +
                "06:00-11:59(" + Math.round(stay6 / totalStayTime * 100) + "%); " +
                "12:00-17:59(" + Math.round(stay12 / totalStayTime * 100) + "%); " +
                "18:00-23:59(" + Math.round(stay18 / totalStayTime * 100) + "%)";
    }

    public String getTop3EnterTime() {
        List<Pair<Integer, Long>> inTimes = Arrays.asList(
                Pair.of(0, in0), Pair.of(1, in1), Pair.of(2, in2), Pair.of(3, in3),
                Pair.of(4, in4), Pair.of(5, in5), Pair.of(6, in6), Pair.of(7, in7),
                Pair.of(8, in8), Pair.of(9, in9), Pair.of(10, in10), Pair.of(11, in11),
                Pair.of(12, in12), Pair.of(13, in13), Pair.of(14, in14), Pair.of(15, in15),
                Pair.of(16, in16), Pair.of(17, in17), Pair.of(18, in18), Pair.of(19, in19),
                Pair.of(20, in20), Pair.of(21, in21), Pair.of(22, in22), Pair.of(23, in23));

        return getTop3Time(inTimes);
    }

    public String getTop3LeaveTime() {
        List<Pair<Integer, Long>> outTimes = Arrays.asList(
                Pair.of(0, out0), Pair.of(1, out1), Pair.of(2, out2), Pair.of(3, out3),
                Pair.of(4, out4), Pair.of(5, out5), Pair.of(6, out6), Pair.of(7, out7),
                Pair.of(8, out8), Pair.of(9, out9), Pair.of(10, out10), Pair.of(11, out11),
                Pair.of(12, out12), Pair.of(13, out13), Pair.of(14, out14), Pair.of(15, out15),
                Pair.of(16, out16), Pair.of(17, out17), Pair.of(18, out18), Pair.of(19, out19),
                Pair.of(20, out20), Pair.of(21, out21), Pair.of(22, out22), Pair.of(23, out23));

        return getTop3Time(outTimes);
    }

    private String getTop3Time(List<Pair<Integer, Long>> times) {
        times.sort(new MyComparator());
        if (times.get(0).getRight() == 0)
            return "无";
        String first = String.format("%02d:00-%02d:59(%d)",
                times.get(0).getLeft(), times.get(0).getLeft(), times.get(0).getRight());
//        if (times.get(1).getRight() == 0)
//            return first;
        String second = String.format("%02d:00-%02d:59(%d)",
                times.get(1).getLeft(), times.get(1).getLeft(), times.get(1).getRight());
        String third = String.format("%02d:00-%02d:59(%d)",
                times.get(2).getLeft(), times.get(2).getLeft(), times.get(2).getRight());
//        if (times.get(2).getRight() == 0)
//            return first + "; " + second;
        return first + "; " + second + "; " + third;
    }

    private class MyComparator implements Comparator<Pair<Integer, Long>> {
        @Override
        public int compare(Pair<Integer, Long> o1, Pair<Integer, Long> o2) {
            if (o1.getRight() < o2.getRight())
                return 1;
            if (o1.getRight() > o2.getRight())
                return -1;
            if (o1.getLeft() > o2.getLeft())
                return 1;
            if (o1.getLeft() > o2.getLeft())
                return -1;
            return 0;
        }
    }
}
