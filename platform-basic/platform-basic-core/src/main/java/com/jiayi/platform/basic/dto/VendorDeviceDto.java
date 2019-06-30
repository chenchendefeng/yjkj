package com.jiayi.platform.basic.dto;

import com.google.common.collect.Lists;
import com.jiayi.platform.common.web.util.CsvContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "供应商设备统计结果")
public class VendorDeviceDto implements CsvContent {
    @ApiModelProperty(value = "供应商ID", example = "123")
    private Integer verderId;
    @ApiModelProperty(value = "供应商名称", example = "甲易科技")
    private String vender;
    @ApiModelProperty(value = "供应商数据列表", example = "供应商数据列表")
    private List<TypeDeviceDto> list = Lists.newArrayList();

    public VendorDeviceDto() {
        super();
    }

    public VendorDeviceDto(String vender, List<TypeDeviceDto> list) {
        super();
        this.vender = vender;
        this.list = list;
    }

    public void add(TypeDeviceDto typeDeviceDto) {
        list.add(typeDeviceDto);
    }

    public Integer getPlaceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getPlaceCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer getDeviceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getDeviceCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer getOnlineTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getOnlineCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer getOfflineTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getOfflineCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer getQualifiedDeviceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getQualifiedCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer getUnqualifiedDeviceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getUnqualifiedCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer getUndeterminedDeviceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getUndeterminedCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer get7dayUpDeviceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getSevenDaysCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public Integer get30NewDeviceTotal() {
        double total = list.stream().mapToDouble(TypeDeviceDto::getNewDevicesCount).sum();
        DecimalFormat df = new DecimalFormat("######0");
        return Integer.parseInt(df.format(total));
    }

    public String getOnlinePercent() {
        if (getDeviceTotal() > 0 && getOnlineTotal() > 0) {
            double percent = (getOnlineTotal() / (float) getDeviceTotal()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getOfflinePercent() {
        if (getDeviceTotal() > 0 && getOfflineTotal() > 0) {
            double percent = (getOfflineTotal() / (float) getDeviceTotal()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getQualifiedPercent() {
        if (getDeviceTotal() > 0 && getQualifiedDeviceTotal() > 0) {
            double percent = (getQualifiedDeviceTotal() / (float) getDeviceTotal()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getUnqualifiedPercent() {
        if (getDeviceTotal() > 0 && getUnqualifiedDeviceTotal() > 0) {
            double percent = (getUnqualifiedDeviceTotal() / (float) getDeviceTotal()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    public String getUndeterminedPercent() {
        if (getDeviceTotal() > 0 && getUndeterminedDeviceTotal() > 0) {
            double percent = (getUndeterminedDeviceTotal() / (float) getDeviceTotal()) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return "(" + df.format(percent) + "%)";
        } else {
            return "(0%)";
        }
    }

    @Override
    public String getContent() {
        return ",\t" + "总计,\t" + this.getPlaceTotal() + ",\t" + this.getDeviceTotal() + ",\t" + this.getOnlineTotal() + getOnlinePercent() + ",\t" +
                this.getOfflineTotal() + getOfflinePercent()
//                + ",\t" + this.getQualifiedDeviceTotal()+getQualifiedPercent() + ",\t" +
//                this.getUnqualifiedDeviceTotal()+getUnqualifiedPercent() + ",\t" +
//                this.getUndeterminedDeviceTotal()+getUndeterminedPercent()
                + ",\t" + this.get7dayUpDeviceTotal() + ",\t" + this.get30NewDeviceTotal();
    }
}
