package com.rgzn.ttd;

import com.alibaba.fastjson.JSONObject;
import com.rgzn.ttd.service.DynamicsService;
import com.rgzn.ttd.service.PromptService;
import com.rgzn.ttd.service.SqlTemplateService;
import com.rgzn.ttd.service.TableMeteService;
import com.rgzn.ttd.utils.CacheManagerHelper;
import com.rgzn.ttd.utils.DateUtil;
import com.rgzn.ttd.utils.MegawattUtil;
//import com.rgzn.ttd.utils.OpenAiUtil;
import com.rgzn.ttd.utils.SSLSocketClient;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestService extends Tester {

    @Autowired
    private PromptService promptService;

   /* @Autowired
    private OpenAiUtil openAiUtil;*/

    @Autowired
    private DynamicsService dynamicsService;

    @Autowired
    private MegawattUtil megawattUtil;

    @Autowired
    private SqlTemplateService sqlTemplateService;

    @Test
    public void removeCache(){

        String key1 = "今日海南新能源总出力预测情况" + "echarts" + DateUtil.today();
        String key2 = "今日海南新能源总出力预测情况" + DateUtil.today();
        String key3 = "今日海南新能源总出力预测情况" + "true" + DateUtil.today();
        String key4 = "今日海南新能源总出力预测情况" + "false" + DateUtil.today();

        String key11 = "海南地区各市场主体类型的数量" + "echarts" + DateUtil.today();
        String key12 = "海南地区各市场主体类型的数量" + DateUtil.today();
        String key13 = "海南地区各市场主体类型的数量" + "true" + DateUtil.today();
        String key14 = "海南地区各市场主体类型的数量" + "false" + DateUtil.today();
        CacheManagerHelper.getInstance().remove("oneDay",key1);
        CacheManagerHelper.getInstance().remove("oneDay",key2);
        CacheManagerHelper.getInstance().remove("oneDay",key3);
        CacheManagerHelper.getInstance().remove("oneDay",key4);

        CacheManagerHelper.getInstance().remove("oneDay",key11);
        CacheManagerHelper.getInstance().remove("oneDay",key12);
        CacheManagerHelper.getInstance().remove("oneDay",key13);
        CacheManagerHelper.getInstance().remove("oneDay",key14);

    }

    @Test
    public void dynamicsMapper(){
        List<LinkedHashMap<String, Object>> dataMao = dynamicsService.dynamicsSql("select * from mk_td_his_elec");
        String jsonString = JSONObject.toJSONString(dataMao);
        System.out.println("输出jsonString:"+jsonString);

    }

    @Test
    public void megawattUtil(){
        System.out.println(megawattUtil.requestMegawatt("测试问题"));
    }


    @Test
    public void sqlTemplateService(){
        JSONObject sqlTemplate = sqlTemplateService.getTemplateByLargeModel("海南有多少电网企业");
        String  sql = sqlTemplate.getString("sql");
        System.out.println(sql);

    }

    @Test
    public void testHttpClient(){

        //2.调用接口获取密钥
        String result = "";
        String query = "dkhfkahf2222skjhdj";
        String jsonStr = "{\n" +
                "\t\"query\": \""+query+"\",\n" +
                "\t\"query1\": \""+query+"\"\n" +
                "}";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();
        // 创建MediaType对象，指定发送数据的格式
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // 创建RequestBody对象，发送json数据
        RequestBody requestBody = RequestBody.create(JSON,jsonStr);
        //请求头
        Headers headers = Headers.of(
                "Content-Type", "application/json;charset=utf-8",
                "Accept-Encoding", "utf-8"
        );
        // 创建Request对象，设置url和RequestBody
        String url = "https://127.0.0.1:8080/ttd/v1/query";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(headers)
                .build();

        // 创建Call对象
        okhttp3.Call call = client.newCall(request);

        try {
            // 同步执行请求
            long startTime = System.currentTimeMillis();
            Response response = call.execute();
            long endTime = System.currentTimeMillis();

            // 处理响应
            if (response.isSuccessful()) {
                result = response.body().string();
                System.out.println("https返回:"+result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
