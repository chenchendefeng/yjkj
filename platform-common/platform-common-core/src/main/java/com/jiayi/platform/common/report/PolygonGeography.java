package com.jiayi.platform.common.report;


import java.util.List;

public class PolygonGeography extends BaseGeography{

    public PolygonGeography () {

    }

    public PolygonGeography (List<Point> points) {
        if (points == null || points.size() < 2){
            return;
        }
        setPoints(points);
    }


    @Override
    public boolean isWithin(double latitude, double longitude) {
        List<Point> pts = getPoints();
        int N = pts.size();

        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point p1, p2;//neighbour bound vertices
        Point p = new Point(); //当前点
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        p1 = pts.get(0);//left vertex
        for(int i = 1; i <= N; ++i){//check all rays
            if(p.equals(p1)){
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if(p.getLatitude() < Math.min(p1.getLatitude(), p2.getLatitude()) || p.getLatitude() > Math.max(p1.getLatitude(), p2.getLatitude())){//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if(p.getLatitude() > Math.min(p1.getLatitude(), p2.getLatitude()) && p.getLatitude() < Math.max(p1.getLatitude(), p2.getLatitude())){//ray is crossing over by the algorithm (common part of)
                if(p.getLongitude() <= Math.max(p1.getLongitude(), p2.getLongitude())){//x is before of ray
                    if(p1.getLatitude() == p2.getLatitude() && p.getLongitude() >= Math.min(p1.getLongitude(), p2.getLongitude())){//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if(p1.getLongitude() == p2.getLongitude()){//ray is vertical
                        if(p1.getLongitude() == p.getLongitude()){//overlies on a vertical ray
                            return boundOrVertex;
                        }else{//before ray
                            ++intersectCount;
                        }
                    }else{//cross point on the left side
                        double xinters = (p.getLatitude() - p1.getLatitude()) * (p2.getLongitude() - p1.getLongitude()) / (p2.getLatitude() - p1.getLatitude()) + p1.getLongitude();//cross point of y
                        if(Math.abs(p.getLongitude() - xinters) < precision){//overlies on a ray
                            return boundOrVertex;
                        }

                        if(p.getLongitude() < xinters){//before ray
                            ++intersectCount;
                        }
                    }
                }
            }else{//special case when ray is crossing through the vertex
                if(p.getLatitude() == p2.getLatitude() && p.getLongitude() <= p2.getLongitude()){//p crossing over p2
                    Point p3 = pts.get((i+1) % N); //next vertex
                    if(p.getLatitude() >= Math.min(p1.getLatitude(), p3.getLatitude()) && p.getLatitude() <= Math.max(p1.getLatitude(), p3.getLatitude())){//p.getLatitude() lies between p1.getLatitude() & p3.getLatitude()
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        if(intersectCount % 2 == 0){//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }

    /**
     * 传入的经纬度位置，是否在该地址位置区域
     * @param points 经纬度点
     * @return trun 在区域内 false不在
     */
    @Override
    public boolean isWithin(List<Point> points){
        if (points == null || points.size() < 1){
            return false;
        }
        for (Point point:points) {
            if (!isWithin(point.getLatitude(),point.getLongitude())){
                return false;
            }
        }
        return true;
    }
}
