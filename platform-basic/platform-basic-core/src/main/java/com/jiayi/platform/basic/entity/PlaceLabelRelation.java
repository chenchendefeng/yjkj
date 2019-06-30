package com.jiayi.platform.basic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "t_place_label_relation")
@Getter
@Setter
@ToString
public class PlaceLabelRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
    @ManyToOne
    @JoinColumn(name = "label_code", referencedColumnName = "code")
    private PlaceLabel placeLabel;
}
