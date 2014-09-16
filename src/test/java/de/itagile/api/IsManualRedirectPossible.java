package de.itagile.api;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsManualRedirectPossible extends SpecificationPartial<IsManualRedirectPossible.IIsManualRedirectPossible> {
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
