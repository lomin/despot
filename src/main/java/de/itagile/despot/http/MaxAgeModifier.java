package de.itagile.despot.http;

import de.itagile.despot.ResponseModifier;
import de.itagile.model.Model;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MaxAgeModifier implements ResponseModifier {

    private static final String KEY = "max-age";
    private final long duration;
    private final TimeUnit timeUnit;

    private MaxAgeModifier(long duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public static MaxAgeModifier maxAge(int duration, TimeUnit timeUnit) {
        return new MaxAgeModifier(duration, timeUnit);
    }

    private CacheControl cacheHeader(long duration, TimeUnit timeUnit) {
        final CacheControl cc = new CacheControl();
        final Long l = timeUnit.toSeconds(duration);
        cc.setMaxAge(l.intValue());
        return cc;
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) {
        responseBuilder.cacheControl(cacheHeader(duration, timeUnit));
    }

    @Override
    public void spec(Map<String, Object> spec) {
        Map<String, Object> map = new HashMap<>();
        map.put("duration", duration);
        map.put("time-unit", timeUnit.name());
        spec.put(KEY, map);
    }

    @Override
    public String toString() {
        return "Max-Age = " + duration + " seconds";
    }
}
