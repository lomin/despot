package de.itagile.searchresponse;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsStepUpPossible extends SpecificationPartial<ISearchParams> {
    private IsStepUpPossible() {
    }

    public static IsStepUpPossible is_step_up_possible() {
        return new IsStepUpPossible();
    }

    @Override
    public Specification create(ISearchParams param) {
        return is_step_up_possible();
    }

    @Override
    public boolean isTrue() {
        return false;
    }
}
