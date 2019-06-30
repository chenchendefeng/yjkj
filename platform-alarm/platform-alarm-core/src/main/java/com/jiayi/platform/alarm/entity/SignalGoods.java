package com.jiayi.platform.alarm.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bkyj_goods_management")
@Getter
@Setter
@ToString
public class SignalGoods {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private Integer caseId;
	private String name;
	private Integer objType;
	private String objValue;
	private String userName;
	private String mobile;
	private String identitycard;
	private String address;
	private String remark;
	private Integer beActive;
	private Date createAt;
	private Date updateAt;
	private Integer status;

}
