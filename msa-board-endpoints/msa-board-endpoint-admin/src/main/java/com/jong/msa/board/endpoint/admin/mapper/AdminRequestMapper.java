package com.jong.msa.board.endpoint.admin.mapper;

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
import com.jong.msa.board.client.search.request.MemberSearchRequest;
import com.jong.msa.board.client.search.request.PostSearchRequest;
import com.jong.msa.board.endpoint.admin.request.AdminLoginRequest;
import com.jong.msa.board.endpoint.admin.request.AdminMemberSearchRequest;
import com.jong.msa.board.endpoint.admin.request.AdminPostCreateRequest;
import com.jong.msa.board.endpoint.admin.request.AdminPostModifyRequest;
import com.jong.msa.board.endpoint.admin.request.AdminPostSearchRequest;
import com.jong.msa.board.endpoint.admin.request.AdminSessionModifyRequest;
import com.jong.msa.board.endpoint.admin.request.AdminSessionPasswordModifyRequest;
import com.jong.msa.board.endpoint.admin.request.AdminUserModifyRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminRequestMapper {

	default UUID getAuthId() { return SecurityUtils.getSessionId(); }
	
	MemberLoginRequest toRequest(AdminLoginRequest request);

	MemberModifyRequest toRequest(AdminSessionModifyRequest request);

	MemberModifyRequest toRequest(AdminUserModifyRequest request);

	MemberPasswordModifyRequest toRequest(AdminSessionPasswordModifyRequest request);

	MemberSearchRequest toRequest(AdminMemberSearchRequest request);

	@Mapping(target = "writerId", expression = "java(getAuthId())")
	PostCreateRequest toRequest(AdminPostCreateRequest request);

	PostModifyRequest toRequest(AdminPostModifyRequest request);

	PostSearchRequest toRequest(AdminPostSearchRequest request);
	
}
