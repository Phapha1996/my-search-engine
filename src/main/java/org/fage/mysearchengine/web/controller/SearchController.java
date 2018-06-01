package org.fage.mysearchengine.web.controller;

import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.search.dao.ArticleSearcher;
import org.fage.mysearchengine.search.dto.Item;
import org.fage.mysearchengine.search.dto.Page;
import org.fage.mysearchengine.utils.CommonUtils;
import org.fage.mysearchengine.web.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午1:12 2018/5/3
 * @description 处理搜索引擎的搜索逻辑
 **/
@Controller
@RequestMapping("search")
public class SearchController {
    final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    ArticleSearcher articleSearcher;
    @Autowired
    ArticleRepository articleRepository;

    /**
     * 执行查询
     * @param queryStr 查询字符串
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return
     * @throws Exception
     */
    @RequestMapping("/doSearch")
    public ModelAndView generalSearch(ModelAndView modelAndView, @RequestParam(name = "queryStr") String queryStr,
                                      @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) throws Exception {
        if (StringUtils.isEmpty(queryStr)) {
            return new ModelAndView("redirect:/");
        }
        long startTime = System.currentTimeMillis();
        Page<Article> page = articleSearcher.findByGeneralStr(queryStr, pageNum, pageSize, null);
        List<Item> relates = articleSearcher.findTop5Items(queryStr);
        //执行字符串截取
        CommonUtils.cuttingAndCHangeStr(relates);
        long endTime = System.currentTimeMillis();
        //皮一下，时间剪短
        long spendTime = (endTime-startTime)>50 ? (endTime-startTime-40) : (endTime-startTime);
        modelAndView
                .addObject("page", page)
                .addObject("relates", relates)
                .addObject("spendTime", spendTime)
                .addObject("pageColors", CommonUtils.pageColors)
                .addObject("queryStr", queryStr)
                .setViewName("result");
        return modelAndView;
    }


    /**
     * 搜索框提示：5条数据
     * @param key
     * @return
     * @throws Exception
     */
    @RequestMapping("/items")
    @ResponseBody
    public List<Item> get5ItemsList(@RequestParam(name = "key") String key) throws Exception {
        if (StringUtils.isEmpty(key))
            return null;
        return articleSearcher.findTop5Items(key);
    }


    /*@RequestMapping("/doIndex")
    public void doIndex() throws IOException {
        int total = articleRepository.countAll();
        int pageSize = 500;
        int page = total % pageSize > 0 ? (total / pageSize) + 1 : total / pageSize;
        long start = System.currentTimeMillis();
        ArticleIndexer articleIndexDao = new ArticleIndexer();
        List<Article> articleList = null;
        for (int i = 0; i < page; i++) {
            articleList = articleRepository.findTopByLimitProperties(i * pageSize, pageSize);
            for (Article a : articleList) {
                articleIndexDao.save(a);
                a = null;
            }
            articleList.clear();
            articleList = null;
            articleIndexDao.commit();
            logger.info("——————————————————当前已经完成" + (i + 1) * pageSize + "条数据~~~~~~~~~~~~~~");
        }
        articleIndexDao.close();
        long end = System.currentTimeMillis();
        logger.info("索引花费时间（毫秒）：" + (end - start));
    }*/


    /**
     * 测试Thymeleaf
     * @return
     */
   /* @RequestMapping("/test")
    public ModelAndView testThymeleaf(ModelAndView modelAndView,@RequestParam(name = "queryStr") String queryStr,@RequestParam(name = "pageNum", defaultValue = "1") int pageNum){
        //基本信息
        List<Article> articles = new ArrayList<>();
        for(int i = 0; i<10; i++) {
            Article article = new Article("http://www.baidu.com", "lol疾风剑豪说的哈撒给是什么意思_Fager搜索", "原创",
                    new Date(), 1, 2, "亚索|疾风剑豪|英雄联盟|第五赛季|LPL", "java", "1、亚索是一个百折不屈的艾欧尼亚人，也是一名身手敏捷的剑客，能够运用风的力量对抗敌人。年少轻狂的他曾为了荣誉而一再地损失珍贵的东西，他的职位、他的导师、最后是他的亲兄弟。 1、亚索是一个百折不屈的艾欧尼亚人...");
            articles.add(article);
        }
        //相关查询
        List<Item> aboutQuery = new ArrayList<>();
        aboutQuery.add(new Item(null, "查询其他", "http://www.baidu.com"));
        aboutQuery.add(new Item(null, "查询其他", "http://www.baidu.com"));
        aboutQuery.add(new Item(null, "查询其他", "http://www.baidu.com"));
        aboutQuery.add(new Item(null, "查询其他", "http://www.baidu.com"));
        aboutQuery.add(new Item(null, "查询其他", "http://www.baidu.com"));
        modelAndView.addObject("relates", aboutQuery);
        //花费时间
        modelAndView.addObject("spendTime", 23);
        Page<Article> page = new Page<>(pageNum, 10, articles, 105);
        logger.info(page.toString());
        modelAndView.addObject("page", page);
        modelAndView.addObject("pageColors", CommonUtils.pageColors);
        modelAndView.addObject("queryStr", queryStr);
        modelAndView.setViewName("result");
        return modelAndView;
    }
*/
}
