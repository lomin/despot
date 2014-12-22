package de.itagile.despot;

import de.itagile.model.Model;

public interface DespotResponse extends ResponseModifier {

    Model responseModel() throws Exception;
}
