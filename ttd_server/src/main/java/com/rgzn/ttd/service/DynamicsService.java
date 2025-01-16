package com.rgzn.ttd.service;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface DynamicsService {
    List<LinkedHashMap<String,Object>> dynamicsSql(String sql);
    int dynamicsUpdateSql(String sql);
}
