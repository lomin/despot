package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.HashEntity;
import de.itagile.despot.Despot;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MediaTypeTest {

    public static final MFieldKey<String> REL_FIELD = new MFieldKey<>("rel");
    public static final MFieldKey<String> LINK_FIELD = new MFieldKey<>("link");
    public static final Set<JsonFormat> LINK_SCHEMA_JSON = set(REL_FIELD, LINK_FIELD);
    public static final MSetKey LINKS_FIELD = new MSetKey("links", LINK_SCHEMA_JSON);
    public static final MFieldKey<Integer> PRICE_FIELD = new MFieldKey<>("price");
    public static final MRequiredFieldKey<String> PRODUCT_ID_FIELD = new MRequiredFieldKey<>("productId");
    public static final MEnumKey<Availability> AVAILABILITY_FIELD =
            new MEnumKey<>(
                    "availability",
                    Availability.AVAILABLE,
                    set(Availability.class, Availability.AVAILABLE, Availability.DELAYED));
    public static final MFieldKey<String> PRODUCT_NAME_FIELD = new MFieldKey<>("name");
    public static final Set<JsonFormat> VARIATION_MEDIA_TYPE = set(PRICE_FIELD, AVAILABILITY_FIELD);
    public static final MObjectKey VARIATION_FIELD = new MObjectKey("variation", VARIATION_MEDIA_TYPE);
    public static final Set<JsonFormat> PRODUCT_MEDIA_TYPE = set(PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD, VARIATION_FIELD, AVAILABILITY_FIELD, LINKS_FIELD);
    public static final Set<JsonFormat> ERROR_MEDIA_TYPE = set(PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD, VARIATION_FIELD, AVAILABILITY_FIELD, LINKS_FIELD);
    public static final MHTMLTemplateField TEMPLATE_NAME_FIELD = new MHTMLTemplateField();
    public static final MHTMLModelKey TEMPLATE_MODEL_FIELD = new MHTMLModelKey(set(PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD));
    public static final Set<HtmlFormat> HTML_MEDIA_TYPE = set(HtmlFormat.class, TEMPLATE_NAME_FIELD, TEMPLATE_MODEL_FIELD);

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
        Entity link1 = new HashEntity().
                attach(REL_FIELD, "rel1").
                attach(LINK_FIELD, "http://otto.de/1");
        Entity link2 = new HashEntity().
                attach(REL_FIELD, "rel2").
                attach(LINK_FIELD, "http://otto.de/2");
        Entity variation = new HashEntity().
                attach(PRICE_FIELD, 100).
                attach(AVAILABILITY_FIELD, Availability.FORBIDDEN);
        Set<Entity> links = new HashSet<>();
        links.add(link1);
        links.add(link2);
        Entity product = new HashEntity().
                attach(PRODUCT_ID_FIELD, "XY123").
                attach(PRODUCT_NAME_FIELD, "TestProduct").
                attach(VARIATION_FIELD, variation).
                attach(LINKS_FIELD, links);

        JSONObject jsonObject = new JSONObject();
        Despot<?>.MediaTyped<Map, JsonFormat> mediaTyped = new Despot<>().new MediaTyped<>();
        mediaTyped.serialize(product, jsonObject, PRODUCT_MEDIA_TYPE);
        JSONObject parsedObject = (JSONObject) JSONValue.parse(jsonObject.toString());

        assertEquals("XY123", parsedObject.get(PRODUCT_ID_FIELD.name));
        assertEquals("TestProduct", parsedObject.get(PRODUCT_NAME_FIELD.name));
        JSONObject variation1 = (JSONObject) parsedObject.get("variation");
        assertEquals("AVAILABLE", variation1.get(AVAILABILITY_FIELD.name));
        assertEquals(100L, variation1.get(PRICE_FIELD.name));
    }

    @Test
    public void testHtmlMediaType() throws Exception {
        Entity modelEntity = new HashEntity()
                .attach(PRODUCT_ID_FIELD, "ModelTestProductId")
                .attach(PRODUCT_NAME_FIELD, "ModelTestProductName");
        Entity viewable = new HashEntity()
                .attach(TEMPLATE_NAME_FIELD, "/path/to/template1")
                .attach(TEMPLATE_MODEL_FIELD, modelEntity);

        Viewable result = new Viewable();
        Despot<?>.MediaTyped<Viewable, HtmlFormat> mediaTyped = new Despot<>().new MediaTyped<>();
        mediaTyped.serialize(viewable, result, HTML_MEDIA_TYPE);

        assertEquals("/path/to/template1", result.getTemplate());
        assertEquals("ModelTestProductId", result.getModel().get("productId"));
        assertEquals("ModelTestProductName", result.getModel().get("name"));
    }
}