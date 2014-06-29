package ru.mail.teamcity.web.patameters.controller;

import jetbrains.buildServer.controllers.parameters.InvalidParametersException;
import jetbrains.buildServer.controllers.parameters.ParameterEditContext;
import jetbrains.buildServer.controllers.parameters.ParameterRenderContext;
import jetbrains.buildServer.controllers.parameters.api.ParameterControlProviderAdapter;
import jetbrains.buildServer.serverSide.ControlDescription;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import ru.mail.teamcity.web.patameters.data.Options;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 27.06.14
 * Time: 17:51
 */
public class WebParameterController extends ParameterControlProviderAdapter {

    @NotNull
    private final PluginDescriptor pluginDescriptor;

    public WebParameterController(
            @NotNull PluginDescriptor pluginDescriptor
    ) {
        this.pluginDescriptor = pluginDescriptor;
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
        modelAndView.getModel().put("options", requestOptions(context.getDescription()));
        return modelAndView;
    }

    private Options requestOptions(ControlDescription description) {
        Options options = new Options();
        return options;
    }

    @NotNull
    @Override
    public ModelAndView renderSpecEditor(@NotNull HttpServletRequest request, @NotNull ParameterEditContext parameterEditContext) throws InvalidParametersException {
        return new ModelAndView(pluginDescriptor.getPluginResourcesPath("ru/mail/teamcity/web/parameters/jsp/webParameterConfiguration.jsp"));
    }
}