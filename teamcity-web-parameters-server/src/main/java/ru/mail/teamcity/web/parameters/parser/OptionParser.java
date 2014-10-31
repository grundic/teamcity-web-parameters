package ru.mail.teamcity.web.parameters.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;

import java.io.InputStream;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 01.07.14
 * Time: 15:23
 */
public interface OptionParser {
    @NotNull
    public String getId();

    @Nullable
    public Options parse(InputStream inputStream, @NotNull Map<String, String> errors);
}
