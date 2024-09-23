package com.jong.msa.board.domain.post.repository;

import org.springframework.stereotype.Repository;

import com.jong.msa.board.domain.core.repository.BaseRepository;
import com.jong.msa.board.domain.post.entity.PostEntity;

@Repository
public interface PostRepository extends BaseRepository<PostEntity> {

}
