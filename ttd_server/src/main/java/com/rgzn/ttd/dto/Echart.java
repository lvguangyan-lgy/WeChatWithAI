package com.rgzn.ttd.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Echart implements Serializable {

    /**
     * 标题名称
     */
    private String title;

    /**
     * legend
     */
    private List<String> legendData;
    /**
     * X轴名称
     */
    private List<String> xAxisData;

    /**
     * 数据
     */
    private List<EchartSerie> series;

}
