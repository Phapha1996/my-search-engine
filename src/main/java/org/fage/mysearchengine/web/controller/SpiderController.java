package org.fage.mysearchengine.web.controller;

import org.fage.mysearchengine.spider.Spiders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午1:06 2018/5/3
 * @description 专注爬虫的控制器，可以自己手动使爬虫开始执行逻辑
 **/
@RestController
@RequestMapping("crawler")
public class SpiderController {
    final Logger logger = LoggerFactory.getLogger(SpiderController.class);
    @Autowired
    private Spiders spiders;

    //主动爬取（有定时任务爬取）
    @RequestMapping("/csdn")
    public String csdnCrawler(HttpServletRequest request){
        try {
            ServletContext servletContext = request.getServletContext();
            Spider csdnSpider = spiders.csdnSpider(true,10);
            servletContext.setAttribute("csdnSpider", csdnSpider);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return "启动失败,请查看失败信息...";
        }
        return "The csdn spider is starting...";
    }

    //停止爬取
    @RequestMapping("/stop_csdn")
    public String csdnStop(HttpServletRequest request){
        //此处加入停止逻辑
        ServletContext servletContext = request.getServletContext();
        Spider csdnSpider = (Spider) servletContext.getAttribute("csdnSpider");
        if(null != csdnSpider && csdnSpider.getStatus().equals(Spider.Status.Running)) {
            csdnSpider.stop();
            logger.info("CSDN爬虫被手动停止了");
        }
        servletContext.removeAttribute("csdnSpider");
        csdnSpider = null;
        return "stop...";
    }

}
