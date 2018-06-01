package org.fage.mysearchengine;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.fage.mysearchengine.search.analizer.MyIkAnalyzer;
import org.fage.mysearchengine.spider.common.CSDNBlogPagingInfo;
import org.fage.mysearchengine.utils.IndexUtils;
import org.fage.mysearchengine.utils.SpiderUtils;
import org.fage.mysearchengine.utils.CommonUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午4:23 2018/5/4
 * @description
 **/
public class SimpleTest {
    final Logger logger = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void testReplaceUrl() {
        String source = "https://blog.csdn.net/xxx";
        String target = source.replace("blog.csdn.net", "my.csdn.net");
        logger.info(target);
    }

    @Test
    public void testGetUsernameFromReplace() {
        String username = "https://my.csdn.net/hhthrthrthrt234234nan".replaceAll("http[s]*://my\\.csdn\\.net/", "");
        logger.info(username);
    }

    @Test
    public void testGetUsernameFromReplaceBlogUrl() {
        String username = "https://blog.csdn.net/hellocaizhfya_aa1002/article/details/79014353".replaceAll("http[s]*://blog\\.csdn\\.net/|/article/details/\\w+", "");
        logger.info(username);
    }

    @Test
    public void testMatchRegex() {
        String regex = "http[s]*://my\\.csdn\\.net/\\w+";
        logger.info("http://my.csdn.net/hudi1990".matches(regex) + "");
    }

    //测试一下代理ip是否可用
    @Test
    public void testProxy() throws IOException {
        //Proxy类代理方法
        URL url = new URL("http://www.baidu.com");
        // 创建代理服务器
        InetSocketAddress addr = null;
        addr = new InetSocketAddress("119.120.76.21", 6666);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
        URLConnection conn = url.openConnection(proxy);
        InputStream in = conn.getInputStream();
        String s = IOUtils.toString(in);
        //System.out.println(s);
        if (s.indexOf("百度") > 0) {
            System.out.println("ok~~~~~~~~~~~~~~~~~~~~~~~");
            logger.info(s);
        }
    }

    @Test
    public void testProxySpeed_Ping() throws IOException {
        String ip = "119.120.76.21:6666";
        Process p = Runtime.getRuntime().exec("ping " + ip);
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        boolean flag;
        String time = null;
        String temp = null;
        while ((line = reader.readLine()) != null) {
            temp += line;
            //判断是否丢包
            if (line.indexOf("out") > 0) {
                flag = false;
                break;
            }
            //取得延时
            if (line.indexOf("time") > 0 && line.indexOf("ms") > 0) {
                time = line.substring(line.indexOf("time") + 5, line.indexOf("ms"));
                System.out.println(line.substring(line.indexOf("time") + 5, line.indexOf("ms")));
                //如果延时小于5秒
                if (Integer.valueOf(time) < 5000)
                    flag = true;
                else
                    flag = false;
                break;
            }
        }
        System.out.println(temp);
        is.close();
        reader.close();
        p.destroy();
    }

    //分析Js分页变量截取
    @Test
    public void testJavaScriptSplit() {
        String jsStr = "var currentPage = 1;\n" +
                "    var baseUrl = 'https://blog.csdn.net/HHTNAN/article/list' ;\n" +
                "    var pageSize = 20 ;\n" +
                "    var listTotal = 302 ;\n" +
                "    var pageQueryStr = '?';\n" +
                "    function getAllUrl(page) {\n" +
                "        return baseUrl + \"/\" + page + pageQueryStr;\n" +
                "    }";
        jsStr = jsStr.replaceAll("\\s|;|'", "").substring(1).replace("functiongetAllUrl(page){returnbaseUrl+\"/\"+page+pageQueryStr}", "");

        String[] strs = jsStr.split("var");
        for (String str : strs) {
//            logger.info("字符串：" + str + "\n");
            String[] keyAndValue = str.split("=");
            logger.info(keyAndValue[0] + "的值为" + keyAndValue[1] + "\n");
        }
    }

    //测试csdn分页信息
    @Test
    public void testCSDNBlogPagingInfo() {
        CSDNBlogPagingInfo page = new CSDNBlogPagingInfo(0, null, 623);
        logger.info(page.toString());
    }

    //测试时间工具类
    @Test
    public void testTimeUtils() {
        Date date = CommonUtils.getDateFromChStr("2018年05月01日 22:29:21");
        logger.info(date + "");
    }

    //测试加签效果
    @Test
    public void testGetTagsStrFromTagList() {
        String[] tags = {"java", "web", "python", "go"};
        String str = SpiderUtils.getTagsStrFromTagList(Arrays.asList(tags));
        logger.info(str);
        //测试还原
        String[] tagArr = str.split("\\|");
        for (String tag : tagArr) {
            logger.info(tag);
        }
    }

