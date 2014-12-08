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
    public Verifaction verify(final Set<Map<String, Object>> canonicalSpec) {
        final Set<Map<String, Object>> specsCopy = new HashSet<>(specs);
        specsCopy.removeAll(canonicalSpec);
        if (!specsCopy.isEmpty()) {
            return new UnknownRoutes(specsCopy, canonicalSpec);
        }
        canonicalSpec.removeAll(specs);
        if (!canonicalSpec.isEmpty()) {
            return new UnreachableRoutes(canonicalSpec);
        }
        return new Verified();
    }

    public static interface Verifaction {
        boolean verified();
        RuntimeException exception();
    }

    private static class Verified implements Verifaction {
        @Override
        public boolean verified() {
            return true;
        }

        @Override
        public RuntimeException exception() {
            return null;
        }
    }

    private static class UnknownRoutes implements Verifaction {
        private final Set<Map<String, Object>> specsCopy;
        private final Set<Map<String, Object>> canonicalSpec;

        public UnknownRoutes(Set<Map<String, Object>> specsCopy, Set<Map<String, Object>> canonicalSpec) {
            this.specsCopy = specsCopy;
            this.canonicalSpec = canonicalSpec;
        }

        @Override
        public boolean verified() {
            return false;
        }

        @Override
        public RuntimeException exception() {
            return new UnknownRoutesException("Unknown routes:\n" + specsCopy + "\ndo not fulfill the spec:\n" + canonicalSpec);
        }
    }

    private class UnreachableRoutes implements Verifaction {
        private final Set<Map<String, Object>> canonicalSpec;

        public UnreachableRoutes(Set<Map<String, Object>> canonicalSpec) {
            this.canonicalSpec = canonicalSpec;
        }

        @Override
        public boolean verified() {
            return false;
        }

        @Override
        public RuntimeException exception() {
            return new UnreachableRoutesException("Unreachable routes:\n" + canonicalSpec + "\ncannot be fulfilled by routes:\n" + specs);
        }
    }

    public static class UnreachableRoutesException extends RuntimeException {

        public UnreachableRoutesException(String message) {
            super(message);
        }
    }

    public static class UnknownRoutesException extends RuntimeException {

        public UnknownRoutesException(String message) {
            super(message);
        }
    }
}
