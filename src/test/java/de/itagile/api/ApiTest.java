package de.itagile.api;

import de.itagile.despot.*;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.specification.SpecificationPartial;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;

import static de.itagile.api.FullResponse.full_response;
import static de.itagile.api.IsInvalidPage.is_invalid_page;
import static de.itagile.api.IsInvalidUri.is_invalid_uri;
import static de.itagile.api.IsManualRedirectPossible.is_manual_redirect_possible;
import static de.itagile.api.IsPageNrOutOfRange.is_page_nr_out_of_range;
import static de.itagile.api.IsPartialMenu.is_partial_menu;
import static de.itagile.api.IsResultOk.is_result_ok;
import static de.itagile.api.ManualRedirect.manual_redirect;
import static de.itagile.api.RedirectToFirstPage.redirect_to_first_page;
import static de.itagile.api.RedirectToFirstPageFull.redirect_to_first_page_full;
import static de.itagile.api.SpecBuilder.*;
import static de.itagile.despot.Despot.despot;
import static de.itagile.despot.Despot.pre;
import static de.itagile.specification.SpecificationPartial.and;
import static de.itagile.specification.SpecificationPartial.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ApiTest {

    private final static Despot<IProductSearchParams> PRODUCT_SEARCH_API =
            despot("/items/{path}", Despot.Method.GET, IProductSearchParams.class).
                    next(
                            is_invalid_page(),
                            redirect_to_first_page(), status(301)).
                    next(
                            and(is_invalid_uri(), is_manual_redirect_possible(), IProductSearchParams.class),
                            manual_redirect(), status(301)).
                    next(
                            pre(not(is_partial_menu())).
                                    next(
                                            is_page_nr_out_of_range(),
                                            redirect_to_first_page_full(), status(301)).
                                    next(
                                            is_result_ok(),
                                            full_response(), status(200), MediaTypeTest.PRODUCT_MEDIA_TYPE)).
                    last(redirect_to_first_page(), status(301)).
                    error(RedirectException.class, MediaTypeTest.HTML_MEDIA_TYPE)
                    .verify("/de.itagile.spec/spec.json");
    private static SpecificationPartial<IProductSearchParams> x;
    private static ResponsePartial<? super IProductSearchParams> y;

    private static ResponseModifier status(final int status) {
        return new ResponseModifier() {
            @Override
            public void spec(Map spec) {
                spec.put(DespotSpecParser.STATUS_CODE, status);
            }

            @Override
            public DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) {
                responseBuilder.status(status);
                return despotResponse;
            }

            @Override
            public String toString() {
                return "Status: " + status;
            }
        };
    }

    @Test
    public void responsesWith200AndProductMediaType() throws Exception {
        Response response = PRODUCT_SEARCH_API.response(new ProductSearchParams());

        assertEquals(200, response.getStatus());
        assertEquals("tracking-value", response.getMetadata().getFirst("tracking-header"));

        Map result = (Map) response.getEntity();
        assertEquals("AVAILABLE", result.get("availability"));
        assertEquals("testId123", result.get("productId"));
    }

    @Test
    public void fulfillsTheFullSpec() throws Exception {
        assertEquals(PRODUCT_SEARCH_API, PRODUCT_SEARCH_API.verify("/de.itagile.spec/spec.json"));
    }

    @Test
    public void doesNotFulfillsTheSpecIfNoMatchingStatusCode() throws Exception {
        Set<Map> spec =
                spec()
                        .addEndpoint(
                                endpoint()
                                        .uri("/items/{path}")
                                        .addMethod(
                                                method()
                                                        .method(Despot.Method.GET)
                                                        .addStatusCode(201, "text/html")))
                        .buildAsSet();

        try {
            PRODUCT_SEARCH_API.verify(spec);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void doesNotFulfillsTheSpecIfNoMatchingMediaType() throws Exception {
        Set<Map> spec =
                spec()
                        .addEndpoint(
                                endpoint()
                                        .uri("/items/{path}")
                                        .addMethod(
                                                method()
                                                        .method(Despot.Method.GET)
                                                        .addStatusCode(200, "text/html")
                                                        .addStatusCode(301, "text/html")
                                                        .addStatusCode(404, "text/html")))
                        .buildAsSet();
        try {
            PRODUCT_SEARCH_API.verify(spec);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }
}
