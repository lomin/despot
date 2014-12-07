package de.itagile.despot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DespotVerifier implements Verifier {
    private final Set<Map<String, Object>> specs = new HashSet<>();

    @Override
    public void add(Map<String, Object> spec) {
        specs.add(spec);
    }

    @Override
    public boolean verify(Set<Map<String, Object>> canonicalSpec) {
        Set<Map<String, Object>> specsCopy = new HashSet<>(specs);
        for (Map m : canonicalSpec) {
            specsCopy.remove(m);
        }
        return specsCopy.isEmpty();
    }
}
