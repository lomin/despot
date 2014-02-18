package de.itagile.searchresponse;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsInvalidRequestBasedOnConfiguredMenu extends SpecificationPartial<ISearchParams> {
    private IsInvalidRequestBasedOnConfiguredMenu() {
    }

    public static IsInvalidRequestBasedOnConfiguredMenu if_invalid_request_based_on_configured_menu() {
        return new IsInvalidRequestBasedOnConfiguredMenu();
    }

    @Override
    public Specification create(ISearchParams param) {
        return if_invalid_request_based_on_configured_menu();
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
