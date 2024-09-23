package com.jong.msa.board.client.core.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.client.core.response.ErrorResponse;
import com.jong.msa.board.client.core.swagger.ApiErrorResponses;
import com.jong.msa.board.client.core.swagger.ApiErrorResponses.ApiErrorResponse;
import com.jong.msa.board.common.constants.DateTimeFormats;
import com.jong.msa.board.common.constants.HeaderNames;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	private List<ApiErrorResponse> findAllApiErrorResponse(HandlerMethod handlerMethod) {
	
    	List<ApiErrorResponse> apiErrorResponseList = new ArrayList<>();

    	ApiErrorResponses classApiErrorResponses = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ApiErrorResponses.class);
    	ApiErrorResponses methodApiErrorResponses = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ApiErrorResponses.class);

    	if (classApiErrorResponses != null) apiErrorResponseList.addAll(Arrays.asList(classApiErrorResponses.value()));
    	if (methodApiErrorResponses != null) apiErrorResponseList.addAll(Arrays.asList(methodApiErrorResponses.value()));
    	
    	return apiErrorResponseList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void convertAllDateSchemaToStringSchema(Map<String, Schema> properties) {

		if (properties != null) {
			
			properties.entrySet().forEach(entry -> {
				
				Schema schema = entry.getValue();
				String format = schema.getFormat();
				
				if ("time".equals(format)) {
				
					entry.setValue(new StringSchema()
							.name(schema.getName())
							.description(schema.getDescription())
							.format(DateTimeFormats.TIME_FORMAT)
							.example(LocalTime.now().format(DateTimeFormats.TIME_FORMATTER)));
				
				} else if ("date".equals(format)) {
					
					entry.setValue(new StringSchema()
							.name(schema.getName())
							.description(schema.getDescription())
							.format(DateTimeFormats.DATE_FORMAT)
							.example(LocalDate.now().format(DateTimeFormats.DATE_FORMATTER)));
				
				} else if ("date-time".equals(format)) {
				
					entry.setValue(new StringSchema()
							.name(schema.getName())
							.description(schema.getDescription())
							.format(DateTimeFormats.DATE_TIME_FORMAT)
							.example(LocalDateTime.now().format(DateTimeFormats.DATE_TIME_FORMATTER)));
				
				} else {
					
					convertAllDateSchemaToStringSchema(schema.getProperties());
				}	
			});
		}
	} 
	
	@Bean
	OpenAPI openAPI() {

		SecurityScheme securityScheme = new SecurityScheme()
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.HEADER)
				.name(HeaderNames.ACCESS_TOKEN);
		
        return new OpenAPI().components(new Components()
        		.addSecuritySchemes(HeaderNames.ACCESS_TOKEN, securityScheme));
	}

	@Bean
	OperationCustomizer operationCustomizer(ObjectMapper objectMapper) {

	    return (Operation operation, HandlerMethod handlerMethod) -> {

	    	ApiResponses responses = operation.getResponses();

	    	List<ApiErrorResponse> apiErrorResponseList = findAllApiErrorResponse(handlerMethod);
	    	Map<String, List<ApiErrorResponse>> apiErrorResponseMap = apiErrorResponseList.stream()
	    			.collect(Collectors.groupingBy(x -> x.responseCode()));

	    	apiErrorResponseMap.forEach((responseCode, responseList) -> {
	    		
	    		io.swagger.v3.oas.models.responses.ApiResponse response = new io.swagger.v3.oas.models.responses.ApiResponse();
	    		io.swagger.v3.oas.models.media.Content content = new io.swagger.v3.oas.models.media.Content();
	    		io.swagger.v3.oas.models.media.MediaType mediaType = new io.swagger.v3.oas.models.media.MediaType();

	    		responseList.stream().forEach(x -> {
	    				
					try {
						
						ErrorResponse errorResponse = ErrorResponse.builder().errorCode(x.errorCode()).build();
						
						mediaType.addExamples(errorResponse.getCode(), 
								new io.swagger.v3.oas.models.examples.Example().value(objectMapper.writeValueAsString(errorResponse)));
	
					} catch (JsonProcessingException e) {
						
						throw new RuntimeException(e);
					}
				});
	    		
	    		responses.addApiResponse(responseCode, response.content(content.addMediaType(MediaType.APPLICATION_JSON_VALUE, mediaType)));
	    	});
	    	
	        return operation;
	    };
	}
	
	@Bean
	OpenApiCustomiser openApiCustomiser() {
		
		return openApi -> convertAllDateSchemaToStringSchema(openApi.getComponents().getSchemas());
	}
	
}
