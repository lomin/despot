package de.itagile.mediatype.html;

import java.util.Map;

public class Viewable {

    private String template;
    private Map viewModel;

    public void setViewModel(Map viewModel) {
        this.viewModel = viewModel;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public Map getViewModel() {
        return viewModel;
    }
}
