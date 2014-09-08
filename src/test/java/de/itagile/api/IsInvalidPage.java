package de.itagile.api;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

public class IsInvalidPage <T extends IsInvalidPage.Pageable> extends SpecificationPartial<T> {
    private Pageable pageable;

    private IsInvalidPage() {
    }

    private IsInvalidPage(Pageable pageable) {
        this.pageable = pageable;
    }

    public static IsInvalidPage is_invalid_page() {
        return new IsInvalidPage();
    }

    @Override
    public boolean isTrue() {
        return pageable.getPage() < 0;
    }

    @Override
    public Specification create(Pageable pageable) {
        return new IsInvalidPage(pageable);
    }

    public static interface Pageable {
        int getPage();
    }
}
