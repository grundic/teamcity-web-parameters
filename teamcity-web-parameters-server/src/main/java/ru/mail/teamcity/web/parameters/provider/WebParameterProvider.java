package ru.mail.teamcity.web.parameters.provider;

import jetbrains.buildServer.controllers.parameters.InvalidParametersException;
import jetbrains.buildServer.controllers.parameters.ParameterEditContext;
import jetbrains.buildServer.controllers.parameters.ParameterRenderContext;
import jetbrains.buildServer.controllers.parameters.api.ParameterControlProviderAdapter;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.manager.WebOptionsManager;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 27.06.14
 * Time: 17:51
 */
public class WebParameterProvider extends ParameterControlProviderAdapter {

    @NotNull
    public final static String PARAMETER_TYPE = "webPopulatedSelect";
    @NotNull
    public final static String URL_PARAMETER = "url";
    public final static String FORMAT_PARAMETER = "format";
    public final static String ENABLE_EDIT_ON_ERROR_PARAMETER = "enableEditOnError";

    @NotNull
    private final PluginDescriptor pluginDescriptor;
    @NotNull
    private final WebOptionsManager webOptionsManager;
    @NotNull
    private final Map<String, String> errors;


    public WebParameterProvider(
            @NotNull PluginDescriptor pluginDescriptor,
            @NotNull WebOptionsManager webOptionsManager
    ) {
        this.pluginDescriptor = pluginDescriptor;
        this.webOptionsManager = webOptionsManager;
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
    @Override
    public ModelAndView renderControl(@NotNull HttpServletRequest request, @NotNull ParameterRenderContext context) throws InvalidParametersException {
        ModelAndView modelAndView = new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterControl.jsp"));

        Map<String, String> config = context.getDescription().getParameterTypeArguments();

        String url = config.get(URL_PARAMETER);
        String format = config.get(FORMAT_PARAMETER);
        Boolean enableEditOnError;
        enableEditOnError = config.containsKey(ENABLE_EDIT_ON_ERROR_PARAMETER) && config.get(ENABLE_EDIT_ON_ERROR_PARAMETER).equalsIgnoreCase("true");

        errors.clear();
        Options options = webOptionsManager.read(url, format, errors);
        modelAndView.getModel().put("options", options);
        modelAndView.getModel().put(ENABLE_EDIT_ON_ERROR_PARAMETER, enableEditOnError);
        modelAndView.getModel().put("errors", errors);
        return modelAndView;
    }

    @NotNull
    @Override
    public ModelAndView renderSpecEditor(@NotNull HttpServletRequest request, @NotNull ParameterEditContext parameterEditContext) throws InvalidParametersException {
        ModelAndView modelAndView = new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterConfiguration.jsp"));
        modelAndView.getModel().put("parsers", ParserFactory.registry);
        return modelAndView;
    }
}