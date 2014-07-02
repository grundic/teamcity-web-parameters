package ru.mail.teamcity.web.parameters.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.parser.OptionParser;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 17:29
 */
public interface WebOptionsManager {
    @NotNull
    public Options read(@NotNull String url, @NotNull String format);

    @Nullable
    public OptionParser guess(@NotNull String url);
}
