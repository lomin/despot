package de.itagile.despot.http;

import de.itagile.despot.Method;
import de.itagile.despot.Specified;

import java.util.Map;

public class MethodSpecified {

    public static Specified method(final Method method) {
        return new Specified() {
            @Override
            public void spec(Map<String, Object> spec) {
                spec.put("method", method.name());
            }
        };
    }

}
