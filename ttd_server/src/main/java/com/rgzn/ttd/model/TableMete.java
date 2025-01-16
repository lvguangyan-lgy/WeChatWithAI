package com.rgzn.ttd.model;

import javax.persistence.*;

@Table(name = "table_mete")
public class TableMete {
    @Id
    @Column(name = "ch_id")
    private String chId;

    /**
     * 数据库类型
     */
    @Column(name = "ch_table_type")
    private String chTableType;

    /**
     * 表名称
     */
    @Column(name = "ch_table_name")
    private String chTableName;

    /**
     * 表说明
     */
    @Column(name = "ch_table_comment")
    private String chTableComment;

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
     * 获取数据库类型
     *
     * @return ch_table_type - 数据库类型
     */
    public String getChTableType() {
        return chTableType;
    }

    /**
     * 设置数据库类型
     *
     * @param chTableType 数据库类型
     */
    public void setChTableType(String chTableType) {
        this.chTableType = chTableType;
    }

    /**
     * 获取表名称
     *
     * @return ch_table_name - 表名称
     */
    public String getChTableName() {
        return chTableName;
    }

    /**
     * 设置表名称
     *
     * @param chTableName 表名称
     */
    public void setChTableName(String chTableName) {
        this.chTableName = chTableName;
    }

    /**
     * 获取表说明
     *
     * @return ch_table_comment - 表说明
     */
    public String getChTableComment() {
        return chTableComment;
    }

    /**
     * 设置表说明
     *
     * @param chTableComment 表说明
     */
    public void setChTableComment(String chTableComment) {
        this.chTableComment = chTableComment;
    }
}