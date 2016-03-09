package ru.mail.teamcity.web.parameters.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 01.07.14
 * Time: 15:39
 */
public class JsonOptionParser implements OptionParser {

    @NotNull
    public String getId() {
        return "json";
    }

    @Nullable
    public Options parse(InputStream inputStream, @NotNull Map<String, String> errors) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(inputStream, Options.class);
        } catch (IOException e) {
            errors.put("Failed to parse Json format", e.getCause().getMessage());
        }
        return null;
    }
}
