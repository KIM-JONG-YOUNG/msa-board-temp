package com.jong.msa.board.endpoint.user.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.jong.msa.board.client.core.utils.SecurityUtils;
import com.jong.msa.board.client.member.request.MemberLoginRequest;
import com.jong.msa.board.client.member.request.MemberModifyRequest;
import com.jong.msa.board.client.member.request.MemberPasswordModifyRequest;
import com.jong.msa.board.client.post.request.PostCreateRequest;
import com.jong.msa.board.client.post.request.PostModifyRequest;
import com.jong.msa.board.client.search.request.PostSearchRequest;
import com.jong.msa.board.endpoint.user.request.UserLoginRequest;
import com.jong.msa.board.endpoint.user.request.UserPostCreateRequest;
import com.jong.msa.board.endpoint.user.request.UserPostModifyRequest;
import com.jong.msa.board.endpoint.user.request.UserPostSearchRequest;
import com.jong.msa.board.endpoint.user.request.UserSessionModifyRequest;
import com.jong.msa.board.endpoint.user.request.UserSessionPasswordModifyRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRequestMapper {

	default UUID getAuthId() { return SecurityUtils.getSessionId(); }

	MemberLoginRequest toRequest(UserLoginRequest request);

	MemberModifyRequest toRequest(UserSessionModifyRequest request);

	MemberPasswordModifyRequest toRequest(UserSessionPasswordModifyRequest request);

	@Mapping(target = "writerId", expression = "java(getAuthId())")
	PostCreateRequest toRequest(UserPostCreateRequest request);

	PostModifyRequest toRequest(UserPostModifyRequest request);

	PostSearchRequest toRequest(UserPostSearchRequest request);
	
}
