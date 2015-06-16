package de.itagile.shareable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckedShareable {
    private CheckedShareable() {
    }

    public static <T, E extends Exception> Shareable0<T, E> create(Closure0<T, E> c) {
        return new Shareable0<>(c);
    }

    public static <T, E extends Exception, P1> Shareable1<T, E, P1> create(Closure1<T, E, P1> c) {
        return new Shareable1<>(c);
    }

    public static <T, E extends Exception, P1, P2> Shareable2<T, E, P1, P2> create(Closure2<T, E, P1, P2> c) {
        return new Shareable2<>(c);
    }

    public static <T, E extends Exception, P1, P2, P3> Shareable3<T, E, P1, P2, P3> create(Closure3<T, E, P1, P2, P3> c) {
        return new Shareable3<>(c);
    }

    public static <T, E extends Exception, P1, P2, P3, P4> Shareable4<T, E, P1, P2, P3, P4> create(Closure4<T, E, P1, P2, P3, P4> c) {
        return new Shareable4<>(c);
    }

    public static <T, E extends Exception, P1, P2, P3, P4, P5> Shareable5<T, E, P1, P2, P3, P4, P5> create(Closure5<T, E, P1, P2, P3, P4, P5> c) {
        return new Shareable5<>(c);
    }

    public static <T, E extends Exception, P1, P2, P3, P4, P5, P6> Shareable6<T, E, P1, P2, P3, P4, P5, P6> create(Closure6<T, E, P1, P2, P3, P4, P5, P6> c) {
        return new Shareable6<>(c);
    }

    public static <T, E extends Exception, P1, P2, P3, P4, P5, P6, P7> Shareable7<T, E, P1, P2, P3, P4, P5, P6, P7> create(Closure7<T, E, P1, P2, P3, P4, P5, P6, P7> c) {
        return new Shareable7<>(c);
    }

    public static <T, E extends Exception, P1, P2, P3, P4, P5, P6, P7, P8> Shareable8<T, E, P1, P2, P3, P4, P5, P6, P7, P8> create(Closure8<T, E, P1, P2, P3, P4, P5, P6, P7, P8> c) {
        return new Shareable8<>(c);
    }

    public interface Closure0<T, E extends Exception> {
        public T call() throws E;
    }

    public interface Closure1<T, E extends Exception, P1> {
        T call(P1 p1) throws E;
    }

    public interface Closure2<T, E extends Exception, P1, P2> {
        T call(P1 p1, P2 p2) throws E;
    }

    public interface Closure3<T, E extends Exception, P1, P2, P3> {
        T call(P1 p1, P2 p2, P3 p3) throws E;
    }

    public interface Closure4<T, E extends Exception, P1, P2, P3, P4> {
        T call(P1 p1, P2 p2, P3 p3, P4 p4) throws E;
    }

    public interface Closure5<T, E extends Exception, P1, P2, P3, P4, P5> {
        T call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws E;
    }

    public interface Closure6<T, E extends Exception, P1, P2, P3, P4, P5, P6> {
        T call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) throws E;
    }

    public interface Closure7<T, E extends Exception, P1, P2, P3, P4, P5, P6, P7> {
        T call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) throws E;
    }

    public interface Closure8<T, E extends Exception, P1, P2, P3, P4, P5, P6, P7, P8> {
        T call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) throws E;
    }

    public static class Shareable0<T, E extends Exception> {
        private final Closure0<T, E> c;
        private T cache;

        public Shareable0(Closure0<T, E> c) {
            this.c = c;
        }

        public T get() throws E {
            if (cache == null) {
                cache = c.call();
            }
            return cache;
        }
    }

    public static class Shareable1<T, E extends Exception, P1> {
        private final Closure1<T, E, P1> c;
        private Map<P1, T> cache = new HashMap<>();

        public Shareable1(Closure1<T, E, P1> c) {
            this.c = c;
        }

        public T get(P1 p1) throws E {
            T result = cache.get(p1);
            if (result == null) {
                result = c.call(p1);
                cache.put(p1, result);
            }
            return result;
        }
    }

    public static class Shareable2<T, E extends Exception, P1, P2> {
        private final Closure2<T, E, P1, P2> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable2(Closure2<T, E, P1, P2> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2) throws E {
            ArrayList<Object> key = new ArrayList<>(2);
            key.add(p1);
            key.add(p2);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2);
                cache.put(key, result);
            }
            return result;
        }
    }

    public static class Shareable3<T, E extends Exception, P1, P2, P3> {
        private final Closure3<T, E, P1, P2, P3> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable3(Closure3<T, E, P1, P2, P3> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2, P3 p3) throws E {
            ArrayList<Object> key = new ArrayList<>(3);
            key.add(p1);
            key.add(p2);
            key.add(p3);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2, p3);
                cache.put(key, result);
            }
            return result;
        }
    }

    public static class Shareable4<T, E extends Exception, P1, P2, P3, P4> {
        private final Closure4<T, E, P1, P2, P3, P4> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable4(Closure4<T, E, P1, P2, P3, P4> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2, P3 p3, P4 p4) throws E {
            ArrayList<Object> key = new ArrayList<>(4);
            key.add(p1);
            key.add(p2);
            key.add(p3);
            key.add(p4);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2, p3, p4);
                cache.put(key, result);
            }
            return result;
        }
    }

    public static class Shareable5<T, E extends Exception, P1, P2, P3, P4, P5> {
        private final Closure5<T, E, P1, P2, P3, P4, P5> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable5(Closure5<T, E, P1, P2, P3, P4, P5> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws E {
            ArrayList<Object> key = new ArrayList<>(5);
            key.add(p1);
            key.add(p2);
            key.add(p3);
            key.add(p4);
            key.add(p5);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2, p3, p4, p5);
                cache.put(key, result);
            }
            return result;
        }
    }

    public static class Shareable6<T, E extends Exception, P1, P2, P3, P4, P5, P6> {
        private final Closure6<T, E, P1, P2, P3, P4, P5, P6> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable6(Closure6<T, E, P1, P2, P3, P4, P5, P6> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) throws E {
            ArrayList<Object> key = new ArrayList<>(6);
            key.add(p1);
            key.add(p2);
            key.add(p3);
            key.add(p4);
            key.add(p5);
            key.add(p6);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2, p3, p4, p5, p6);
                cache.put(key, result);
            }
            return result;
        }
    }

    public static class Shareable7<T, E extends Exception, P1, P2, P3, P4, P5, P6, P7> {
        private final Closure7<T, E, P1, P2, P3, P4, P5, P6, P7> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable7(Closure7<T, E, P1, P2, P3, P4, P5, P6, P7> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) throws E {
            ArrayList<Object> key = new ArrayList<>(7);
            key.add(p1);
            key.add(p2);
            key.add(p3);
            key.add(p4);
            key.add(p5);
            key.add(p6);
            key.add(p7);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2, p3, p4, p5, p6, p7);
                cache.put(key, result);
            }
            return result;
        }
    }

    public static class Shareable8<T, E extends Exception, P1, P2, P3, P4, P5, P6, P7, P8> {
        private final Closure8<T, E, P1, P2, P3, P4, P5, P6, P7, P8> c;
        private Map<List<Object>, T> cache = new HashMap<>();

        public Shareable8(Closure8<T, E, P1, P2, P3, P4, P5, P6, P7, P8> c) {
            this.c = c;
        }

        public T get(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) throws E {
            ArrayList<Object> key = new ArrayList<>(8);
            key.add(p1);
            key.add(p2);
            key.add(p3);
            key.add(p4);
            key.add(p5);
            key.add(p6);
            key.add(p7);
            key.add(p8);
            T result = cache.get(key);
            if (result == null) {
                result = c.call(p1, p2, p3, p4, p5, p6, p7, p8);
                cache.put(key, result);
            }
            return result;
        }
    }
}
