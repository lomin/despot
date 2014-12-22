package de.itagile.mediatype.html;

import de.itagile.despot.EntityFactory;
import de.itagile.mediatype.MediaType;

public class HtmlMediaType extends MediaType<Viewable, HtmlFormat> {

    public HtmlMediaType(String name, HtmlFormat... htmlFormats) {
        super(name, new EntityFactory<Viewable>() {
            @Override
            public Viewable create() {
                return new Viewable();
            }
        }, htmlFormats);
    }
}
