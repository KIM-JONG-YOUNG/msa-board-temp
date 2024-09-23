package com.jong.msa.board.microservice.search.consumer;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.constants.KafkaTopics;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.constants.RedisPrefixes;
import com.jong.msa.board.domain.core.transaction.DistributeTransaction;
import com.jong.msa.board.domain.member.entity.MemberEntity;
import com.jong.msa.board.domain.member.repository.MemberRepository;
import com.jong.msa.board.domain.post.entity.PostEntity;
import com.jong.msa.board.domain.post.repository.PostRepository;
import com.jong.msa.board.microservice.search.mapper.SearchEntityMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchEntityConsumer {

	private final SearchEntityMapper entityMapper;
	
	private final MemberRepository memberRepository;
	
	private final PostRepository postRepository;

	private final ObjectProvider<SearchEntityConsumer> objectProvider;

	private final ObjectMapper objectMapper;

	@KafkaListener(
			topics = KafkaTopics.MEMBER_TOPIC, 
			groupId = MicroserviceNames.SEARCH_MICROSERVICE)
	public void consumeMemberTopic(String message) throws Exception {
		
		objectProvider.getObject().save(objectMapper.readValue(message, MemberEntity.class));
	}
	
	@DistributeTransaction(prefix = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#entity.id")
	public void save(MemberEntity entity) {

		try {

			MemberEntity dbEntity = memberRepository.findById(entity.getId())
					.orElseThrow(EntityNotFoundException::new)
					.setAuditable(false);
			
			memberRepository.save(entityMapper.updateEntity(entity, dbEntity));
			
		} catch (EntityNotFoundException e) {

			memberRepository.save(entity);
		}
	}

	@KafkaListener(
			topics = KafkaTopics.POST_TOPIC, 
			groupId = MicroserviceNames.SEARCH_MICROSERVICE)
	public void consumePostTopic(String message) throws Exception {
		
		objectProvider.getObject().save(objectMapper.readValue(message, PostEntity.class));
	}

	@DistributeTransaction(prefix = RedisPrefixes.POST_LOCK_PREFIX, key = "#entity.id")
	public void save(PostEntity entity) {

		try {

			PostEntity dbEntity = postRepository.findById(entity.getId())
					.orElseThrow(EntityNotFoundException::new)
					.setAuditable(false);
			
			postRepository.save(entityMapper.updateEntity(entity, dbEntity));
			
		} catch (EntityNotFoundException e) {

			postRepository.save(entity);
		}
	}
}
