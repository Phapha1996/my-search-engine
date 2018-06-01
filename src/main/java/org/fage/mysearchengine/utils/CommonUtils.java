package org.fage.mysearchengine.utils;


import org.fage.mysearchengine.search.dto.Item;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午8:27 2018/5/5
 * @description
 **/
public class CommonUtils {
    /**
     * fager 5原色
     */
    public static String[] pageColors = {"#e6796a","#B0E2FF", "#c2cd62", "#daad5c", "#e96fce","#B0E2FF", "#e6796a", "#c2cd62", "#daad5c", "#e96fce"};

    //格式化如下字串为Date实例
    //2018年05月01日 22:29:21
    public static Date getDateFromChStr(String timeStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date time = null;
        try {
            time = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    //从2017121111获取
    public static Date getDateFromyyyyMMddHH(String yyyyMMddHH){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        Date time = null;
        try {
            time = sdf.parse(yyyyMMddHH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 截取长冗字符串，分title与tags
     * @param str
     * @param attr
     * @return
     */
    public static String cuttingAndChangeStr(String str, String attr){
        if(StringUtils.isEmpty(str) || StringUtils.isEmpty(attr))
            return "";
        if("title".equals(attr) && str.length()>30){
            return str.substring(0,25) + "...";
        }else if ("tags".equals(attr) && str.length()>15){
            return str.substring(0,10) + "...";
        }
        return str;
    }

    /**
     * 文章截取150字符，去除空白类字符
     * 去除所有的html标签，以免影响显示
     * @param content
     * @return
     */
    public static String cuttingAndChangeContent(String content){
        if(StringUtils.isEmpty(content))
            return "";
        String result = content.replaceAll("\\s|<[^>]*>|>", "");
        if(result.length()>150){
            result = result.substring(0, 150);
        }
        return result;
    }

    /**
     * 截取冗长标题字符串
     * @param itemList
     * @return
     */
    public static void cuttingAndCHangeStr(List<Item> itemList){
        for(Item item : itemList) {
            String title = item.getLabel();
            if(StringUtils.isEmpty(title)) {
                continue;
            }
            if (title.length() > 12) {
                title = title.substring(0, 12) + "...";
            }
            item.setLabel(title);
        }
    }
}


