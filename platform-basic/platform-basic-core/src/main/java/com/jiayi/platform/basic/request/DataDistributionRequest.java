package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DataDistributionRequest {
	@ApiModelProperty(value = "名称", example = "甲易测试设备")
	@NotBlank(message = "名称不能为空")
	private String name;
	@ApiModelProperty(value = "数据源ID", example = "1")
	@NotNull(message = "数据源不能为空")
	private Long srcId;
	@ApiModelProperty(value = "自动处理,1自动,0非自动", example = "1")
	private Integer isAutoMode;
	@ApiModelProperty(value = "处理器", example = "测试处理器")
	@NotBlank(message = "处理器不能为空")
	private String processor;
	@ApiModelProperty(value = "文件列表", example = "")
	private String fileList;
	@ApiModelProperty(value = "下载类型:ftp,local", example = "ftp")
	@NotBlank(message = "下载类型不能为空")
	private String downloaderType;
	@ApiModelProperty(value = "下载参数,json字符串(本地目录：\"{'src_dir':'123','do_bak':'1','bak_dir':'d:/work'}\",ftp:如example)", example = "\"{'host':'192.168.0.137','port':22,'user':'admin','password':'123456','ftp_dir':'d:/work','timeout':10,'do_bak':'0','bak_dir':'0'}\"")
	private String downloaderParam;//{'src_dir':'123','do_bak':'1','bak_dir':'d:/work'}
	@ApiModelProperty(value = "kafka参数,json字符串", example = "\"{'address':'123','group':'1','permission':'1'}\"")
	private String kafkaParam;
	@ApiModelProperty(value = "是否启用,0停用,1启用", example = "1")
	private Integer isActive;
}
