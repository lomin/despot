package de.itagile.mediatype;

import de.itagile.mediatype.html.TemplateField;
import de.itagile.mediatype.html.HtmlFormat;
import de.itagile.mediatype.html.HtmlModelField;
import de.itagile.mediatype.html.Viewable;
import de.itagile.mediatype.simpleJson.*;
import de.itagile.model.HashModel;
import de.itagile.model.Model;
import de.itagile.despot.Despot;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MediaTypeTest {

    public static final StringField<String> REL_FIELD = new StringField<>("rel");
    public static final StringField<String> LINK_FIELD = new StringField<>("link");
    public static final MediaType<JsonFormat> LINK_SCHEMA_JSON = new MediaType<JsonFormat>("application/vnd.itagile.link+json", REL_FIELD, LINK_FIELD);
    public static final SetField LINKS_FIELD = new SetField("links", LINK_SCHEMA_JSON);
    public static final StringField<Integer> PRICE_FIELD = new StringField<>("price");
    public static final RequiredStringField<String> PRODUCT_ID_FIELD = new RequiredStringField<>("productId");
    public static final EnumField<Availability> AVAILABILITY_FIELD =
            new EnumField<>(
                    "availability",
                    Availability.AVAILABLE,
                    set(Availability.class, Availability.AVAILABLE, Availability.DELAYED));
    public static final StringField<String> PRODUCT_NAME_FIELD = new StringField<>("name");
    public static final Set<JsonFormat> VARIATION_MEDIA_TYPE = set(PRICE_FIELD, AVAILABILITY_FIELD);
    public static final ObjectField VARIATION_FIELD = new ObjectField("variation", VARIATION_MEDIA_TYPE);
    public static final MediaType<JsonFormat> PRODUCT_MEDIA_TYPE = new MediaType<JsonFormat>("application/vnd.itagile.product+json", PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD, VARIATION_FIELD, AVAILABILITY_FIELD, LINKS_FIELD);
    public static final TemplateField TEMPLATE_NAME_FIELD = new TemplateField();
    public static final HtmlModelField TEMPLATE_MODEL_FIELD = new HtmlModelField(set(PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD));
    public static final MediaType<HtmlFormat> HTML_MEDIA_TYPE = new MediaType<HtmlFormat>("application/vnd.itagile.product+html", TEMPLATE_NAME_FIELD, TEMPLATE_MODEL_FIELD);

    @SafeVarargs
    public static <T, K extends T> Set<T> set(Class<T> _, K... keys) {
        Set<T> set = new HashSet<T>();
        Collections.addAll(set, keys);
        return set;
    }

    @SafeVarargs
    public static <K extends JsonFormat> Set<JsonFormat> set(K... keys) {
        return set(JsonFormat.class, keys);
    }


    @Test
    public void testMediaType() throws Exception {
        Model link1 = new HashModel().
                update(REL_FIELD, "rel1").
                update(LINK_FIELD, "http://otto.de/1");
        Model link2 = new HashModel().
                update(REL_FIELD, "rel2").
                update(LINK_FIELD, "http://otto.de/2");
        Model variation = new HashModel().
                update(PRICE_FIELD, 100).
                update(AVAILABILITY_FIELD, Availability.FORBIDDEN);
        Set<Model> links = new HashSet<>();
        links.add(link1);
        links.add(link2);
        Model product = new HashModel().
                update(PRODUCT_ID_FIELD, "XY123").
                update(PRODUCT_NAME_FIELD, "TestProduct").
                update(VARIATION_FIELD, variation).
                update(LINKS_FIELD, links);

        JSONObject entity = new JSONObject();
        Despot.transform(product, entity, PRODUCT_MEDIA_TYPE);

        assertEquals("XY123", entity.get(PRODUCT_ID_FIELD.name));
        assertEquals("TestProduct", entity.get(PRODUCT_NAME_FIELD.name));
        JSONObject variation1 = (JSONObject) entity.get("variation");
        assertEquals("AVAILABLE", variation1.get(AVAILABILITY_FIELD.name));
        assertEquals(100, variation1.get(PRICE_FIELD.name));
    }

    @Test
    public void testHtmlMediaType() throws Exception {
        Model modelEntity = new HashModel()
                .update(PRODUCT_ID_FIELD, "ModelTestProductId")
                .update(PRODUCT_NAME_FIELD, "ModelTestProductName");
        Model viewableModel = new HashModel()
                .update(TEMPLATE_NAME_FIELD, "/path/to/template1")
                .update(TEMPLATE_MODEL_FIELD, modelEntity);

        Viewable entity = new Viewable();
        Despot.transform(viewableModel, entity, HTML_MEDIA_TYPE);


        assertEquals("/path/to/template1", entity.getTemplate());
        assertEquals("ModelTestProductId", entity.getViewModel().get("productId"));
        assertEquals("ModelTestProductName", entity.getViewModel().get("name"));
    }
}