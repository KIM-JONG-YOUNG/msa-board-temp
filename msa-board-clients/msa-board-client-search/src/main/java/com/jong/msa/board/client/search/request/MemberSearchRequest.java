package com.jong.msa.board.client.search.request;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.jong.msa.board.client.search.validation.BetweenDate;
import com.jong.msa.board.common.enums.CodeEnum.Gender;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.common.enums.CodeEnum.State;
import com.jong.msa.board.common.enums.SortEnum.MemberSortBy;
import com.jong.msa.board.common.enums.SortEnum.OrderBy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearchRequest {

	@Schema(description = "계정", example = "username")
	private String username;
	
	@Schema(description = "이름", example = "name")
	private String name;
	
	@Schema(description = "성별", example = "MAIL")
	private Gender gender;
	
	@Schema(description = "이메일", example = "email")
	private String email;
	
	@Schema(description = "그룹", example = "ADMIN")
	private Group group;

	@Schema(description = "생성일자")
	@BetweenDate(message = "시작일자가 종료일자 이전이 아닙니다.")
	private DateRange createdDate;
	
	@Schema(description = "수정일자")
	@BetweenDate(message = "시작일자가 종료일자 이전이 아닙니다.")
	private DateRange updatedDate;
	
	@Schema(description = "상태", example = "ACTIVE")
	private State state;
	
	@Builder.Default
	@Schema(description = "조회 시작 행", example = "0")
	@PositiveOrZero(message = "조회 시작 행은 0보다 작을 수 없습니다.")
	private long offset = 0;
	
	@Builder.Default
	@Schema(description = "조회 건수", example = "10")
	@Positive(message = "조회 시작 행은 1보다 작을 수 없습니다.")
	private long limit = 10;

	@Schema(description = "정렬 기준", example = "USERNAME")
	private MemberSortBy sortBy;
	
	@Schema(description = "정렬 방식", example = "ASC")
	private OrderBy orderBy;
	
}
