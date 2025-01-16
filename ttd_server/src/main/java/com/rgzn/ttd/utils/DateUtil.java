package com.rgzn.ttd.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateUtil {

    public static String shiftedTime(String timeStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(timeStr, formatter);

        // 往后推一个小时
        LocalDateTime shiftedTime = time.plusHours(1);

        // 输出结果
        return shiftedTime.format(formatter);
    }

    public static String pushDay(int day){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayTime = now.plusDays(day);
        // 输出结果
        return dayTime.format(formatter);
    }

    public static String now(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    public static String today(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    public static void main(String[] args) {

        System.out.println(pushDay(0));
    }
}
