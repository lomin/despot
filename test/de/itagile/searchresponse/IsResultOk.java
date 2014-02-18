package de.itagile.searchresponse;

import de.itagile.specification.Specification;

public class IsResultOk extends de.itagile.specification.SpecificationPartial<ISearchParams> {
    private IsResultOk() {
    }

    public static IsResultOk is_result_ok() {
        return new IsResultOk();
    }

    @Override
    public Specification create(ISearchParams param) {
        return is_result_ok();
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
