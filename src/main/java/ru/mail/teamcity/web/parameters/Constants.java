package ru.mail.teamcity.web.parameters;

import org.jetbrains.annotations.NotNull;

/**
 * User: g.chernyshev
 * Date: 23/10/16
 * Time: 23:11
 */
public class Constants {
    @NotNull
    public final static String PARAMETER_TYPE = "webPopulatedSelect";
    @NotNull
    public final static String PARAMETER_DESCRIPTION = "Web populated select";
    @NotNull
    public final static String URL_PARAMETER = "url";
    @NotNull
    public final static String TIMEOUT_PARAMETER = "timeout";
    @NotNull
    public final static String DEFAULT_TIMEOUT_PARAMETER = Integer.toString(60 * 1000);
    @NotNull
    public final static String METHOD_PARAMETER = "method";
    @NotNull
    public final static String DEFAULT_METHOD_PARAMETER = "GET";
    @NotNull
    public final static String PAYLOAD_PARAMETER = "payload";
    @NotNull
    public final static String HEADERS_PARAMETER = "headers";
    @NotNull
    public final static String HEADERS_SEPARATOR = "\n";
    @NotNull
    public final static String HEADERS_NAME_VALUE_SEPARATOR = ":";
    @NotNull
    public final static String USERNAME_PARAMETER = "username";
    @NotNull
    public final static String PASSWORD_PARAMETER = "password";
    @NotNull
    public final static String FORMAT_PARAMETER = "format";
    @NotNull
    public final static String MULTIPLE_PARAMETER = "multiple";
    @NotNull
    public final static String VALUE_SEPARATOR_PARAMETER = "valueSeparator";
    @NotNull
    public final static String DEFAULT_VALUE_SEPARATOR = ",";
    @NotNull
    public final static String ENABLE_EDIT_ON_ERROR_PARAMETER = "enableEditOnError";
    @NotNull
    public final static String TAG_SUPPORT_PARAMETER = "tagSupport";
    @NotNull
    public final static String OPTIONS_NAME = "options";
    @NotNull
    public final static String VALUES_NAME = "values";
    @NotNull
    public final static String ERRORS_NAME = "errors";
    @NotNull
    public final static String PARSERS_NAME = "parsers";
    @NotNull
    public static final String EMPTY_STRING = "";
    @NotNull
    public static final String BUILD_TYPE_ID = "buildTypeId";
}
