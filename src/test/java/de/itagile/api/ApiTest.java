package de.itagile.api;

import de.itagile.despot.Despot;
import de.itagile.despot.EntityFactory;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.mediatype.html.Viewable;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Map;

import static de.itagile.api.ManualRedirect.manual_redirect;
import static de.itagile.api.SpecBuilder.*;
import static de.itagile.despot.Despot.despot;
import static de.itagile.despot.Despot.pre;
import static de.itagile.specification.SpecificationPartial.and;
import static de.itagile.specification.SpecificationPartial.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ApiTest {

    private
    @SuppressWarnings("unchecked")
    Despot<IProductSearchParams> despot;

    @Before
    public void setUp() throws Exception {
        despot = despot("/items/{path}", Despot.Method.GET, IProductSearchParams.class).
                next(
                        IsInvalidPage.is_invalid_page(),
                        RedirectToFirstPage.redirect_to_first_page(), 301).
                next(
                        and(IsInvalidUri.is_invalid_uri(), IsManualRedirectPossible.is_manual_redirect_possible()),
                        manual_redirect(), 301).
                next(
                        pre(not(IsPartialMenu.is_partial_menu())).
                                next(
                                        IsPageNrOutOfRange.is_page_nr_out_of_range(),
                                        RedirectToFirstPageFull.redirect_to_first_page_full(), 301).
                                next(
                                        IsResultOk.is_result_ok(),
                                        FullResponse.full_response(), 200)).
                last(RedirectToFirstPage.redirect_to_first_page(), 301).
                error(RedirectException.class, 404)
                .status(200, MediaTypeTest.PRODUCT_MEDIA_TYPE, new EntityFactory<JSONObject>() {
                    @Override
                    public JSONObject create() {
                        return new JSONObject();
                    }
                })
                .status(404, MediaTypeTest.HTML_MEDIA_TYPE, new EntityFactory<Viewable>() {
                    @Override
                    public Viewable create() {
                        return new Viewable();
                    }
                });
    }

    @Test
    public void responsesWith200AndProductMediaType() throws Exception {
        Response response = despot.response(new ProductSearchParams());

        assertEquals(200, response.getStatus());
        assertEquals("tracking-value", response.getMetadata().getFirst("tracking-header"));

        Map result = (Map) response.getEntity();
        assertEquals("AVAILABLE", result.get("availability"));
        assertEquals("testId123", result.get("productId"));
    }

    @Test
    public void doesNotFulfillsTheSpecIfNoMatchingEndpointUri() throws Exception {
        Map spec =
                spec()
                        .addEndpoint(
                                endpoint()
                                        .uri("/test"))
                        .build();

        assertFalse(despot.verifyAllEndpoints(spec));
    }

    @Test
    public void doesNotFulfillsTheSpecIfNoMatchingEndpointMethod() throws Exception {
        Map spec =
                spec()
                        .addEndpoint(
                                endpoint()
                                        .uri("/items/{path}")
                                        .addMethod(
                                                method()
                                                        .method(Despot.Method.POST)))
                        .build();

        assertFalse(despot.verifyAllEndpoints(spec));
    }

    @Test
    public void doesNotFulfillsTheSpecIfNoMatchingStatusCode() throws Exception {
        Map spec =
                spec()
                        .addEndpoint(
                                endpoint()
                                        .uri("/items/{path}")
                                        .addMethod(
                                                method()
                                                        .method(Despot.Method.GET)
                                                        .addStatusCode(201, "text/html")))
                        .build();

        assertFalse(despot.verifyAllEndpoints(spec));
    }

    @Test
    public void doesNotFulfillsTheSpecIfNoMatchingMediaType() throws Exception {
        Map spec =
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
                        .build();
        assertFalse(despot.verifyAllEndpoints(spec));
    }

    @Test
    public void fulfillsTheFullSpec() throws Exception {
        assertEquals(despot, despot.verify("/de.itagile.spec/spec.json"));
    }
}
