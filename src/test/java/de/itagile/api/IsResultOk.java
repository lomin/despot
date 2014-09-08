package de.itagile.api;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsResultOk extends SpecificationPartial<IProductSearchParams> {
    private IsResultOk() {
    }

    public static IsResultOk is_result_ok() {
        return new IsResultOk();
    }

    @Override
    public Specification create(IProductSearchParams param) {
        return is_result_ok();
    }

    @Override
    public boolean isTrue() {
        return true;
    }
}
