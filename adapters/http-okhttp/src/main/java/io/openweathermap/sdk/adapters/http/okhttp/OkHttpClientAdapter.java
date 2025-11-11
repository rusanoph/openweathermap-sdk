package io.openweathermap.sdk.adapters.http.okhttp;

import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class OkHttpClientAdapter implements HttpClientPort {

    private static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");
    private static final RequestBody EMPTY_BODY  = RequestBody.create(new byte[0], OCTET_STREAM);
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final OkHttpClient client;

    public OkHttpClientAdapter() {
        this(new OkHttpClient());
    }

    public OkHttpClientAdapter(OkHttpClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        Objects.requireNonNull(request, "request");

        OkHttpClient client = clientFor(request);
        Request okReq = toOkHttp(request);

        Call call = client.newCall(okReq);
        CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
        cf.whenComplete((__, ___) -> {
            if (cf.isCancelled()) call.cancel();
        });

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                cf.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response resp) {
                try (ResponseBody body = resp.body()) {
                    byte[] bytes = body != null ? body.bytes() : new byte[0];
                    String contentType = (body != null && body.contentType() != null)
                            ? Objects.requireNonNull(body.contentType()).toString()
                            : resp.header(CONTENT_TYPE_HEADER);

                    cf.complete(HttpResponse.builder()
                            .status(resp.code())
                            .body(bytes)
                            .contentType(contentType)
                            .build());
                } catch (IOException e) {
                    cf.completeExceptionally(e);
                }
            }
        });

        return cf;
    }

    private OkHttpClient clientFor(HttpRequest request) {
        long connectMs = Math.max(0, request.getConnectTimeoutMillis());
        long readMs = Math.max(0, request.getReadTimeoutMillis());

        if (connectMs == 0 && readMs == 0) {
            return client;
        }

        OkHttpClient.Builder builder = client.newBuilder();

        if (connectMs > 0) {
            builder.connectTimeout(connectMs, TimeUnit.MILLISECONDS);
        }

        if (readMs > 0) {
            builder.readTimeout(readMs, TimeUnit.MILLISECONDS);
            builder.writeTimeout(readMs, TimeUnit.MILLISECONDS);
            builder.callTimeout(Duration.ofMillis(readMs + (connectMs > 0 ? connectMs : 0)));
        }

        return builder.build();
    }

    private static Request toOkHttp(HttpRequest request) {
        String method = Objects.toString(request.getMethod(), "GET").toUpperCase();
        Request.Builder requestBuilder = new Request.Builder().url(request.getUri().toString());

        String contentType = extractContentType(request.getHeaders());
        addHeadersExceptContentType(requestBuilder, request.getHeaders());

        RequestBody requestBody = buildBody(request.getBody(), contentType);

        switch (method) {
            case "GET"  -> requestBuilder.get();
            case "HEAD" -> requestBuilder.head();
            case "POST" -> requestBuilder.post(nonNull(requestBody));
            case "PUT"  -> requestBuilder.put(nonNull(requestBody));
            case "PATCH"-> requestBuilder.patch(nonNull(requestBody));
            case "DELETE" -> {
                if (requestBody != null) {
                    requestBuilder.delete(requestBody);
                } else {
                    requestBuilder.delete();
                }
            }
            default -> requestBuilder.method(method, allowsBody(method) ? nonNull(requestBody) : null);
        }
        return requestBuilder.build();
    }

    private static String extractContentType(Map<String,String> headers) {
        if (headers == null) {
            return null;
        }

        for (var e : headers.entrySet()) {
            if (CONTENT_TYPE_HEADER.equalsIgnoreCase(e.getKey())) {
                return e.getValue();
            }
        }

        return null;
    }

    private static void addHeadersExceptContentType(Request.Builder b, Map<String,String> headers) {
        if (headers == null) {
            return;
        }

        headers.forEach((k, v) -> {
            if (!CONTENT_TYPE_HEADER.equalsIgnoreCase(k)) {
                b.header(k, v);
            }
        });
    }

    private static RequestBody buildBody(byte[] body, String contentType) {
        if (body == null) {
            return null;
        }

        MediaType mt = contentType != null ? MediaType.parse(contentType) : OCTET_STREAM;

        if (mt == null) {
            mt = OCTET_STREAM;
        }

        return RequestBody.create(body, mt);
    }

    private static boolean allowsBody(String method) {
        return !"GET".equals(method) && !"HEAD".equals(method);
    }

    private static RequestBody nonNull(RequestBody rb) {
        return rb != null ? rb : EMPTY_BODY;
    }
}
