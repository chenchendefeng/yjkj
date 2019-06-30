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
@Table(name = "t_data_access")
public class DataAccess {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "src_id")
	private Src src;
	private Integer priority;
	@Column(name = "geo_type")
	private String geoType;
	@Column(name = "expire_days")
	private Integer expireDays;
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
	@Column(name = "treated_file_num")
	private Integer treatedFileNum;
	@Column(name = "untreated_file_num")
	private Integer untreatedFileNum;
	@Column(name = "create_date")
	private Date createDate;
	@Column(name = "update_date")
	private Date updateDate;
}
