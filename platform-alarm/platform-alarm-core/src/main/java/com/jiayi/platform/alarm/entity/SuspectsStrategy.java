package com.jiayi.platform.alarm.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bkyj_suspects_strategy")
@Getter
@Setter
@ToString
public class SuspectsStrategy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "suspect_id")
	private long suspectId;
	@Column(name = "alarm_strategy_id")
	private long alarmStrategyId;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_at")
	private Date createAt;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_at")
	private Date updateAt;

}
