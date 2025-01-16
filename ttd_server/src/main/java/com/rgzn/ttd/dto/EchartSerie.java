package com.rgzn.ttd.dto;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.List;

@Data
public class EchartSerie implements Serializable {

    /**
     * 类型
     */
    private String type;
    /**
     * 名称
     */
    private String name;
    /**
     * 数据
     */
    private List<String> data;
    public static void main(String[] args) {
        System.out.println(NumberUtils.isCreatable("2024-12-19"));
    }
}
