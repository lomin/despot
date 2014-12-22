package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class WriteErrorMsg extends ResponsePartial<WriteErrorMsg.IErrorMsg> {
    private WriteErrorMsg() {
    }

    public static WriteErrorMsg write_error_msg() {
        return new WriteErrorMsg();
    }

    @Override
    public ResponseModifier create(IErrorMsg param) {
        return write_error_msg();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        model.update(MediaTypeTest.REASON_FIELD, "Something went wrong.");
    }

    public static interface IErrorMsg {

    }
}
