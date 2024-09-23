package com.jong.msa.board.client.core.config;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.core.response.ErrorResponse;
import com.jong.msa.board.common.constants.DateTimeFormats;
import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.utils.EnumUtils;
import com.jong.msa.board.common.utils.StringUtils;

import feign.FeignException;
import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;

@Configuration
@EnableFeignClients(basePackages = PackageNames.FEIGN_PACKAGE)
public class FeignClientConfig {

	@Bean
	Logger.Level feginLoggingLevel() {
		
		return Logger.Level.FULL;
	}
	
	@Bean
	FeignFormatterRegistrar localDateFeignFormatterRegister() {

		return registry -> {

			DateTimeFormatterRegistrar localDateTimeRegistrar = new DateTimeFormatterRegistrar();
			localDateTimeRegistrar.setTimeFormatter(DateTimeFormats.TIME_FORMATTER);
			localDateTimeRegistrar.setDateFormatter(DateTimeFormats.DATE_FORMATTER);
			localDateTimeRegistrar.setDateTimeFormatter(DateTimeFormats.DATE_TIME_FORMATTER);
			localDateTimeRegistrar.registerFormatters(registry);
		};
	}
	
	@Bean
	ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
		
		return new ErrorDecoder() {

			@Override
			public Exception decode(String methodKey, Response response) {

				try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {

					ErrorResponse errorResponse = objectMapper.readValue(IOUtils.toString(reader), ErrorResponse.class);
					ErrorCode errorCode = EnumUtils.converToCodeEnum(errorResponse.getCode(), ErrorCode.class);
					HttpStatus errorStatus = HttpStatus.valueOf(response.status());
					
					boolean hasErrorDetails = errorResponse.getDetailsList() != null && errorResponse.getDetailsList().size() > 0;
					
					if (hasErrorDetails) {
						return new RestServiceException(errorStatus, errorCode);
					} else {
						return new RestServiceException(errorStatus, errorCode, errorResponse.getDetailsList().stream()
								.map(x -> (StringUtils.isBlank(x.getField()))
										? new ObjectError("request", x.getMessage())
										: new FieldError("request", x.getField(), x.getMessage()))
								.collect(Collectors.toList()));
					}

				} catch (Exception e) {
					
					return FeignException.errorStatus(methodKey, response);
				}
			}
		};
	}
	
}
