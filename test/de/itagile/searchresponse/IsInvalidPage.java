package de.itagile.searchresponse;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsInvalidPage extends SpecificationPartial<ISearchParams> {
    private IsInvalidPage() {
    }

    public static IsInvalidPage is_invalid_page() {
        return new IsInvalidPage();
    }

    @Override
    public Specification create(ISearchParams param) {
        return is_invalid_page();
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
