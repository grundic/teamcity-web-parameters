package ru.mail.teamcity.web.parameters.manager;

import jetbrains.buildServer.serverSide.CriticalErrors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.parser.OptionParser;

import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 17:29
 */
public interface WebOptionsManager {
    @NotNull
    public Options read(@NotNull String url, @NotNull String format, @NotNull Map<String, String> errors);

    @Nullable
    public OptionParser guess(@NotNull String url, @NotNull Map<String, String> errors);
}
