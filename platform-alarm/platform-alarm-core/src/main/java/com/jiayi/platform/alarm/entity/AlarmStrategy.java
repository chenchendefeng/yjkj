package com.jiayi.platform.alarm.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "bkyj_alarm_strategy")
@Getter
@Setter
@ToString
public class AlarmStrategy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "case_id")
	@NotNull(message = "请先选择案件")
	private Integer caseId;
	@NotBlank(message = "策略名称不能为空")
	private String name;
	@NotNull(message = "策略类型不能为空")
	private Integer type;
	@Column(name = "map_region")
	private String mapRegion;
	@Column(name = "ex_info")
	private String exInfo;
	private String remark;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "create_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	@Column(name = "be_active")
	private Integer beActive;//删除状态: 0正常、1停止
	private Integer status;//状态: 0禁用、1启用

}
