package de.itagile.api;

public interface IProductSearchParams
        extends
        IsInvalidUri.IIsInvalidUri,
        IsManualRedirectPossible.IIsManualRedirectPossible,
        ManualRedirect.IManualRedirect,
        IsInvalidPage.Pageable,
        FullResponse.IFullResponseParams {
}
