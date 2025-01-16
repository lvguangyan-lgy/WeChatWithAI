package com.rgzn.ttd.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by lgy on 2024/11/01.
 */
public interface SqlTemplateService {

    /**
     * 通过大模型接口进行语言识别,匹配数据模板
     * @param query
     * @return
     */
    JSONObject getTemplateByLargeModel(String query);

    /**
     * 精准配置数据模板(不经过大模型语言识别)
     * @param query
     * @return
     */
    JSONObject getTemplate(String query);
}
