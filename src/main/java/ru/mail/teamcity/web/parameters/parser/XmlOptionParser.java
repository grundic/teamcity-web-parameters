package ru.mail.teamcity.web.parameters.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 01.07.14
 * Time: 15:39
 */
public class XmlOptionParser implements OptionParser {
    @NotNull
    public String getId() {
        return "xml";
    }

    @Nullable
    public Options parse(InputStream inputStream, @NotNull Map<String, String> errors) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Options.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(new StreamSource(inputStream), Options.class).getValue();
        } catch (JAXBException e) {
            errors.put("Failed to parse Xml format", e.toString());
        }
        return null;
    }
}
