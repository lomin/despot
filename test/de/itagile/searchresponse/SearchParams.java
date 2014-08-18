package de.itagile.searchresponse;

public class SearchParams implements ISearchParams {
    @Override
    public String getUri() {
        return "/uri";
    }

    @Override
    public String findRedirectMappingByOldPath(String path) {
        return "/oldPath";
    }
}
