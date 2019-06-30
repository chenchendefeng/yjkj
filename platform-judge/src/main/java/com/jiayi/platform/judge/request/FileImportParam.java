package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileImportParam extends JudgeRequest {
    private List<String> title;
    private Integer successNum;
    private Integer errorNum;
    private String errorFilePath;
    private Long time;

    public FileImportParam(List<String> title, Integer successNum, Integer errorNum) {
        this.title = title;
        this.successNum = successNum;
        this.errorNum = errorNum;
    }
}
