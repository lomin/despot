package de.itagile.mediatype.html;

import de.itagile.model.Model;
import de.itagile.model.Key;

import java.util.Map;

public class TemplateField implements Key<String>, HtmlFormat {

    @Override
    public void transform(Model e, Viewable result) {
        String template = e.get(this);
        result.setTemplate(template);
    }

    @Override
    public void spec(Map spec) {

    }

    @Override
    public String getUndefined() {
        throw new IllegalStateException("Template-Name is mandatory!");
    }
}
