package com.jiayi.platform.security.core.dto;

import java.util.List;

public class PageResult<T>
{
    private List<T> data;
    private Long total;
    private Integer page;
    private Integer size;

    public PageResult(List<T> data)
    {
        this(data, null, null, null);
    }

	public PageResult(List<T> data, Long total, Integer page, Integer size) {
		super();
		this.data = data;
		this.total = total;
		this.page = page;
		this.size = size;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}


}
