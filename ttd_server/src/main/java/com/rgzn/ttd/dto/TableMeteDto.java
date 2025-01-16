package com.rgzn.ttd.dto;

import java.io.Serializable;
import java.util.List;

public class TableMeteDto implements Serializable {


    /**
     * 表名称
     */
    private String chTableName;

    /**
     * 表说明
     */
    private String chTableComment;

    private List<ColumnMeteDto> columnMetes;

    public String getChTableName() {
        return chTableName;
    }

    public void setChTableName(String chTableName) {
        this.chTableName = chTableName;
    }

    public String getChTableComment() {
        return chTableComment;
    }

    public void setChTableComment(String chTableComment) {
        this.chTableComment = chTableComment;
    }

    public List<ColumnMeteDto> getColumnMetes() {
        return columnMetes;
    }

    public void setColumnMetes(List<ColumnMeteDto> columnMetes) {
        this.columnMetes = columnMetes;
    }

    @Override
    public String toString() {
        return "TableMeteDto{" +
                "chTableName='" + chTableName + '\'' +
                ", chTableComment='" + chTableComment + '\'' +
                ", columnMetes=" + columnMetes +
                '}';
    }
}
