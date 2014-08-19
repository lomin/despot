package de.itagile.searchresponse;

import de.itagile.despot.Despot;
import de.itagile.despot.EntityFactory;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.html.Viewable;
import de.itagile.mediatype.MediaTypeTest;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Map;

import static de.itagile.despot.Despot.first;
import static de.itagile.despot.Despot.pre;
import static de.itagile.searchresponse.FullResponse.full_response;
import static de.itagile.searchresponse.IsInvalidPage.is_invalid_page;
import static de.itagile.searchresponse.IsInvalidUri.is_invalid_uri;
import static de.itagile.searchresponse.IsManualRedirectPossible.is_manual_redirect_possible;
import static de.itagile.searchresponse.IsPageNrOutOfRange.is_page_nr_out_of_range;
import static de.itagile.searchresponse.IsPartialMenu.is_partial_menu;
import static de.itagile.searchresponse.IsResultOk.is_result_ok;
import static de.itagile.searchresponse.ManualRedirect.manual_redirect;
import static de.itagile.searchresponse.RedirectToFirstPage.redirect_to_first_page;
import static de.itagile.searchresponse.RedirectToFirstPageFull.redirect_to_first_page_full;
import static de.itagile.specification.SpecificationPartial.and;
import static de.itagile.specification.SpecificationPartial.not;
import static org.junit.Assert.assertEquals;

public class SearchResponseTest {

    private ResponsePartial<ISearchParams> add_tracking_header(ResponsePartial<ISearchParams> fullResponse) {
        return fullResponse;
    }

    @Test
    public void integrationTest() throws Exception {
        Despot<ISearchParams> despot =
                first(
                        is_invalid_page(),
                        redirect_to_first_page(), 301).
                        next(
                                and(is_invalid_uri(), is_manual_redirect_possible()),
                                manual_redirect(), 301).
                        next(
                                pre(not(is_partial_menu())).
                                        next(
                                                is_page_nr_out_of_range(),
                                                redirect_to_first_page_full(), 301).
                                        next(
                                                is_result_ok(),
                                                add_tracking_header(full_response()), 200)).
                        last(redirect_to_first_page(), 301).
                        error(RedirectException.class, 404);

        despot
                .status(201, MediaTypeTest.HTML_MEDIA_TYPE, new EntityFactory<Viewable>() {
                    @Override
                    public Viewable create() {
                        return new Viewable();
                    }
                })
                .status(301, MediaTypeTest.PRODUCT_MEDIA_TYPE, new EntityFactory<JSONObject>() {
                    @Override
                    public JSONObject create() {
                        return new JSONObject();
                    }
                });

        /*
            public Response category(HttpServletRequest request, String sortBy, Integer pageNr, Integer pageSize, String menuPath):
         */

        Response response = despot.response(new SearchParams());

        assertEquals(301, response.getStatus());
        assertEquals("redirect-value", response.getMetadata().getFirst("redirect-header"));

        Map result = (Map) response.getEntity();
        assertEquals("AVAILABLE", result.get("availability"));
        assertEquals("testId123", result.get("productId"));
    }

}
