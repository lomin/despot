package de.itagile.mediatype.html;

import de.itagile.mediatype.simpleJson.JsonFormat;
import de.itagile.model.Key;
import de.itagile.model.Model;
import de.itagile.model.UndefinedModel;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

public class HtmlModelField implements Key<Model>, HtmlFormat {

    private static Model UNDEFINED = new UndefinedModel();
    private final Set<JsonFormat> keys;

    public HtmlModelField(Set<JsonFormat> keys) {
        this.keys = keys;
    }

    @Override
    public void transform(Model source, Viewable target) {
        Model subEntity = source.get(this);
        if (subEntity == UNDEFINED) return;
        JSONObject model = new JSONObject();
        target.setViewModel(model);
        for (JsonFormat key : keys) {
            key.transform(subEntity, model);
        }
    }

    @Override
    public void spec(Map spec) {

    }

    @Override
    public Model getUndefined() {
        return UNDEFINED;
    }
}
