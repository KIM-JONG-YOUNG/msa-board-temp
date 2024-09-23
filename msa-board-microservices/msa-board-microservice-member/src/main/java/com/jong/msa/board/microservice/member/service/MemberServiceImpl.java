package com.jong.msa.board.microservice.member.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.member.request.MemberCreateRequest;
import com.jong.msa.board.client.member.request.MemberLoginRequest;
import com.jong.msa.board.client.member.request.MemberModifyRequest;
import com.jong.msa.board.client.member.request.MemberPasswordModifyRequest;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.common.constants.RedisPrefixes;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.enums.CodeEnum.State;
import com.jong.msa.board.domain.core.transaction.DistributeTransaction;
import com.jong.msa.board.domain.member.entity.MemberEntity;
import com.jong.msa.board.domain.member.entity.QMemberEntity;
import com.jong.msa.board.domain.member.repository.MemberRepository;
import com.jong.msa.board.microservice.member.event.MemberSaveEvent;
import com.jong.msa.board.microservice.member.mapper.MemberEntityMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final PasswordEncoder passwordEncoder;
	
	private final MemberEntityMapper entityMapper;

	private final MemberRepository repository;
	
	private final ApplicationEventPublisher eventPublisher;
	
	private final JPAQueryFactory queryFactory;
	
	private final QMemberEntity memberEntity = QMemberEntity.memberEntity;
	
	@DistributeTransaction(prefix = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#request.username")
	@Override
	public UUID createMember(MemberCreateRequest request) {

		boolean isExistsUsername = queryFactory
				.selectOne()
				.from(memberEntity)
				.where(memberEntity.username.eq(request.getUsername()))
				.fetchFirst() != null;
		
		if (isExistsUsername) {
			
			throw new RestServiceException(HttpStatus.CONFLICT, ErrorCode.EXISTS_MEMBER_USERNAME);
		} else {

			MemberEntity entity = entityMapper.toEntity(request)
					.setPassword(passwordEncoder.encode(request.getPassword()));

			MemberEntity savedEntity = repository.saveAndFlush(entity);
			
			eventPublisher.publishEvent(new MemberSaveEvent(savedEntity));
			
			return savedEntity.getId();
		}
	}

	@DistributeTransaction(prefix = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#id")
	@Override
	public void modifyMember(UUID id, MemberModifyRequest request) {
		
		MemberEntity entity = repository.findById(id)
				.orElseThrow(() -> new RestServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

		MemberEntity savedEntity = repository.saveAndFlush(entityMapper.updateEntity(request, entity));
		
		eventPublisher.publishEvent(new MemberSaveEvent(savedEntity));
	}

	@DistributeTransaction(prefix = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#id")
	@Override
	public void modifyMemberPassword(UUID id, MemberPasswordModifyRequest request) {
		
		MemberEntity entity = repository.findById(id)
				.orElseThrow(() -> new RestServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER));

		if (passwordEncoder.matches(request.getCurrentPassword(), entity.getPassword())) {
			
			MemberEntity savedEntity = repository.saveAndFlush(entity
					.setPassword(passwordEncoder.encode(request.getNewPassword())));
			
			eventPublisher.publishEvent(new MemberSaveEvent(savedEntity));

		} else {
			
			throw new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_MATCHED_MEMBER_PASSWORD);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public MemberDetailsResponse getMember(UUID id) {
		
		return entityMapper.toResponse(repository.findById(id)
				.orElseThrow(() -> new RestServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER)));
	}

	@Transactional(readOnly = true)
	@Override
	public MemberDetailsResponse loginMember(MemberLoginRequest request) {
		
		MemberEntity entity = queryFactory
				.select(memberEntity)
				.from(memberEntity)
				.where(memberEntity.username.eq(request.getUsername()))
				.fetchOne();
		
		if (entity == null) {
			throw new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER_USERNAME);
		} else if (entity.getState() != State.ACTIVE) {
			throw new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_ACTIVE_MEMBER_USERNAME);
		} else if (passwordEncoder.matches(request.getPassword(), entity.getPassword()) == false) {
			throw new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_MATCHED_MEMBER_PASSWORD);
		} else {
			return entityMapper.toResponse(entity);
		}
	}

}
