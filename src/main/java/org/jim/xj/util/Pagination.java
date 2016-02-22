package org.jim.xj.util;

import org.nutz.dao.pager.Pager;

public class Pagination {

	private Integer total;
	private Integer pageSize;
	private Integer pageIndex;
	
	
	
	public Pagination(Integer pageSize, Integer pageIndex) {
		super();
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
	}
	public Pagination() {
		super();
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public Pager create(){
		Pager p = new Pager();
		p.setPageSize(pageSize);
		p.setPageNumber(pageIndex);
		return p;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pageIndex == null) ? 0 : pageIndex.hashCode());
		result = prime * result
				+ ((pageSize == null) ? 0 : pageSize.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pagination other = (Pagination) obj;
		if (pageIndex == null) {
			if (other.pageIndex != null)
				return false;
		} else if (!pageIndex.equals(other.pageIndex))
			return false;
		if (pageSize == null) {
			if (other.pageSize != null)
				return false;
		} else if (!pageSize.equals(other.pageSize))
			return false;
		return true;
	}
	
}
