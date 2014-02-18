package de.itagile.searchresponse;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsPartialMenu extends SpecificationPartial<ISearchParams> {
    private IsPartialMenu() {
    }

    public static IsPartialMenu is_partial_menu() {
        return new IsPartialMenu();
    }

    @Override
    public Specification create(ISearchParams param) {
        return is_partial_menu();
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
