package com.jong.msa.board.endpoint.user.request;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.jong.msa.board.client.search.request.DateRange;
import com.jong.msa.board.client.search.validation.BetweenDate;
import com.jong.msa.board.common.enums.SortEnum.OrderBy;
import com.jong.msa.board.common.enums.SortEnum.PostSortBy;

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
public class UserPostSearchRequest {

	@Schema(description = "제목", example = "title")
	private String title;
	
	@Schema(description = "내용", example = "content")
	private String content;
	
	@Schema(description = "작성자 계정", example = "username")
	private String writerUsername;
	
	@Schema(description = "생성일자")
	@BetweenDate(message = "시작일자가 종료일자 이전이 아닙니다.")
	private DateRange createdDate;
	
	@Builder.Default
	@Schema(description = "조회 시작 행", example = "0")
	@PositiveOrZero(message = "조회 시작 행은 0보다 작을 수 없습니다.")
	private long offset = 0;
	
	@Builder.Default
	@Schema(description = "조회 건수", example = "10")
	@Positive(message = "조회 시작 행은 1보다 작을 수 없습니다.")
	private long limit = 10;

	@Schema(description = "정렬 기준", example = "CREATED_DATE_TIME")
	private PostSortBy sortBy;
	
	@Schema(description = "정렬 방식", example = "DESC")
	private OrderBy orderBy;
	
}
