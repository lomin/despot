package de.itagile.searchresponse;

import de.itagile.despot.Despot;
import de.itagile.despot.ResponseOptions;
import de.itagile.despot.ResponsePartial;
import de.itagile.ces.Entity;
import de.itagile.ces.HashEntity;
import de.itagile.ces.Key;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.*;

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
                redirect_to_first_page()).
        next(
            and(is_invalid_uri(), is_manual_redirect_possible()),
                manual_redirect()).
        next(
            and(is_invalid_uri(), not(is_manual_redirect_possible())),
                not_found()).
        next(
                and(if_invalid_request_based_on_configured_menu(), is_manual_redirect_possible()),
                manual_redirect()).
        next(
            and(if_invalid_request_based_on_configured_menu(), not(is_manual_redirect_possible())),
                redirect_to_last_configured_level()).
        next(
            pre(is_partial_menu()).
                next(
                    is_page_nr_out_of_range(),
                        redirect_to_first_page_bracket()).
                next(
                        is_result_ok(),
                        add_tracking_header(bracket_response()))).
        next(
            pre(not(is_partial_menu())).
                    next(
                        is_page_nr_out_of_range(),
                            redirect_to_first_page_full()).
                    next(
                            is_result_ok(),
                            add_tracking_header(full_response()))).
        next(
            is_manual_redirect_possible(),
                manual_redirect()).
        next(is_step_up_possible(),
                step_up()).
        last(redirect_to_first_page());

        ResponseOptions responseOptions = response.complete(new SearchParams());
        Response finalResponse = responseOptions.response();
        assertEquals(301, finalResponse.getStatus());
    }

    private ResponsePartial<ISearchParams> add_tracking_header(ResponsePartial<ISearchParams> fullResponse) {
        return fullResponse;
    }
}
