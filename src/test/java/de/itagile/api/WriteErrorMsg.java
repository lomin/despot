package de.itagile.api;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.model.HashModel;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class WriteErrorMsg extends ResponsePartial<WriteErrorMsg.IErrorMsg> {
    private WriteErrorMsg() {
    }

    public static WriteErrorMsg write_error_msg() {
        return new WriteErrorMsg();
    }

    @Override
    public DespotResponse create(IErrorMsg param) {
        return write_error_msg();
    }

    @Override
    public Model responseModel() {
        Model e = new HashModel();
        e.update(MediaTypeTest.REASON_FIELD, "Something went wrong.");
        return e;
    }

    public static interface IErrorMsg {

    }
}
