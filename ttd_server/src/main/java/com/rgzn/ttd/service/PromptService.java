package com.rgzn.ttd.service;

import com.rgzn.ttd.core.Service;
import com.rgzn.ttd.dto.PromptDto;
import com.rgzn.ttd.model.MkTdHisElec;


/**
 * Created by lgy on 2024/11/01.
 */
public interface PromptService{

    /**
     * 根据用户输入生成提示词
     * Prompt = instruction(指令) + context(上下文) + input_data(用户输入) + output_indicator(输出指引)
     * @param input
     * @return
     */
    PromptDto getPrompt(String input);
}
