package ru.mail.teamcity.web.parameters.manager;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
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

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 17:34
 */
public class WebOptionsManagerImpl implements WebOptionsManager {

    private final static Logger LOG = Logger.getInstance(WebOptionsManagerImpl.class.getName());

    @NotNull
    public Options read(
            @NotNull RequestConfiguration configuration,
            @NotNull Map<String, String> errors
    ) {
        Options options;
        HttpClient httpClient = HttpClientBuilder.create().build();

        @NotNull HttpRequestBase request;
        try {
            request = getRequest(configuration);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            e.printStackTrace();
            errors.put("Failed to initialize request", e.toString());
            return Options.empty();
        }

        if (!StringUtil.isEmpty(configuration.getBasicAuthorisation())) {
            request.setHeader(HttpHeaders.AUTHORIZATION, configuration.getBasicAuthorisation());
        }

        configuration.getHeaders().forEach(request::addHeader);

        final RequestConfig params = RequestConfig.custom().setConnectTimeout(configuration.getTimeout()).setSocketTimeout(configuration.getTimeout()).build();
        request.setConfig(params);
        LOG.debug(String.format("Requesting dynamic parameters from %s", configuration.getUrl()));

        HttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            errors.put("Failed to execute request", e.toString());
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

        options = configuration.getParser().parse(content, errors);
        request.releaseConnection();

        return null == options ? Options.empty() : options;
    }

    @NotNull
    private HttpRequestBase getRequest(@NotNull RequestConfiguration configuration) throws UnsupportedEncodingException, IllegalArgumentException {
        if (configuration.getMethod().name().equalsIgnoreCase(HttpGet.METHOD_NAME)) {
            return new HttpGet(configuration.getUrl());
        } else if (configuration.getMethod().name().equalsIgnoreCase(HttpPost.METHOD_NAME)) {
            HttpPost request = new HttpPost(configuration.getUrl());
            if (!StringUtil.isEmpty(configuration.getPayload())) {
                request.setEntity(new StringEntity(configuration.getPayload()));
            }
            return request;
        } else {
            throw new IllegalArgumentException(String.format("Request method got unexpected value '%s'!", configuration.getMethod().name()));
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
}
