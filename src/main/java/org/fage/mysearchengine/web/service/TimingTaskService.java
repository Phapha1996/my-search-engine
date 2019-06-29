package org.fage.mysearchengine.web.service;

import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.entity.IndexStatus;
import org.fage.mysearchengine.search.dao.ArticleIndexer;
import org.fage.mysearchengine.web.repository.ArticleRepository;
import org.fage.mysearchengine.web.repository.IndexStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午5:33 2018/5/22
 * @description 定时任务处理
 * 定时任务表达式
 *       秒	 	        0-59	 	        , - * /
 *       分	 	        0-59	 	        , - * /
 *       小时	 	    0-23	 	        , - * /
 *       日期	 	    1-31	 	        , - *   / L W C
 *       月份	 	    1-12 或者 JAN-DEC	, - * /
 *       星期	 	    1-7 或者 SUN-SAT	 	, - *   / L C #
 *       年（可选）	 	留空, 1970-2099	 	, - * /
 *       5 * * * * ？
 *
 **/
@Component
public class TimingTaskService {
    final Logger logger = LoggerFactory.getLogger(TimingTaskService.class);
    /**
     * 每次500条
     */
    final int pageSize = 500;
    /**
     * 每次上限5万条
     */
    final int maxSave = 50000;

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    IndexStatusRepository indexStatusRepository;

    /**
     * 写索引定时任务
     * 每小时分钟异步触发一次写索引执行，每次执行5万条
     */
//    @Scheduled(cron = "0 */20 * * * ?")
//    public void indexTask() throws IOException {
//        logger.info("——————————>开始执行索引数据...");
//        int articleCount = articleRepository.countAll();
//        IndexStatus indexStatus = indexStatusRepository.findOne(1);
//        if(null == indexStatus){
//            indexStatus = indexStatusRepository.save(new IndexStatus(0));
//        }
//        //索引数据总数
//        int indexCountResult = indexStatus.getIndexCount();
//        //如果文章总数为0或者索引数量与文章总数相等，就不执行方法
//        if(articleCount == 0 || indexCountResult == articleCount)
//            return ;
//
//        long start = System.currentTimeMillis();
//        ArticleIndexer articleIndexDao = new ArticleIndexer();
//        List<Article> articleList = null;
//        //计数而已的变量
//        int indexCount = 0;
//        for(int i=0; i<maxSave/pageSize ; i++){
//            articleList = articleRepository.findTopByLimitProperties(indexCountResult, pageSize);
//            if(null==articleList || articleList.size()==0) {
//                break;
//            }
//            for(Article article : articleList) {
//                articleIndexDao.save(article);
//            }
//            indexCountResult += articleList.size();
//            indexCount += articleList.size();
//            articleIndexDao.commit();
//            articleList.clear();
//        }
//        //更新索引计数状态
//        indexStatus.setIndexCount(indexCountResult);
//        indexStatusRepository.save(indexStatus);
//        articleIndexDao.close();
//        long end = System.currentTimeMillis();
//        logger.info("——————————>索引花费时间：" + (end - start) + "毫秒 ,本次索引数据量：" + indexCount);
//        System.gc();
//    }



    /**
     * 爬虫抓取定时任务
     * 每周星期三凌晨3点钟触发
     *//*
    @Scheduled(cron = "0 0 3 * * 4")
    public void spiderTask(){
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

    *//**
     * 爬虫数据更新抓取定时任务
     * 每周星期天凌晨2点钟触发
     *//*
    @Scheduled(cron = "0 0 2 * * 1")
    public void spiderRefreshTask(){

    }*/

//    @Scheduled(fixedRate = 5000)
//    public void spiderRefreshTask(){
//        logger.info("定时任务被触发了");
//
//    }
}
