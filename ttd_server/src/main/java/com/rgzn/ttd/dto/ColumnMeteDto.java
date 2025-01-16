package com.rgzn.ttd.dto;


import java.io.Serializable;

public class ColumnMeteDto implements Serializable {

    private String chFiled;

    /**
     * 字段类型
     */
    private String chFiledType;

    /**
     * 字段说明
     */
    private String chComment;

    public String getChFiled() {
        return chFiled;
    }

    public void setChFiled(String chFiled) {
        this.chFiled = chFiled;
    }

    public String getChFiledType() {
        return chFiledType;
    }

    public void setChFiledType(String chFiledType) {
        this.chFiledType = chFiledType;
    }

    public String getChComment() {
        return chComment;
    }

    public void setChComment(String chComment) {
        this.chComment = chComment;
    }

    @Override
    public String toString() {
        return "ColumnMeteDto{" +
                "chFiled='" + chFiled + '\'' +
                ", chFiledType='" + chFiledType + '\'' +
                ", chComment='" + chComment + '\'' +
                '}';
    }
}
