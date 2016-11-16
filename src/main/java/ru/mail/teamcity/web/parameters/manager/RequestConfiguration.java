package ru.mail.teamcity.web.parameters.manager;

import com.google.common.base.Splitter;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.parameters.ValueResolver;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.parser.OptionParser;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static ru.mail.teamcity.web.parameters.Constants.*;

/**
 * User: g.chernyshev
 * Date: 26/10/16
 * Time: 19:23
 */
public class RequestConfiguration {

    public enum Method {GET, POST}

    @NotNull
    private final static Integer DEFAULT_TIMEOUT = 60 * 1000;
    @NotNull
    private final static String HEADERS_SEPARATOR = "\n";
    @NotNull
    private final static String HEADERS_NAME_VALUE_SEPARATOR = ":";
    @NotNull
    private final static Boolean DEFAULT_MULTIPLE = Boolean.FALSE;
    @NotNull
    public final static String DEFAULT_MULTIPLE_SEPARATOR = ",";
    @NotNull
    private final static Boolean DEFAULT_TAG_SUPPORT = Boolean.FALSE;
    @NotNull
    private final static Boolean DEFAULT_EDIT_ON_ERROR = Boolean.FALSE;

    @NotNull
    private static final String EMPTY_STRING = "";

    @NotNull
    private final ValueExtractor extractor;

    @Nullable
    private String url;
    @Nullable
    private Integer timeout;
    @Nullable
    private Method method;
    @Nullable
    private String payload;
    @Nullable
    private Collection<Header> headers;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String format;
    @Nullable
    private String transform;
    @Nullable
    private Boolean multiple;
    @Nullable
    private String multipleSeparator;
    @Nullable
    private Boolean tagSupport;
    @Nullable
    private Boolean enableEditOnError;

    public RequestConfiguration(@NotNull Map<String, String> stringMap, @NotNull ValueResolver resolver) {
        this.extractor = new ValueExtractor(stringMap, resolver);
    }

    public void process() {
        this.url = extractor.getExpandedRequiredValue(URL_PARAMETER);
        this.timeout = extractor.getIntegerValue(TIMEOUT_PARAMETER, DEFAULT_TIMEOUT);
        this.method = Method.valueOf(extractor.getValue(METHOD_PARAMETER, Method.GET.toString()));
        this.payload = extractor.getExpandedValue(PAYLOAD_PARAMETER, EMPTY_STRING);
        this.headers = extractHeaders();
        this.username = extractor.getExpandedValue(USERNAME_PARAMETER, EMPTY_STRING);
        this.password = extractor.getExpandedValue(PASSWORD_PARAMETER, EMPTY_STRING);
        this.format = extractor.getRequiredValue(FORMAT_PARAMETER);
        this.transform = extractor.getExpandedValue(TRANSFORM_PARAMETER, EMPTY_STRING);
        this.multiple = extractor.getBoolValue(MULTIPLE_PARAMETER, DEFAULT_MULTIPLE);
        this.multipleSeparator = extractor.getValue(MULTIPLE_SEPARATOR_PARAMETER, DEFAULT_MULTIPLE_SEPARATOR);
        this.tagSupport = extractor.getBoolValue(TAG_SUPPORT_PARAMETER, DEFAULT_TAG_SUPPORT);
        this.enableEditOnError = extractor.getBoolValue(ENABLE_EDIT_ON_ERROR_PARAMETER, DEFAULT_EDIT_ON_ERROR);
    }


    @NotNull
    private Collection<Header> extractHeaders() {
        String headers = extractor.getValue(HEADERS_PARAMETER, EMPTY_STRING);
        if (StringUtil.isEmpty(headers)) {
            return Collections.emptyList();
        }

        try {
            Collection<Header> result = new ArrayList<>();
            Map<String, String> headersMap = Splitter.on(HEADERS_SEPARATOR).
                    trimResults().
                    omitEmptyStrings().
                    withKeyValueSeparator(Splitter.on(HEADERS_NAME_VALUE_SEPARATOR).limit(2)).
                    split(headers);

            for (Map.Entry<String, String> header : headersMap.entrySet()) {
                String key = extractor.resolve(header.getKey());
                String value = extractor.resolve(header.getValue());
                result.add(new BasicHeader(key, value));
            }
            return result;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RequestConfigurationException(String.format("Incorrect header detected: '%s'", e.toString()));
        }
    }

    @Nullable
    String getUrl() {
        return url;
    }

    @NotNull
    Integer getTimeout() {
        if (null == timeout) {
            return DEFAULT_TIMEOUT;
        }
        return timeout;
    }

    @NotNull
    Method getMethod() {
        if (null == method) {
            return Method.GET;
        }
        return method;
    }

    @Nullable
    String getPayload() {
        return payload;
    }

    @NotNull
    Collection<Header> getHeaders() {
        if (null == headers) {
            return Collections.emptyList();
        }
        return headers;
    }

    @Nullable
    String getBasicAuthorisation() {
        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
            String auth = String.format("%s:%s", username, password);
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
            return String.format("Basic %s", new String(encodedAuth));
        }
        return null;
    }

    @NotNull
    OptionParser getParser() {
        if (null == format) {
            throw new RequestConfigurationException("Format parameter for option parser was not initialized!");
        }
        OptionParser parser = ParserFactory.getOptionParser(format);
        if (null == parser) {
            throw new RequestConfigurationException(String.format("Failed to find suitable parser by '%s' name!", format));
        }
        return parser;
    }

    @Nullable
    public String getTransform() {
        return transform;
    }

    @NotNull
    public Boolean getMultiple() {
        if (this.multiple == null) {
            return DEFAULT_MULTIPLE;
        }
        return multiple;
    }

    @NotNull
    public String getMultipleSeparator() {
        if (this.multipleSeparator == null) {
            return DEFAULT_MULTIPLE_SEPARATOR;
        }
        return multipleSeparator;
    }

    @NotNull
    public Boolean getTagSupport() {
        if (this.tagSupport == null) {
            return DEFAULT_TAG_SUPPORT;
        }
        return tagSupport;
    }

    @NotNull
    public Boolean getEnableEditOnError() {
        if (this.enableEditOnError == null) {
            return DEFAULT_EDIT_ON_ERROR;
        }
        return enableEditOnError;
    }

    @Override
    public String toString() {
        return "RequestConfiguration{" +
                "url='" + url + '\'' +
                ", timeout=" + timeout +
                ", method=" + method +
                ", payload='" + payload + '\'' +
                ", headers=" + headers +
                ", format='" + format + '\'' +
                ", multiple=" + multiple +
                ", multipleSeparator='" + multipleSeparator + '\'' +
                ", tagSupport=" + tagSupport +
                ", enableEditOnError=" + enableEditOnError +
                '}';
    }
}


class RequestConfigurationException extends IllegalArgumentException {
    RequestConfigurationException(String s) {
        super(s);
    }
}