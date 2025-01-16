package com.rgzn.ttd.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.dao.DynamicsMapper;
import com.rgzn.ttd.service.DynamicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lgy on 2024/11/04.
 */
@Service
@Transactional
public class DynamicsServiceImpl implements DynamicsService {

    @Autowired
    private DynamicsMapper dynamicsMapper;

    @Override
    public List<LinkedHashMap<String,Object>> dynamicsSql(String sql) {
        return dynamicsMapper.dynamicsSql(sql);
    }

    @Override
    public int dynamicsUpdateSql(String sql) {
        return dynamicsMapper.dynamicsUpdateSql(sql);
    }
}
