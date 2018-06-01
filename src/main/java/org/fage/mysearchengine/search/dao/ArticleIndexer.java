package org.fage.mysearchengine.search.dao;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.utils.CommonUtils;
import org.fage.mysearchengine.utils.IndexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午10:11 2018/5/6
 * @description 文章索引增删改操作
 **/
public class ArticleIndexer {
    final Logger logger = LoggerFactory.getLogger(ArticleIndexer.class);

    private IndexWriter indexWriter;


    /**
     * 初始化IndexWriter
     */
    public ArticleIndexer() {
        try {
            Directory directory = FSDirectory.open(Paths.get(IndexUtils.getIndexfileName()));
            //中文分词器
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(IndexUtils.getAnalyzer());
            //优化索引
            LogDocMergePolicy mergePolicy  = new LogDocMergePolicy();
            //达到5个文件时就和合并
            mergePolicy.setMergeFactor(5);
            indexWriterConfig.setMergePolicy(mergePolicy);
            indexWriterConfig.setUseCompoundFile(true);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (null != indexWriter && indexWriter.isOpen()) {
                indexWriter.close();
                indexWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commit(){
        if(null != indexWriter){
            try {
                indexWriter.flush();
                indexWriter.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //存储
    public void save(Article article) throws IOException {
//        logger.info("开始存储——————>" + article.getTitle() + " ...");
        Document document = getDocument(article);
        indexWriter.addDocument(document);
//        logger.info("存储成功——————>" + article.getTitle() + " !");
    }

    //修改
    public void update(Article article) throws IOException {
        logger.info("开始更新——————>" + article.getTitle() + " ...");
        IndexReader reader = DirectoryReader.open(indexWriter);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("id",article.getId().toString())),1);
        //如果在索引中没找到，那么就直接存储
        if(topDocs.totalHits==0) {
            logger.info("转向存储逻辑 ...");
            save(article);
        }
        else {
            Document document = getDocument(article);
            indexWriter.updateDocument(new Term("id",article.getId().toString()),document);
            logger.info("更新成功——————>" + article.getTitle() + " ...");
        }
        reader.close();
    }


    private Document getDocument(Article article){
        if(article == null || StringUtils.isEmpty(article.getId())) {
            throw new NullPointerException("文章对象不能为空");
        }
        Document document = new Document();
        //id;
        document.add(new StringField("id", String.valueOf(article.getId()), Field.Store.YES));
        //blogUrl 文章url
        document.add(new StringField("blogUrl", article.getBlogUrl(), Field.Store.YES));
        //title   文章标题
        document.add(new TextField("title", article.getTitle(), Field.Store.YES));
        //type    文章类型（转载或原创）
        if(!StringUtils.isEmpty(article.getType()))
            document.add(new StringField("type", article.getType(), Field.Store.YES));
        //time    最后修改时间
        document.add(new LongPoint("time", Long.parseLong(DateTools.dateToString(article.getTime(), DateTools.Resolution.HOUR))));
        document.add(new StoredField("time", Long.parseLong(DateTools.dateToString(article.getTime(), DateTools.Resolution.HOUR))));
        document.add(new NumericDocValuesField("time", Long.parseLong(DateTools.dateToString(article.getTime(), DateTools.Resolution.HOUR))));
        //praise  被赞数量
        document.add(new IntPoint("praise", article.getPraise()));
        document.add(new StoredField("praise", article.getPraise()));
        document.add(new NumericDocValuesField("praise", article.getPraise()));
        //readNumber 阅读数量
        document.add(new IntPoint("readNumber", article.getReadNumber()));
        document.add(new StoredField("readNumber", article.getReadNumber()));
        document.add(new NumericDocValuesField("readNumber", article.getReadNumber()));
        //tags        文章标签（重要）
        if (!StringUtils.isEmpty(article.getTags()))
            document.add(new TextField("tags", article.getTags(), Field.Store.YES));
        //categories  博主对文章的个人分类（还可以拆分出分类对象）
        if (!StringUtils.isEmpty(article.getCategories()))
            document.add(new TextField("categories", article.getCategories(), Field.Store.YES));
        //content     文章所有内容,不存储
        FieldType contentType = new FieldType();
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);//向量详细存储信息，文档包含索引位置和词频，这是一个典型的默认为全文搜索:全部启用评分和位置查询的支持.
        contentType.setStored(false);   //是否在索引中存储
        contentType.setStoreTermVectors(true);  //向量存储选项
        contentType.setTokenized(true); //是否分词
        String content = article.getContent();
        if(content == null)
            content = "";
        document.add(new Field("content", content, contentType));
        //文章截取150字符，去除空白类字符
        //去除所有的html标签，以免影响显示
        content = CommonUtils.cuttingAndChangeContent(content);
        document.add(new StoredField("content", content));
        return document;
    }

    //删除
    public void delete(Integer id) throws IOException {
        if(null == id)
            throw new NullPointerException("id不能为空");
        Term term = new Term("id", String.valueOf(id));
        indexWriter.deleteDocuments(term);
        indexWriter.forceMergeDeletes();//强制删除
    }

    //删除全部
    public void deleteAll() throws IOException {
        indexWriter.deleteAll();
    }


}
