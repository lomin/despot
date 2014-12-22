package de.itagile.specification;

public class Specifications {

    private static final Specification TRUE = new Specification() {
        @Override
        public boolean isTrue() {
            return true;
        }
    };

    public static Specification not(final Specification spec) {
        return new Specification() {
            @Override
            public boolean isTrue() throws Exception {
                return !spec.isTrue();
            }
        };
    }

    public static Specification and(final Specification first, final Specification second) {
        return new Specification() {
            @Override
            public boolean isTrue() throws Exception {
                return first.isTrue() && second.isTrue();
            }
        };
    }

    public static Specification or(final Specification first, final Specification second) {
        return new Specification() {
            @Override
            public boolean isTrue() throws Exception {
                return first.isTrue() || second.isTrue();
            }
        };
    }

    public static Specification TRUE() {
        return TRUE;
    }

}
