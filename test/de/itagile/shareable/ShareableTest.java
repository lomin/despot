package de.itagile.shareable;

import org.junit.Test;

import static de.itagile.shareable.Shareable.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ShareableTest {

    @Test
    public void testShareable() throws Exception {
        Shareable.Shareable2<String, Integer, String> s2 = create(new Shareable.Closure2<String, Integer, String>() {
            @Override
            public String call(Integer integer, String s) {
                return s + integer;
            }
        });

        assertSame(s2.get(1, "test"), s2.get(1, "test"));
        assertNotSame(s2.get(1, "test"), s2.get(2, "test"));
        assertEquals("test1", s2.get(1, "test"));

        Shareable.Shareable3<String, Integer, Integer, Integer> s3 = create(new Shareable.Closure3<String, Integer, Integer, Integer>() {
            @Override
            public String call(Integer i1, Integer i2, Integer i3) {
                return "Result: " + (i1 + i2 + i3);
            }
        });

        assertSame(s3.get(1, 2, 3), s3.get(1, 2, 3));
        assertNotSame(s3.get(1, 2, 3), s3.get(3, 2, 1));
        assertEquals("Result: 6", s3.get(1, 2, 3));
    }
}
