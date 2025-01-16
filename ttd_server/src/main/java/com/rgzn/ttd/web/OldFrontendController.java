package com.rgzn.ttd.web;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.dto.Echart;
import com.rgzn.ttd.dto.EchartSerie;
import com.rgzn.ttd.service.DynamicsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
* Created by CodeGenerator on 2024/05/22.
 * 前端相关控制器
*/
@RestController
public class OldFrontendController {

    private static final Logger log = LoggerFactory.getLogger(OldFrontendController.class);

    @Autowired
    private DynamicsService dynamicsService;



    /**
     * 聊天窗口(临时)
     * @param context
     * @return
     */
    @PostMapping("/chat/test/1")
    public ResponseEntity<StreamingResponseBody> chat(@RequestBody JSONObject context){
        System.out.println("接收到问题context:"+context);

        StreamingResponseBody responseBody = outputStream -> {
            sendEvents(outputStream,context);
        };
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(responseBody);
    }

    private void sendEvents(OutputStream outputStream,JSONObject context) {
        String question = context.getString("query");
        JSONObject data = new JSONObject();
        data.put("question",question);
        Echart echart = new Echart();
        try {
            //请求text to sql 接口
            String content = "";
            try {

                String echartsType = "bar";
                String sql = "SELECT t.participant_type_name AS 市场主体类型,COUNT(t.participant_type_name) AS 数量 FROM td_marketparticipant_view t WHERE t.trading_center GROUP BY t.participant_type_name";
                if (question.contains("1")){
                    echartsType = "line";
                    sql = "SELECT * FROM energy_view WHERE data_type='新能源总出力' AND prediction_type IN('预测','实际') AND z_name = '海南' AND a_data = '2024-12-15'";
                }
                List<LinkedHashMap<String, Object>> daoDataByQuery = dynamicsService.dynamicsSql(sql);
                //key, x坐标
                ArrayList<String> xAxisData = new ArrayList<>();
                ArrayList<String> legendData = new ArrayList<>();
                ArrayList<EchartSerie> echartSeries = new ArrayList<>();
                if (echartsType.equals("bar")){
                    content = "根据业务数据，海南地区各市场主体类型数量分别为：电力用户510家，售电公司28家，电网企业19家，发电企业12家。其中，电力用户数量最多，占绝对主导地位；售电公司次之，表明市场竞争较为活跃；电网企业和发电企业数量相对较少，但作为关键基础设施和能源供应方，其重要性不可忽视。";
                    //组织柱状图数据
                    String echartSerieName = "数量";
                    ArrayList<String> serieData = new ArrayList<>();
                    for (LinkedHashMap<String,Object> map : daoDataByQuery) {

                        //标记每行数据的第一个字符串值
                        int xFlag = 0;
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (isNumeric(entry.getValue())){
                                echartSerieName = entry.getKey();
                                serieData.add(entry.getValue().toString());
                            }else {
                                //取第一个字符串值作为x轴名称
                                if (xFlag == 0){
                                    xAxisData.add(entry.getValue().toString());
                                }
                                xFlag++;
                            }
                        }
                    }
                    //封装y轴的数据
                    EchartSerie echartSerie = new EchartSerie();
                    echartSerie.setType("bar");
                    echartSerie.setName(echartSerieName);
                    echartSerie.setData(serieData);
                    echartSeries.add(echartSerie);
                }else {
                    content = "根据提供的业务数据，我们可以对2024年12月25日海南地区的新能源总出力进行详细的分析。以下是逐小时的预测情况及其趋势解读：\n" +
                            "\n" +
                            " 00:00 - 06:00：低谷期\n" +
                            "- **00:00 - 06:00**：这一时间段内的新能源总出力相对较低，基本维持在38 MW到46 MW之间。具体数值如下：\n" +
                            "  - 00:00 - 38.04 MW\n" +
                            "  - 06:00 - 46.02 MW\n" +
                            "- **原因分析**：此时间段内，太阳尚未升起，太阳能发电几乎为零；风能发电量也相对较低，主要原因是夜间风速通常较弱。因此，整体出力处于低谷。\n";
                    int index = 0;
                    for (LinkedHashMap<String,Object> map : daoDataByQuery) {
                        //value,y轴值
                        ArrayList<String> serieData = new ArrayList<>();

                        EchartSerie echartSerie = new EchartSerie();
                        echartSerie.setType("line");
                        String name = "";
                        echartSerie.setData(serieData);
                        echartSeries.add(echartSerie);

                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            //把值是数值的字段添加到x轴
                            if (isNumeric(entry.getValue())){
                                if (index == 0){
                                    xAxisData.add(entry.getKey());
                                }
                                //x轴字段对应的值
                                serieData.add(entry.getValue().toString());
                            }else {
                                //统计维度的名称
                                if (StringUtils.isEmpty(name)){
                                    name += entry.getValue().toString();
                                }else {
                                    name += "-"+entry.getValue().toString();
                                }
                            }

                        }
                        echartSerie.setName(name);
                        legendData.add(name);
                        index += 1;
                    }
                }


                echart.setTitle(question);
                echart.setLegendData(legendData);
                echart.setXAxisData(xAxisData);
                echart.setSeries(echartSeries);
                //返回的echart节点,前端取法data.echart


            }catch (Exception e){
                log.warn("生成echarts图表数据异常");
            }

            //返回的content节点,前端取法data.content
            //data.put("content",content);

            // 发送事件的格式：event:type\n data:message\n\n
            //String resultData = "data:"+ JSONObject.toJSONString(data)+"\n\n";
            //outputStream.write("event: message\n".getBytes(StandardCharsets.UTF_8));
            //outputStream.write(resultData.getBytes(StandardCharsets.UTF_8));

            //outputStream.flush(); // 确保消息已经发送

            //业务操作
            log.info("输出回答内容:{}",content);
            // 可以在这里添加更多的事件发送逻辑
            for (int i = 0; i < content.length(); i++) {
                data.put("content",content.charAt(i));
                if (i == content.length()-1){
                    data.put("echart",echart);
                }
                // 发送事件的格式：event:type\n data:message\n\n
                String resultData = "data:"+ JSONObject.toJSONString(data)+"\n\n";
                outputStream.write("event: message\n".getBytes(StandardCharsets.UTF_8));
                outputStream.write(resultData.getBytes(StandardCharsets.UTF_8));

                outputStream.flush(); // 确保消息已经发送
                Thread.sleep(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNumeric(Object object) {
        if (object == null){
            return false;
        }else {
            //判断是不是数字
            if (object.toString().matches("-?\\d+(\\.\\d+)?")){
                boolean isIntNumber = true;
                try {
                    //判断数字是否在int的取值范围
                    if (!object.toString().contains(".")){
                        Integer.parseInt(object.toString());
                    }
                }catch (NumberFormatException e){
                    isIntNumber = false;
                }
                return isIntNumber;
            }else {
                return false;
            }
        }

    }

}
