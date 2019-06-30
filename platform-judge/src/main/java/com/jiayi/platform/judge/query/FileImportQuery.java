package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FileImportQuery {
    protected Long uid;
    List<String> fieldNames;
    List<List<String>> contents;
}
