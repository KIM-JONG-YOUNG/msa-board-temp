package com.jong.msa.board.domain.member.repository;

import org.springframework.stereotype.Repository;

import com.jong.msa.board.domain.core.repository.BaseRepository;
import com.jong.msa.board.domain.member.entity.MemberEntity;

@Repository
public interface MemberRepository extends BaseRepository<MemberEntity> {

}
