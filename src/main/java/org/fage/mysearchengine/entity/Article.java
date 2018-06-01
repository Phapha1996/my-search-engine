package org.fage.mysearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午3:50 2018/5/5
 * @description 博客页
 **/
@Data
@Entity
@Table(name = "t_article")
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String blogUrl;
    private String title;   //文章标题
    private String type;    //文章类型（转载或原创）
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;      //最后修改时间
    private Integer praise;   //被赞数量
    private Integer readNumber; //阅读数量
    private String tags;        //文章标签（重要）
    private String categories;  //博主对文章的个人分类（还可以拆分出分类对象）
    private String content;     //文章内容(还可以拆分出内容对象，如h1，h2这些标签，粗标签一般都比较重要)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Article(String blogUrl, String title, String type, Date time, Integer praise, Integer readNumber, String tags, String categories, String content) {
        this.blogUrl = blogUrl;
        this.title = title;
        this.type = type;
        this.time = time;
        this.praise = praise;
        this.readNumber = readNumber;
        this.tags = tags;
        this.categories = categories;
        this.content = content;
    }
}
