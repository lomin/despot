package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class StepUp extends ResponsePartial<ISearchParams> {
    private StepUp() {
    }

    public static StepUp step_up() {
        return new StepUp();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return step_up();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder) {
    }
}
