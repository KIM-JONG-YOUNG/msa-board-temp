package com.jong.msa.board.client.core.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.jong.msa.board.common.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(requestWrapper, responseWrapper);

		log.info(StringUtils.concat("Request Info",
				StringUtils.concat("\n\t- Method : ", requestWrapper.getMethod()),
				StringUtils.concat("\n\t- URL    : ", getURL(requestWrapper)),
				StringUtils.concat("\n\t- Header : ", getHeaderMap(requestWrapper)),
				StringUtils.concat("\n\t- Body   : ", getContent(requestWrapper))));

		log.info(StringUtils.concat("Response Info",
				StringUtils.concat("\n\t- Status : ", responseWrapper.getStatus()),
				StringUtils.concat("\n\t- Header : ", getHeaderMap(responseWrapper)),
				StringUtils.concat("\n\t- Body   : ", getContent(responseWrapper))));

		responseWrapper.copyBodyToResponse();
	}
	
	private String getURL(ContentCachingRequestWrapper request) {
		
		String requestURI = request.getRequestURI();
		String requestQuery = request.getQueryString();
		
		return StringUtils.concat(requestURI, (requestQuery == null) ? null : "?", requestQuery);
	}
	
	private Map<String, Collection<String>> getHeaderMap(ContentCachingRequestWrapper request) {
		
		Map<String, Collection<String>> headerMap = new HashMap<>();

		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			
			String headerName = headerNames.nextElement();
			List<String> headerValues = Collections.list(request.getHeaders(headerName));

			headerMap.put(headerName, headerValues);
		}
		
		return headerMap;
	}
	
	private String getContent(ContentCachingRequestWrapper request) {
		
		return new String(request.getContentAsByteArray());
	}

	private Map<String, Collection<String>> getHeaderMap(ContentCachingResponseWrapper response) {
		
		Map<String, Collection<String>> headerMap = new HashMap<>();

		Collection<String> headerNames = response.getHeaderNames();

		headerNames.stream().forEach(headerName -> {
			headerMap.put(headerName, response.getHeaders(headerName));
		});
		
		return headerMap;
	}
	
	private String getContent(ContentCachingResponseWrapper request) {
		
		return new String(request.getContentAsByteArray());
	}

}
 