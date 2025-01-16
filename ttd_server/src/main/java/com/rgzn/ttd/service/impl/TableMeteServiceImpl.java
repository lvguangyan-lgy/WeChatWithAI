package com.rgzn.ttd.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.dao.TableMeteMapper;
import com.rgzn.ttd.dto.ColumnMeteDto;
import com.rgzn.ttd.dto.TableMeteDto;
import com.rgzn.ttd.model.ColumnMete;
import com.rgzn.ttd.model.TableMete;
import com.rgzn.ttd.service.ColumnMeteService;
import com.rgzn.ttd.service.TableMeteService;
import com.rgzn.ttd.core.AbstractService;
import com.rgzn.ttd.utils.LargeModelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.rgzn.ttd.enums.PromptEnum.TABLE_PROMPT;


/**
 * Created by lgy on 2024/11/04.
 */
@Service
@Transactional
public class TableMeteServiceImpl extends AbstractService<TableMete> implements TableMeteService {
    @Autowired
    private TableMeteMapper tableMeteMapper;
    @Autowired
    private ColumnMeteService columnMeteService;
    @Autowired
    private LargeModelClient largeModelClient;

    @Override
    public Object findTableMeteByName(String name) {
        JSONArray result = new JSONArray();

        TableMete query = new TableMete();
        query.setChTableName(name);
        //获取表信息
        List<TableMete> tableMetes = tableMeteMapper.select(query);


        if (tableMetes != null && tableMetes.size() > 0){
            for (TableMete tableMete : tableMetes) {
                //获取表字段信息
                List<ColumnMeteDto> columnMete = columnMeteService.findByTableId(tableMete.getChId());

                //封装返回
                TableMeteDto tableMeteDto = new TableMeteDto();
                tableMeteDto.setChTableName(tableMete.getChTableName());
                tableMeteDto.setChTableComment(tableMete.getChTableComment());
                tableMeteDto.setColumnMetes(columnMete);

                result.add(tableMeteDto);
            }
        }

        return result;
    }

    @Override
    public Object findAllTableMete() {
        JSONArray result = new JSONArray();

        //获取表信息
        List<TableMete> tableMetes = tableMeteMapper.selectAll();

        if (tableMetes != null && tableMetes.size() > 0){
            for (TableMete tableMete : tableMetes) {
                //封装返回
                TableMeteDto tableMeteDto = new TableMeteDto();
                tableMeteDto.setChTableName(tableMete.getChTableName());
                tableMeteDto.setChTableComment(tableMete.getChTableComment());
                result.add(tableMeteDto);
            }
        }

        return result;
    }

    @Override
    public Object findTableMeteByName(List<String> tableList) {
        JSONArray result = new JSONArray();

        //获取表信息
        List<TableMete> tableMetes = tableMeteMapper.selectByTableNames(tableList);


        if (tableMetes != null && tableMetes.size() > 0){
            for (TableMete tableMete : tableMetes) {
                //获取表字段信息
                List<ColumnMeteDto> columnMete = columnMeteService.findByTableId(tableMete.getChId());

                //封装返回
                TableMeteDto tableMeteDto = new TableMeteDto();
                tableMeteDto.setChTableName(tableMete.getChTableName());
                tableMeteDto.setChTableComment(tableMete.getChTableComment());
                tableMeteDto.setColumnMetes(columnMete);

                result.add(tableMeteDto);
            }
        }

        return result;

    }

    @Override
    public Object findTableMeteByQuery(String query) {
        //获取所有表的基本信息,通过大模型去匹配业务表
        Object tableContext = findAllTableMete();
        String tablePrompt = TABLE_PROMPT.getInstruction()+"\n"+"业务表:"+JSONObject.toJSONString(tableContext)+"\n"+"用户的问题:"+query+"\n"+TABLE_PROMPT.getOutputIndicator();
        String tableStr = largeModelClient.requestLargeMode(tablePrompt);
        //获取关联的业务表元信息
        String[] tableArr = tableStr.split("\\|");
        //添加基础信息表
        List<String> tableList = new ArrayList<>(Arrays.asList(tableArr));
        Object context = findTableMeteByName(tableList);
        return context;
    }
}
