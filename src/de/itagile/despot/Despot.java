package de.itagile.despot;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

import java.util.ArrayList;
import java.util.List;

import static de.itagile.specification.SpecificationPartial.and;

public class Despot<ParamType> {

    private final List<ResponseStructureElement> options = new ArrayList<>();

    private Despot<ParamType> addOption(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option) {
        this.options.add(new ResponseStructureElement(specification, option));
        return this;
    }

    public Despot<ParamType> next(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option) {
        return addOption(specification, option);
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for(ResponseStructureElement element: preDespot.options) {
            options.add(element);
        }
        return this;
    }

    public Despot<ParamType> last(ResponsePartial<ParamType> option) {
        return addOption(new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return this;
            }

            @Override
            public boolean isTrue() {
                return true;
            }
        }, option);
    }

    public ResponseOptions complete(ParamType param) {
        ResponseOptions responseOptions = new ResponseOptions();
        for (ResponseStructureElement option : options) {
            responseOptions.add(option.specification.create(param), option.response.create(param));
        }
        return responseOptions;
    }

    public static <T> Despot<T> first(SpecificationPartial<T> specification, ResponsePartial<T> option) {
        return new Despot<T>().addOption(specification, option);
    }

    public static <ParamType> PreDespot<ParamType> pre(SpecificationPartial<ParamType> specification) {
        return new PreDespot<>(specification);
    }

    public static class PreDespot<ParamType> extends Despot<ParamType> {

        private final SpecificationPartial<ParamType> s;

        private PreDespot(SpecificationPartial<ParamType> s) {
            this.s = s;
        }

        public PreDespot<ParamType> next(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option) {
            super.next(and(s, specification), option);
            return this;
        }
    }

    private class ResponseStructureElement {
        private final SpecificationPartial<ParamType> specification;
        private final ResponsePartial<ParamType> response;

        private ResponseStructureElement(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> response) {
            this.specification = specification;
            this.response = response;
        }
    }
}