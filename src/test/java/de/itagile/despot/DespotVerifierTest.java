package de.itagile.despot;

import org.junit.Test;

import static de.itagile.util.CollectionUtil.*;
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

        DespotVerifier.Verifaction verifaction = verifier.verify(setOf(mapOf("A", "B"), mapOf("test1", "test2")));
        assertFalse(verifaction.verified());
        assertEquals(DespotVerifier.UnreachableRoutesException.class, verifaction.exception().getClass());
    }

    @Test
    public void verifiesToFalseIfItemsInCanonicalSpecAreNotInSpec() throws Exception {
        DespotVerifier verifier = new DespotVerifier();

        verifier.add(mapOf("test1", "test2"));

        assertFalse(verifier.verify(setOf(mapOf("A", "B"), mapOf("C", "D"))).verified());
        assertFalse(verifier.verify(setOf(mapOf("test1", "test3"))).verified());
        DespotVerifier.Verifaction verifaction = verifier.verify(setOf(mapOf("test3", "test2")));
        assertFalse(verifaction.verified());
        assertEquals(DespotVerifier.UnknownRoutesException.class, verifaction.exception().getClass());
    }

}