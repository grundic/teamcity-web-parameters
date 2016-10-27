package ru.mail.teamcity.web.parameters.provider;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
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
import ru.mail.teamcity.web.parameters.manager.RequestConfiguration;
import ru.mail.teamcity.web.parameters.manager.ValueExtractor;
import ru.mail.teamcity.web.parameters.manager.WebOptionsManager;
import ru.mail.teamcity.web.parameters.manager.WebOptionsManagerImpl;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static ru.mail.teamcity.web.parameters.Constants.*;
import static ru.mail.teamcity.web.parameters.manager.RequestConfiguration.DEFAULT_MULTIPLE_SEPARATOR;

/**
 * User: g.chernyshev
 * Date: 27.06.14
 * Time: 17:51
 */
public class WebParameterProvider extends ParameterControlProviderAdapter {

    private final static Logger LOG = Logger.getInstance(WebOptionsManagerImpl.class.getName());

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
        return PARAMETER_DESCRIPTION;
    }

    @NotNull
    @Override
    public ModelAndView renderControl(@NotNull HttpServletRequest request, @NotNull ParameterRenderContext context) throws InvalidParametersException {
        ModelAndView modelAndView = new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterControl.jsp"));

        String buildTypeId = request.getParameter(BUILD_TYPE_ID);
        SBuildType buildType = projectManager.findBuildTypeByExternalId(buildTypeId);
        assert buildType != null;

        errors.clear();

        Map<String, String> config = context.getDescription().getParameterTypeArguments();
        RequestConfiguration configuration = new RequestConfiguration(config, buildType.getValueResolver());
        Options options = Options.empty();
        Collection<String> values = Collections.emptyList();

        try {
            configuration.process();

            options = webOptionsManager.read(configuration, errors);

            values = Lists.newArrayList(
                    Splitter.on(configuration.getMultipleSeparator())
                            .trimResults()
                            .omitEmptyStrings()
                            .split(context.getParameter().getValue())
            );
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
            errors.put("Unexpected web-parameters error", e.toString());
        }

        modelAndView.getModel().put(OPTIONS_NAME, options);
        modelAndView.getModel().put(VALUES_NAME, values);


        modelAndView.getModel().put(MULTIPLE_PARAMETER, configuration.getMultiple());
        modelAndView.getModel().put(TAG_SUPPORT_PARAMETER, configuration.getTagSupport());
        modelAndView.getModel().put(ENABLE_EDIT_ON_ERROR_PARAMETER, configuration.getEnableEditOnError());

        modelAndView.getModel().put(ERRORS_NAME, errors);
        return modelAndView;
    }

    @NotNull
    @Override
    public ModelAndView renderSpecEditor(@NotNull HttpServletRequest request, @NotNull ParameterEditContext parameterEditContext) throws InvalidParametersException {
        ModelAndView modelAndView = new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterConfiguration.jsp"));
        modelAndView.getModel().put(PARSERS_NAME, ParserFactory.registry);
        modelAndView.getModel().put(AVAILABLE_METHODS_PARAMETER, Arrays.asList(RequestConfiguration.Method.values()));

        return modelAndView;
    }


    @Override
    public String convertParameterValue(@NotNull HttpServletRequest httpServletRequest, @NotNull ParameterRenderContext context, @Nullable String s) throws InvalidParametersException {
        Map<String, String> config = context.getDescription().getParameterTypeArguments();

        String separator = ValueExtractor.getValue(config, MULTIPLE_SEPARATOR_PARAMETER, DEFAULT_MULTIPLE_SEPARATOR);

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

        String separator = ValueExtractor.getValue(config, MULTIPLE_SEPARATOR_PARAMETER, DEFAULT_MULTIPLE_SEPARATOR);
        String[] values = httpServletRequest.getParameterValues(context.getId());
        if (null == values) {
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