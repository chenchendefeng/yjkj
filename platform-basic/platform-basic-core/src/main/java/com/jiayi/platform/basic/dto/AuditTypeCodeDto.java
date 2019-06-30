package com.jiayi.platform.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditTypeCodeDto {

    private Long code;
    private String name;
    private List<AuditTypeCodeDto> nextLevel;
}
