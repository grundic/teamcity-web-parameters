package ru.mail.teamcity.web.parameters.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 13:47
 */
@XmlRootElement
public final class Option {
    @NotNull
    private String key;
    @NotNull
    private String value;
    private boolean enabled = true;
    private boolean isDefault = false;
    @Nullable
    private String image;

    public Option() {
        // empty constructor for JAXB
    }

    public Option(@NotNull String key, @NotNull String value) {
        this.key = key;
        this.value = value;
        this.enabled = true;
    }

    public Option(@NotNull String key, @NotNull String value, boolean enabled) {
        this.key = key;
        this.value = value;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public void setKey(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @XmlElement(required = false)
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }
}