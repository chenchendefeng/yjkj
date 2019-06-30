package com.jiayi.platform.judge.common.bean;

/**
 * 为了和老接口兼容，这里的分页信息没用有common-web里的，继续延用之前碰撞分析模块的
 */
public class PageInfo {

    /**
     * 当前页数
     */
    private long pageIndex;
    /**
     * 当前页的数量
     */
    private int elementCount;
    /**
     * 总页数
     */
    private long totalPages;
    /**
     * 数据总数
     */
    private long totalElements;
    /**
     * 每一页的条数
     */
    private long pageSize;

    public PageInfo(){

    }

    public PageInfo(Integer elementCount, Long totalElements, PageRequest pageRequest) {
        pageIndex = pageRequest.getPageIndex();
        pageSize = pageRequest.getPageSize();
        this.elementCount = elementCount;
        this.totalElements = totalElements;
        this.totalPages = (totalElements + pageSize - 1) / pageSize;
    }
    
    public PageInfo(Integer elementCount, Long totalElements, Long pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.elementCount = elementCount;
        this.totalElements = totalElements;
        this.totalPages = (totalElements + pageSize - 1) / pageSize;
    }

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getElementCount() {
        return elementCount;
    }

    public void setElementCount(int elementCount) {
        this.elementCount = elementCount;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageIndex=" + pageIndex +
                ", elementCount=" + elementCount +
                ", totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                ", pageSize=" + pageSize +
                '}';
    }
}
