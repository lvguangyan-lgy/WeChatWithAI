package com.frost.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ${Frost-YAN} on 2024/2/20
 */
public class DishesUtil {

    private static final JSONObject DISHES_JSON = new JSONObject();
    private static final ArrayList<String> KEY_LIST = new ArrayList();
    static {
        String dishesJsonStr = "";
        ClassPathResource classPathResource = new ClassPathResource("dishesList.json");
        try {
            InputStream inputStream =classPathResource.getInputStream();
            byte[] bytes = FileUtil.readStream(inputStream);
            dishesJsonStr = new String(bytes,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject tem = JSONObject.parseObject(dishesJsonStr);
        for (String key : tem.keySet()) {
            KEY_LIST.add(key);
            DISHES_JSON.put(key,tem.getString(key));
        }
    }

    /**
     * 根据关键词获取菜名,关键词为空获取所有
     * @param key
     * @return
     */
    public static String getDishesName(String key){
        key = key == null ? "" : key;
        ArrayList<String> dishesNameList = new ArrayList<>();
        for (String name : KEY_LIST) {
            if (name.contains(key)){
                dishesNameList.add(name);
            }
        }
        return dishesNameList.toString();
    }

    /**
     * 根据菜名获取详情
     * @param name
     * @return
     */
    public static String getDishesByName(String name){
        String dishes = "";
        if ("".equals(name) || name == null){
            return "菜肴名不能为空";
        }

        try {
            ClassPathResource classPathResource = new ClassPathResource(DISHES_JSON.getString(name));
            InputStream inputStream = classPathResource.getInputStream();
            byte[] bytes = FileUtil.readStream(inputStream);
            dishes = new String(bytes,"UTF-8");
        } catch (Exception e) {
            dishes = "暂未收录该菜肴";
        }
        return dishes;
    }

    public static void main(String[] args) {

        System.out.println(getDishesName("学习111111"));
        //String a = getDishesByName("学习-如何选择现在吃什么");
        //System.out.println(a);
    }

}