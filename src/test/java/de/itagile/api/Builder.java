package de.itagile.api;

import java.util.HashMap;
import java.util.Map;

abstract class Builder {
    protected Map model = new HashMap();

    protected Map build() {
        return model;
    }
}
