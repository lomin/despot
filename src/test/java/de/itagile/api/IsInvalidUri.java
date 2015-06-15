package de.itagile.api;

import de.itagile.predicate.Predicate;
import de.itagile.predicate.PredicateFactory;

public class IsInvalidUri implements Predicate {
    private final String uri;

    private IsInvalidUri(IIsInvalidUri param) {
        this(param.getUri());
    }

    public IsInvalidUri(String uri) {
        this.uri = uri;
    }

    public static PredicateFactory<IIsInvalidUri> is_invalid_uri() {
        return new PredicateFactory<IIsInvalidUri>() {
            @Override
            public Predicate createPredicate(IIsInvalidUri param) {
                return new IsInvalidUri(param);
            }
        };
    }

    @Override
    public boolean isTrue() {
        return uri == null;
    }

    public interface IIsInvalidUri {
        String getUri();
    }
}
