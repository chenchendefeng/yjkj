package com.jiayi.platform.basic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "t_place_tag_relation")
@Getter
@Setter
@ToString
public class PlaceTagRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "tag_id", updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private PlaceTag tag;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "place_id", updatable = false)
    private Place place;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
