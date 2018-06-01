package org.fage.mysearchengine.spider.common;

import lombok.Data;

/**
 * @author Caizhf
 * @version 1.0
 * @date 上午11:19 2018/5/5
 * @description CSDN博客分页信息
 **/
@Data
public class CSDNBlogPagingInfo {
    //当前第几页（可以爬到）
    private Integer number;
    //每页的记录数大小(可以爬到,默认20条)
    private Integer size = 20;
    //记录总数（可以爬到）
    private Integer totalElements;
    //总页数（需要自己计算）
    private Integer totalPages;

    public CSDNBlogPagingInfo(Integer number, Integer size, Integer totalElements) {
        this.number = number;
        if(size!=null)
            this.size = size;
        this.totalElements = totalElements;
        //总页数计算方法
        totalPages = this.totalElements%this.size>0 ? (this.totalElements/this.size+1) : this.totalElements/this.size;
    }
}
