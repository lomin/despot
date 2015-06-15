package de.itagile.predicate;

public class Operations<ParamType> {

    public static <ParamType> Operations<ParamType> operations(Class<ParamType> clazz) {
        return new Operations<>();
    }

    public PredicateFactory<? super ParamType> and(final PredicateFactory<? super ParamType> first, final PredicateFactory<? super ParamType> second) {
        return new PredicateFactory<ParamType>() {
            @Override
            public Predicate createPredicate(ParamType param) {
                return Predicates.and(first.createPredicate(param), second.createPredicate(param));
            }
        };
    }

    public PredicateFactory<? super ParamType> or(final PredicateFactory<? super ParamType> first, final PredicateFactory<? super ParamType> second) {
        return new PredicateFactory<ParamType>() {
            @Override
            public Predicate createPredicate(ParamType param) {
                return Predicates.or(first.createPredicate(param), second.createPredicate(param));
            }
        };
    }

    public PredicateFactory<? super ParamType> not(final PredicateFactory<? super ParamType> spec) {
        return new PredicateFactory<ParamType>() {
            @Override
            public Predicate createPredicate(ParamType param) {
                return Predicates.not(spec.createPredicate(param));
            }
        };
    }

    public PredicateFactory<? super ParamType> TRUE() {
        return new PredicateFactory<ParamType>() {
            @Override
            public Predicate createPredicate(ParamType param) {
                return Predicates.TRUE();
            }
        };
    }
}
