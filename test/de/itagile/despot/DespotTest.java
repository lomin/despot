package de.itagile.despot;

import de.itagile.specification.SpecificationPartial;
import org.junit.Before;
import org.junit.Test;

import static de.itagile.despot.Despot.first;
import static de.itagile.specification.SpecificationPartial.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DespotTest {

    private final SpecificationPartial<String> TRUE = TRUE(String.class);
    private SpecificationPartial<String> spec;
    private ResponsePartial<String> response1;
    private ResponsePartial<String> response2;
    private ResponsePartial<String> response3;
    private ResponsePartial<String> response4;

    @Before
    public void setUp() throws Exception {
        spec = mock(SpecificationPartial.class);
        response1 = mock(ResponsePartial.class);
        response2 = mock(ResponsePartial.class);
        response3 = mock(ResponsePartial.class);
        response4 = mock(ResponsePartial.class);
        initResponses(response1, response2, response3, response4);

    }

    private void initResponses(ResponsePartial<String>... options) {
        for (ResponsePartial<String> option : options) {
            when(option.create(anyString())).thenReturn(option);
        }
    }

    @Test
    public void createsNewTreeWithParams() throws Exception {
        Despot structure =
                first(spec, response1, 301).
                        next(spec, response2, 301).
                        next(spec, response3, 301).
                        next(spec, response4, 301);

        structure.complete("test");

        verify(response1).create("test");
        verify(response2).create("test");
        verify(response3).create("test");
        verify(response4).create("test");

        verify(spec, times(4)).create("test");
    }

    @Test
    public void returnsResponseForTrueSpecification() throws Exception {
        Despot structure =
                first(not(TRUE), response1, 301).
                        next(and(
                                TRUE,
                                not(TRUE)),
                                response2, 301).
                        next(TRUE, response3, 301).
                        next(TRUE, response4, 301);

        DespotElement despotElement = structure.complete("test");
        despotElement.response();

        verify(response1, times(0)).response();
        verify(response2, times(0)).response();
        verify(response3, times(1)).response();
        verify(response4, times(0)).response();
    }
}
