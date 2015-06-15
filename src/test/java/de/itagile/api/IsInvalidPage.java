package de.itagile.api;

import de.itagile.predicate.Predicate;
import de.itagile.predicate.PredicateFactory;

public class IsInvalidPage implements Predicate {
    private Pageable pageable;

    private IsInvalidPage(Pageable pageable) {
        this.pageable = pageable;
    }

    public static PredicateFactory<Pageable> is_invalid_page() {
        return new PredicateFactory<Pageable>() {
            @Override
            public Predicate createPredicate(Pageable param) {
                return new IsInvalidPage(param);
            }
        };
    }

    @Override
    public boolean isTrue() {
        return pageable.getPage() < 0;
    }

    public interface Pageable {
        int getPage();
    }
}
