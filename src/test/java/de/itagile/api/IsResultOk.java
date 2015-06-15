package de.itagile.api;

import de.itagile.predicate.Predicate;
import de.itagile.predicate.PredicateFactory;

public class IsResultOk implements Predicate {
    private IsResultOk() {
    }

    public static PredicateFactory<IProductSearchParams> is_result_ok() {
        return new PredicateFactory<IProductSearchParams>() {
            @Override
            public IsResultOk createPredicate(IProductSearchParams param) {
                return new IsResultOk();
            }
        };
    }

    @Override
    public boolean isTrue() {
        return true;
    }
}
