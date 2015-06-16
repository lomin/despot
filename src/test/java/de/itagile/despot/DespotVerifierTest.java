package de.itagile.despot;

import org.junit.Test;

import static de.itagile.despot.CollectionUtil.mapOf;
import static de.itagile.despot.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DespotVerifierTest {


    @Test
    public void verifiesToTrueIfSpecsIsInCanonicalSpec() throws Exception {
        DespotVerifier verifier = new DespotVerifier();

        verifier.add(mapOf("test1", "test2"));
        verifier.add(mapOf("A", "B"));

        assertTrue(verifier.verify(setOf(mapOf("A", "B"), mapOf("test1", "test2"))).verified());
    }

    @Test
    public void verifiesToFalseIfItemsInSpecAreNotInCanonicalSpec() throws Exception {
        DespotVerifier verifier = new DespotVerifier();

        verifier.add(mapOf("test1", "test2"));

        Verification verification = verifier.verify(setOf(mapOf("A", "B"), mapOf("test1", "test2")));
        assertFalse(verification.verified());
        assertEquals(DespotVerifier.UnreachableRoutesException.class, verification.exception().getClass());
    }

    @Test
    public void verifiesToFalseIfItemsInCanonicalSpecAreNotInSpec() throws Exception {
        DespotVerifier verifier = new DespotVerifier();

        verifier.add(mapOf("test1", "test2"));

        assertFalse(verifier.verify(setOf(mapOf("A", "B"), mapOf("C", "D"))).verified());
        assertFalse(verifier.verify(setOf(mapOf("test1", "test3"))).verified());
        Verification verification = verifier.verify(setOf(mapOf("test3", "test2")));
        assertFalse(verification.verified());
        assertEquals(DespotVerifier.UnknownRoutesException.class, verification.exception().getClass());
    }

}