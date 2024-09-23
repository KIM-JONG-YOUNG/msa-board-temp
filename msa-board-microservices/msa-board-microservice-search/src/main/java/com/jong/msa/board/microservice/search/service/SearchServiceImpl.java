package com.jong.msa.board.microservice.search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jong.msa.board.client.search.request.DateRange;
import com.jong.msa.board.client.search.request.MemberSearchRequest;
import com.jong.msa.board.client.search.request.PostSearchRequest;
import com.jong.msa.board.client.search.response.MemberListResponse;
import com.jong.msa.board.client.search.response.PostListResponse;
import com.jong.msa.board.common.enums.SortEnum.MemberSortBy;
import com.jong.msa.board.common.enums.SortEnum.OrderBy;
import com.jong.msa.board.common.enums.SortEnum.PostSortBy;
import com.jong.msa.board.domain.core.utils.QueryDSLUtils;
import com.jong.msa.board.domain.member.entity.MemberEntity;
import com.jong.msa.board.domain.member.entity.QMemberEntity;
import com.jong.msa.board.domain.post.entity.QPostEntity;
import com.jong.msa.board.microservice.search.mapper.SearchEntityMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

	private final SearchEntityMapper entityMapper;
	
	private final JPAQueryFactory queryFactory;

	private final QMemberEntity memberEntity = QMemberEntity.memberEntity;
	
	private final QPostEntity postEntity = QPostEntity.postEntity;
	
	@Transactional(readOnly = true)
	@Override
	public MemberListResponse searchMemberList(MemberSearchRequest request) {

		DateRange createdDateTime = request.getCreatedDate();
		DateRange updatedDateTime = request.getUpdatedDate();
		
		BooleanExpression[] searchConditions = new BooleanExpression[] {
		
				QueryDSLUtils.containIfPresent(memberEntity.username, request.getUsername()),
				QueryDSLUtils.containIfPresent(memberEntity.name, request.getName()),
				QueryDSLUtils.containIfPresent(memberEntity.email, request.getEmail()),
				
				QueryDSLUtils.equalsIfPresent(memberEntity.gender, request.getGender()),
				QueryDSLUtils.equalsIfPresent(memberEntity.group, request.getGroup()),
				QueryDSLUtils.equalsIfPresent(memberEntity.state, request.getState()),
				
				(createdDateTime == null) ? null : QueryDSLUtils.betweenIfPresent(
						memberEntity.createdDateTime, createdDateTime.getFrom(), createdDateTime.getTo()),
				(updatedDateTime == null) ? null : QueryDSLUtils.betweenIfPresent(
						memberEntity.updatedDateTime, updatedDateTime.getFrom(), updatedDateTime.getTo()),
		};
		
		MemberSortBy sortBy = (request.getSortBy() != null) ? request.getSortBy() : MemberSortBy.USERNAME; 
		OrderBy orderBy = (request.getOrderBy() != null) ? request.getOrderBy() : sortBy.getDefaultOrderBy();
		
		ComparableExpressionBase<?> sortColumn = entityMapper.toColumn(sortBy);
		OrderSpecifier<?> orderCondition = (orderBy == OrderBy.ASC) ? sortColumn.asc() : sortColumn.desc();
		
		long totalCount = queryFactory
				.select(memberEntity.count())
				.from(memberEntity)
				.where(searchConditions)
				.fetchOne();
		
		List<MemberEntity> list = queryFactory
				.select(memberEntity)
				.from(memberEntity)
				.where(searchConditions)
				.orderBy(orderCondition)
				.offset(request.getOffset())
				.limit(request.getLimit())
				.fetch();
		
		return MemberListResponse.builder()
				.totalCount(totalCount)
				.list(list.stream()
						.map(entityMapper::toItem)
						.collect(Collectors.toList()))
				.build();
	}

	@Transactional(readOnly = true)
	@Override
	public PostListResponse searchPostList(PostSearchRequest request) {
		
		DateRange createdDateTime = request.getCreatedDate();
		DateRange updatedDateTime = request.getUpdatedDate();
		
		BooleanExpression[] searchConditions = new BooleanExpression[] {
				
				QueryDSLUtils.containIfPresent(postEntity.title, request.getTitle()),
				QueryDSLUtils.containIfPresent(postEntity.content, request.getContent()),
				QueryDSLUtils.containIfPresent(memberEntity.username, request.getWriterUsername()),
				
				QueryDSLUtils.equalsIfPresent(postEntity.state, request.getState()),
				
				(createdDateTime == null) ? null : QueryDSLUtils.betweenIfPresent(
						postEntity.createdDateTime, createdDateTime.getFrom(), createdDateTime.getTo()),
				(updatedDateTime == null) ? null : QueryDSLUtils.betweenIfPresent(
						postEntity.updatedDateTime, updatedDateTime.getFrom(), updatedDateTime.getTo()),
		};
		
		PostSortBy sortBy = (request.getSortBy() != null) ? request.getSortBy() : PostSortBy.CREATED_DATE_TIME; 
		OrderBy orderBy = (request.getOrderBy() != null) ? request.getOrderBy() : sortBy.getDefaultOrderBy();

		ComparableExpressionBase<?> sortColumn = entityMapper.toColumn(sortBy);
		OrderSpecifier<?> orderCondition = (orderBy == OrderBy.ASC) ? sortColumn.asc() : sortColumn.desc();

		long totalCount = queryFactory
				.select(postEntity.count())
				.from(postEntity)
				.leftJoin(memberEntity)
					.on(postEntity.writerId.eq(memberEntity.id))
				.where(searchConditions)
				.fetchOne();

		List<Tuple> list = queryFactory
				.select(postEntity, memberEntity)
				.from(postEntity)
				.leftJoin(memberEntity)
					.on(postEntity.writerId.eq(memberEntity.id))
				.where(searchConditions)
				.orderBy(orderCondition)
				.offset(request.getOffset())
				.limit(request.getLimit())
				.fetch();

		return PostListResponse.builder()
				.totalCount(totalCount)
				.list(list.stream()
						.map(x -> entityMapper.toItem(
								x.get(postEntity), 
								x.get(memberEntity)))
						.collect(Collectors.toList()))
				.build();
	}

}
