package de.itagile.searchresponse;

import de.itagile.despot.Despot;
import de.itagile.despot.DespotElement;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.JsonFormat;
import de.itagile.mediatype.MediaTypeTest;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

import static de.itagile.despot.Despot.first;
import static de.itagile.despot.Despot.pre;
import static de.itagile.searchresponse.BracketResponse.*;
import static de.itagile.searchresponse.FullResponse.full_response;
import static de.itagile.searchresponse.IsPartialMenu.*;
import static de.itagile.searchresponse.IsInvalidPage.is_invalid_page;
import static de.itagile.searchresponse.IsInvalidRequestBasedOnConfiguredMenu.*;
import static de.itagile.searchresponse.IsInvalidUri.*;
import static de.itagile.searchresponse.IsManualRedirectPossible.*;
import static de.itagile.searchresponse.IsPageNrOutOfRange.*;
import static de.itagile.searchresponse.IsResultOk.*;
import static de.itagile.searchresponse.IsStepUpPossible.*;
import static de.itagile.searchresponse.ManualRedirect.*;
import static de.itagile.searchresponse.NotFound.*;
import static de.itagile.searchresponse.RedirectToFirstPage.*;
import static de.itagile.searchresponse.RedirectToFirstPageBracket.redirect_to_first_page_bracket;
import static de.itagile.searchresponse.RedirectToFirstPageFull.*;
import static de.itagile.searchresponse.RedirectToLastConfiguredLevel.*;
import static de.itagile.searchresponse.StepUp.step_up;
import static de.itagile.specification.SpecificationPartial.and;
import static de.itagile.specification.SpecificationPartial.not;
import static org.junit.Assert.assertEquals;

public class SearchResponseTest {

    @Test
    public void integrationTest() throws Exception {
        Despot<ISearchParams> response =
        first(
                is_invalid_page(),
                redirect_to_first_page(), 301).
        next(
            and(is_invalid_uri(), is_manual_redirect_possible()),
                manual_redirect(), 301).
        next(
            and(is_invalid_uri(), not(is_manual_redirect_possible())),
                not_found(), 301).
        next(
                and(if_invalid_request_based_on_configured_menu(), is_manual_redirect_possible()),
                manual_redirect(), 301).
        next(
            and(if_invalid_request_based_on_configured_menu(), not(is_manual_redirect_possible())),
                redirect_to_last_configured_level(), 301).
        next(
            pre(is_partial_menu()).
                next(
                        is_page_nr_out_of_range(),
                        redirect_to_first_page_bracket(), 301).
                next(
                        is_result_ok(),
                        add_tracking_header(bracket_response()), 301)).
        next(
            pre(not(is_partial_menu())).
                    next(
                        is_page_nr_out_of_range(),
                            redirect_to_first_page_full(), 301).
                    next(
                            is_result_ok(),
                            add_tracking_header(full_response()), 301)).
        next(
            is_manual_redirect_possible(),
                manual_redirect(), 301).
        next(is_step_up_possible(),
                step_up(), 301).
        last(redirect_to_first_page(), 301);

        Despot<ISearchParams>.MediaTyped<Map, JsonFormat> typedResponse =
                response.
                        mediaType(Map.class, 200, MediaTypeTest.PRODUCT_SCHEMA_JSON).
                        mediaType(301, MediaTypeTest.PRODUCT_SCHEMA_JSON);

        Map result = typedResponse.response(new SearchParams(), new HashMap());
        assertEquals("AVAILABLE", result.get("availability"));
        assertEquals("testId123", result.get("productId"));
    }

    private ResponsePartial<ISearchParams> add_tracking_header(ResponsePartial<ISearchParams> fullResponse) {
        return fullResponse;
    }
}
