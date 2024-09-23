package com.jong.msa.board.domain.post.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.jong.msa.board.common.enums.CodeEnum.State;
import com.jong.msa.board.domain.core.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Entity
@Builder
@Accessors(chain = true)
@Table(name = "tb_post")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity extends BaseEntity {

	@Setter
	@Column(name = "post_title", length = 300, nullable = false)
	private String title;
	
	@Setter
	@Column(name = "post_content", columnDefinition = "TEXT", nullable = false)
	private String content;
	
	@Column(name = "post_writer_id", columnDefinition = "BINARY(16)", nullable = false)
	private UUID writerId;
	
	@Setter
	@Column(name = "post_views", nullable = false)
	private int views;

	@PrePersist
	@PreUpdate
	public void beforeSave() {

		if (this.views < 0) this.views = 0;
	}

	@Override
	public PostEntity setState(State state) {
		
		return (PostEntity) super.setState(state);
	}
	
	@Override
	public PostEntity setAuditable(boolean auditable) {
		
		return (PostEntity) super.setAuditable(auditable);
	}

}
