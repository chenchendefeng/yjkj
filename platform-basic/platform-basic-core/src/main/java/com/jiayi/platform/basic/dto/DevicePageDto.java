package com.jiayi.platform.basic.dto;

import com.jiayi.platform.common.web.dto.PageResult;

import java.util.List;

public class DevicePageDto<T> extends PageResult<T> {

	private Long onlineCount = 0l;
	private Long offlineCount = 0l;
	private Long qualifiedCount = 0l;
	private Long undeterminedCount = 0l;
	private Long unqualifiedCount = 0l;

	public DevicePageDto(List<T> data, Long total, Integer page, Integer size) {
		super(data, total, page, size);
	}

	public DevicePageDto(List<T> data, Long total, Integer page, Integer size, Long onlineCount, Long offlineCount,
                         Long qualifiedCount, Long undeterminedCount, Long unqualifiedCount) {
		super(data, total, page, size);
		this.onlineCount = onlineCount;
		this.offlineCount = offlineCount;
		this.qualifiedCount = qualifiedCount;
		this.undeterminedCount = undeterminedCount;
		this.unqualifiedCount = unqualifiedCount;
	}

	public Long getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(Long onlineCount) {
		this.onlineCount = onlineCount;
	}

	public Long getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(Long offlineCount) {
		this.offlineCount = offlineCount;
	}

	public Long getQualifiedCount() {
		return qualifiedCount;
	}

	public void setQualifiedCount(Long qualifiedCount) {
		this.qualifiedCount = qualifiedCount;
	}

	public Long getUndeterminedCount() {
		return undeterminedCount;
	}

	public void setUndeterminedCount(Long undeterminedCount) {
		this.undeterminedCount = undeterminedCount;
	}

	public Long getUnqualifiedCount() {
		return unqualifiedCount;
	}

	public void setUnqualifiedCount(Long unqualifiedCount) {
		this.unqualifiedCount = unqualifiedCount;
	}
	
}
