package com.jong.msa.board.endpoint.user.request;

import com.jong.msa.board.client.member.validation.MemberPassword;
import com.jong.msa.board.client.member.validation.MemberUsername;

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
public class UserLoginRequest {

	@Schema(description = "계정", example = "username")
	@MemberUsername(nullable = false,
			notBlankMessage = "계정은 비어있을 수 없습니다.",
			overLengthMessage = "계정은 30자를 초과할 수 없습니다.",
			notFormatMessage = "계정이 형식에 맞지 않습니다.")
	private String username;
	
	@Schema(description = "비밀번호", example = "password")
	@MemberPassword(nullable = false,
			notBlankMessage = "비밀번호는 비어있을 수 없습니다.",
			underLengthMessage = "비밀번호는 8자 미만일 수 없습니다.")
	private String password;
	
}
