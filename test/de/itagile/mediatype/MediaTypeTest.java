package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.HashEntity;
import de.itagile.ces.Key;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MediaTypeTest {

    @Test
    public void testMediaType() throws Exception {
        MFieldKey productName = new MFieldKey("name");
        MFieldKey productId = new MFieldKey("productId");
        MFieldKey priceKey = new MFieldKey("price");
        MObjectKey variationKey = new MObjectKey("variation", priceKey);

        Entity variation = new HashEntity().attach(priceKey, new MIntegerField(100));
        Entity product = new HashEntity().
                attach(productId, new MStringField("XY123")).
                attach(productName, new MStringField("TestProduct")).
                attach(variationKey, variation);

        MediaType keys = new MediaType(productId, productName, variationKey);

        assertEquals(
                "{\"variation\":{\"price\":100},\"name\":\"TestProduct\",\"productId\":\"XY123\"}",
                keys.start(product).toString());
    }

    public interface MKey {
        void start(Entity e, Map result);
    }

    public class MediaType {
        private final MKey[] keys;

        public MediaType(MKey... keys) {
            this.keys = keys;
        }

        public Map start(Entity e) {
            JSONObject result = new JSONObject();
            for (MKey key : keys) {
                key.start(e, result);
            }
            return result;
        }
    }

    private class MObjectKey implements Key<Entity>, MKey {
        private final String name;
        private final MKey[] keys;

        public MObjectKey(String name, MKey... keys) {
            this.name = name;
            this.keys = keys;
        }

        @Override
        public Entity getUndefined() {
            throw new RuntimeException();
        }

        @Override
        public void start(Entity e, Map result) {
            Map subType = new JSONObject();
            Entity subEntity = e.get(this);
            result.put(name, subType);
            for (MKey key : keys) {
                key.start(subEntity, subType);
            }
        }
    }

    public class MFieldKey implements Key<MField>, MKey {
        private final String name;

        public MFieldKey(String name) {
            this.name = name;
        }

        @Override
        public MField getUndefined() {
            return null;
        }

        @Override
        public void start(Entity e, Map result) {
            result.put(name, e.get(this).getValue());
        }
    }

    public abstract class MField {
        abstract Object getValue();
    }

    private class MIntegerField extends MField {
        private final int value;

        public MIntegerField(int value) {
            this.value = value;
        }

        @Override
        Integer getValue() {
            return value;
        }
    }

    private class MStringField extends MField {
        private final String value;

        public MStringField(String value) {
            this.value = value;
        }

        @Override
        String getValue() {
            return value;
        }
    }
}
