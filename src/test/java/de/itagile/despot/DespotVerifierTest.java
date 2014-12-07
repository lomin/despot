package de.itagile.despot;

import org.junit.Test;

import static de.itagile.util.CollectionUtil.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DespotVerifierTest {


    @Test
    public void verifiesToTrueIfSpecsIsInCanonicalSpec() throws Exception {
        DespotVerifier verifier = new DespotVerifier();

        verifier.add(mapOf("test1", "test2"));

        assertTrue(verifier.verify(setOf(mapOf("A", "B"), mapOf("test1", "test2"))));
    }

    @Test
    public void verifiesToFalseIfSpecsIsNotInCanonicalSpec() throws Exception {
        DespotVerifier verifier = new DespotVerifier();

        verifier.add(mapOf("test1", "test2"));

        assertFalse(verifier.verify(setOf(mapOf("A", "B"), mapOf("C", "D"))));
        assertFalse(verifier.verify(setOf(mapOf("test1", "test3"))));
        assertFalse(verifier.verify(setOf(mapOf("test3", "test2"))));
    }

}