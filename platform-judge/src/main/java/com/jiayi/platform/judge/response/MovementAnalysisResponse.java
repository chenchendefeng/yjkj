package com.jiayi.platform.judge.response;

import com.jiayi.platform.judge.dto.MovementAnalysisDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MovementAnalysisResponse {
    private Integer id;
    private Long gridCode;
    private String address = "";
    private Integer deviceCount;
    private Long avgStayTime;
    private Long beginDate;
    private Long endDate;
    private Double latitude;
    private Double longitude;
    private List<Long> enterCount;
    private List<Long> leaveCount;
    private List<Long> stayTime;

    public void setEnterAndLeaveCount(MovementAnalysisDto dto) {
        List<Long> enter = new ArrayList<>();
        List<Long> leave = new ArrayList<>();
        enter.add(dto.getIn0());
        enter.add(dto.getIn1());
        enter.add(dto.getIn2());
        enter.add(dto.getIn3());
        enter.add(dto.getIn4());
        enter.add(dto.getIn5());
        enter.add(dto.getIn6());
        enter.add(dto.getIn7());
        enter.add(dto.getIn8());
        enter.add(dto.getIn9());
        enter.add(dto.getIn10());
        enter.add(dto.getIn11());
        enter.add(dto.getIn12());
        enter.add(dto.getIn13());
        enter.add(dto.getIn14());
        enter.add(dto.getIn15());
        enter.add(dto.getIn16());
        enter.add(dto.getIn17());
        enter.add(dto.getIn18());
        enter.add(dto.getIn19());
        enter.add(dto.getIn20());
        enter.add(dto.getIn21());
        enter.add(dto.getIn22());
        enter.add(dto.getIn23());
        leave.add(dto.getOut0());
        leave.add(dto.getOut1());
        leave.add(dto.getOut2());
        leave.add(dto.getOut3());
        leave.add(dto.getOut4());
        leave.add(dto.getOut5());
        leave.add(dto.getOut6());
        leave.add(dto.getOut7());
        leave.add(dto.getOut8());
        leave.add(dto.getOut9());
        leave.add(dto.getOut10());
        leave.add(dto.getOut11());
        leave.add(dto.getOut12());
        leave.add(dto.getOut13());
        leave.add(dto.getOut14());
        leave.add(dto.getOut15());
        leave.add(dto.getOut16());
        leave.add(dto.getOut17());
        leave.add(dto.getOut18());
        leave.add(dto.getOut19());
        leave.add(dto.getOut20());
        leave.add(dto.getOut21());
        leave.add(dto.getOut22());
        leave.add(dto.getOut23());
        this.enterCount = enter;
        this.leaveCount = leave;
    }

    public void setStayTime(MovementAnalysisDto dto) {
        List<Long> stay = new ArrayList<>();
        stay.add(dto.getStay0());
        stay.add(dto.getStay6());
        stay.add(dto.getStay12());
        stay.add(dto.getStay18());
        this.stayTime = stay;
    }
}
