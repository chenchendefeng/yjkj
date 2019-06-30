package com.jiayi.platform.judge.query;

import com.jiayi.platform.common.bo.Location;
import com.jiayi.platform.judge.enums.AreaTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class LineQuery extends BaseAreaQuery {
	public static final double DISTANCE = 200.0;
	private List<Location> points;
	
	public LineQuery() {
		type = AreaTypeEnum.LINE.name();
	}
	
	public LineQuery(List<Location> points) {
		type = AreaTypeEnum.LINE.name();
		this.points = points;
	}
}