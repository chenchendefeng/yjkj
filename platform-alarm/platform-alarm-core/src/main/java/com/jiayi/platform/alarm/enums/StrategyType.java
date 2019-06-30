//package com.jiayi.platform.alarm.enums;
//
//public enum StrategyType {
//	SIGNAL_ALARM(1, "SIGNAL_ALARM","物品布控"),
//	SUSPECT_TOUCH_ALARM(2, "SUSPECT_TOUCH_ALARM","人员触碰"),
//	SUSPECT_INSCOPE_ALARM(3,"SUSPECT_INSCOPE_ALARM","人员入圈"),
//	SUSPECT_OUTSCOPE_ALARM(4, "SUSPECT_OUTSCOPE_ALARM","人员出圈"),
//	SUSPECT_DISAPPEAR_ALARM(5,"SUSPECT_DISAPPEAR_ALARM","人员消失"),
//	SUSPECT_ASSEMBLE_ALARM(6,"SUSPECT_ASSEMBLE_ALARM","人员聚集");
////	AREA_ALARM(7, "AREA_ALARM", "地域监控");
//
//	private String name;
//	private int type;
//	private String describe;
//
//	StrategyType(int type, String name, String describe) {
//		this.type = type;
//		this.name = name;
//		this.describe = describe;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}
//
//	public String getDescribe() {
//		return describe;
//	}
//
//	public void setDescribe(String describe) {
//		this.describe = describe;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public static StrategyType getEnumByType(int type) {
//		for(StrategyType strategyType : StrategyType.values()) {
//			if(strategyType.type == type) {
//				return strategyType;
//			}
//		}
//		return null;
//	}
//
//	public String getCacheKey(Long suspenctId) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(name).append(":").append(suspenctId);
//		return sb.toString();
//	}
//}
