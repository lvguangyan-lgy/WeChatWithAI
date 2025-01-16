package com.frost.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ${Frost-YAN} on 2024/2/20
 */
public class BqbUtil {

    private static final JSONObject BQB_JSON = new JSONObject();
    private static final ArrayList<String> KEY_LIST = new ArrayList();
    static {
        String bqbJsonStr = "";
        ClassPathResource classPathResource = new ClassPathResource("bqb.json");
        try {
            InputStream inputStream =classPathResource.getInputStream();
            byte[] bytes = FileUtil.readStream(inputStream);
            bqbJsonStr = new String(bytes,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject bqbJson = JSONObject.parseObject(bqbJsonStr);
        JSONArray data = bqbJson.getJSONArray("data");
        for (Object datum : data) {
            JSONObject bqb = JSONObject.parseObject(datum.toString());
            String key = bqb.getString("name") + bqb.getString("category");
            KEY_LIST.add(key);
            BQB_JSON.put(key,bqb.getString("url"));
        }
    }

    /**
     * 随机获取一个表情
     * @return
     */
    public static String getRandomBqb(){
        //// 从指定范围内生成一个int类型的随机数
        Random random = new Random();
        int randomInt = random.nextInt(KEY_LIST.size());
        return BQB_JSON.getString(KEY_LIST.get(randomInt));
    }

    /**
     * 根据关键词随机获取一个表情.如果获取不到则返回空串
     * @param keyWork
     * @return
     */
    public static String getRandomBqbByKeyWork(String keyWork){
        //存储匹配的key
        ArrayList<String> bqbKeyList = new ArrayList<>();
        String[] keyWorks = keyWork.split("\\|");

        for (String key : KEY_LIST) {
            boolean flag = true;
            for (String keyWorkN : keyWorks) {
                if (!key.contains(keyWorkN)){
                    flag = false;
                    break;
                }
            }

            if (flag){
                bqbKeyList.add(key);
            }
        }

        String url = "";
        //// 从指定范围内生成一个int类型的随机数
        if (bqbKeyList.size() > 0){
            System.out.println("存在:"+bqbKeyList.size()+"个");
            Random random = new Random();
            int randomInt = random.nextInt(bqbKeyList.size());
            url =BQB_JSON.getString(bqbKeyList.get(randomInt));
        }else {
            System.out.println("未找到符合条件的表情");
        }
        return url;
    }

    public static void main(String[] args) {

        System.out.println(getRandomBqbByKeyWork("奇妙猫"));
    }

}