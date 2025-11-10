package io.openweathermap.sdk.util.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryBuilder {

    private final Map<String,String> q = new LinkedHashMap<>();

    public QueryBuilder add(String k, Object v) {
        if (v != null) {
            q.put(k, String.valueOf(v));
        }

        return this;
    }

    public String build() {
        return q.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

