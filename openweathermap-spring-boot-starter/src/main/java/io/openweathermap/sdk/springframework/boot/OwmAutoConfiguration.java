package io.openweathermap.sdk.springframework.boot;

import io.openweathermap.sdk.adapters.cache.guava.GuavaCacheAdapter;
import io.openweathermap.sdk.adapters.http.jdk.HttpJdkClientAdapter;
import io.openweathermap.sdk.adapters.http.okhttp.OkHttpClientAdapter;
import io.openweathermap.sdk.adapters.serializer.jackson.JacksonSerializerAdapter;
import io.openweathermap.sdk.core.cache.CachePort;
import io.openweathermap.sdk.core.client.OwmClient;
import io.openweathermap.sdk.core.client.OwmClientConfig;
import io.openweathermap.sdk.core.client.OwmEndpointHelper;
import io.openweathermap.sdk.core.client.OwmRuntime;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.interceptor.OwmHttpPipeline;
import io.openweathermap.sdk.core.interceptor.OwmInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.CacheInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.DefaultHeadersInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.LoggingInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.RetryInterceptor;
import io.openweathermap.sdk.core.logger.JdkLoggerAdapter;
import io.openweathermap.sdk.core.serializer.SerializerPort;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoConfiguration
@EnableConfigurationProperties(OwmProperties.class)
@ConditionalOnClass(OwmClient.class)
public class OwmAutoConfiguration {
    /* -------- HttpClientPort: OkHttp if exists, else JDK -------- */
    @Bean
    @ConditionalOnClass(OkHttpClient.class)
    @ConditionalOnMissingBean
    HttpClientPort okHttpPort(
            ObjectProvider<OkHttpClient> provided
    ) {
        OkHttpClient base = provided.getIfAvailable(OkHttpClient::new);
        return new OkHttpClientAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(HttpClientPort.class)
    HttpClientPort jdkPort() { return new HttpJdkClientAdapter(); }

    /* -------- Serializer -------- */
    @Bean
    @ConditionalOnMissingBean
    SerializerPort serializerPort() { return new JacksonSerializerAdapter(); }

    /* -------- Cache -------- */
    @Bean
    @ConditionalOnMissingBean
    CachePort<String, byte[]> cachePort(OwmProperties p) {
        return new GuavaCacheAdapter(p.cacheSizeBytesOrDefault());
    }

    /* -------- Config & Runtime -------- */
    @Bean
    @ConditionalOnMissingBean
    OwmClientConfig owmClientConfig(OwmProperties p) {
        return OwmClientConfig.builder()
                .host(p.hostOrDefault())
                .apiKey(p.apiKey())
                .connectTimeout(p.connectOrDefault())
                .readTimeout(p.readOrDefault())
                .retryAttempts(p.attemptsOrDefault())
                .retryBaseDelay(p.baseDelayOrDefault())
                .retryMaxDelay(p.maxDelayOrDefault())
                .retryJitter(p.jitterOrDefault())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    OwmRuntime owmRuntime(OwmClientConfig cfg, HttpClientPort http, SerializerPort ser, CachePort<String, byte[]> cache) {
        return new OwmRuntime(cfg, http, ser, cache);
    }

    /* -------- Pipeline + Interceptors -------- */
    @Bean
    @ConditionalOnMissingBean
    OwmHttpPipeline owmPipeline(OwmRuntime rt,
                                OwmProperties p,
                                ObjectProvider<OwmInterceptorsCustomizer> customizers) {
        List<OwmInterceptor> list = new ArrayList<>(List.of(
                new LoggingInterceptor(JdkLoggerAdapter.get("OpenWeatherMap-SDK")),
                new DefaultHeadersInterceptor(Map.of(
                        "Accept", "application/json",
                        "User-Agent", p.userAgentOrDefault()
                )),
                new CacheInterceptor(rt.getCache(), System::currentTimeMillis),
                new RetryInterceptor(
                        rt.getConfig().getRetryAttempts(),
                        rt.getConfig().getRetryBaseDelay(),
                        rt.getConfig().getRetryMaxDelay(),
                        rt.getConfig().getRetryJitter(),
                        rt.getConfig().getScheduler()
                )
        ));

        customizers.orderedStream().forEach(c -> c.customize(list)); // пользователь может добавить/удалить/переставить

        return new OwmHttpPipeline(rt, list);
    }

    /* -------- Endpoint helper + Client -------- */
    @Bean
    @ConditionalOnMissingBean
    OwmEndpointHelper owmEndpointHelper(OwmRuntime rt, OwmHttpPipeline pipeline) {
        return new OwmEndpointHelper(rt, pipeline);
    }

    @Bean
    @ConditionalOnMissingBean
    public OwmClient owmClient(OwmRuntime rt, OwmEndpointHelper helper) {
        return new OwmClient(rt, helper);
    }

    /* -------- Version for User-Agent from manifest -------- */
    static String version() {
        String v = OwmAutoConfiguration.class.getPackage().getImplementationVersion();
        return (v != null && !v.isBlank()) ? v : "dev";
    }
}
