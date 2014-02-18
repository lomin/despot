package de.itagile.searchresponse;

public class SearchParams implements ISearchParams {
    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public String findRedirectMappingByOldPath(Path path) {
        return null;
    }

    @Override
    public MenuItem getMenuItem(Path path) {
        return null;
    }
}
