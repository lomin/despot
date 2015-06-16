package de.itagile.api;

import de.itagile.despot.*;
import de.itagile.despot.http.StatusModifier;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.mediatype.simpleJson.JsonMediaType;
import de.itagile.model.Model;
import de.itagile.predicate.Operations;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Map;

import static de.itagile.api.FullResponse.full_response;
import static de.itagile.api.IsInvalidPage.is_invalid_page;
import static de.itagile.api.IsInvalidUri.is_invalid_uri;
import static de.itagile.api.IsManualRedirectPossible.is_manual_redirect_possible;
import static de.itagile.api.IsPageNrOutOfRange.is_page_nr_out_of_range;
import static de.itagile.api.IsPartialMenu.is_partial_menu;
import static de.itagile.api.IsResultOk.is_result_ok;
import static de.itagile.api.ManualRedirect.manual_redirect;
import static de.itagile.api.RedirectToFirstPage.redirect_to_first_page;
import static de.itagile.api.WriteErrorMsg.write_error_msg;
import static de.itagile.despot.CollectionUtil.mapOf;
import static de.itagile.despot.Despot.despot;
import static de.itagile.despot.Despot.pre;
import static de.itagile.despot.http.ConsumesSpecified.consumes;
import static de.itagile.despot.http.LocationModifier.location;
import static de.itagile.despot.http.MaxAgeModifier.maxAge;
import static de.itagile.despot.http.StatusModifier.status;
import static de.itagile.despot.http.TextEntityModifier.text;
import static de.itagile.predicate.Operations.operations;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ApiTest {

    public static final Operations<IProductSearchParams> OPS = operations(IProductSearchParams.class);
    private final Verifier verifier = mock(Verifier.class);

    private static Despot<IProductSearchParams> PRODUCT_SEARCH_API() {
        return despot(IProductSearchParams.class, "/items/{path}", Method.GET, consumes("application/x-www-form-urlencoded")).
                next(
                        is_invalid_page(),
                        write_error_msg(), status(404), MediaTypeTest.ERROR_MEDIA_TYPE).
                next(
                        OPS.and(is_invalid_uri(), is_manual_redirect_possible()),
                        manual_redirect(), status(301), maxAge(5, SECONDS), location("/items".split("/"), "{path}")).
                next(
                        pre(OPS.not(is_partial_menu())).
                                next(
                                        is_page_nr_out_of_range(),
                                        text("Page Number Out of Range!"), status(301)).
                                next(
                                        is_result_ok(),
                                        full_response(), status(200), MediaTypeTest.PRODUCT_MEDIA_TYPE)).
                last(redirect_to_first_page(), status(301)).
                error(RedirectException.class, write_error_msg(), status(503)).
                error(NullPointerException.class, status(503))
                .verify("/de.itagile.spec/spec.json");
    }

    @Test
    public void responsesWith200AndProductMediaType() throws Exception {
        Response response = PRODUCT_SEARCH_API().response(new ProductSearchParams());

        assertEquals(200, response.getStatus());
        assertEquals("tracking-value", response.getMetadata().getFirst("tracking-header"));

        Map result = (Map) response.getEntity();
        assertEquals("testId123", result.get("productId"));
    }

    @Test
    public void fulfillsTheFullSpec() throws Exception {
        Despot<IProductSearchParams> api = PRODUCT_SEARCH_API();
        assertEquals(api, api.verify("/de.itagile.spec/spec.json"));
    }

    @Test
    public void addsOneModifierToVerifier() throws Exception {
        new Despot<IProductSearchParams>(verifier, "/items/{path}", Method.GET).
                next(
                        is_invalid_page(),
                        redirect_to_first_page(), status(301));

        verify(verifier).add(mapOf(StatusModifier.KEY, 301L));
    }

    @Test
    public void addsAllModifiersToVerifier() throws Exception {
        new Despot<IProductSearchParams>(verifier, "/items/{path}", Method.GET).
                next(
                        is_invalid_page(),
                        redirect_to_first_page(), status(200), new JsonMediaType("test-mediatype"));

        verify(verifier).add(mapOf(StatusModifier.KEY, 200L, "produces", mapOf("name", "test-mediatype", "fields", new HashSet<>())));
    }

    @Test
    public void addsModifierDefinedInPreBlockToVerifier() throws Exception {
        new Despot<IProductSearchParams>(verifier, "/items/{path}", Method.GET)
                .next(
                        pre(OPS.not(is_partial_menu())).
                                next(
                                        is_result_ok(),
                                        full_response(), status(503), new JsonMediaType("error")));

        verify(verifier).add(mapOf(StatusModifier.KEY, 503L, "produces", mapOf("name", "error", "fields", new HashSet<>())));
    }

    @Test
    public void handlesExceptionsWithinDespotExecution() throws Exception {
        Response response = new Despot<IProductSearchParams>(verifier, "/items/{path}", Method.GET)
                .next(
                        OPS.TRUE(),
                        redirectExceptionThrowingResponse(), status(200), new JsonMediaType("ignore"))
                .error(RedirectException.class, status(503))
                .response(new ProductSearchParams());

        assertEquals(503, response.getStatus());
    }

    private ResponseFactory<IProductSearchParams> redirectExceptionThrowingResponse() {
        return new ResponseFactory<IProductSearchParams>() {
            @Override
            public ResponseModifier createResponseModifier(IProductSearchParams param) {
                return new RedirectExceptionThrowingResponse();
            }
        };
    }

    private class RedirectExceptionThrowingResponse implements ResponseModifier {
        @Override
        public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
            throw new RedirectException();
        }

        @Override
        public void spec(Map spec) {
        }
    }
}