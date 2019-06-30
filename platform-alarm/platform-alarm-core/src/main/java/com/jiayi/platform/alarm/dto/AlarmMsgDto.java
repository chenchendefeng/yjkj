//package com.jiayi.platform.alarm.dto;
//
//import io.swagger.annotations.ApiModelProperty;
//
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;
//
//public class AlarmMsgDto {
//    @NotNull
//	@Min(value = 0, message = "工号需要大于等于0")
//	private Integer staffId;
//    @ApiModelProperty(value = "案件名称")
//	private String caseName;
//	@ApiModelProperty(value = "null:全部，0：未读，1：已读")
//	private Integer status;
//	private Integer pageNo=0;
//	private Integer pageSize=10;
//	@ApiModelProperty(value = "0物品布控，1人员布控(触碰，入圈，出圈)，2聚集，3消失")
//	private Integer msgType;
//
//	private String beginDate;
//	private String endDate;
//
//	@ApiModelProperty(value = "嫌疑人")
//	private String suspector;
//	// 嫌疑人监控值
//	@ApiModelProperty(value = "物品实体值")
//	private String objectValue;
//	@ApiModelProperty(value = "数据类型1:mac,2:carno,3:imsi,imei", example = "1")
//	private Integer objectType;
//
//	public String getCaseName() {
//		return caseName;
//	}
//	public void setCaseName(String caseName) {
//		this.caseName = caseName;
//	}
//	public Integer getStatus() {
//		return status;
//	}
//	public void setStatus(Integer status) {
//		this.status = status;
//	}
//	public String getBeginDate() {
//		return beginDate;
//	}
//	public void setBeginDate(String beginDate) {
//		this.beginDate = beginDate;
//	}
//	public String getEndDate() {
//		return endDate;
//	}
//	public void setEndDate(String endDate) {
//		this.endDate = endDate;
//	}
//	public String getSuspector() {
//		return suspector;
//	}
//	public void setSuspector(String suspector) {
//		this.suspector = suspector;
//	}
//	public String getObjectValue() {
//		return objectValue;
//	}
//	public void setObjectValue(String objectValue) {
//		this.objectValue = objectValue;
//	}
//	public Integer getPageNo() {
//		return pageNo;
//	}
//	public void setPageNo(Integer pageNo) {
//		this.pageNo = pageNo;
//	}
//	public Integer getPageSize() {
//		return pageSize;
//	}
//	public void setPageSize(Integer pageSize) {
//		this.pageSize = pageSize;
//	}
//	public Integer getStaffId() {
//		return staffId;
//	}
//	public void setStaffId(Integer staffId) {
//		this.staffId = staffId;
//	}
//	public Integer getMsgType() {
//		return msgType;
//	}
//	public void setMsgType(Integer msgType) {
//		this.msgType = msgType;
//	}
//	public Integer getObjectType() {
//		return objectType;
//	}
//	public void setObjectType(Integer objectType) {
//		this.objectType = objectType;
//	}
//}
