package de.itagile.api;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsPageNrOutOfRange extends SpecificationPartial<IProductSearchParams> {
    private IsPageNrOutOfRange() {
    }

    public static IsPageNrOutOfRange is_page_nr_out_of_range() {
        return new IsPageNrOutOfRange();
    }

    @Override
    public Specification create(IProductSearchParams param) {
        return is_page_nr_out_of_range();
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
