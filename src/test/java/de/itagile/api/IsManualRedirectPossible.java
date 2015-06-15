package de.itagile.api;

import de.itagile.predicate.Predicate;
import de.itagile.predicate.PredicateFactory;

public class IsManualRedirectPossible implements Predicate {
    private IsManualRedirectPossible(IIsManualRedirectPossible param) {
    }

    public static PredicateFactory<IIsManualRedirectPossible> is_manual_redirect_possible() {
        return new PredicateFactory<IIsManualRedirectPossible>(){
            @Override
            public Predicate createPredicate(IIsManualRedirectPossible param) {
                return new IsManualRedirectPossible(param);
            }
        };
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    public interface IIsManualRedirectPossible {
        String findRedirectMappingByOldPath(String path);
    }
}
