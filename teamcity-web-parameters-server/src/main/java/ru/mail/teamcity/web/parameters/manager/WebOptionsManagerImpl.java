package ru.mail.teamcity.web.parameters.manager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.teamcity.web.parameters.data.Options;
import ru.mail.teamcity.web.parameters.parser.OptionParser;
import ru.mail.teamcity.web.parameters.parser.ParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 17:34
 */
public class WebOptionsManagerImpl implements WebOptionsManager {

    private final static Logger LOG = Logger.getLogger(WebOptionsManagerImpl.class);

    @NotNull
    public Options read(@NotNull String url, @NotNull String format) {
        Options options = null;

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
        LOG.debug(String.format("Requesting parameters from %s", url));
        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("Method failed: " + response.getStatusLine());
                return Options.empty();
            }
            options = parse(response.getEntity().getContent(), format);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            getRequest.releaseConnection();
        }
        return null == options ? Options.empty() : options;
    }

    @Nullable
    public OptionParser guess(@NotNull String url) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
        LOG.debug(String.format("Requesting parameters from %s", url));
        try {
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("Method failed: " + response.getStatusLine());
                return null;
            }
            String content;
            try {
                content = IOUtils.toString(response.getEntity().getContent());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return null;
            }
            for (OptionParser parser : ParserFactory.registry) {
                InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

                if (null != parser.parse(stream)) {
                    return parser;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            getRequest.releaseConnection();
        }
        return null;
    }

    @Nullable
    private Options parse(@NotNull InputStream inputStream, @NotNull String format) {
        OptionParser parser = ParserFactory.getOptionParser(format);
        if (null != parser) {
            return parser.parse(inputStream);
        }
        return null;
    }
}
