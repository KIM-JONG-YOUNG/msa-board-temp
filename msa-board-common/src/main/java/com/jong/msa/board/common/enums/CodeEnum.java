package com.jong.msa.board.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface CodeEnum<V> {

	V getCode();
	
	@Getter
	@ToString
	@RequiredArgsConstructor
	public enum Gender implements CodeEnum<Character> {
		
		MAIL('M'), FEMAIL('F');
		
		private final Character code;
	}

	@Getter
	@ToString
	@RequiredArgsConstructor
	public enum Group implements CodeEnum<Integer> {
		
		ADMIN(1), USER(2);
		
		private final Integer code;
	}

	@Getter
	@ToString
	@RequiredArgsConstructor
	public enum State implements CodeEnum<Integer> {
		
		ACTIVE(1), DEACTIVE(0);
		
		private final Integer code;
	}

	@Getter
	@ToString
	@RequiredArgsConstructor
	public enum ErrorCode implements CodeEnum<String> {

		// Parameter Error Code
		INVALID_PARAMETER("PARAM-001", "파라미터가 유효하지 않습니다."),
		
		// Access Error Code
		NOT_ACCESSABLE_URL("ACCESS-001", "접근할 수 없는 URL입니다."),

		// Access Token Error Code
		EXPIRED_ACCESS_TOKEN("TOKEN-001", "만료된 Access Token 입니다."),
		REVOKED_ACCESS_TOKEN("TOKEN-002", "취소된 Access Token 입니다."),
		INVALID_ACCESS_TOKEN("TOKEN-003", "유효하지 않은 Access Token 입니다."),

		// Refresh Token Error Code
		EXPIRED_REFRESH_TOKEN("TOKEN-011", "만료된 Refresh Token 입니다."),
		REVOKED_REFRESH_TOKEN("TOKEN-012", "취소된 Refresh Token 입니다."),
		INVALID_REFRESH_TOKEN("TOKEN-013", "유효하지 않은 Refresh Token 입니다."),

		NOT_ADMIN_GROUP_MEMBER_REFRESH_TOKEN("TOKEN-111", "관리자 그룹 회원의 Refresh Token 입니다."),
		NOT_USER_GROUP_MEMBER_REFRESH_TOKEN("TOKEN-211", "일반 그룹 회원의 Refresh Token 입니다."),

		// Member Common Error Code
		NOT_FOUND_MEMBER("MEMBER-001", "존재하지 않는 회원입니다."),
		NOT_FOUND_MEMBER_USERNAME("MEMBER-002", "존재하지 않는 계정입니다."),
		NOT_ACTIVE_MEMBER_USERNAME("MEMBER-003", "비활성화된 회원입니다."),
		EXISTS_MEMBER_USERNAME("MEMBER-004", "동일한 계정의 회원이 존재합니다."),
		NOT_MATCHED_MEMBER_PASSWORD("MEMBER-005", "비밀번호가 일치하지 않습니다."),

		// Member Error Code For Admin
		NOT_ADMIN_GROUP_MEMBER_USERNAME("MEMBER-101", "비활성화된 회원입니다."),
		NOT_USER_GROUP_MEMBER("MEMBER-102", "일반 그룹 회원이 아닙니다."),

		// Member Error Code For User
		NOT_USER_GROUP_MEMBER_USERNAME("MEMBER-201", "비활성화된 회원입니다."),
		
		// Post Common Error Code
		NOT_FOUND_POST("POST-001", "게시글이 존재하지 않습니다."),
		NOT_FOUND_POST_WRITER("POST-002", "작성자가 존재하지 않습니다."),

		// Post Error Code For Admin
		NOT_ADMIN_POST("POST-101", "관리자 게시글이 아닙니다."),
	
		// Post Error Code For User
		NOT_ACTIVE_POST("POST-201", "비활성화된 게시글입니다."),
		NOT_POST_WRITER("POST-202", "게시글 작성자가 아닙니다."),

		// System Error Code
		UNCHECKED_INTERNAL_ERROR("SYSTEM-001", "내부 시스템에 오류가 발생했습니다."),
		UNCHECKED_EXTERNAL_ERROR("SYSTEM-002", "외부 시스템에 오류가 발생했습니다.");
		
		private final String code;
		private final String message;

	}
	
}
