package ru.mail.teamcity.web.parameters.controller;

import jetbrains.buildServer.controllers.parameters.InvalidParametersException;
import jetbrains.buildServer.controllers.parameters.ParameterEditContext;
import jetbrains.buildServer.controllers.parameters.ParameterRenderContext;
import jetbrains.buildServer.controllers.parameters.api.ParameterControlProviderAdapter;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.manager.WebOptionsManager;
import ru.mail.teamcity.web.parameters.parser.OptionParser;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * User: g.chernyshev
 * Date: 27.06.14
 * Time: 17:51
 */
public class WebParameterController extends ParameterControlProviderAdapter {

    @NotNull
    private final PluginDescriptor pluginDescriptor;
    @NotNull
    private final WebOptionsManager webOptionsManager;


    public WebParameterController(
            @NotNull PluginDescriptor pluginDescriptor,
            @NotNull WebOptionsManager webOptionsManager) {
        this.pluginDescriptor = pluginDescriptor;
        this.webOptionsManager = webOptionsManager;
    }

    @NotNull
    @Override
    public String getParameterType() {
        return "webPopulatedSelect";
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

        String url = context.getDescription().getParameterTypeArguments().get("url");
        String format = context.getDescription().getParameterTypeArguments().get("format");

        Options options = webOptionsManager.read(url, format);
        modelAndView.getModel().put("options", options);
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