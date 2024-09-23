package com.jong.msa.board.endpoint.user.request;

import com.jong.msa.board.client.member.validation.MemberEmail;
import com.jong.msa.board.client.member.validation.MemberName;
import com.jong.msa.board.common.enums.CodeEnum.Gender;

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
public class UserSessionModifyRequest {

	@Schema(description = "이름", example = "name")
	@MemberName(nullable = true,
			notBlankMessage = "이름은 비어있을 수 없습니다.",
			overLengthMessage = "이름은 30자를 초과할 수 없습니다.")
	private String name;
	
	@Schema(description = "성별", example = "MAIL")
	private Gender gender;
	
	@Schema(description = "이메일", example = "email@example.com")
	@MemberEmail(nullable = true,
			notBlankMessage = "이메일은 비어있을 수 없습니다.",
			overLengthMessage = "이메일은 30자를 초과할 수 없습니다.",
			notFormatMessage = "이메일이 형식에 맞지 않습니다.")
	private String email;
	
}
