package com.jiayi.platform.common.util;

import com.jiayi.platform.common.exception.ArgumentException;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TableUtil {

	private static final String TRACK_TABLE_PATTERN = "{objectType}_track_{year}";
	private static final String GROUP = "_org";

	private static final String[] TIME_PATTERN = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" };

	public static String getTrackTableName(String objectType, String time) throws ParseException {
		Date date = DateUtils.parseDate(time, TIME_PATTERN);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		return TRACK_TABLE_PATTERN.replace("{objectType}", objectType).replace("{year}", year + "");
	}

	public static String getTrackTableName(String objectType, long timeMillis) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		int year = calendar.get(Calendar.YEAR);
		return TRACK_TABLE_PATTERN.replace("{objectType}", objectType).replace("{year}", year + "");
	}

	public static String getGroupTrackTableName(String objectType, long timeMillis) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		int year = calendar.get(Calendar.YEAR);
		return TRACK_TABLE_PATTERN.replace("{objectType}", objectType).replace("{year}", year + "") + GROUP;
	}

	public static String getTrackGroupTableName(String objectType, String time) throws ParseException {
		return getTrackTableName(objectType, time) + GROUP;
	}

	public static List<String> getTrackTableNameRange(String objectType, long startTime, long endTime)
	    throws ParseException {
		List<Integer> yearList = getYearRangeList(startTime, endTime);
		return getTrackTableNameRange(objectType,yearList);
	}

	public static List<String> getTrackTableNameRange(String objectType, long startTime, long endTime,
	    int secondOffset) throws ParseException {
		List<Integer> yearList = getYearRangeList(startTime, endTime, secondOffset);
		return getTrackTableNameRange(objectType,yearList);
	}

	public static List<String> getTrackGroupTableNameRange(String objectType, String startTime, String endTime)
	    throws ParseException {
		List<Integer> yearList = getYearRangeList(startTime, endTime);
		return getTrackGroupTableNameRange(objectType,yearList);
	}

	public static List<String> getTrackGroupTableNameRange(String objectType, long startTime, long endTime)
	    throws ParseException {
		List<Integer> yearList = getYearRangeList(startTime, endTime);
		return getTrackGroupTableNameRange(objectType,yearList);
	}

	public static List<String> getTrackGroupTableNameRange(String objectType, long startTime, long endTime,
	    int secondOffset) throws ParseException {
		List<Integer> yearList = getYearRangeList(startTime, endTime, secondOffset);
		return getTrackGroupTableNameRange(objectType,yearList);
	}

	private static List<Integer> getYearRangeList(String startTime, String endTime) throws ParseException {
		Date startDate = DateUtils.parseDate(startTime, TIME_PATTERN);
		Date endDate = DateUtils.parseDate(endTime, TIME_PATTERN);
		return getYearRangeList(startDate,endDate);
	}

	private static List<Integer> getYearRangeList(long startTime, long endTime) throws ParseException {
		Date startDate = new Date(startTime);
		Date endDate = new Date(endTime);
		return getYearRangeList(startDate,endDate);
	}

	private static List<Integer> getYearRangeList(long startTime, long endTime, int secondOffset)
	    throws ParseException {
	    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String sTime = format.format(startTime);
	    String eTime = format.format(endTime);
		Date startDate = DateUtils.parseDate(sTime, TIME_PATTERN);
		Date endDate = DateUtils.parseDate(eTime, TIME_PATTERN);
		Date realStartDate = DateUtils.addSeconds(startDate, -secondOffset);
		Date realEndDate = DateUtils.addSeconds(endDate, secondOffset);
		return getYearRangeList(realStartDate,realEndDate);
	}

	/**
	 * 取得开始时间-结束时间之间年的范围
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return
	 */
	private static List<Integer> getYearRangeList(Date startDate,Date endDate){
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		endCalendar.setTime(endDate);
		if (startCalendar.after(endCalendar)) {
			throw new ArgumentException("end time can't be less than start time");
		}
		List<Integer> result = new ArrayList<>();
		for (int i = startCalendar.get(Calendar.YEAR); i <= endCalendar.get(Calendar.YEAR);  i++ ){
			result.add(i);
		}
		return result;
	}

	private static List<String> getTrackGroupTableNameRange(String objectType,List<Integer> yearList){
		List<String> result = new ArrayList<>();
		yearList.forEach(year -> {
			result.add(TRACK_TABLE_PATTERN.replace("{objectType}", objectType).replace("{year}", year + "") + GROUP);
		});
		return result;
	}

	private static List<String> getTrackTableNameRange(String objectType,List<Integer> yearList) {
		List<String> result = new ArrayList<>();
		yearList.forEach(year -> {
			result.add(TRACK_TABLE_PATTERN.replace("{objectType}", objectType).replace("{year}", year + ""));
		});
		return result;
	}
}
