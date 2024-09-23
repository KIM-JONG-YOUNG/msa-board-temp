package com.jong.msa.board.client.core.response;

import java.time.LocalDateTime;
import java.util.List;

import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private String code;
	
	private String message;
	
	private List<Details> detailsList;
	
	private LocalDateTime timestamp;
	
	@Builder
	protected ErrorResponse(ErrorCode errorCode, List<Details> errorDetailsList) {

		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.detailsList = errorDetailsList;
		this.timestamp = LocalDateTime.now();
	}
	
	@Getter
	@Builder
	@ToString
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Details {

		private String field;
		
		private String message;

	}

}
