package ru.mail.teamcity.web.parameters.manager;

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
    Options read(@NotNull RequestConfiguration configuration, @NotNull Map<String, String> errors);

    @Nullable
    OptionParser guess(@NotNull String url, @NotNull Map<String, String> errors);
}
