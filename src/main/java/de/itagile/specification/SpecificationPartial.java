package de.itagile.specification;

import de.itagile.despot.Completable;

public abstract class SpecificationPartial<ParamType> implements Completable<ParamType, Specification>, Specification {

    public static <ParamType> SpecificationPartial<ParamType> not(final SpecificationPartial<ParamType> spec) {
        return new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return Specifications.not(spec.create(param));
            }

            @Override
            public boolean isTrue() {
                throw new IllegalStateException();
            }
        };
    }

    public static <ParamType> SpecificationPartial<? super ParamType> and(final SpecificationPartial<? super ParamType> first, final SpecificationPartial<? super ParamType> second, Class<ParamType> _) {
        return new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return Specifications.and(first.create(param), second.create(param));
            }

            @Override
            public boolean isTrue() {
                throw new IllegalStateException();
            }
        };
    }

    public static <ParamType> SpecificationPartial<ParamType> or(final SpecificationPartial<ParamType> first, final SpecificationPartial<ParamType> second) {
        return new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return Specifications.or(first.create(param), second.create(param));
            }

            @Override
            public boolean isTrue() {
                throw new IllegalStateException();
            }
        };
    }

    public static <ParamType> SpecificationPartial<ParamType> TRUE(Class<ParamType> clazz) {
        return new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return this;
            }

            @Override
            public boolean isTrue() {
                return true;
            }
        };
    }
}
