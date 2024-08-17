package ru.dstreltsov.transferapi.logging;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
@NoArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_FORMAT =
            "Before request [{} {}]\n" +
                    "Content-Type: {}\n" +
                    "Headers: {}\n" +
                    "Query: {}\n" +
                    "Body: {}";
    private static final String RESPONSE_FORMAT =
            "Response [{} {}]\n" +
                    "Response-Code: {}\n" +
                    "Content-Type: {}\n" +
                    "Body: {}";

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private static boolean isMultipartType(String contentType) {
        return contentType != null && contentType.startsWith("multipart/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final boolean multipartRequest = isMultipartType(request.getContentType());
        final HttpServletRequest requestToUse = multipartRequest ? request : new MyContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        logRequestDetails(requestToUse, multipartRequest);

        filterChain.doFilter(requestToUse, responseWrapper);

        logResponseDetails(requestToUse, responseWrapper);

        responseWrapper.copyBodyToResponse();
    }

    private void logRequestDetails(HttpServletRequest request, boolean multipartRequest) throws IOException {
        log.info(REQUEST_FORMAT,
                request.getMethod(),
                request.getRequestURI(),
                request.getContentType(),
                new ServletServerHttpRequest(request).getHeaders(),
                StringUtils.trimToEmpty(request.getQueryString()),
                multipartRequest
                        ? StringUtils.EMPTY
                        : inputStreamToString(request.getInputStream()));
    }

    private void logResponseDetails(HttpServletRequest request, ContentCachingResponseWrapper response) {
        log.info(RESPONSE_FORMAT,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                response.getContentType(),
                isMultipartType(response.getContentType())
                        ? StringUtils.EMPTY
                        : new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }
}
