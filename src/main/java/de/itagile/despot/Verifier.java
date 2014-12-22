package de.itagile.despot;

import java.util.Map;
import java.util.Set;

public interface Verifier {
    void add(Map<String, Object> spec);

    Verification verify(Set<Map<String, Object>> canonicalSpec);
}
