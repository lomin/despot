package de.itagile.shareable;

import org.junit.Test;

import static de.itagile.shareable.Shareable.create;
import static org.junit.Assert.*;

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

    @Test
    public void testSpecialCase0() throws Exception {
        Shareable.Shareable0<String> s0 = create(new Shareable.Closure0<String>() {
            @Override
            public String call() {
                return "test";
            }
        });
        assertSame(s0.get(), s0.get());
        assertEquals("test", s0.get());
    }

    @Test
    public void testSpecialCase1() throws Exception {
        Shareable.Shareable1<String, String> s1 = create(new Shareable.Closure1<String, String>() {
            @Override
            public String call(String p1) {
                return p1;
            }
        });
        assertSame(s1.get("test"), s1.get("test"));
        assertNotSame(s1.get("test1"), s1.get("test2"));
        assertEquals("test", s1.get("test"));
    }
}
