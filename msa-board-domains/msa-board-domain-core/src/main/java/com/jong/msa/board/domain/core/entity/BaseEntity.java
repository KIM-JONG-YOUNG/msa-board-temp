package com.jong.msa.board.domain.core.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.jong.msa.board.common.enums.CodeEnum.State;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@MappedSuperclass
@Accessors(chain = true)
public abstract class BaseEntity {

	@Id
	@Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
	private UUID id;

	@Column(name = "created_date_time", nullable = false)
	private LocalDateTime createdDateTime;

	@Column(name = "updated_date_time", nullable = false)
	private LocalDateTime updatedDateTime;

	@Setter
	@Column(name = "state", nullable = false)
	private State state;
	
	@Setter
	@Transient
	private boolean auditable = true;

	@PrePersist
	public void prePersist() {

		LocalDateTime now = LocalDateTime.now();

		if (this.id == null) this.id = UUID.randomUUID();
		if (this.state == null) this.state = State.ACTIVE;

		if (this.createdDateTime == null) this.createdDateTime = now;
		if (this.updatedDateTime == null) this.updatedDateTime = now;
	}

	@PreUpdate
	public void preUpdate() {
		
		if (this.auditable) this.updatedDateTime = LocalDateTime.now();
	}

}
