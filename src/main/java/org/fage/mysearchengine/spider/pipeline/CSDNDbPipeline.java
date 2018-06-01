package org.fage.mysearchengine.spider.pipeline;

import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.entity.User;
import org.fage.mysearchengine.web.repository.ArticleRepository;
import org.fage.mysearchengine.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.transaction.Transactional;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午4:03 2018/5/4
 * @description
 **/
@Component("csdnDbPipeline")
public class CSDNDbPipeline implements Pipeline {

    final Logger logger = LoggerFactory.getLogger(CSDNDbPipeline.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        //如果是my.csdn.net/xx该处应该为true
        Boolean isMyPage = resultItems.get("isMyPage");
        //如果是https://blog.csdn.net/xx/article/details/80160465该处应该为true
        Boolean isBlogPage = resultItems.get("isArticlePage");

        if(null!=isMyPage && isMyPage){
            doMyProcess(resultItems);
        }else if(null!=isBlogPage && isBlogPage){
            doBlogProcess(resultItems);
        }
    }

    //执行my页面逻辑，插入用户
    void doMyProcess(ResultItems resultItems){
        User user = resultItems.get("user");
        userRepository.save(user);
    }

    //执行文章页面逻辑，插入文章
    @Transactional
    void doBlogProcess(ResultItems resultItems){
        Article article = resultItems.get("article");
        String username = resultItems.get("username");

        Integer userId = userRepository.findIdByUsername(username);
        //插入文章页
        if(null!=userId){
            article.setUser(new User(userId));
            articleRepository.save(article);
        }
    }

}
