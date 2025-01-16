package com.rgzn.ttd.service;
import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.model.TableMete;
import com.rgzn.ttd.core.Service;

import java.util.List;


/**
 * Created by lgy on 2024/11/04.
 */
public interface TableMeteService extends Service<TableMete> {

    /**
     * 根据表名获取表的元信息
     * @param name
     * @return
     */
    Object findTableMeteByName(String name);

    /**
     * 获取所有表的基本信息(不含字段)
     * @return
     */
    Object findAllTableMete();


    Object findTableMeteByName(List<String> tableList);

    /**
     *
     * @param query
     * @return
     */
    Object findTableMeteByQuery(String query);

}
