package de.itagile.predicate;

public interface PredicateFactory<ParamType> {

    Predicate createPredicate(ParamType param);
}
