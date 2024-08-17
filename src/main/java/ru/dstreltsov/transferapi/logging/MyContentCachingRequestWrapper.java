package ru.dstreltsov.transferapi.logging;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyContentCachingRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedContent;

    public MyContentCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cachedContent = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new MyCachedServletInputStream(cachedContent);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(cachedContent)));
    }
}
