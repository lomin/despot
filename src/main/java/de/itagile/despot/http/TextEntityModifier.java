package de.itagile.despot.http;

import de.itagile.despot.StatelessResponsePartial;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class TextEntityModifier<T> extends StatelessResponsePartial<T> {
    private final String text;

    private TextEntityModifier(String text) {
        this.text = text;
    }

    public static <T> TextEntityModifier<T> text(String text) {
        return new TextEntityModifier<T>(text);
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        responseBuilder.entity(text);
    }
}
