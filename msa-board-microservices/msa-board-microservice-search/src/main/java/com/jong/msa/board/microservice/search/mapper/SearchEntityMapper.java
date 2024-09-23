package com.jong.msa.board.microservice.search.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.jong.msa.board.client.search.response.MemberListResponse;
import com.jong.msa.board.client.search.response.PostListResponse;
import com.jong.msa.board.common.enums.SortEnum.MemberSortBy;
import com.jong.msa.board.common.enums.SortEnum.PostSortBy;
import com.jong.msa.board.domain.member.entity.MemberEntity;
import com.jong.msa.board.domain.member.entity.QMemberEntity;
import com.jong.msa.board.domain.post.entity.PostEntity;
import com.jong.msa.board.domain.post.entity.QPostEntity;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SearchEntityMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	MemberEntity updateEntity(MemberEntity source, @MappingTarget MemberEntity target);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	PostEntity updateEntity(PostEntity source, @MappingTarget PostEntity target);

	MemberListResponse.Item toItem(MemberEntity entity);

	@Mapping(target = "id", source = "entity.id")
	@Mapping(target = "title", source = "entity.title")
	@Mapping(target = "writer.id", source = "writer.id")
	@Mapping(target = "writer.username", source = "writer.username")
	@Mapping(target = "writer.group", source = "writer.group")
	@Mapping(target = "views", source = "entity.views")
	@Mapping(target = "createdDateTime", source = "entity.createdDateTime")
	@Mapping(target = "updatedDateTime", source = "entity.updatedDateTime")
	@Mapping(target = "state", source = "entity.state")
	PostListResponse.Item toItem(PostEntity entity, MemberEntity writer);

	default ComparableExpressionBase<?> toColumn(MemberSortBy sortBy) {

		switch (sortBy) {
		case USERNAME:
			return QMemberEntity.memberEntity.username;
		case NAME:
			return QMemberEntity.memberEntity.name;
		case EMAIL:
			return QMemberEntity.memberEntity.email;
		case CREATED_DATE_TIME:
			return QMemberEntity.memberEntity.createdDateTime;
		case UPDATED_DATE_TIME:
			return QMemberEntity.memberEntity.updatedDateTime;
		default:
			return QMemberEntity.memberEntity.username;
		}
	}

	default ComparableExpressionBase<?> toColumn(PostSortBy sortBy) {

		switch (sortBy) {
		case TITLE:
			return QPostEntity.postEntity.title;
		case WRITER:
			return QMemberEntity.memberEntity.username;
		case VIEWS:
			return QPostEntity.postEntity.views;
		case CREATED_DATE_TIME:
			return QPostEntity.postEntity.createdDateTime;
		case UPDATED_DATE_TIME:
			return QPostEntity.postEntity.updatedDateTime;
		default:
			return QPostEntity.postEntity.createdDateTime;
		}
	}

}
