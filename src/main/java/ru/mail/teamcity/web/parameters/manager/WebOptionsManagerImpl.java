package ru.mail.teamcity.web.parameters.manager;

import com.google.common.base.Splitter;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.parser.OptionParser;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static ru.mail.teamcity.web.parameters.Constants.*;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 17:34
 */
public class WebOptionsManagerImpl implements WebOptionsManager {

    private final static Logger LOG = Logger.getInstance(WebOptionsManagerImpl.class.getName());

    @NotNull
    public Options read(
            @NotNull String url,
            @NotNull Map<String, String> extraOptions,
            @NotNull String format,
            @NotNull Map<String, String> errors
    ) {
        Options options;
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpRequestBase request;
        try {
            request = getRequest(url, extraOptions.get(METHOD_PARAMETER), extraOptions.get(PAYLOAD_PARAMETER));
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            e.printStackTrace();
            errors.put("Failed to initialize request", e.getMessage() != null ? e.getMessage() : e.getCause().getMessage());
            return Options.empty();
        }

        String headers = extraOptions.get(HEADERS_PARAMETER);
        if (null != headers) {
            try {
                Map<String, String> headersMap = Splitter.on(HEADERS_SEPARATOR).
                        trimResults().
                        omitEmptyStrings().
                        withKeyValueSeparator(Splitter.on(HEADERS_NAME_VALUE_SEPARATOR).limit(2)).
                        split(headers);

                for (Map.Entry<String, String> header : headersMap.entrySet()) {
                    request.addHeader(header.getKey(), header.getValue());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                errors.put("Incorrect header", e.toString());
            }
        }

        String stringTimeout = extraOptions.get(TIMEOUT_PARAMETER);
        int timeout;
        try {
            timeout = Integer.valueOf(stringTimeout);
        } catch (NumberFormatException e) {
            timeout = Integer.valueOf(DEFAULT_TIMEOUT_PARAMETER);
        }

        final RequestConfig params = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
        request.setConfig(params);
        LOG.debug(String.format("Requesting parameters from %s", url));

        HttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            errors.put("Failed to execute request", e.getMessage() != null ? e.getMessage() : e.getCause().getMessage());
            LOG.error(e);
            return Options.empty();
        }

        int code = response.getStatusLine().getStatusCode();
        if (code != 200) {
            errors.put(String.format("Unexpected status code %d", code), response.getStatusLine().getReasonPhrase());
            LOG.error("Method execution failed: " + response.getStatusLine());
            return Options.empty();
        }

        InputStream content;
        try {
            content = response.getEntity().getContent();
        } catch (IOException e) {
            errors.put("Failed to read content", e.getMessage() != null ? e.getMessage() : e.getCause().getMessage());
            LOG.error(e);
            return Options.empty();
        }

        options = parse(content, format, errors);
        request.releaseConnection();

        return null == options ? Options.empty() : options;
    }

    @NotNull
    private HttpRequestBase getRequest(@NotNull String url, @NotNull String method, @Nullable String payload) throws UnsupportedEncodingException, IllegalArgumentException {
        if (method.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
            return new HttpGet(url);
        } else if (method.equalsIgnoreCase(HttpPost.METHOD_NAME)) {
            HttpPost request = new HttpPost(url);
            if (null != payload) {
                request.setEntity(new StringEntity(payload));
            }
            return request;
        } else {
            throw new IllegalArgumentException(String.format("Request method got unexpected value '%s'!", method));
        }
    }

    @Nullable
    public OptionParser guess(@NotNull String url, @NotNull Map<String, String> errors) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
        LOG.debug(String.format("Requesting parameters from %s", url));
        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("Method failed: " + response.getStatusLine());
                return null;
            }
            String content;
            try {
                content = IOUtils.toString(response.getEntity().getContent());
            } catch (IOException e) {
                errors.put("Failed to convert stream to string", e.getMessage());
                return null;
            }
            for (OptionParser parser : ParserFactory.registry) {
                InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

                Map<String, String> parseErrors = new HashMap<>();
                if (null != parser.parse(stream, parseErrors)) {
                    return parser;
                }
            }
        } catch (IOException e) {
            errors.put("Failed to execute request", e.getMessage());
        } finally {
            getRequest.releaseConnection();
        }
        return null;
    }

    @Nullable
    private Options parse(@NotNull InputStream inputStream, @NotNull String format, @NotNull Map<String, String> errors) {
        OptionParser parser = ParserFactory.getOptionParser(format);
        if (null != parser) {
            return parser.parse(inputStream, errors);
        }
        return null;
    }
}
