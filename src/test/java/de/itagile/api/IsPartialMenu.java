package de.itagile.api;

import de.itagile.predicate.Predicate;
import de.itagile.predicate.PredicateFactory;

public class IsPartialMenu implements Predicate {
    private IsPartialMenu() {
    }

    public static PredicateFactory<IProductSearchParams> is_partial_menu() {
        return new PredicateFactory<IProductSearchParams>() {
            @Override
            public Predicate createPredicate(IProductSearchParams param) {
                return new IsPartialMenu();
            }
        };
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
