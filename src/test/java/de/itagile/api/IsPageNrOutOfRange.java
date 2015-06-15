package de.itagile.api;

import de.itagile.predicate.Predicate;
import de.itagile.predicate.PredicateFactory;

public class IsPageNrOutOfRange implements Predicate {
    private IsPageNrOutOfRange() {
    }

    public static PredicateFactory<IProductSearchParams> is_page_nr_out_of_range() {
        return new PredicateFactory<IProductSearchParams>() {
            @Override
            public Predicate createPredicate(IProductSearchParams param) {
                return new IsPageNrOutOfRange();
            }
        };
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
