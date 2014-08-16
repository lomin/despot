package de.itagile.mediatype;

import java.util.Map;

public class Viewable {

    private String template;
    private Map model;

    public void setModel(Map model) {
        this.model = model;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public Map getModel() {
        return model;
    }
}
