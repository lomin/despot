package de.itagile.predicate;

public class Predicates {

    private static final Predicate TRUE = new Predicate() {
        @Override
        public boolean isTrue() {
            return true;
        }
    };

    public static Predicate not(final Predicate spec) {
        return new Predicate() {
            @Override
            public boolean isTrue() throws Exception {
                return !spec.isTrue();
            }
        };
    }

    public static Predicate and(final Predicate first, final Predicate second) {
        return new Predicate() {
            @Override
            public boolean isTrue() throws Exception {
                return first.isTrue() && second.isTrue();
            }
        };
    }

    public static Predicate or(final Predicate first, final Predicate second) {
        return new Predicate() {
            @Override
            public boolean isTrue() throws Exception {
                return first.isTrue() || second.isTrue();
            }
        };
    }

    public static Predicate TRUE() {
        return TRUE;
    }

}
