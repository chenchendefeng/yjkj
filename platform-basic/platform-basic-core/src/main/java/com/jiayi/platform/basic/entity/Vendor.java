package com.jiayi.platform.basic.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "t_vendor")
@Getter
@Setter
@ToString
public class Vendor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
    @Column(name = "short_name")
    private String shortName;
    private String address;
    private String contact;
    private String phone;
    private String email;
    @Column(name = "ex_info")
    private String exInfo;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;

    @JsonSerialize(using = Date2StrSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = Date2StrSerializer.class)
    public Date getUpdateDate() {
        return updateDate;
    }
}

class Date2StrSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }
}
