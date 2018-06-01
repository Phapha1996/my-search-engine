package org.fage.mysearchengine.spider;

import org.fage.mysearchengine.spider.common.Constant;
import org.fage.mysearchengine.utils.SpiderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

import javax.annotation.Resource;

/**
 * @author Caizhf
 * @version 1.0
 * @date 上午11:31 2018/5/4
 * @description 爬虫类
 **/
@Component
public class Spiders {
    private final static Logger logger = LoggerFactory.getLogger(Spiders.class);

    @Resource(name = "csdnDbPipeline")
    private Pipeline csdnDbPipeline;
    @Resource(name = "csdnPageProcessor")
    private PageProcessor csdnPageProcessor;


    /**
     * @param threadNums  开启的线程数
     * @param asynchronus 是否异步
     */
    public Spider csdnSpider(boolean asynchronus, int threadNums) throws Exception {
        if (threadNums <= 0)
            throw new Exception("线程数必须大于0");
        Spider spider = Spider.create(csdnPageProcessor)     //url选择器
                .setScheduler(new FileCacheQueueScheduler(SpiderUtils.getFileCachePath())  //url队列
                        .setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)) //去重器
                )
                .addPipeline(csdnDbPipeline) //持久器
                .addUrl(Constant.CSDN_SEEDS_URL)
                .thread(threadNums);

        if (asynchronus)
            spider.start();
        else {
            spider.run();
        }
        return spider;
    }

}
