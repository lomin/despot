package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponsePartial;

public class ManualRedirect extends ResponsePartial<ManualRedirect.IManualRedirect> {
    private ManualRedirect() {
    }

    public static ManualRedirect manual_redirect() {
        return new ManualRedirect();
    }

    @Override
    public ResponseModifier create(IManualRedirect param) {
        return manual_redirect();
    }

    public static interface IManualRedirect {

    }
}
