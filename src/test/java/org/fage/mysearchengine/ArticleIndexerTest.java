package org.fage.mysearchengine;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.search.analizer.MyIkAnalyzer;
import org.fage.mysearchengine.search.dto.Item;
import org.fage.mysearchengine.search.dto.Page;
import org.fage.mysearchengine.search.dao.ArticleIndexer;
import org.fage.mysearchengine.search.dao.ArticleSearcher;
import org.fage.mysearchengine.utils.IndexUtils;
import org.fage.mysearchengine.web.repository.ArticleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午4:49 2018/5/7
 * @description
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleIndexerTest {

    final Logger logger = LoggerFactory.getLogger(ArticleIndexerTest.class);
    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void testCreate() throws IOException {
        Article article = new Article();
        article.setId(1);
        article.setBlogUrl("https://blog.csdn.net/crazyhacking/article/details/8169702");
        article.setTitle("java学习攻略java学习攻略java学习攻略java学习攻略java学习攻略java学习攻略java学习攻略java学习攻略");
        article.setType("");
        article.setTime(new Date());
        article.setPraise(11);
        article.setReadNumber(2006);
        article.setTags(null);
        article.setCategories(null);
        article.setContent("\n" +
                "最近需要速成java，制定一个学习计划。\n" +
                "\n"+
                "                           "+
                "1基本语法\n" +
                "一篇博文搞定：http://blog.csdn.net/crazyhacking/article/details/8144622 \n" +
                "2多线程\n" +
                "3网络通信socket编程\n" +
                "non block socket\n" +
                "4 设计模式\n" +
                "不会的可以查阅jdk手册\n" +
                "先从java的基础学起吧，既然楼主学过c++,java就很好学了。 sun公司的《java2入门经典》，适合java的初学者 太厚 不看 \n" +
                "然后是《java2核心技术》，在有一定基础的时候来看 也就是core java 最后一定要看《java编程思想》也就是think in java \n" +
                "。这本书似乎不怎么样 然后，个人觉得还是学习java比较好，毕竟它的跨平台性是别的语言所不能比拟的 \n" +
                "JAVA的底层就是C++来的啊 所以的话。 Java 方面的书去看下 IO流 SQL的操作 那些集合 Map 啊。 set list vector \n" +
                "。主要就是 IO包。util包 SQL包 这三个包下常用的类 JAVA的看完后那么就开始看 《jsp开发应用与详解》这本书 \n" +
                "然后自己做登陆的例子。然后在上面增加功能 也就是增删改查的4个功能 然后再看下 Struts + Hibernate Spring 的话先别看 Struts \n" +
                "详细看下。Hibernate 搞清楚怎么写HQL语句就可以了，日你妈怎么写配置文件就可以了 基本上只能这个样子了 这就是最速成的方法了 自己试试吧 \n" +
                "\n");
        ArticleIndexer articleIndexer = new ArticleIndexer();
        articleIndexer.save(article);
        articleIndexer.close();
    }


    @Test
    public void testDeleteAll() throws IOException {
        ArticleIndexer dao =  new ArticleIndexer();
        dao.deleteAll();
        dao.close();
    }



    @Test
    public void testInsertIndexFromDB() throws IOException {
        int total = articleRepository.countAll();
        int pageSize = 2000;
        int page = total%pageSize>0 ? (total/pageSize)+1 : total/pageSize;
        long start = System.currentTimeMillis();
        ArticleIndexer articleIndexDao = new ArticleIndexer();
        for(int i = 0 ; i<page ; i++) {
            List<Article> articleList = articleRepository.findTopByLimitProperties(i*pageSize, pageSize);
            for(Article a : articleList){
                articleIndexDao.save(a);
            }
        }
        articleIndexDao.close();
        long end = System.currentTimeMillis();
        logger.info("索引花费时间（毫秒）：" + (end-start));
    }


    //测试简单的词查询
    @Test
    public void testSimpleSearch() throws IOException {
        Directory d = FSDirectory.open(Paths.get(IndexUtils.getIndexfileName()));
        IndexReader indexReader = DirectoryReader.open(d);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = new TermQuery(new Term("title", "java"));

        long start = System.currentTimeMillis();
        TopDocs topDocs = indexSearcher.search(query, 10);

        ScoreDoc[] scoreDoc =topDocs.scoreDocs;
        for(ScoreDoc hit : scoreDoc){
            Document document = indexSearcher.doc(hit.doc);
            logger.info("查询到结果------>");
            logger.info(document.get("id"));
            logger.info(document.get("title"));
            logger.info(document.get("blogUrl"));
            logger.info("文档分数：" + hit.score);
        }
        logger.info("最大评分：" + topDocs.getMaxScore());
        logger.info("总共有" + topDocs.totalHits + "条数据");
        long end = System.currentTimeMillis();

        logger.info("检索花费时间（毫秒）：" + (end-start));
    }


    //测试布尔查询
    @Test
    public void testBoolSearch() throws IOException, ParseException {
        Directory d = FSDirectory.open(Paths.get("./indexes/article_index"));
        IndexReader indexReader = DirectoryReader.open(d);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //查询title域
        QueryParser queryParser = new QueryParser("title", new MyIkAnalyzer());
        Query linuxQuery = queryParser.parse("SpringBoot框架");
        //查询content域
        queryParser = new QueryParser("content", new MyIkAnalyzer());
        Query javaQuery = queryParser.parse("SpringBoot框架 +readNumber:{500 TO 1000}");
        //构建布尔查询，must是合取，NOT_MUST是必须不包含，Should是析取
        BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        boolQuery.add(linuxQuery, BooleanClause.Occur.SHOULD);
        boolQuery.add(javaQuery, BooleanClause.Occur.SHOULD);
        //设置匹配至少拥有1项条件以上的查询需求，这个1就是下面方法中的参数
//        boolQuery.setMinimumNumberShouldMatch(1);
        Query query = boolQuery.build();

        logger.info(query.toString());

        TopDocs topDocs = indexSearcher.search(query, 15);
        ScoreDoc[] scoreDoc =topDocs.scoreDocs;
        for(ScoreDoc hit : scoreDoc){
            Document document = indexSearcher.doc(hit.doc);
            logger.info("查询到结果------>");
            logger.info(document.get("id"));
            logger.info("标题：" + document.get("title"));
            logger.info("地址：" + document.get("blogUrl"));
            logger.info("阅读数量" + document.get("readNumber"));
            logger.info("被赞数量" + document.get("praise"));
            logger.info("发帖时间：" + document.get("time"));
            logger.info("文章关键字：" + document.get("tags"));
            logger.info("文档分数：" + hit.score);
        }
        logger.info("文档总数" + topDocs.totalHits);
        indexReader.close();
        d.close();
    }

    //测试范围查询与排序
    @Test
    public void testRangeQuery() throws IOException, ParseException, QueryNodeException {
        Directory d = FSDirectory.open(Paths.get("./indexes/article_index"));
        IndexReader indexReader = DirectoryReader.open(d);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

//        Query query = IntPoint.newRangeQuery("praise", 500, 1000);

        StandardQueryParser queryParser = new StandardQueryParser(new MyIkAnalyzer());
        queryParser.setAllowLeadingWildcard(true);
        Query query = queryParser.parse("title:[j TO m]", "praise");
        //true为倒序
        Sort sort = new Sort(new SortField("praise", SortField.Type.INT,true));

        long start = System.currentTimeMillis();
        TopDocs topDocs = indexSearcher.search(query, 10, sort);
        logger.info(query.toString());
        ScoreDoc[] scoreDoc =topDocs.scoreDocs;
        for(ScoreDoc hit : scoreDoc){
            Document document = indexSearcher.doc(hit.doc);
            logger.info(document.get("id"));
            logger.info("标题：" + document.get("title"));
            logger.info("地址：" + document.get("blogUrl"));
            logger.info("阅读数量" + document.get("readNumber"));
            logger.info("被赞数量" + document.get("praise"));
            logger.info("发帖时间：" + document.get("time"));
            logger.info("文章关键字：" + document.get("tags"));
            logger.info("文档分数：" + hit.score);
        }
        logger.info("最大评分：" + topDocs.getMaxScore());
        logger.info("总共有" + topDocs.totalHits + "条数据");
        long end = System.currentTimeMillis();

        logger.info("检索花费时间（毫秒）：" + (end-start));
    }

    @Test
    public void testMultiFieldsQuery() throws IOException, ParseException {
        Directory d = FSDirectory.open(Paths.get("./indexes/article_index"));
        IndexReader indexReader = DirectoryReader.open(d);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "tags", "content"}, IndexUtils.getAnalyzer());
