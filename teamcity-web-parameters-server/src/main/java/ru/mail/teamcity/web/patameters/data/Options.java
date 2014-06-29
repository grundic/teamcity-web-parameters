package ru.mail.teamcity.web.patameters.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: g.chernyshev
 * Date: 29/06/14
 * Time: 13:44
 */
@XmlRootElement
public class Options {

    private List<Option<String, String>> options = new ArrayList<Option<String, String>>();

    @XmlElement(name = "option")
    public List<Option<String, String>> getOptions() {
        return options;
    }

    public void setOptions(List<Option<String, String>> options) {
        this.options = options;
    }

    public void addOption(Option<String, String> option) {
        this.options.add(option);
    }
}
