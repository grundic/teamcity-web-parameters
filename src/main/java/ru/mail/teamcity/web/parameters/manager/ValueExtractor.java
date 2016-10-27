package ru.mail.teamcity.web.parameters.manager;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.parameters.ValueResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 26/10/16
 * Time: 23:28
 */
public class ValueExtractor {
    @NotNull
    private final Map<String, String> stringMap;
    @NotNull
    private final ValueResolver resolver;

    ValueExtractor(@NotNull Map<String, String> stringMap, @NotNull ValueResolver resolver) {
        this.stringMap = stringMap;
        this.resolver = resolver;
    }

    @NotNull
    public static String getValue(@NotNull Map<String, String> stringMap, @NotNull String key, @NotNull String defaultValue) {
        String value = stringMap.get(key);
        if (null == value) {
            return defaultValue;
        }
        return value;
    }

    @NotNull
    String resolve(@NotNull String value) {
        return resolver.resolve(value).getResult();
    }

    @NotNull
    String getRequiredValue(@NotNull String key) {
        String value = stringMap.get(key);
        if (StringUtil.isEmptyOrSpaces(value)) {
            throw new RequestConfigurationException(String.format("Required parameter '%s' was not given!", key));
        }
        return value;
    }

    @NotNull
    String getExpandedRequiredValue(@NotNull String key) {
        String value = getRequiredValue(key);
        String expanded = resolve(value);
        if (StringUtil.isEmptyOrSpaces(expanded)) {
            throw new RequestConfigurationException(String.format("Required parameter '%s' was expanded to nothing!", key));
        }
        return expanded;
    }


    @NotNull
    String getValue(@NotNull String key, @NotNull String defaultValue) {
        return getValue(this.stringMap, key, defaultValue);
    }

    @NotNull
    String getExpandedValue(@NotNull String key, @NotNull String defaultValue) {
        String value = getValue(key, defaultValue);
        return resolve(value);
    }

    @NotNull
    Boolean getBoolValue(@NotNull String key, @NotNull Boolean defaultValue) {
        String value = getValue(key, defaultValue.toString());
        return Boolean.parseBoolean(value);
    }

    @NotNull
    Integer getIntegerValue(@NotNull String key, @NotNull Integer defaultValue) {
        String value = getValue(key, defaultValue.toString());
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
