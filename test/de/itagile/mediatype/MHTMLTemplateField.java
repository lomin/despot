package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;

public class MHTMLTemplateField implements Key<String>, HtmlFormat {

    @Override
    public void serialize(Entity e, Viewable result) {
        String template = e.get(this);
        result.setTemplate(template);
    }

    @Override
    public String getUndefined() {
        throw new IllegalStateException("Template-Name is mandatory!");
    }
}
