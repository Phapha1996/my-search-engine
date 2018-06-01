package org.fage.mysearchengine.search.dao;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.search.dto.Item;
import org.fage.mysearchengine.search.dto.Page;
import org.fage.mysearchengine.utils.CommonUtils;
import org.fage.mysearchengine.utils.IndexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午11:06 2018/5/11
 * @description 文章索引查询操作
 **/
@Component
//@Scope("prototype")
public class ArticleSearcher {

    final Logger logger = LoggerFactory.getLogger(ArticleSearcher.class);


    /**
     * 根据用户的字串常规分页查询
     * @param generalStr
     * @param pageNum
     * @param pageSize
     * @param fields
     * @return
     * @throws Exception
     */
    public Page<Article> findByGeneralStr(String generalStr, int pageNum, int pageSize, String[] fields) throws Exception {
        if(null==fields || fields.length==0){
            fields = new String[]{"title", "tags", "content"};
        }
        IndexSearcher indexSearcher = new IndexSearcher(IndexUtils.getIndexReader());
        //对lucene特殊字符转义
        generalStr = QueryParserBase.escape(generalStr);
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, IndexUtils.getAnalyzer());
        Query query = queryParser.parse(generalStr);

        TopDocs topDocs = null;
        //处理第一页与其他页的分歧
        if(pageNum>1) {
            ScoreDoc lastScoreDoc = getLastScoreDoc(pageNum, pageSize, query, indexSearcher);
            topDocs = indexSearcher.searchAfter(lastScoreDoc, query, pageSize);
            if(null == lastScoreDoc)
                return new Page(pageNum,pageSize,null,topDocs.totalHits);
        }else {
            topDocs = indexSearcher.searchAfter(null, query, pageSize);
        }

        List<Article> results = new ArrayList<>();
        for(ScoreDoc hit : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(hit.doc);
            results.add(getFromDocument(document, query));
        }
//        IndexUtils.closeReader();
        return new Page<Article>(pageNum, pageSize, results,topDocs.totalHits);
    }

    /**
     * 搜索框响应事件给予5条搜索数据
     * @param key
     * @return
     */
    public List<Item> findTop5Items(String key) throws Exception {
        IndexSearcher indexSearcher = new IndexSearcher(IndexUtils.getIndexReader());
        //对lucene特殊字符转义
        key = QueryParserBase.escape(key);
        StandardQueryParser queryParser = new StandardQueryParser(IndexUtils.getAnalyzer());
        queryParser.setAllowLeadingWildcard(true);
        Query query = queryParser.parse("*" + key + "*","title");
//        logger.info(query.toString());
        TopDocs topDocs = indexSearcher.search(query, 5);
        List<Item> results = new ArrayList<>();
        for(ScoreDoc hit : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(hit.doc);
            results.add(new Item(CommonUtils.cuttingAndChangeStr(document.get("tags"), "tags"),
                    CommonUtils.cuttingAndChangeStr(document.get("title"), "title"), document.get("blogUrl")));
        }
        return results;
    }
   /* *//**
     * 根据开发者用户的lucene查询语句查询
     * @param queryStr
     * @param pageNum
     * @param pageSize
     * @return
     *//*
    public Page<Article> findByLuceneQueryStr(String queryStr, int pageNum, int pageSize){
        return null;
    }*/


    /**
     * 根据页码和分页大小获取上一次的最后一个scoredocs
     * @param pageNum
     * @param pageSize
     * @param query
     * @param searcher
     * @return
     * @throws IOException
     */
    private ScoreDoc getLastScoreDoc(int pageNum, int pageSize, Query query, IndexSearcher searcher) throws IOException {
        int num = pageSize*(pageNum-1);//获取上一页的最后数量
        TopDocs tds = searcher.search(query, num);
        if(tds.totalHits>=num)
            return tds.scoreDocs[num-1];
        return null;
    }

    /**
     * 封装高亮后的字段返回
     * @param document
     * @return
     */
    private Article getFromDocument(Document document, Query query) throws IOException, InvalidTokenOffsetsException {
        Article article = new Article();
//        article.setId(Integer.parseInt(document.get("id")));
        String title = document.get("title");
        String emTitle = IndexUtils.displayHtmlHighlight(query, "title", title, 30);
        article.setTitle(emTitle==null ? title : emTitle);
        article.setBlogUrl(document.get("blogUrl"));
//        article.setType(document.get("type"));
        article.setTime(CommonUtils.getDateFromyyyyMMddHH(document.get("time").toString()));
//        article.setPraise(Integer.parseInt(document.get("praise")));
//        article.setReadNumber(Integer.parseInt(document.get("readNumber")));
        article.setTags(document.get("tags"));
        article.setCategories(document.get("categories"));
        String content = document.get("content");
        String emContent = IndexUtils.displayHtmlHighlight(query, "content", content, 150);
        article.setContent(emContent==null ? content : emContent);
        return article;
    }


}
