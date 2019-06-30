package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:20
 */
@Getter
@Setter
@ToString
public class PageBaseQuery {
    protected Integer limit;
    protected Long offset;
    // add type attribute for aggregate query
//    protected String type;
    // add uid attribute for saving collision result in kudu
    protected Long uid;
}
