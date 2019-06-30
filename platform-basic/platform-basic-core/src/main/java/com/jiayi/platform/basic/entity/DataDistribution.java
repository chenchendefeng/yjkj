package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_data_distribution")
public class DataDistribution {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "src_id")
	private Src src;
	@Column(name = "is_auto_mode")
	private Integer isAutoMode;
	private String processor;
	@Column(name = "file_list")
	private String fileList;
	@Column(name = "downloader_type")
	private String downloaderType;
	@Column(name = "downloader_param")
	private String downloaderParam;
	@Column(name = "kafka_param")
	private String kafkaParam;
	@Column(name = "is_active")
	private Integer isActive;
	@Column(name = "distribute_num")
	private Integer distributeNum;
	@Column(name = "undistribute_num")
	private Integer undistributeNum;
	@Column(name = "create_date")
	private Date createDate;
	@Column(name = "update_date")
	private Date updateDate;
}
