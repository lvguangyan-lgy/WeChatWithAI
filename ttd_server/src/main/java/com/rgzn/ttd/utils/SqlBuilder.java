package com.rgzn.ttd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SqlBuilder {

    private static final Logger log = LoggerFactory.getLogger(SqlBuilder.class);

    public static String buildSql(String sql){
        //返回查询类SQL语句
        if (isDdlSql(sql)){
            log.error("sql含有DDL操作,禁止执行");
            return "";
        }else if (sql.toUpperCase().startsWith("SELECT")){
            return sql;
        }else {
            log.error("sql不是查询类语句,禁止执行");
            return "";
        }
    }

    /**
     * 判断sql语句是否含有ddl操作
     * @param sql
     * @return
     */
    private static boolean isDdlSql(String sql) {
        String upperCaseSql = sql.toUpperCase();
        String[] ddlStatements = {"UPDATE","DELETE","INSERT","CREATE","ALTER","DROP","TRUNCATE"};

        for (String ddl : ddlStatements) {
            if (upperCaseSql.startsWith(ddl)) {
                return true;
            }
        }
        return false;
    }

    public static String buildUpdateSql(String sql){
        return sql;
    }

}
