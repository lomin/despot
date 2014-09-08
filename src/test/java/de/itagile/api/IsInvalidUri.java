package de.itagile.api;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsInvalidUri<T extends IsInvalidUri.IIsInvalidUri> extends SpecificationPartial<T> {
    private IsInvalidUri() {
    }

    public static IsInvalidUri is_invalid_uri() {
        return new IsInvalidUri();
    }

    @Override
    public Specification create(IIsInvalidUri param) {
        return is_invalid_uri();
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    public static interface IIsInvalidUri {
        String getUri();
    }
}
