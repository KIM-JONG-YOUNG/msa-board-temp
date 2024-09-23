package com.jong.msa.board.domain.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.util.StringUtils;

import com.jong.msa.board.common.enums.SortEnum.OrderBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.StringPath;

public final class QueryDSLUtils {

	public static <E extends Enum<E>> BooleanExpression equalsIfPresent(EnumPath<E> column, E value) {

		return (value != null) ? column.eq(value) : null;
	}

	public static BooleanExpression containIfPresent(StringPath column, String value) {

		return (StringUtils.hasText(value)) ? column.contains(value) : null;
	}

	public static BooleanExpression betweenIfPresent(DateTimePath<LocalDateTime> column, LocalDate from, LocalDate to) {

		LocalDateTime fromDateTime = (from != null) ? LocalDateTime.of(from, LocalTime.of(0, 0, 0)) : null;
		LocalDateTime toDateTime = (to != null) ? LocalDateTime.of(to, LocalTime.of(23, 59, 59)) : null;
		
		return (fromDateTime != null || toDateTime != null) ? column.between(column, column) : null;
	}

	public static OrderSpecifier<?> toOrderSpecifier(
			ComparableExpressionBase<?> column, OrderBy orderBy) {

		return (orderBy == OrderBy.ASC) ? column.asc().nullsFirst() : column.desc().nullsLast();
	}

}
