package org.fage.mysearchengine.search.analizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.IOUtils;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午3:14 2018/5/7
 * @description
 **/
public class MyIkAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String arg0) {
        Reader reader=null;
        try{
            reader=new StringReader(arg0);
            MyIKTokenizer it = new MyIKTokenizer(reader);
            return new Analyzer.TokenStreamComponents(it);
        }finally {
            IOUtils.closeWhileHandlingException(reader);
        }
    }

}
