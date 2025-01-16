package com.rgzn.ttd.configurer;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueryTempConfigurer {

    private final Map<String, JSONObject> queryTempMap = new HashMap<>();

    @PostConstruct
    public void loadExcelData() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/query_temp.xlsx")) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                //模板问题
                String key = row.getCell(0).getStringCellValue();
                JSONObject value = new JSONObject();

                //模板sql
                String sql = row.getCell(1).getStringCellValue();
                //类型 1:单条  2:列表
                Double type = row.getCell(2).getNumericCellValue();

                value.put("sql",sql);
                value.put("type",type);
                value.put("query",key);

                queryTempMap.put(key, value);
            }
        }
    }

    public JSONObject getValueByKey(String key) {
        return queryTempMap.get(key);
    }

    public Map<String, JSONObject> getQueryTempMap() {
        return queryTempMap;

    }


}
