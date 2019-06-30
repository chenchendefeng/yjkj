package com.jiayi.platform.common.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FactoryGeography {

    public static final String RECT_TYPE = "rect";
    public static final String LINE_TYPE = "line";
    public static final String POLYGON_TYPE = "polygon";
    /**
     * 通过类型取得 经纬度对像实例 RECT_TYPE  LINE_TYPE
     * @param type 见RECT_TYPE  LINE_TYPE
     * @return RECT_TYPE 类型返回 RectGeography，LINE_TYPE 返回LineGeography
     */
    public static BaseGeography getInstance(String type){
        if (RECT_TYPE.equals(type)){
            RectGeography geography = new RectGeography();
            geography.setType(RECT_TYPE);
            return geography;
        }else if(LINE_TYPE.equals(type)){
            LineGeography geography = new LineGeography();
            geography.setType(LINE_TYPE);
            return geography;
        }else if(POLYGON_TYPE.equals(type)){
            PolygonGeography geography = new PolygonGeography();
            geography.setType(POLYGON_TYPE);
            return geography;
        }else {
            return null;
        }
    }

    /**
     * 通过json信息得到 经纬度对像实例
     * 例如：{"points":[{"latitude":23.21846812,"longitude":112.61766343},{"latitude":23.21996212,"longitude":112.61766343},
     * {"latitude":23.21996212,"longitude":112.61816647},{"latitude":23.21846812,"longitude":112.61816647}],"type":"rect"}
     * 返回 RectGeography对像
     * @param json
     * @return
     */
    public static BaseGeography getInstanceByJson(String json){
        if(StringUtils.isEmpty(json)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(json);
        String type = jsonObject.getString("type");
        if (StringUtils.isEmpty(type)){
            return null;
        }

        BaseGeography baseGeography = getInstance(type);
        if(baseGeography == null){
            return null;
        }

        JSONArray array = jsonObject.getJSONArray("points");
        List<Point> points = new ArrayList<Point>();
        for (Object object : array) {
            Point point = JSONObject.parseObject(object.toString(), Point.class);
            points.add(point);
        }
        baseGeography.setPoints(points);
        return baseGeography;
    }

}
