package com.jiayi.platform.common.bo;

import lombok.*;

import java.util.Objects;

/**
 * @author : weichengke
 * @date : 2019-04-20 14:06
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private double latitude;
    private double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Location){
            Location point = (Location) o;
            return Double.compare(point.latitude, latitude) == 0 &&
                    Double.compare(point.longitude, longitude) == 0;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(latitude, longitude);
    }
}
