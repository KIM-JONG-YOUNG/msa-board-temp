package com.jong.msa.board.client.member.request;

import com.jong.msa.board.client.member.validation.MemberPassword;

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
public class MemberPasswordModifyRequest {

	@Schema(description = "현재 비밀번호", example = "password")
	@MemberPassword(nullable = false,
			notBlankMessage = "현재 비밀번호는 비어있을 수 없습니다.",
			underLengthMessage = "현재 비밀번호는 8자 미만일 수 없습니다.")
	private String currentPassword;
	
	@Schema(description = "새로운 비밀번호", example = "newPassword")
	@MemberPassword(nullable = false,
			notBlankMessage = "새로운 비밀번호는 비어있을 수 없습니다.",
			underLengthMessage = "새로운 비밀번호는 8자 미만일 수 없습니다.")
	private String newPassword;
	
}
