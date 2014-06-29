package ru.mail.teamcity.web.patameters.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 13:47
 */
@XmlRootElement
public final class Option {
    private final String key;
    private String value;
    @XmlElement(required = false, defaultValue="true")
    private boolean enabled;

    public Option(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Option(String key, String value, boolean enabled) {
        this.key = key;
        this.value = value;
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String setValue(String value) {
        String old = this.value;
        this.value = value;
        return old;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}