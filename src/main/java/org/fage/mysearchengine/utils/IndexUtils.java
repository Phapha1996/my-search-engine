package org.fage.mysearchengine.utils;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.fage.mysearchengine.search.analizer.MyIkAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午3:25 2018/5/12
 * @description 主要管理Seacher
 **/
public class IndexUtils {
    private static Analyzer analyzer = null;
    private static String indexfileName = null;
    private static IndexReader indexReader = null;
    private static Formatter formatter  = null;

    static {
        analyzer  = new MyIkAnalyzer();
        indexfileName = System.getProperty("user.dir") + "/res/indexes/article_index";
        //高亮显示使用<em></em>标签
        formatter = new SimpleHTMLFormatter("<em>", "</em>");
    }

    /**
     * 获取单个indexReader实例
     * @return
     */
    public static IndexReader getIndexReader(){
        if(null == indexReader){
            try {
                Directory directory = FSDirectory.open(Paths.get(indexfileName));
                indexReader = DirectoryReader.open(directory);
                return indexReader;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return indexReader;
    }

    /**
     * 获取parser
     * @return
     */
    public static StandardQueryParser getStandardQueryParser(){
        return new StandardQueryParser(analyzer);
    }

    public static void closeReader(){
        if(null != indexReader){
            try {
                indexReader.close();
                indexReader = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 高亮
     * @param query 查询语句
     * @param fieldName 域名
     * @param fieldContent 域内容
     * @param fragmentSize 结果的长度（不含html标签长度）
     * @return
     * @throws IOException
     * @throws InvalidTokenOffsetsException
     */
    public static String displayHtmlHighlight(Query query, String fieldName, String fieldContent, int fragmentSize) throws IOException, InvalidTokenOffsetsException {
        //query里面的条件，条件里面有搜索关键词
        Scorer fragmentScorer = new QueryScorer(query);
        //构建高亮插寻
        Highlighter highlighter  = new Highlighter(formatter, fragmentScorer);
        Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
        highlighter.setTextFragmenter(fragmenter);
        return highlighter.getBestFragment(analyzer, fieldName, fieldContent);
    }


    public static Analyzer getAnalyzer() {
        return analyzer;
    }

    public static String getIndexfileName() {
        return indexfileName;
    }

    public static void setAnalyzer(Analyzer analyzer) {
        IndexUtils.analyzer = analyzer;
    }

    public static void setIndexfileName(String indexfileName) {
        IndexUtils.indexfileName = indexfileName;
    }
}
