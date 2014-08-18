package de.itagile.searchresponse;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsManualRedirectPossible<T extends IsManualRedirectPossible.IIsManualRedirectPossible> extends SpecificationPartial<T> {
    private IsManualRedirectPossible() {
    }

    public static IsManualRedirectPossible is_manual_redirect_possible() {
        return new IsManualRedirectPossible();
    }

    @Override
    public Specification create(IIsManualRedirectPossible param) {
        return is_manual_redirect_possible();
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    public static interface IIsManualRedirectPossible {
        String findRedirectMappingByOldPath(String path);
    }
}
