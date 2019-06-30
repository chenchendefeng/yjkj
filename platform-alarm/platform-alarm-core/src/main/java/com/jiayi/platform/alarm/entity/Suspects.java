package com.jiayi.platform.alarm.entity;

import com.jiayi.platform.security.core.entity.UserBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bkyj_suspects")
@Getter
@Setter
public class Suspects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Integer caseId;
    private String name;
    private String mobile;
    private String identitycard;
    private String address;
    private String remark;
    @Column(name = "user_id")
    private long userId;
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserBean user;
    private Integer beActive;//fixme 软删除状态，现在没用上，是否去掉
    private Date createAt;
    private Date updateAt;
    private int status;//布控状态0未布控 1布控

    @OrderBy("id ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "suspects", fetch = FetchType.EAGER)//cascade申明级联保存和删除
    private List<DeviceInfo> deviceInfoData = new ArrayList<>();

}
