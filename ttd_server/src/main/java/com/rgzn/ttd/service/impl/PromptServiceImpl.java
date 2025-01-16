package com.rgzn.ttd.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.dto.PromptDto;
import com.rgzn.ttd.service.PromptService;
import com.rgzn.ttd.service.TableMeteService;
import com.rgzn.ttd.utils.DateUtil;
import com.rgzn.ttd.utils.LargeModelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rgzn.ttd.enums.PromptEnum.*;


/**
 * Created by lgy on 2024/11/01.
 */
@Service
public class PromptServiceImpl  implements PromptService {

    @Autowired
    private LargeModelClient largeModelClient;

    @Autowired
    private TableMeteService tableMeteService;

    @Override
    public PromptDto getPrompt(String input) {
        //Prompt = instruction(指令) + context(上下文) + input_data(用户输入) + output_indicator(输出指引)

        //1.获取instruction(指令), 暂时设置为固定值


        //2.获取context(上下文)

        //2.1把时间日期转换成标准格式的时间
        String transferInput = text2timeTransfer(input);

        //2.1.根据用户输入input获取关联的业务表, 获取不到则匹配所有的业务表
        //获取所有表的基本信息,通过大模型去匹配业务表
        Object tableContext = tableMeteService.findAllTableMete();

        String tablePrompt = TABLE_PROMPT.getInstruction()+"\n"+"业务表:"+JSONObject.toJSONString(tableContext)+"\n"+"用户的问题:"+input+"\n"+TABLE_PROMPT.getOutputIndicator();
        String tableStr = largeModelClient.requestLargeMode(tablePrompt);
        //获取关联的业务表元信息
        String[] tableArr = tableStr.split("\\|");
        if (tableArr.length == 0){
            throw new RuntimeException("没有相关的业务数据");
        }
        List<String> tableList = new ArrayList<>(Arrays.asList(tableArr));
        Object context = tableMeteService.findTableMeteByName(tableList);

        //3.input_data(用户输入) = input

        //4.output_indicator(输出指引),暂时设置为固定值

        //5.组成提示词prompt
        PromptDto promptDto = new PromptDto();
        promptDto.setInstruction(SQL_PROMPT.getInstruction());
        promptDto.setContext(JSONObject.toJSONString(context));
        promptDto.setInputData(transferInput);
        promptDto.setOutputIndicator(SQL_PROMPT.getOutputIndicator());

        return promptDto;
    }

    /**
     * 把时间日期转换成标准格式的时间 yyyy-mm-dd
     * @param input
     * @return
     */
    private String text2timeTransfer(String input){
        String prompt = DATE_PROMPT.getInstruction()+"\n"+"当前时间:"+ DateUtil.now()+"\n"+"用户的问题:"+input+"\n"+DATE_PROMPT.getOutputIndicator();
        //调用大模型获取转换日期后的问题
        return largeModelClient.requestLargeMode(prompt);
    }



}
