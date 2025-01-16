package com.rgzn.ttd.dao;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.utils.SqlBuilder;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface DynamicsMapper {

    @SelectProvider(type = SqlBuilder.class,method = "buildSql")
    List<LinkedHashMap<String,Object>> dynamicsSql(String sql);

    @UpdateProvider(type = SqlBuilder.class,method = "buildUpdateSql")
    int dynamicsUpdateSql(String sql);
}
