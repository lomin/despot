package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponsePartial;

public class RedirectToFirstPageFull extends ResponsePartial<IProductSearchParams> {
    private RedirectToFirstPageFull() {
    }

    public static RedirectToFirstPageFull redirect_to_first_page_full() {
        return new RedirectToFirstPageFull();
    }

    @Override
    public ResponseModifier create(IProductSearchParams param) {
        return redirect_to_first_page_full();
    }
}
