package com.jiayi.platform.basic.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class IMEIUtil {

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);// 创建输入流扫描器

        while (true){
            System.out.println("请输入IMEI：");
            String line = scanner.nextLine();
            String imei = checkDigit(line);
            System.out.println("\r\n计数之后的IMEI:" + imei);
            System.out.println("\r\n");
        }
    }


    /**
     * 计算IMEI最后一数checkDigit，并将最第15位放在最后
     * @param imei
     * @return
     */
    public static String checkDigit(String imei){
        if (StringUtils.isEmpty(imei) || imei.length() < 14){
            return imei;
        }
        String string =imei.substring(0,14);
        int sum = 0;
        int length = imei.length();
        for (int i = 0; i < 14 && i < length; i++) {
            try {
                int digit = Integer.parseInt(imei.substring(i,i+1));
                if (i % 2 == 1) {
                    digit *= 2;
                }
                sum += digit > 9 ? digit - 9 : digit;
            }catch (Exception e){
                return imei;
            }
        }
        int value = sum%10;
        if (value == 0 || value == 10){
            value = 0;
        }else {
            value = 10 - value;
        }
        return string+ Integer.toString(value);
    }

}