    //测试IK分词
    @Test
    public void testPaodingAnalyzer() throws IOException {
        Analyzer analyzer = new MyIkAnalyzer();
        String indexStr = "\n" +
                "最近需要速成java，制定一个学习计划。\n" +
                "\n" +
                "1基本语法\n" +
                "\n" +
                "一篇博文搞定：http://blog.csdn.net/crazyhacking/article/details/8144622 \n" +
                "<http://blog.csdn.net/crazyhacking/article/details/8144622>\n" +
                "\n" +
                "2多线程\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "3网络通信socket编程\n" +
                "\n" +
                "non block socket\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "4 设计模式\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "不会的可以查阅jdk手册\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "先从java的基础学起吧，既然楼主学过c++,java就很好学了。 sun公司的《java2入门经典》，适合java的初学者 太厚 不看 \n" +
                "然后是《java2核心技术》，在有一定基础的时候来看 也就是core java 最后一定要看《java编程思想》也就是think in java \n" +
                "。这本书似乎不怎么样 然后，个人觉得还是学习java比较好，毕竟它的跨平台性是别的语言所不能比拟的 \n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "JAVA的底层就是C++来的啊 所以的话。 Java 方面的书去看下 IO流 SQL的操作 那些集合 Map 啊。 set list vector \n" +
                "。主要就是 IO包。util包 SQL包 这三个包下常用的类 JAVA的看完后那么就开始看 《jsp开发应用与详解》这本书 \n" +
                "然后自己做登陆的例子。然后在上面增加功能 也就是增删改查的4个功能 然后再看下 Struts + Hibernate Spring 的话先别看 Struts \n" +
                "详细看下。Hibernate 搞清楚怎么写HQL语句就可以了，怎么写配置文件就可以了 基本上只能这个样子了 这就是最速成的方法了 自己试试吧 \n" +
                "\n" +
                "\n";
        StringReader reader = new StringReader(indexStr);
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(indexStr));
        tokenStream.reset();

        long start = System.currentTimeMillis();
        while (tokenStream.incrementToken()) {
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
            logger.info(new String(charTermAttribute.toString()) + "\n");
        }
        tokenStream.close();
        analyzer.close();
        long end = System.currentTimeMillis();
        logger.info("所花时间---->" + (start - end) + "毫秒");
    }

    @Test
    public void simulationTest() {
//        Request request = new Request("https://www.csdn.net/");
//        request.setMethod(HttpConstant.Method.POST);
//        Map map = new HashMap();
//        map.put("username", "Phapha1996");
//        map.put("password", "lyl520..../");
//        map.put("fkid", "WHJMrwNw1k/FG12WF0EwoWp05NxmsXoC8yaC8ViQN0hFXDb3bEbp5V+WUMiZFH/2sPTy1kVEeCQCvEZNuX0cSksTu+8AZmKVJ2NE0nXPW/8aPx2sG4lBwgw/GllaqVP3BvWdC0QQex8g/JECVE0q4X77RwtqxvhO9+F0kPvYTR+ETJrGzzeI4grz2ToGz/nioER6bGwPai5XNtAKvckCXn26ptDDKwhSG5wdB52TobdQptWz1bETJ3dwuOXG+4ocFpOSOnthzPGo=1487582755342");
//        map.put("lt", "LT-2580032-MrqkJqgDVzVBYUcZNnLIVBqi5TZwpy");
//        request.setRequestBody(HttpRequestBody.form(map,"utf-8"));
//        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        request.addHeader("Accept-Encoding", "gzip, deflate, br");
//        request.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
//        request.addHeader("Content-Type","application/x-www-form-urlencoded");
//        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
//        List<Request> requestList = new ArrayList<Request>();
//        requestList.add(request);
        Spider.create(new PageProcessor() {
            @Override
            public void process(Page page) {
                String gps = page.getHtml().xpath("//input[@id='gps']/@value").get();
                String it = page.getHtml().xpath("//form[@id='fm1']/input[@name='lt']/@value").get();
                String execution = page.getHtml().xpath("//form[@id='fm1']/input[@name='execution']/@value").get();
                String fkid = page.getHtml().xpath("//form[@id='fm1']/input[@id='fkid']/@value").get();
                String submit = page.getHtml().xpath("//form[@id='fm1']/input[@name='_eventId']/@value").get();
                logger.info("it" + it);
                logger.info("execution" + execution);
                logger.info("fkid" + fkid);
                logger.info("submit" + submit);
            }
            @Override
            public Site getSite() {
                return Site.me().setTimeOut(3000).setCharset("UTF-8");
            }
        }).addUrl("https://passport.csdn.net/account/login").run();
    }

    @Test
    public void subStringTest(){
        String indexStr = "ArcGISMaritimeServer开发教程（一）了解ArcGISMaritimeServerArcGISMaritimeServer是ArcGIS平台面向海洋、海事以及航道等行业发布的海图发布平台。MaritimeServer与之前NauticalSolution有着较大的差别，在地图渲染、地图发布、使用模式上均重新进";
        logger.info(indexStr.length() + "");
    }

    @Test
    public void testSaveEmTag(){
        String str = "<body>你知<em>道</em><script>的</script rel=''></body>";
        String s=str.replaceAll("<[^>]*>|>","");
        logger.info(s);
    }
}
