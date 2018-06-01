package org.fage.mysearchengine.utils;

import org.fage.mysearchengine.spider.common.CSDNBlogPagingInfo;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午2:15 2018/5/5
 * @description
 **/
public class SpiderUtils {

    private static String fileCachePath = null;
    static {
        fileCachePath = System.getProperty("user.dir") + "/res/spider/urlcache";
    }
    /**
     * @param blogUserMyPage
     * 获取用户blog主页的文章分页信息
     */
    public static CSDNBlogPagingInfo getBlogPagingInfo(Page blogUserMyPage){

        //提取分页的js源代码
        String javaScriptCode = blogUserMyPage.getHtml().xpath("//body/script[3]").get().replaceAll("(<[^>]+>)", "");
        //分析js源代码提取分页变量
        javaScriptCode = javaScriptCode.replaceAll("\\s|;|'", "").substring(1).replace("functiongetAllUrl(page){returnbaseUrl+\"/\"+page+pageQueryStr}", "");
        if(StringUtils.isEmpty(javaScriptCode))
            return null;
        //根据前面的分割得到的字串键值对,数组下标0位键，1为值
        String[] vars = javaScriptCode.split("var");
        String[] currentPageKeyAndValue = vars[0].split("=");   //当前页码
        String[] pageSizeKeyAndValue = vars[2].split("=");      //每页大小
        String[] listTotalKeyAndValue = vars[3].split("=");     //总记录数
        CSDNBlogPagingInfo pageInfo = new CSDNBlogPagingInfo(Integer.parseInt(currentPageKeyAndValue[1]), Integer.parseInt(pageSizeKeyAndValue[1]), Integer.parseInt(listTotalKeyAndValue[1]));

        return pageInfo;
    }

    public static String getTagsStrFromTagList(List<String> tagList){
        if(tagList==null || tagList.size()==0)
            return "";
        StringBuffer tagsSb = new StringBuffer("");
        for(String tag : tagList){
            tagsSb.append(tag).append("|");
        }
        String tagStr = tagsSb.substring(0,tagsSb.length()-1).toString();
        return tagStr;
    }

    public static String getFileCachePath() {
        return fileCachePath;
    }
}
