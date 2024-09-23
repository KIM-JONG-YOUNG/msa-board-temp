package com.jong.msa.board.client.member.request;

import javax.validation.constraints.NotNull;

import com.jong.msa.board.client.member.validation.MemberEmail;
import com.jong.msa.board.client.member.validation.MemberName;
import com.jong.msa.board.client.member.validation.MemberPassword;
import com.jong.msa.board.client.member.validation.MemberUsername;
import com.jong.msa.board.common.enums.CodeEnum.Gender;
import com.jong.msa.board.common.enums.CodeEnum.Group;

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
public class MemberCreateRequest {

	@Schema(description = "계정", example = "username")
	@MemberUsername(nullable = false,
			notBlankMessage = "계정은 비어있을 수 없습니다.",
			overLengthMessage = "계정은 30자를 초과할 수 없습니다.",
			notFormatMessage = "계정이 형식에 맞지 않습니다.")
	private String username;
	
	@Schema(description = "비밀번호", example = "password")
	@MemberPassword(nullable = false,
			notBlankMessage = "비밀번호는 비어있을 수 없습니다.",
			underLengthMessage = "비밀번호는 8자를 미만일 수 없습니다.")
	private String password;
	
	@Schema(description = "이름", example = "name")
	@MemberName(nullable = false,
			notBlankMessage = "이름은 비어있을 수 없습니다.",
			overLengthMessage = "이름은 30자를 초과할 수 없습니다.")
	private String name;
	
	@Schema(description = "성별", example = "MAIL")
	@NotNull(message = "성별은 비어있을 수 없습니다.")
	private Gender gender;
	
	@Schema(description = "이메일", example = "email@example.com")
	@MemberEmail(nullable = false,
			notBlankMessage = "이메일은 비어있을 수 없습니다.",
			overLengthMessage = "이메일은 30자를 초과할 수 없습니다.",
			notFormatMessage = "이메일이 형식에 맞지 않습니다.")
	private String email;
	
	@Schema(description = "그룹", example = "ADMIN")
	@NotNull(message = "그룹은 비어있을 수 없습니다.")
	private Group group;
	
}
