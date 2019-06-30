package com.jiayi.platform.basic.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMSIUtil {



    /**
     * 取得中国移动IMSI对应的手机号码段
     * @param imsi
     * @return 手机号码段
     */
    private static String getMobileCMCC(String imsi){
        String segment = "";
        String area = "";
        if (imsi.startsWith("46002")){
            int areaBeginIndex = 6;
            int araEndIndex = 10;
            char imsi6 = imsi.charAt(5);
            switch (imsi6)
            {
                case '0':
                    segment = "134";
                    break;
                case '1':
                    segment = "151";
                    break;
                case '2':
                    segment = "152";
                    break;
                case '3':
                    segment = "150";
                    break;
                case '4':
                    segment = "184";
                    break;
                case '5':
                    segment = "183";
                    break;
                case '6':
                    segment = "182";
                    break;
                case '7':
                    segment = "187";
                    break;
                case '8':
                    segment = "158";
                    break;
                case '9':
                    segment = "159";
                    break;
            }

            area = imsi.substring(areaBeginIndex,araEndIndex );
        }else if(imsi.startsWith("46000")){
            String s13x0 = "^46000(\\d{3})([5,6,7,8,9])\\d+";
            String s13x = "^46000(\\d{3})([0,1,2,3,4])(\\d)\\d+";
            String[] result = compile(s13x0, imsi);
            if ((result != null) && (result.length == 2)) {
                return "13" + result[1] + "0" + result[0];
            }
            result = compile(s13x, imsi);
            if ((result != null) && (result.length == 3)) {
                return "13" + (Integer.parseInt(result[1]) + 5) + result[2] + result[0];
            }
        }else if(imsi.startsWith("46007")){
            int areaBeginIndex = 6;
            int araEndIndex = 10;
            char imsi6 = imsi.charAt(5);
            switch (imsi6)
            {
                case '0':
                    segment = "170";
                    break;
                case '5':
                    segment = "178";
                    break;
                case '7':
                    segment = "157";
                    break;
                case '8':
                    segment = "188";
                    break;
                case '9':
                    segment = "147";
                    break;
            }
            area = imsi.substring(areaBeginIndex,araEndIndex);
        }

        if (StringUtils.isEmpty(segment) || StringUtils.isEmpty(area)) {
            return "";
        }else
        {
            return segment+area;
        }

    }

    /**
     * 取得中国联通IMSI对应的手机号码段
     * @param imsi
     * @return 手机号码段
     */
    private static String getMobileUNICOM(String imsi){
        String segment = "";
        String area = "";
        if (imsi.startsWith("46001")){
            char imsi6 = imsi.charAt(9);
            switch (imsi6)
            {
                case '0':
                case '1':
                    segment = "130";
                    break;
                case '2':
                    segment = "132";
                    break;
                case '3':
                    segment = "156";
                    break;
                case '4':
                    segment = "155";
                    break;
                case '5':
                    segment = "185";
                    break;
                case '6':
                    segment = "186";
                    break;
                case '7':
                    segment = "145";
                    break;
                case '8':
                    segment = "170";
                    break;
                case '9':
                    segment = "131";
                    break;
            }
            String area0 = imsi.substring(8,9);
            String area1 = imsi.substring(5,8);
            area = area0+area1;
        }

        if (StringUtils.isEmpty(segment) || StringUtils.isEmpty(area)) {
            return "";
        }else
        {
            return segment+area;
        }
    }

    public static String getMobileNumber(String imsi) {
        if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46004") || imsi.startsWith("46007")) {
            //"中国移动"
            return getMobileCMCC(imsi);
        } else if (imsi.startsWith("46001") || imsi.startsWith("46006") || imsi.startsWith("46009")) {
            // 中国联通
            return getMobileUNICOM(imsi);
        } else if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011") || imsi.startsWith("46020")) {
            // 中国电信
            return getMobileCT(imsi);
        }else {
            return "";
        }
    }

    /**
     * 取得手机号码段
     * @param imsi
     * @return
     */
    private static String getMobileCT(String imsi){
        String segment = "";
        String area = "";
        if (imsi.startsWith("46003")){

            char imsi10 = imsi.charAt(9);
            switch (imsi10)
            {
                case '3':
                    segment = "133";
                    break;
                case '7':
                    segment = "180";
                    break;
                case '8':
                    segment = "153";
                    break;
                case '9':
                    segment = "189";
                    break;
            }
            String area0 = imsi.substring(8,9);
            String area1 = imsi.substring(5,8);
            area = area0+area1;
        }

        if (StringUtils.isEmpty(segment) || StringUtils.isEmpty(area)) {
            return "";
        }else
        {
            return segment+area;
        }
    }

    private static String[] compile(String reg, String imsi) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(imsi);


        if (matcher.find()) {
            String[] sArr = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                sArr[i] = matcher.group(i + 1);
            }
            return sArr;
        }
        return null;
    }


    /**
     * 获取imsi厂商
     *
     * @Description:
     * @date:Dec 15, 2017 10:39:21 AM
     * @param imsi
     * @return
     * @return String 返回类型
     * @throws
     */
    public static String getMobileOperator(String imsi) {
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46004") || imsi.startsWith("46007")) {
                return "中国移动";
            } else if (imsi.startsWith("46001") || imsi.startsWith("46006") || imsi.startsWith("46009")) {
                return "中国联通";
            } else if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011")) {
                return "中国电信";
            } else if (imsi.startsWith("46020")) {
                return "中国铁通";
            }
        }
        return "";
    }
}
