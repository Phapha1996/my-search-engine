package org.fage.mysearchengine.search.dto;

import lombok.Data;
import org.apache.lucene.search.ScoreDoc;

import java.util.Collection;

/**
 * lucene分页两种方式的基础文章参考：https://blog.csdn.net/hu948162999/article/details/41209699
 * page出处：https://yq.aliyun.com/articles/45314
 *
 * @param <T>
 */
@Data
public class Page<T> {
    /**
     * 当前第几页(从1开始计算)
     */
    private int currentPage;
    /**
     * 每页显示几条
     */
    private int pageSize;
    /**
     * 总记录数
     */
    private int totalRecord;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 分页数据集合[用泛型T来限定集合元素类型]
     */
    private Collection<T> items;
    /**
     * 当前显示起始索引(从零开始计算)
     */
    private int startIndex;
    /**
     * 当前显示结束索引(从零开始计算)
     */
    private int endIndex;
    /**
     * 一组最多显示几个页码[比如Google一组最多显示10个页码]
     */
    private int groupSize;

    /**
     * 左边偏移量
     */
    private int leftOffset = 5;
    /**
     * 右边偏移量
     */
    private int rightOffset = 4;
    /**
     * 当前页码范围
     */
    private String[] pageRange;

    /**
     * 上一页最后一个ScoreDoc对象
     */
    private ScoreDoc afterDoc;

    public Page(int currentPage, int pageSize, Collection<T> items, int totalRecord) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.items = items;
        this.totalRecord = totalRecord;
        setRangeIndex();
    }


    private void setRangeIndex() {
        //获取组范围大小
        int groupSize = getGroupSize();
        //获取总页数
        int totalPage = getTotalPage();
        if (totalPage < 2) {
            startIndex = 0;
            endIndex = totalPage - startIndex;
        } else {
            int currentPage = getCurrentPage();
            if (groupSize >= totalPage) {
                startIndex = 0;
                endIndex = totalPage - startIndex - 1;
            } else {
                //5
                int leftOffset = getLeftOffset();
                //6
                int middleOffset = getMiddleOffset();
                if (-1 == middleOffset) {
                    startIndex = 0;
                    endIndex = groupSize - 1;
                } else if (currentPage <= leftOffset) {
                    startIndex = 0;
                    endIndex = groupSize - 1;
                } else {
                    startIndex = currentPage - leftOffset - 1;
                    if (currentPage + rightOffset > totalPage) {
                        endIndex = totalPage - 1;
                    } else {
                        endIndex = currentPage + rightOffset - 1;
                    }
                }
            }
        }
    }

    public int getCurrentPage() {
        //如果超前，那就显示第一页
        if (currentPage <= 0) {
            currentPage = 1;
        } else {
            //如果超后就显示最后一页
            int totalPage = getTotalPage();
            if (totalPage > 0 && currentPage > getTotalPage()) {
                currentPage = totalPage;
            }
        }
        //如果都没超，就正常返回
        return currentPage;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public int getTotalPage() {
        int totalRecord = getTotalRecord();
        if (totalRecord == 0) {
            totalPage = 0;
        } else {
            int pageSize = getPageSize();
            totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : (totalRecord / pageSize) + 1;
        }
        return totalPage;
    }

    public int getGroupSize() {
        if (groupSize <= 0) {
            groupSize = 10;
        }
        return groupSize;
    }

    public int getLeftOffset() {
        leftOffset = getGroupSize() / 2;
        return leftOffset;

    }

    public int getRightOffset() {
        int groupSize = getGroupSize();
        if (groupSize % 2 == 0) {
            rightOffset = (groupSize / 2) - 1;
        } else {
            rightOffset = groupSize / 2;
        }
        return rightOffset;
    }

    /**
     * 中心位置索引[从1开始计算]
     */
    public int getMiddleOffset() {
        int groupSize = getGroupSize();
        int totalPage = getTotalPage();
        if (groupSize >= totalPage) {
            return -1;
        }
        return getLeftOffset() + 1;
    }

    public String[] getPageRange() {
        setRangeIndex();
        int size = endIndex - startIndex + 1;
        if (size <= 0) {
            return new String[0];
        }
        if (totalPage == 1) {
            return new String[]{"1"};
        }
        pageRange = new String[size];
        for (int i = 0; i < size; i++) {
            pageRange[i] = (startIndex + i + 1) + "";
        }
        return pageRange;
    }

    public static void main(String[] args) {
        for (int i=1;i<=30;i++) {
            Page<Integer> page = new Page<>(i, 10, null, 200);
            System.out.println(page.toString());
        }
    }
}
