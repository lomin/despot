package de.itagile.despot;

public interface ResponseFactory<ParamType> {
    ResponseModifier createResponseModifier(ParamType param);
}
