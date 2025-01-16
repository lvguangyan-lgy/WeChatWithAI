package com.rgzn.ttd.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PromptDto implements Serializable {

    /**
     * 指令
     */
    private String instruction;
    /**
     * 上下文
     */
    private String context;
    /**
     * 用户输入
     */
    private String inputData;
    /**
     * 输出指引
     */
    private String outputIndicator;

}
