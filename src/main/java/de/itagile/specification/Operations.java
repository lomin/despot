package de.itagile.specification;

public class Operations<ParamType> {

    private Operations() {
    }

    public static <ParamType> Operations<ParamType> operations(Class<ParamType> clazz) {
        return new Operations<>();
    }

    public SpecificationPartial<? super ParamType> and(final SpecificationPartial<? super ParamType> first, final SpecificationPartial<? super ParamType> second) {
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

    public SpecificationPartial<? super ParamType> or(final SpecificationPartial<? super ParamType> first, final SpecificationPartial<? super ParamType> second) {
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
}
