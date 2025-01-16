package org.zhong.chatgpt.wechat.bot.schedule;

import cn.hutool.core.date.DateUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.core.Core;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zhong.chatgpt.wechat.bot.builder.OpenAiServiceBuilder;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * @version : 1.0
 * @Auther: lgy
 * @Date: 2024/01/04 11:24
 **/

@Component
@EnableScheduling
public class HolidaySchedule {
    private static Core core = Core.getInstance();
    /**
     * 定时任务: 判断当天是否节日,在白名单群组发祝福语
     */
    @Scheduled(cron = "${custom.schedule.cron.holiday}")
    public void holidaySchedule() throws IOException {
        String date = DateUtil.format(new Date(), "yyyy-MM-dd");
        JSONObject holiday = BotConfig.getHoliday();
        String holidayName = holiday.getString(date);
        System.out.println("节日名称:"+holidayName);
        //今天是节日
        if (StringUtils.isNotEmpty(holidayName)){
            //调用接口获取祝福语
            String holidayMsg = "";
            if (holidayName.contains("疯狂星期四")){
                boolean flag = false;
                //要求文案中必须有:v我50（如果3次都获取不到就算了）
                int count = 1;
                while (!flag && count < 4){
                    holidayMsg = getHolidayMsg(holidayName);
                    count++;
                    flag = holidayMsg.contains("v我50") || holidayMsg.contains("V我50");
                }
            }else {
                holidayMsg = getHolidayMsg(holidayName);
            }
            //获取群组白名单,群发节日祝福语
            List<String> groupWhiteList = BotConfig.getGroupWhiteList();
            for (String groupName : groupWhiteList) {
                List<JSONObject> groupList = core.getGroupList();
                for (JSONObject wechatGroup : groupList) {
                    if (groupName.contains(wechatGroup.getString("NickName"))){
                        //发送到群
                        MessageTools.sendMsgById(holidayMsg,wechatGroup.getString("UserName"));
                    }
                }

            }
        }
    }

    private String getHolidayMsg(String holidayName) throws IOException {
        OkHttpClient httpClient = OpenAiServiceBuilder.okHttpClient(BotConfig.getAppKey(), Duration.ofSeconds(300));
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.get("application/json");
        JSONObject jsonParams = new JSONObject();
        String context = "";
        if (holidayName.contains("疯狂星期四")){
            context = "今天是"+holidayName+",以"+holidayName+"为主题 要求文中必须包含字符'v我50',写一个50字以内的故事";
        }else {
            context = "今天是"+holidayName+",以"+holidayName+"为主题写一个50字以内的温馨提示";
        }
        jsonParams.put("context", context);
        jsonParams.put("sessionId","systemMessage");

        RequestBody requestBody = RequestBody.create(jsonParams.toJSONString(),MEDIA_TYPE_MARKDOWN);
        Request request = new Request.Builder()
                .url(BotConfig.getProxyService())
                .post(requestBody)
                .build();

        Call call = httpClient.newCall(request);
        Response response = call.execute();
        String StringTemp = response.body().string();
        JSONObject parse = (JSONObject) JSONObject.parse(StringTemp);
        String text = "";
        if (holidayName.contains("疯狂星期四")){
            text = parse.getString("msg");
        }else {
            text = "今天是"+holidayName+"。"+parse.getString("msg");
        }
        return text;
    }

}