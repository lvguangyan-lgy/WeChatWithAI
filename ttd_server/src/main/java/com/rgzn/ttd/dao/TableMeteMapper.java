package com.rgzn.ttd.dao;

import com.rgzn.ttd.core.Mapper;
import com.rgzn.ttd.model.TableMete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableMeteMapper extends Mapper<TableMete> {

    List<TableMete> selectByTableNames(@Param("nameList") List<String>  nameList);
}