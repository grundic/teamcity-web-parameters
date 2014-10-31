package ru.mail.teamcity.web.parameters.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * User: g.chernyshev
 * Date: 01.07.14
 * Time: 15:54
 */
public class ParserFactory {
    public static ArrayList<OptionParser> registry = new ArrayList<OptionParser>() {{
        add(new XmlOptionParser());
        add(new JsonOptionParser());
    }};


    @Nullable
    public static OptionParser getOptionParser(@NotNull String format) {
        for (OptionParser parser : registry) {
            if (format.equals(parser.getId())) {
                return parser;
            }
        }
        return null;
    }
}
