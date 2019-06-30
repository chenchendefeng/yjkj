package com.jiayi.platform.repo.minerepo.manager;

import com.jiayi.platform.common.util.MacUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RepoSearchManager {

    public List<String> trimQueryString(String queryString) {
        List<String> querys = new ArrayList<>();
        queryString = queryString.trim();
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(queryString);
        if (isNum.matches()) {
            //全为数字
            querys.add(queryString);
            return querys;
        }

        pattern = Pattern.compile("[0-9a-fA-F]*");
        Matcher isMAC = pattern.matcher(queryString);
        if (isMAC.matches() && queryString.length() <= 12 && queryString.length() > 6) {
            //全为数字跟字母
            querys.add(toMac(queryString, ":"));
            querys.add(toMac(queryString, "-"));
            querys.add(queryString);
            return querys;
        }

        //34:23:AF 格式的MAC
        String patternColonMac="^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){2,5}$";
        if(Pattern.compile(patternColonMac).matcher(queryString).find()){
            querys.add(queryString);
            String value = queryString.toUpperCase().replaceAll(":","-");
            querys.add(value);
            querys.add(MacUtil.toTrimMac(queryString));
            return querys;
        }

        //34-23-AF 格式的MAC
        String patternLineMac="^[0-9a-fA-F]{2}(-[0-9a-fA-F]{2}){2,5}$";
        if(Pattern.compile(patternLineMac).matcher(queryString).find()){
            querys.add(queryString);
            String value = queryString.toUpperCase().replaceAll("-",":");
            querys.add(value);
            querys.add(MacUtil.toTrimMac(queryString));
            return querys;
        }
        querys.add(queryString);
        return querys;
    }

    private String toMac(String value, String separator){
        String string = value.toUpperCase().replaceAll("(.{2})","$1" + separator);
        if(string.endsWith(separator)){
            string = string.substring(0,string.length()-1);
        }
        return string;
    }

}
