package de.itagile.mediatype;

import de.itagile.mediatype.html.*;
import de.itagile.mediatype.simpleJson.*;
import de.itagile.model.HashModel;
import de.itagile.model.Model;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class MediaTypeTest {

    public static final IntegerField PRICE_FIELD = new IntegerField("price");
    public static final RequiredStringField<String> PRODUCT_ID_FIELD = new RequiredStringField<>("productId");
    public static final Set<Availability> AVAILABILITIES = set(Availability.AVAILABLE, Availability.DELAYED, Availability.SOLDOUT);
    public static final EnumField<Availability> AVAILABILITY_FIELD =
            new EnumField<>(
                    "availability",
                    Availability.AVAILABLE,
                    AVAILABILITIES);
    public static final StringField PRODUCT_NAME_FIELD = new StringField("name");
    public static final RequiredStringField<String> VARIATION_ID_FIELD = new RequiredStringField<>("variationId");
    public static final JsonMediaType VARIATION_MEDIA_TYPE = new VariationMediaType(VARIATION_ID_FIELD, PRICE_FIELD, AVAILABILITY_FIELD);
    public static final MediaTypeSetField VARIATIONS_FIELD = new MediaTypeSetField("variations", VARIATION_MEDIA_TYPE);
    public static final JsonMediaType PRODUCT_MEDIA_TYPE = new ProductMediaType(PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD, VARIATIONS_FIELD);
    public static final TemplateField TEMPLATE_NAME_FIELD = new TemplateField();
    public static final HtmlModelField TEMPLATE_MODEL_FIELD = new HtmlModelField(set(PRODUCT_ID_FIELD, PRODUCT_NAME_FIELD));
    public static final MediaType<Viewable, HtmlFormat> HTML_MEDIA_TYPE = new HtmlMediaType("application/vnd.itagile.product+html", TEMPLATE_NAME_FIELD, TEMPLATE_MODEL_FIELD);
    public static final StringField REASON_FIELD = new StringField("reason");
    public static final JsonMediaType ERROR_MEDIA_TYPE = new JsonMediaType("application/vnd.itagile.error+json", REASON_FIELD);

    private static <T> Set<T> set(T... keys) {
        Set<T> result = new HashSet<T>();
        Collections.addAll(result, keys);
        return result;
    }

    @SafeVarargs
    public static <K extends JsonFormat> Set<JsonFormat> set(K... keys) {
        Set<JsonFormat> result = new HashSet<>();
        Collections.addAll(result, keys);
        return result;
    }

    @Test
    public void testMediaType() throws Exception {
        Model variation = new HashModel().
                update(PRICE_FIELD, 100).
                update(VARIATION_ID_FIELD, "ABC567").
                update(AVAILABILITY_FIELD, Availability.FORBIDDEN);
        Model product = new HashModel().
                update(PRODUCT_ID_FIELD, "XY123").
                update(PRODUCT_NAME_FIELD, "TestProduct").
                update(VARIATIONS_FIELD, set(variation));

        JSONObject entity = PRODUCT_MEDIA_TYPE.modify(product);

        assertEquals("XY123", entity.get(PRODUCT_ID_FIELD.name));
        assertEquals("TestProduct", entity.get(PRODUCT_NAME_FIELD.name));
        Iterable variations = (Iterable) entity.get("variations");
        Map variation1 = (Map) variations.iterator().next();
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

        Viewable entity = HTML_MEDIA_TYPE.modify(viewableModel);

        assertEquals("/path/to/template1", entity.getTemplate());
        assertEquals("ModelTestProductId", entity.getViewModel().get("productId"));
        assertEquals("ModelTestProductName", entity.getViewModel().get("name"));
    }

    @Test
    public void testMediaTypeHasSpecRepresentation() throws Exception {
        Map htmlSpec = new HashMap();
        htmlSpec.put("name", "application/vnd.itagile.variation+json");

        Set fields = new HashSet();

        Map variationId = new HashMap();
        variationId.put("name", "variationId");
        Map variationIdType = new HashMap();
        variationIdType.put("name", "String");
        variationIdType.put("required", "true");
        variationId.put("type", variationIdType);
        fields.add(variationId);

        Map availability = new HashMap();
        availability.put("name", "availability");
        Map<Object, Object> availabilityType = new HashMap<>();
        availabilityType.put("name", "Enum");
        availabilityType.put("subtype", "String");
        Set<String> availabilityTypeValues = new HashSet<>();
        availabilityTypeValues.add("AVAILABLE");
        availabilityTypeValues.add("DELAYED");
        availabilityTypeValues.add("SOLDOUT");
        availabilityType.put("values", availabilityTypeValues);
        availabilityType.put("default", "AVAILABLE");
        availability.put("type", availabilityType);
        fields.add(availability);

        Map price = new HashMap();
        price.put("name", "price");
        Map priceType = new HashMap();
        priceType.put("name", "Integer");
        price.put("type", priceType);
        fields.add(price);

        htmlSpec.put("fields", fields);

        assertEquals(htmlSpec, VARIATION_MEDIA_TYPE.getSpec());
    }

    @Test
    public void addsConsumes() throws Exception {
        final Map<String, Object> spec = new HashMap<>();

        de.itagile.mediatype.MediaType.consumes(javax.ws.rs.core.MediaType.TEXT_HTML_TYPE).spec(spec);

        assertEquals("text/html", spec.get("consumes"));
    }


    public enum Availability {
        AVAILABLE, DELAYED, SOLDOUT, FORBIDDEN
    }

    public static class VariationMediaType extends JsonMediaType {
        public VariationMediaType(JsonFormat... jsonFormats) {
            super("application/vnd.itagile.variation+json", jsonFormats);
        }
    }

    public static class ProductMediaType extends JsonMediaType {
        public ProductMediaType(JsonFormat... jsonFormats) {
            super("application/vnd.itagile.product+json", jsonFormats);
        }
    }
}