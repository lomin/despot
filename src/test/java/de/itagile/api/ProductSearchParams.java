package de.itagile.api;

public class ProductSearchParams implements IProductSearchParams {
    @Override
    public String getUri() {
        return "/uri";
    }

    @Override
    public String findRedirectMappingByOldPath(String path) {
        return "/oldPath";
    }

    @Override
    public int getPage() {
        return 0;
    }
}
