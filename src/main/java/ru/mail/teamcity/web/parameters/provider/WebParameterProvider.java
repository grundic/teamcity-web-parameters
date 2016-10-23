package ru.mail.teamcity.web.parameters.provider;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import jetbrains.buildServer.controllers.parameters.InvalidParametersException;
import jetbrains.buildServer.controllers.parameters.ParameterEditContext;
import jetbrains.buildServer.controllers.parameters.ParameterRenderContext;
import jetbrains.buildServer.controllers.parameters.api.ParameterControlProviderAdapter;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.manager.WebOptionsManager;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.mail.teamcity.web.parameters.Constants.*;

/**
 * User: g.chernyshev
 * Date: 27.06.14
 * Time: 17:51
 */
public class WebParameterProvider extends ParameterControlProviderAdapter {

    @NotNull
    private final PluginDescriptor pluginDescriptor;
    @NotNull
    private final WebOptionsManager webOptionsManager;
    @NotNull
    private final ProjectManager projectManager;
    @NotNull
    private final Map<String, String> errors;


    public WebParameterProvider(
            @NotNull PluginDescriptor pluginDescriptor,
            @NotNull WebOptionsManager webOptionsManager,
            @NotNull ProjectManager projectManager) {
        this.pluginDescriptor = pluginDescriptor;
        this.webOptionsManager = webOptionsManager;
        this.projectManager = projectManager;
        this.errors = new HashMap<>();
    }

    @NotNull
    @Override
    public String getParameterType() {
        return PARAMETER_TYPE;
    }

    @NotNull
    @Override
    public String getParameterDescription() {
        return "Web populated select";
    }

    @NotNull
    private String getValue(@NotNull Map<String, String> config, @NotNull String key, @NotNull String defaultValue) {
        String value = config.get(key);
        if (null == value) {
            return defaultValue;
        }
        return value;
    }

    private Boolean getBoolValue(@NotNull Map<String, String> config, @NotNull String key, @NotNull String defaultValue) {
        String value = getValue(config, key, defaultValue);
        return Boolean.parseBoolean(value);
    }

    @NotNull
    @Override
    public ModelAndView renderControl(@NotNull HttpServletRequest request, @NotNull ParameterRenderContext context) throws InvalidParametersException {
        ModelAndView modelAndView = new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterControl.jsp"));

        Map<String, String> config = context.getDescription().getParameterTypeArguments();
        Map<String, String> extraOptions = new HashMap<>();

        String buildTypeId = request.getParameter(BUILD_TYPE_ID);
        SBuildType buildType = projectManager.findBuildTypeByExternalId(buildTypeId);
        assert buildType != null;

        String urlRaw = getValue(config, URL_PARAMETER, EMPTY_STRING);
        String url = buildType.getValueResolver().resolve(urlRaw).getResult();

        // extra parameters
        String timeout = getValue(config, TIMEOUT_PARAMETER, DEFAULT_TIMEOUT_PARAMETER);
        extraOptions.put(TIMEOUT_PARAMETER, timeout);

        String method = getValue(config, METHOD_PARAMETER, DEFAULT_METHOD_PARAMETER);
        extraOptions.put(METHOD_PARAMETER, method);

        String payloadRaw = getValue(config, PAYLOAD_PARAMETER, EMPTY_STRING);
        String payload = buildType.getValueResolver().resolve(payloadRaw).getResult();
        extraOptions.put(PAYLOAD_PARAMETER, payload);

        String format = getValue(config, FORMAT_PARAMETER, EMPTY_STRING);
        Boolean multiple = getBoolValue(config, MULTIPLE_PARAMETER, EMPTY_STRING);
        String separator = getValue(config, VALUE_SEPARATOR_PARAMETER, DEFAULT_VALUE_SEPARATOR);
        Boolean tagSupport = getBoolValue(config, TAG_SUPPORT_PARAMETER, EMPTY_STRING);
        Boolean enableEditOnError = getBoolValue(config, ENABLE_EDIT_ON_ERROR_PARAMETER, EMPTY_STRING);

        errors.clear();

        Options options = webOptionsManager.read(url, extraOptions, format, errors);
        Collection<String> values = Lists.newArrayList(
                Splitter.on(separator)
                        .trimResults()
                        .omitEmptyStrings()
                        .split(context.getParameter().getValue())
        );

        modelAndView.getModel().put(OPTIONS_NAME, options);
        modelAndView.getModel().put(VALUES_NAME, values);
        modelAndView.getModel().put(MULTIPLE_PARAMETER, multiple);
        modelAndView.getModel().put(TAG_SUPPORT_PARAMETER, tagSupport);
        modelAndView.getModel().put(ENABLE_EDIT_ON_ERROR_PARAMETER, enableEditOnError);
        modelAndView.getModel().put(ERRORS_NAME, errors);
        return modelAndView;
    }

    @NotNull
    @Override
    public ModelAndView renderSpecEditor(@NotNull HttpServletRequest request, @NotNull ParameterEditContext parameterEditContext) throws InvalidParametersException {
        ModelAndView modelAndView = new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterConfiguration.jsp"));
        modelAndView.getModel().put(PARSERS_NAME, ParserFactory.registry);
        return modelAndView;
    }


    @Override
    public String convertParameterValue(@NotNull HttpServletRequest httpServletRequest, @NotNull ParameterRenderContext context, @Nullable String s) throws InvalidParametersException {
        Map<String, String> config = context.getDescription().getParameterTypeArguments();

        String separator = getValue(config, VALUE_SEPARATOR_PARAMETER, DEFAULT_VALUE_SEPARATOR);

        Joiner joiner = Joiner.on(separator).skipNulls();
        String[] values = httpServletRequest.getParameterValues(context.getId());
        if (null != values) {
            return joiner.join(values);
        }
        return super.convertParameterValue(httpServletRequest, context, s);
    }

    @NotNull
    @Override
    public Collection<InvalidProperty> validateParameterValue(@NotNull HttpServletRequest httpServletRequest, @NotNull ParameterRenderContext context, @Nullable String s) throws InvalidParametersException {
        Collection<InvalidProperty> result = new ArrayList<>();

        Map<String, String> config = context.getDescription().getParameterTypeArguments();

        String separator = getValue(config, VALUE_SEPARATOR_PARAMETER, DEFAULT_VALUE_SEPARATOR);
        String[] values = httpServletRequest.getParameterValues(context.getId());
        if (null == values){
            return result;
        }

        for (String value : values) {
            if (value.contains(separator)) {
                result.add(new InvalidProperty(
                        context.getId(),
                        String.format("Selected value '%s' contains separator '%s'!", value, separator))
                );
            }
        }

        return result;
    }
}