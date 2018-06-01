package org.fage.mysearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午10:12 2018/5/30
 * @description 索引状态,用来记录定时任务索引过文章的状态
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_index_status")
public class IndexStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "last_update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateTime;    //最后更新时间
    private Integer indexCount;        //索引数量

    public IndexStatus(Integer indexCount) {
        this.indexCount = indexCount;
    }
}
