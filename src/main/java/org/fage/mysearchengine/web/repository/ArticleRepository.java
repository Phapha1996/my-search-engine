package org.fage.mysearchengine.web.repository;

import org.fage.mysearchengine.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午9:11 2018/5/5
 * @description
 **/
@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>{

    //测试方法，1000条数据
    @Query(value = "select a.* from t_article a limit :start,:size",nativeQuery = true)
    List<Article> findTopByLimitProperties(@Param("start") int start, @Param("size") int size);

    @Query(value = "select count(*) from t_article ", nativeQuery = true)
    int countAll();
}
