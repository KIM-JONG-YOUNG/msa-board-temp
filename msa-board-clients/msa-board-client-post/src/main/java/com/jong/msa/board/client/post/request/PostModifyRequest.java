package com.jong.msa.board.client.post.request;

import com.jong.msa.board.client.post.validation.PostContent;
import com.jong.msa.board.client.post.validation.PostTitle;
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
public class PostModifyRequest {

	@Schema(name = "제목", example = "title")
	@PostTitle(nullable = true,
			notBlankMessage = "제목은 비어있을 수 없습니다.",
			overLengthMessage = "제목은 300자를 초과할 수 없습니다.")
	private String title;
	
	@Schema(name = "내용", example = "content")
	@PostContent(nullable = true,
			notBlankMessage = "내용은 비어있을 수 없습니다.")
	private String content;
	
	@Schema(description = "상태", example = "ACTIVE")
	private State state;

}
