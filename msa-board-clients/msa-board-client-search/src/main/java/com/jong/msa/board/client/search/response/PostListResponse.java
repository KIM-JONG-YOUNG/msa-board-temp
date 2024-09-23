package com.jong.msa.board.client.search.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.common.enums.CodeEnum.State;

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
public class PostListResponse {

	private long totalCount;

	private List<Item> list;
	
	@Getter
	@Builder
	@ToString
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Schema(name = "PostListResponse.Item")
	public static class Item {
		
		@Schema(description = "ID")
		private UUID id;
		
		@Schema(description = "제목")
		private String title;

		@Schema(description = "작성자")
		private Writer writer;
		
		@Schema(description = "조회수")
		private int views;
		
		@Schema(description = "생성 일시")
		private LocalDateTime createdDateTime;
		
		@Schema(description = "수정 일시")
		private LocalDateTime updatedDateTime;
		
		@Schema(description = "상태")
		private State state;

	}
	
	@Getter
	@Builder
	@ToString
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	@Schema(name = "PostListResponse.Writer")
	public static class Writer {
		
		@Schema(description = "ID")
		private UUID id;
		
		@Schema(description = "계정")
		private String username;
		
		@Schema(description = "그룹")
		private Group group;
		
	}
	
}
