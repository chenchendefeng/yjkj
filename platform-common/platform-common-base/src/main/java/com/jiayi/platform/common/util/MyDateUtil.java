package com.jiayi.platform.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.http.impl.cookie.DateUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyDateUtil {
	public static Log log = LogFactory.getLog(MyDateUtil.class);
	public static final String day = "day";
	public static final String week = "week";
	public static final String month = "month";
	public static final String quarter = "quarter";
	public static final String year = "year";

	public static String yyyy_MM_dd = "yyyy-MM-dd";
	public static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static String HH_mm_ss = "HH:mm:ss";
	public static String yyyyMMdd = "yyyyMMdd";
	public static String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static String yyyyMMddHH = "yyyyMMddHH";
	public static String MM_dd_yyyy = "MM-dd-yyyy";
	public static String MM_dd_yyyy_HH_mm_ss = "MM-dd-yyyy HH:mm:ss";
	public static String[] IMPORT_DATE_PATTERNS = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-mm-dd HH:mm",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};

	private static SimpleDateFormat shortSdf = new SimpleDateFormat(yyyy_MM_dd);
	private static SimpleDateFormat longSdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
	private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private static DateFormat simpleSdf = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
	private static SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat csvSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static SimpleDateFormat daySdf = new SimpleDateFormat(yyyyMMdd);
	private static SimpleDateFormat hourSdf = new SimpleDateFormat(yyyyMMddHH);

	private static SimpleDateFormat isoDdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


	public static String getHourOfDay(long time){
		return hourSdf.format(new Date(time));
	}

	public static String getDay(long time){
		return daySdf.format(new Date(time));
	}

	public static long getTimestampFromISO(String isoDate){
		try {
			return isoDdf.parse(isoDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static long[] getDayTimestamp(Date day){
        try {
            long[] time = new long[2];
            Calendar c1 = new GregorianCalendar();
            c1.setTime(day);
            c1.set(Calendar.HOUR_OF_DAY, 0);
            c1.set(Calendar.MINUTE, 0);
            c1.set(Calendar.SECOND, 0);
            time[0]=c1.getTimeInMillis();

            c1.add(Calendar.DAY_OF_YEAR,1);
            time[1]=c1.getTimeInMillis();
            return  time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public static long getSpecifyDay(int i) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, i);
		return calendar.getTimeInMillis();
	}

	public static String getISODateStr(Long d) {
		Date date = new Date(d);
		return sdf.format(date);
	}

	public static String getDateStr(Long d) {
		Date date = new Date(d);
		return getDateStr(date);
	}

	public static String getDateStr(Date date) {
		return longSdf.format(date);
	}

	public static String getDateFromISO(String isoDate) {
		try {
			return longSdf.format(isoDdf.parse(isoDate));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static long getDateFromPhpTime(String dateStr) throws ParseException {
		simpleSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return simpleSdf.parse(dateStr).getTime();
	}

	public static long getTimestamp(String dateStr) throws ParseException {
		return longSdf.parse(dateStr).getTime();
	}

	public static String getYearTime(String dateStr) throws ParseException {
		return yearSdf.format(longSdf.parse(dateStr));
	}

	public static String getYearTime(Date d) throws ParseException {
		return yearSdf.format(d);
	}

	/**
	 * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
	 * 
	 * @param date
	 * @return
	 */
	public static String getISO8601Timestamp(Date date) {
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(date);
		return nowAsISO;
	}

	public static Date toDate(String format, Object obj) {
		return toDate(format, obj, getLocale(), getTimeZone());
	}

	public static Date toDate(String format, Object obj, Locale locale) {
		return toDate(format, obj, locale, getTimeZone());
	}

	public static Locale getLocale() {
		return Locale.getDefault();
	}

	public static TimeZone getTimeZone() {
		return TimeZone.getDefault();
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
		Date date = new Date();
		return sdf.format(date);
	}

	public static Date toDate(String format, Object obj, Locale locale, TimeZone timezone) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Date) {
			return (Date) obj;
		} else if (obj instanceof Calendar) {
			return ((Calendar) obj).getTime();
		} else if (obj instanceof Long) {
			Date e1 = new Date();
			e1.setTime(((Long) obj).longValue());
			return e1;
		} else if (obj instanceof String) {
			try {
				return (new SimpleDateFormat(format)).parse((String) obj);
			} catch (Exception var5) {
				return null;
			}
		} else {
			try {
				DateFormat e = getDateFormat(format, locale, timezone);
				return e.parse(String.valueOf(obj));
			} catch (Exception var6) {
				return null;
			}
		}
	}

	private static DateFormat getDateFormat(String format, Locale locale, TimeZone timezone) {
		if (format == null) {
			return null;
		} else {
			Object df = null;
			String style;
			int style1;
			if (format.endsWith("_date")) {
				style = format.substring(0, format.length() - 5);
				style1 = getStyleAsInt(style);
				df = getDateFormat(style1, -1, locale, timezone);
			} else if (format.endsWith("_time")) {
				style = format.substring(0, format.length() - 5);
				style1 = getStyleAsInt(style);
				df = getDateFormat(-1, style1, locale, timezone);
			} else {
				int style2 = getStyleAsInt(format);
				if (style2 < 0) {
					df = new SimpleDateFormat(format, locale);
					((DateFormat) df).setTimeZone(timezone);
				} else {
					df = getDateFormat(style2, style2, locale, timezone);
				}
			}

			return (DateFormat) df;
		}
	}

	private static DateFormat getDateFormat(int dateStyle, int timeStyle, Locale locale, TimeZone timezone) {
		try {
			DateFormat suppressed;
			if (dateStyle < 0 && timeStyle < 0) {
				suppressed = DateFormat.getInstance();
			} else if (timeStyle < 0) {
				suppressed = DateFormat.getDateInstance(dateStyle, locale);
			} else if (dateStyle < 0) {
				suppressed = DateFormat.getTimeInstance(timeStyle, locale);
			} else {
				suppressed = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
			}

			suppressed.setTimeZone(timezone);
			return suppressed;
		} catch (Exception var5) {
			return null;
		}
	}

	private static int getStyleAsInt(String style) {
		return style != null && style.length() >= 4 && style.length() <= 7
				? (style.equalsIgnoreCase("full")
						? 0
						: (style.equalsIgnoreCase("long")
								? 1
								: (style.equalsIgnoreCase("medium")
										? 2
										: (style.equalsIgnoreCase("short")
												? 3
												: (style.equalsIgnoreCase("default") ? 2 : -1)))))
				: -1;
	}

	public static long[] getBeginEndTimestamp(String input, String timeType) {
		long[] beginEndTime = new long[2];
		Calendar cal = Calendar.getInstance();
		switch (timeType) {
			case day :
				Date dateDay = toDate(yyyy_MM_dd, input);
				cal.setTime(dateDay);
				cal.add(Calendar.DAY_OF_YEAR, +1);
				beginEndTime[0] = dateDay.getTime();
				beginEndTime[1] = cal.getTime().getTime() - 1;
				return beginEndTime;
			case week :
				String[] yearAndWeek = input.split("-");
				int yearStr = Integer.parseInt(yearAndWeek[0]);
				int weekStr = Integer.parseInt(yearAndWeek[1]);

				beginEndTime[0] = getFirstDayOfWeek(yearStr, weekStr).getTime();
				beginEndTime[1] = getLastDayOfWeek(yearStr, weekStr).getTime();
				return beginEndTime;
			case month :
				Date dateMonth = toDate("yyyy-MM", input);
				cal.setTime(dateMonth);
				cal.add(Calendar.MONTH, +1);
				beginEndTime[0] = dateMonth.getTime();
				beginEndTime[1] = cal.getTime().getTime() - 1;
				return beginEndTime;
			case quarter :
				String[] yearAndQuarter = input.split("-");
				int yearNum = Integer.parseInt(yearAndQuarter[0]);
				int quarterNum = Integer.parseInt(yearAndQuarter[1]);

				Date dateQuarter = toDate("yyyy", yearNum);

				beginEndTime[0] = getCurrentQuarterStartTime(dateQuarter, quarterNum).getTime();
				beginEndTime[1] = getCurrentQuarterEndTime(dateQuarter, quarterNum).getTime();
				return beginEndTime;
			case year :
				Date dateYear = toDate("yyyy", input);
				cal.setTime(dateYear);
				cal.add(Calendar.YEAR, +1);
				beginEndTime[0] = dateYear.getTime();
				beginEndTime[1] = cal.getTime().getTime() - 1;
				return beginEndTime;
			default :
				return beginEndTime;
		}
	}

	// 获取某年的第几周的开始日期
	public static Date getFirstDayOfWeek(int year, int week) {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DATE, 1);

		Calendar cal = (GregorianCalendar) c.clone();
		cal.add(Calendar.DATE, week * 7);

		return getFirstDayOfWeek(cal.getTime());
	}

	// 获取当前时间所在周的开始日期
	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		try {
			return longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	// 获取某年的第几周的结束日期
	public static Date getLastDayOfWeek(int year, int week) {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DATE, 1);

		Calendar cal = (GregorianCalendar) c.clone();
		cal.add(Calendar.DATE, week * 7);

		return getLastDayOfWeek(cal.getTime());
	}

	// 获取当前时间所在周的结束日期
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
		try {
			return longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date getCurrentQuarterStartTime(Date date, int quarter) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		Date now = null;
		try {
			if (quarter == 1)
				c.set(Calendar.MONTH, 0);
			else if (quarter == 2)
				c.set(Calendar.MONTH, 3);
			else if (quarter == 3)
				c.set(Calendar.MONTH, 6);
			else if (quarter == 4)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 当前季度的结束时间，即2012-03-31 23:59:59
	 * 
	 * @return
	 */
	public static Date getCurrentQuarterEndTime(Date date, int quarter) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		Date now = null;
		try {
			if (quarter == 1) {
				c.set(Calendar.MONTH, 2);
				c.set(Calendar.DATE, 31);
			} else if (quarter == 2) {
				c.set(Calendar.MONTH, 5);
				c.set(Calendar.DATE, 30);
			} else if (quarter == 3) {
				c.set(Calendar.MONTH, 8);
				c.set(Calendar.DATE, 30);
			} else if (quarter == 4) {
				c.set(Calendar.MONTH, 11);
				c.set(Calendar.DATE, 31);
			}
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获取某天0:00的时间戳
	 * 
	 * @param amount
	 *            偏移几天 0当天 -1昨天 1明天
	 * @return
	 */
	public static long getDayZeroTime(int amount) {
		long now = System.currentTimeMillis();
		Date d1 = new Date(now);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String d1Str = sdf.format(d1);
		Date d1Zero = null;
		try {
			d1Zero = sdf.parse(d1Str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d1Zero);
		calendar.add(Calendar.DAY_OF_MONTH, amount);
		Date date = calendar.getTime();
		return date.getTime();
	}

	/**
	 * 获取时间戳时间是周几
	 *
	 * @param timestamp
	 * @return
	 */
	public static Integer getWeekday(Long timestamp) {
	    // 星期日, 星期一, 星期二, 星期三, 星期四, 星期五, 星期六
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timestamp));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
	}

	public static Date getDatebByCsvDate(String csvDate){
		try{
			return csvSdf.parse(csvDate);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

		log.debug("enter in!!!!");
		log.info("初始化Log4j。。。。");
		log.error("error in!!!");
		// log.info("path is " + log4jPath);
//		Date d = new Date(1513851150754l);
//		String str = DateUtil.formatDate(d, "yyyy-MM-dd hh:mm:ss.SSS");
//		System.out.println("--------->" + str);

		long time = MyDateUtil.getSpecifyDay(-140);
		System.out.println(time);

        long[] times = getDayTimestamp(new Date(1535817600000l));
        System.out.println("开始时间："+times[0]+"；结束时间："+times[1]);
	}

	public static long[] getFirstAndEndDayOfLastMonth(int n){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + n;
		return getBeginEndTimestamp(year+"-"+month, "month");
	}
}