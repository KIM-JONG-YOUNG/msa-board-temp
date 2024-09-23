package com.jong.msa.board.client.core.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jong.msa.board.client.core.filter.LoggingFilter;
import com.jong.msa.board.common.constants.DateTimeFormats;

@Configuration
public class WebCoreConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/**")     	
			.allowedOriginPatterns("*")	
			.allowedMethods("*")       	
			.allowedHeaders("*")       	
			.exposedHeaders("*");      	
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {

		registry.addConverter(String.class, LocalTime.class, x -> LocalTime.parse(x, DateTimeFormats.TIME_FORMATTER));
		registry.addConverter(String.class, LocalDate.class, x -> LocalDate.parse(x, DateTimeFormats.DATE_FORMATTER));
		registry.addConverter(String.class, LocalDateTime.class, x -> LocalDateTime.parse(x, DateTimeFormats.DATE_TIME_FORMATTER));
		
		WebMvcConfigurer.super.addFormatters(registry);
	}

	@Bean
	Jackson2ObjectMapperBuilderCustomizer customizer() {

		return builder-> builder
				.featuresToDisable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)	
				.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)	
				.serializationInclusion(JsonInclude.Include.NON_NULL)					
				.serializationInclusion(JsonInclude.Include.NON_EMPTY)					
				.modules(new JavaTimeModule())
				.serializers(new LocalTimeSerializer(DateTimeFormats.TIME_FORMATTER))
		        .serializers(new LocalDateSerializer(DateTimeFormats.DATE_FORMATTER))
		        .serializers(new LocalDateTimeSerializer(DateTimeFormats.DATE_TIME_FORMATTER))
		        .deserializers(new LocalTimeDeserializer(DateTimeFormats.TIME_FORMATTER))
		        .deserializers(new LocalDateDeserializer(DateTimeFormats.DATE_FORMATTER))
		        .deserializers(new LocalDateTimeDeserializer(DateTimeFormats.DATE_TIME_FORMATTER))
				.build();
	}
	
	@Bean
	FilterRegistrationBean<LoggingFilter> loggingFilterRegistrationBean(LoggingFilter filter) {
		
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/apis/*");

        return registrationBean;
	}
	
}
