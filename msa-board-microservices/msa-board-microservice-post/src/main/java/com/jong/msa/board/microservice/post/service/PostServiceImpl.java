package com.jong.msa.board.microservice.post.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.member.feign.MemberFeignClient;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.client.post.request.PostCreateRequest;
import com.jong.msa.board.client.post.request.PostModifyRequest;
import com.jong.msa.board.client.post.response.PostDetailsResponse;
import com.jong.msa.board.common.constants.RedisPrefixes;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.domain.core.transaction.DistributeTransaction;
import com.jong.msa.board.domain.post.entity.PostEntity;
import com.jong.msa.board.domain.post.repository.PostRepository;
import com.jong.msa.board.microservice.post.event.PostSaveEvent;
import com.jong.msa.board.microservice.post.mapper.PostEntityMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostEntityMapper entityMapper;
	
	private final PostRepository repository;
	
	private final ApplicationEventPublisher eventPublisher;

	private final MemberFeignClient memberFeignClient;
	
	@Transactional
	@Override
	public UUID createPost(PostCreateRequest request) {
		
		try {
			
			memberFeignClient.getMember(request.getWriterId());
			
		} catch (RestServiceException e) {
			
			throw (e.getErrorCode() == ErrorCode.NOT_FOUND_MEMBER) 
				? new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_POST_WRITER) 
				: new RestServiceException(HttpStatus.BAD_GATEWAY, ErrorCode.UNCHECKED_EXTERNAL_ERROR);
		}
		
		PostEntity savedEntity = repository.saveAndFlush(entityMapper.toEntity(request));

		eventPublisher.publishEvent(new PostSaveEvent(savedEntity));
		
		return savedEntity.getId();
	}

	@DistributeTransaction(prefix = RedisPrefixes.POST_LOCK_PREFIX, key = "#id")
	@Override
	public void modifyPost(UUID id, PostModifyRequest request) {

		PostEntity entity = repository.findById(id)
				.orElseThrow(() -> new RestServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_POST));
		
		PostEntity savedEntity = repository.saveAndFlush(entityMapper.updateEntity(request, entity));

		eventPublisher.publishEvent(new PostSaveEvent(savedEntity));
	}

	@DistributeTransaction(prefix = RedisPrefixes.POST_LOCK_PREFIX, key = "#id")
	@Override
	public void increasePostViews(UUID id) {

		PostEntity entity = repository.findById(id)
				.orElseThrow(() -> new RestServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_POST));
		
		PostEntity savedEntity = repository.saveAndFlush(entity.setViews(entity.getViews() + 1));

		eventPublisher.publishEvent(new PostSaveEvent(savedEntity));
	}

	@Transactional(readOnly = true)
	@Override
	public PostDetailsResponse getPost(UUID id) {

		PostEntity entity = repository.findById(id)
				.orElseThrow(() -> new RestServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_POST));

		try {
			
			MemberDetailsResponse writer = memberFeignClient.getMember(entity.getWriterId()).getBody();
		
			return entityMapper.toResponse(entity, writer);
			
		} catch (RestServiceException e) {
			
			throw (e.getErrorCode() == ErrorCode.NOT_FOUND_MEMBER) 
				? new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_POST_WRITER) 
				: new RestServiceException(HttpStatus.BAD_GATEWAY, ErrorCode.UNCHECKED_EXTERNAL_ERROR);
		}		
	}

}
