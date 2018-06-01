package org.fage.mysearchengine.spider.processor;

import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.entity.User;
import org.fage.mysearchengine.spider.common.CSDNBlogPagingInfo;
import org.fage.mysearchengine.spider.common.Constant;
import org.fage.mysearchengine.utils.SpiderUtils;
import org.fage.mysearchengine.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午4:03 2018/5/4
 * @description CSDN爬虫
 **/
@Component("csdnPageProcessor")
public class CSDNPageProcessor implements PageProcessor {

    final Logger logger = LoggerFactory.getLogger(CSDNPageProcessor.class);
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(3000);

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();

        //执行种子页逻辑
        if (url.equals(Constant.CSDN_SEEDS_URL)) {
            doFirstPage(page);
            //执行用户my页逻辑
        } else if (url.matches(Constant.REGEX_CSDN_USER_MY_URL)) {
            doUserMyPage(page);
            //执行用户blog主页逻辑
        } else if (url.matches(Constant.REGEX_CSDN_USER_BLOG_URL)) {
            doUserBlogMainPage(page);
            //执行blogList页逻辑
        } else if (url.matches(Constant.REGEX_CSDN_BLOG_LIST_URL)) {
            doBlogListPage(page);
            //处理博客页
        } else if (url.matches(Constant.REGEX_CSDN_BLOG_URL)) {
            doBlogPage(page);
        }
    }


    /**
     * @param page 执行种子页面的逻辑
     */
    private void doFirstPage(Page page) {
        page.setSkip(true);
        //获取初始页博客专家的链接https://blog.csdn.net/xxx
        List<String> blogListUrl = page.getHtml().xpath("//div[@class='feed_media_list clearfix csdn-tracking-statistics']").links().all();

        //用户blog博客主页入队
        page.addTargetRequests(blogListUrl);

        for (String blogUrl : blogListUrl) {
            //替换链接成用户主页https://my.csdn.net/xxx加入队列，以便分析用户页
            String myUrl = blogUrl.replace("blog.csdn.net", "my.csdn.net");
            page.addTargetRequest(myUrl);
        }
    }

    /**
     * @param page 执行用户my主页面逻辑，提取用户信息
     */
    private void doUserMyPage(Page page) {
        Html html = page.getHtml();
        //提取用户信息并传给pipeline
        String nickname = html.xpath("//dt[@class='person-nick-name']/span/text()").get();
        String username = page.getUrl().get().replaceAll("http[s]*://my\\.csdn\\.net/", "");
        String personDetail = html.xpath("//dd[@class='person-detail']/text()").get().replaceAll("(<[^>]+>)|\n", "");
        String personSign = html.xpath("//dd[@class='person-sign']/text()").get();
        Integer fans = Integer.parseInt(html.xpath("//dd[@class='fans_num']/b/text()").get());
        Integer focus = Integer.parseInt(html.xpath("//dd[@class='focus_num']/b/text()").get());
        String userBlogUrl = page.getUrl().get();

        if (!StringUtils.isEmpty(username)) {
            User user = new User(null, username, nickname, personDetail, personSign, fans, focus, userBlogUrl);
            page.putField("user", user);
            page.putField("isMyPage", true);
        } else {
            page.setSkip(true);
        }
        //继续加入其他关注与被关注的博主url
        List<String> myUrlList = page.getHtml().xpath("//div[@class='header clearfix']").links().all();
        page.addTargetRequests(myUrlList);

        //my页面替换成blog页面，入队，以便处理blog的方法去处理
        for (String myUrl : myUrlList) {
            String userBlogMainUrl = myUrl.replaceAll("my.csdn.net", "blog.csdn.net");
            page.addTargetRequest(userBlogMainUrl);
        }
    }

    /**
     * 处理用户blog主页，自己拼接成博客的list页
     *
     * @param page
     */
    private void doUserBlogMainPage(Page page) {
        //获取用户blog分页信息
        CSDNBlogPagingInfo pageInfo = SpiderUtils.getBlogPagingInfo(page);
        if (pageInfo == null)
            return;
        String blogMainUrl = page.getUrl().get();
        //拼接成blogList页面，加入队列
        for (int i = 1; i <= pageInfo.getTotalPages(); i++) {
            //拼接出blogListurl
            String blogListUrl = blogMainUrl + "/article/list/" + i + "?";
            page.addTargetRequest(blogListUrl);
        }
        page.setSkip(true);
    }

    /**
     * 处理用户blogList页面,加入队列
     *
     * @param page
     */
    private void doBlogListPage(Page page) {
        List<String> blogUrls = page.getHtml().$("div.article-list").xpath("//h4[@class='text-truncate']").links().all();
        page.addTargetRequests(blogUrls);
    }

    /**
     * 处理真正的博客页面
     *
     * @param page
     */
    private void doBlogPage(Page page) {
        //提取博客信息，详情请查看Article类
        String username = page.getUrl().get().replaceAll("http[s]*://blog\\.csdn\\.net/|/article/details/\\w+", "");
        String blogUrl = page.getUrl().get();
        String title = page.getHtml().xpath("//h6[@class='title-article']/text()").get();
        String type = page.getHtml().xpath("//span[@class='article-type type-1 float-left']/text()").get();
        String time = page.getHtml().xpath("//span[@class='time']/text()").get();
        String like = page.getHtml().xpath("//button[@class='btn-like']/p[1]/text()").get();
        String readNumber = page.getHtml().xpath("//span[@class='read-count']/text()").get().replaceAll("阅读数：", "");
        List<String> tags = page.getHtml().xpath("//div[@class='tags-box artic-tag-box']/a/text()").all();
        List<String> categories = page.getHtml().xpath("//div[@class='article-bar-bottom']/div[3]/a/text()").all();
        String content = page.getHtml().xpath("//div[@id='article_content']/tidyText()").get();

        Article article = new Article(null, blogUrl, title, type, CommonUtils.getDateFromChStr(time), Integer.parseInt(like), Integer.parseInt(readNumber), SpiderUtils.getTagsStrFromTagList(tags), SpiderUtils.getTagsStrFromTagList(categories), content, null);
//        logger.debug(article.toString());
        page.putField("article", article);
        page.putField("username", username);
        page.putField("isArticlePage", true);
    }


    @Override
    public Site getSite() {
        return site;
    }
}
