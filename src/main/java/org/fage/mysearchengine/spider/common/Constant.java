package org.fage.mysearchengine.spider.common;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午8:45 2018/5/4
 * @description
 **/
public class Constant {

    ////////////////////////////////////正常字串
    /**
     * 种子url
     */
    public static final String CSDN_SEEDS_URL = "https://blog.csdn.net/";

    ////////////////////////////////////正则字串
    /**
     * 匹配用户详情主页（附带有博客list的页面）http://my.csdn.net/xxx
     */
    public static final String REGEX_CSDN_USER_MY_URL = "http[s]*://my\\.csdn\\.net/\\w+";

    /**
     * 匹配用户第一页博客主页http://blog.csdn.net/xxx
     */
    public static final String REGEX_CSDN_USER_BLOG_URL = "http[s]*://blog\\.csdn\\.net/\\w+";

    /**
     *  匹配用户博客列表页http://blog.csdn.net/xxx/article/list/23?
     */
    public static final String REGEX_CSDN_BLOG_LIST_URL = "http[s]*://blog\\.csdn\\.net/\\w+/article/list/\\d+\\?";

    /**
     * 匹配用户博客文章页面http://blog.csdn.net/xxx/article/details/706547
     */
    public static final String REGEX_CSDN_BLOG_URL = "http[s]*://blog\\.csdn\\.net/\\w+/article/details/\\w+";
}