//        queryParser.setAutoGeneratePhraseQueries(true);
//        queryParser.setPhraseSlop(3);
        Query query = queryParser.parse("Ja*");
        long start = System.currentTimeMillis();
        TopDocs topDocs = indexSearcher.search(query, 10);
        logger.info(query.toString());
        ScoreDoc[] scoreDoc =topDocs.scoreDocs;
        for(ScoreDoc hit : scoreDoc){
            Document document = indexSearcher.doc(hit.doc);
            logger.info(document.get("id"));
            logger.info("标题：" + document.get("title"));
            logger.info("地址：" + document.get("blogUrl"));
            logger.info("阅读数量" + document.get("readNumber"));
            logger.info("被赞数量" + document.get("praise"));
            logger.info("发帖时间：" + document.get("time"));
            logger.info("文章关键字：" + document.get("tags"));
            logger.info("文档分数：" + hit.score);
        }
        logger.info("最大评分：" + topDocs.getMaxScore());
        logger.info("总共有" + topDocs.totalHits + "条数据");
        long end = System.currentTimeMillis();

        logger.info("检索花费时间（毫秒）：" + (end-start));
    }

    @Test
    public void testArticleSearcherFindByGeneralStr() throws Exception {
        ArticleSearcher articleSearcher = new ArticleSearcher();
        long start = System.currentTimeMillis();
        Page page = articleSearcher.findByGeneralStr("日你妈",1, 10, null);
        logger.info(page.getItems().toString());
        long end = System.currentTimeMillis();
        logger.info("检索花费时间（毫秒）：" + (end-start));
    }

    @Test
    public void test() throws Exception {
        ArticleSearcher articleSearcher = new ArticleSearcher();
        List<Item> list = articleSearcher.findTop5Items("java");
        logger.info(list.size()+"条数据");
        for (Item item : list){
            logger.info(item.toString());
        }
    }

    @Test
    public void countAll(){
        articleRepository.countAll();
    }
}
