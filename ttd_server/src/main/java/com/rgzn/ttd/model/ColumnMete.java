package com.rgzn.ttd.model;

import javax.persistence.*;

@Table(name = "column_mete")
public class ColumnMete {
    @Id
    @Column(name = "ch_id")
    private String chId;

    /**
     * 关联的表名称
     */
    @Column(name = "ch_table_id")
    private String chTableId;

    /**
     * 字段信息
     */
    @Column(name = "ch_filed")
    private String chFiled;

    /**
     * 字段类型
     */
    @Column(name = "ch_filed_type")
    private String chFiledType;

    /**
     * 字段说明
     */
    @Column(name = "ch_comment")
    private String chComment;

    /**
     * @return ch_id
     */
    public String getChId() {
        return chId;
    }

    /**
     * @param chId
     */
    public void setChId(String chId) {
        this.chId = chId;
    }

    /**
     * 获取关联的表名称
     *
     * @return ch_table_id - 关联的表名称
     */
    public String getChTableId() {
        return chTableId;
    }

    /**
     * 设置关联的表名称
     *
     * @param chTableId 关联的表名称
     */
    public void setChTableId(String chTableId) {
        this.chTableId = chTableId;
    }

    /**
     * 获取字段信息
     *
     * @return ch_filed - 字段信息
     */
    public String getChFiled() {
        return chFiled;
    }

    /**
     * 设置字段信息
     *
     * @param chFiled 字段信息
     */
    public void setChFiled(String chFiled) {
        this.chFiled = chFiled;
    }

    /**
     * 获取字段类型
     *
     * @return ch_filed_type - 字段类型
     */
    public String getChFiledType() {
        return chFiledType;
    }

    /**
     * 设置字段类型
     *
     * @param chFiledType 字段类型
     */
    public void setChFiledType(String chFiledType) {
        this.chFiledType = chFiledType;
    }

    /**
     * 获取字段说明
     *
     * @return ch_comment - 字段说明
     */
    public String getChComment() {
        return chComment;
    }

    /**
     * 设置字段说明
     *
     * @param chComment 字段说明
     */
    public void setChComment(String chComment) {
        this.chComment = chComment;
    }
}