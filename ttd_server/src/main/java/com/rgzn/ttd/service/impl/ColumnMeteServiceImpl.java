package com.rgzn.ttd.service.impl;

import com.rgzn.ttd.dao.ColumnMeteMapper;
import com.rgzn.ttd.dto.ColumnMeteDto;
import com.rgzn.ttd.model.ColumnMete;
import com.rgzn.ttd.service.ColumnMeteService;
import com.rgzn.ttd.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lgy on 2024/11/04.
 */
@Service
@Transactional
public class ColumnMeteServiceImpl extends AbstractService<ColumnMete> implements ColumnMeteService {
    @Resource
    private ColumnMeteMapper columnMeteMapper;

    @Override
    public List<ColumnMeteDto> findByTableId(String tableId) {
        ArrayList<ColumnMeteDto> result = new ArrayList<>();

        ColumnMete query = new ColumnMete();
        query.setChTableId(tableId);
        List<ColumnMete> columnMeteList = columnMeteMapper.select(query);

        if (columnMeteList != null && columnMeteList.size()>0){
            for (ColumnMete columnMete : columnMeteList) {
                ColumnMeteDto columnMeteDto = new ColumnMeteDto();
                columnMeteDto.setChFiled(columnMete.getChFiled());
                columnMeteDto.setChFiledType(columnMete.getChFiledType());
                columnMeteDto.setChComment(columnMete.getChComment());
                result.add(columnMeteDto);
            }
        }

        return result;
    }
}
