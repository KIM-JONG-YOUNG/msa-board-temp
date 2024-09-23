package com.jong.msa.board.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface SortEnum<V> {

	OrderBy getDefaultOrderBy();
	
	public enum OrderBy {
		
		ASC, DESC;
	}

	@Getter
	@ToString
	@RequiredArgsConstructor
	public enum MemberSortBy {
		
		USERNAME,
		NAME,
		EMAIL,
		CREATED_DATE_TIME,
		UPDATED_DATE_TIME;
		
		private OrderBy defaultOrderBy;
	}

	@Getter
	@ToString
	@RequiredArgsConstructor
	public enum PostSortBy {
		
		TITLE,
		WRITER,
		VIEWS,
		CREATED_DATE_TIME,
		UPDATED_DATE_TIME;
		
		private OrderBy defaultOrderBy;
	}

}
