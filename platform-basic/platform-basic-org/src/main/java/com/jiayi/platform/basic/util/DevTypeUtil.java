package com.jiayi.platform.basic.util;

import java.util.HashMap;

// TODO 这个地方是不是从数据库里获取更合适？ @zhenhao
public class DevTypeUtil {

	public final static String IMSI_TYPE = "imsi";

	public final static String MAC_TYPE = "mac";

	public final static String IMEI_TYPE = "imei";

	public final static String CARNO_TYPE = "carno";


	static HashMap<Integer, String> devTypeMap = new HashMap<Integer, String>();
	static {
		devTypeMap.put(1, "其它设备");
		devTypeMap.put(2, "审计设备");
		devTypeMap.put(3, "WIFI围栏");
		devTypeMap.put(4, "电子警察");
		devTypeMap.put(5, "电子围栏");
	}

	static HashMap<Integer, String> srcTypeMap = new HashMap<Integer, String>();
	static {
		srcTypeMap.put(1, "社会化数据");
		srcTypeMap.put(2, "甲易审计设备");
		srcTypeMap.put(3, "甲易WIFI围栏");
		srcTypeMap.put(4, "甲易WIFI围栏(Q6)");
		srcTypeMap.put(5, "甲易WIFI围栏(Q7)");
		srcTypeMap.put(6, "甲易审计设备(A1)");
		srcTypeMap.put(1001, "WIFI围栏");
		srcTypeMap.put(1002, "电子警察");
		srcTypeMap.put(1003, "电子围栏");
		srcTypeMap.put(1004, "手机IMSI对应数据");
	}

	static HashMap<Integer, String> objTypeMap = new HashMap<Integer, String>();
	static {
		objTypeMap.put(1, "MAC");
		objTypeMap.put(2, "车牌");
		objTypeMap.put(3, "IMEI");
		objTypeMap.put(4, "IMSI");
	}

	public static String getDevTypeName(int type) {
		return devTypeMap.get(type);
	}

	public static String getSrcName(int type) {
		return srcTypeMap.get(type);
	}

	public static String getObjTypeName(int type) {
		return objTypeMap.get(type);
	}
	
	public static Integer getObjTypeByCode(String code) {
	    switch(code.toLowerCase()) {
	    case "mac": return 1;
	    case "carno": return 2;
	    case "imei": return 3;
	    case "imsi": return 4;
	    default: return -1;
	    }
	}
}
