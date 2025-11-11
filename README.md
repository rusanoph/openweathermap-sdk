# OpenWeatherMap SDK for Java

A modern, async-first Java SDK for [the OpenWeatherMap API](https://openweathermap.org/api).
Clean ports/adapters architecture, pluggable HTTP/JSON/cache, interceptor pipeline (logging, caching, retries), and an optional Spring Boot starter that gives you a ready-to-use OwmClient bean.

> Java 21+, Jackson 3, works with JDK HttpClient or OkHttp.

## ✨ Features

- Async-first APIs (CompletableFuture) with convenient sync wrappers.

- Hexagonal architecture: HttpClientPort, SerializerPort, CachePort — swap adapters without touching core.

- Interceptor pipeline: logging, default headers, caching (TTL), retries with backoff/jitter; easy to add your own.

- Thread-safe, immutable config and stateless client.

- Spring Boot starter: drop-in OwmClient bean + owm.* properties.

- Endpoints: Geocoding, Current Weather, 5-day/3-hour Forecast, Air Pollution.

## 📦 Modules

Pick what you need:

- Core: openweathermap-sdk-core (client, APIs, pipeline)

- HTTP adapters: adapters-http-jdk, adapters-http-okhttp

- Serializer adapter: adapters-serializer-jackson (Jackson 3 / snake_case)

- Cache adapter: adapters-cache-guava (simple in-memory TTL)

- Spring Boot: openweathermap-spring-boot-starter

- Artifact coordinates below use io.openweathermap.sdk and a placeholder <version>. Replace with the current release.

## 🚀 Installation
### Gradle (Kotlin DSL)
```kotlin
dependencies {
  // Core
  implementation("io.openweathermap.sdk:openweathermap-sdk-core:<version>")

  // Choose HTTP adapter
  implementation("io.openweathermap.sdk:adapters-http-jdk:<version>")
  // or: implementation("io.openweathermap.sdk:adapters-http-okhttp:<version>")

  // JSON + cache
  implementation("io.openweathermap.sdk:adapters-serializer-jackson:<version>")
  implementation("io.openweathermap.sdk:adapters-cache-guava:<version>")
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>io.openweathermap.sdk</groupId>
    <artifactId>openweathermap-sdk-core</artifactId>
    <version>{version}</version>
  </dependency>

  <!-- HTTP adapter (pick one) -->
  <dependency>
    <groupId>io.openweathermap.sdk</groupId>
    <artifactId>adapters-http-jdk</artifactId>
    <version>{version}</version>
  </dependency>
  <!-- <dependency>
    <groupId>io.openweathermap.sdk</groupId>
    <artifactId>adapters-http-okhttp</artifactId>
    <version>{version}</version>
  </dependency> -->

  <dependency>
    <groupId>io.openweathermap.sdk</groupId>
    <artifactId>adapters-serializer-jackson</artifactId>
    <version>{version}</version>
  </dependency>
  <dependency>
    <groupId>io.openweathermap.sdk</groupId>
    <artifactId>adapters-cache-guava</artifactId>
    <version>{version}</version>
  </dependency>
</dependencies>
```

## ⚡ Quickstart (Console)

Minimal example using the JDK HTTP adapter, Jackson serializer, and Guava cache.
Set `OPENWEATHERMAP_API_KEY` in your environment.

```java
public class OwmConsoleExample {

    private static final String OPENWEATHERMAP_API_KEY_ENV = "OPENWEATHERMAP_API_KEY";
    private static final Integer CACHE_SIZE = 128 * 1024 * 1024;

    public static void main(String[] args) {
        String apiKey = System.getenv(OPENWEATHERMAP_API_KEY_ENV);

        HttpClientPort http = new HttpJdkClientAdapter();
        SerializerPort serializer = new JacksonSerializerAdapter();
        CachePort<String, byte[]> cache = new GuavaCacheAdapter(CACHE_SIZE);

        var owmConfig = OwmClientConfig.builder()
                .apiKey(apiKey)
                .build();

        var runtime = new OwmRuntime(owmConfig, http, serializer, cache);
        var pipeline = new OwmHttpPipeline(runtime);  // By default: logging + headers + cache + retry
        var helper = new OwmEndpointHelper(runtime, pipeline);
        var owm = new OwmClient(runtime, helper);

        // 1) Geocoding
        List<GeoCity> geoCities = owm.getGeoApi().direct("Saint-Petersburg", 10);

        // 2) Coordinates
        double lat = geoCities.getFirst().lat();
        double lon = geoCities.getFirst().lon();

        var request = CoordinatesRequest.builder()
                .lat(lat)
                .lon(lon)
                .build();

        // 3) Weather, forecast, air quality
        Weather weather = owm.getWeatherApi().byCoords(request);
        Forecast5d forecast5d = owm.getForecastApi().byCoords(request);
        AirPollution airPollution = owm.getAirPollutionApi().now(request);

        System.out.println(weather);
        System.out.println(forecast5d);
        System.out.println(airPollution);
    }
}
```

> All SDK endpoints are async-first. Each sync method is a thin .join() over its async counterpart.

## ☕ Spring Boot Starter

Add the starter and (optionally) OkHttp if you prefer it.

### Gradle
```kotlin
dependencies {
  implementation("io.openweathermap.sdk:openweathermap-spring-boot-starter:<version>")
}
```

### Configure `application.yml`
```yaml
...

owm:
  api-key: ${OPENWEATHERMAP_API_KEY}  # or inline your key
  connect-timeout: 5s
  read-timeout: 10s
  retry-attempts: 3
  retry-base-delay: 200ms
  retry-max-delay: 2s
  cache-size-mb: 128
  
...
```

The starter auto-configures:

- `OwmClientConfig`, `OwmRuntime`

- HTTP adapter (OkHttp if on classpath, otherwise JDK)

- Jackson serializer & cache adapter

- Interceptor pipeline (logging → headers → cache → retry)

- `OwmEndpointHelper` and `OwmClient` bean

### Controller example
```java
@RestController
@RequestMapping("/api/openweathermap/v1")
@RequiredArgsConstructor
public class OwmController {

    private final OwmClient client;

    // Async usage (non-blocking)
    @GetMapping("/geo")
    public CompletableFuture<ResponseEntity<List<GeoCity>>> geo(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", defaultValue = "1") Integer limit
    ) {
        return client.getGeoApi().directAsync(query, limit)
                     .thenApply(ResponseEntity::ok);
    }

    // Sync usage (blocks request thread)
    @GetMapping("/weather")
    public ResponseEntity<Weather> weather(
            @RequestParam(name = "lat") Double lat,
            @RequestParam(name = "lon") Double lon,
            @RequestParam(value = "units", defaultValue = "METRIC") OwmUnits units,
            @RequestParam(value = "lang",  defaultValue = "EN")     OwmLanguage lang
    ) {
        var req = CoordinatesRequest.builder()
                .lat(lat).lon(lon)
                .units(units).language(lang)
                .build();
        return ResponseEntity.ok(client.getWeatherApi().byCoords(req));
    }
}
```

## 🧩 Interceptor Pipeline (overview)

The HTTP flow is a chain of interceptors that can augment or short-circuit requests:

- `LoggingInterceptor` — request/response logs with API-key redaction.

- `DefaultHeadersInterceptor` — adds `Accept: application/json` and a `User-Agent`.

- `CacheInterceptor` — TTL-based response cache (serves hits immediately).

- `RetryInterceptor` — retries on `429`/`5xx` and transport errors with exponential backoff + jitter.

You can add/replace interceptors via the Spring customizer using `OwmInterceptorsCustomizer` interface
or by composing your own pipeline when building `OwmClient`.

## 🧭 API Surface

- **GeoApi** — direct(query, limit) / directAsync(...)

- **WeatherApi** — byCoords(CoordinatesRequest) / byCoordsAsync(...)

- **ForecastApi** — byCoords(...) (5-day/3-hour)

- **AirPollutionApi** — now(...)

`CoordinatesRequest` accepts `lat`, `lon`, 
and optional units (`METRIC`, `IMPERIAL`, `STANDARD`) 
and language (e.g., `EN`, `DE`).

## 🛠️ Requirements & Notes

- **Java 17+**

- **Jackson 3** (package `tools.jackson.*`); `JsonMapper.builder().propertyNamingStrategy(SNAKE_CASE)` is used.

- Replace HTTP/Serializer/Cache adapters freely; everything is wired via ports.

- Timeouts/retries/TTL are configurable in `OwmClientConfig` (or owm.* properties with the starter).

## 🔗 OpenWeatherMap Docs

- [Geocoding API](https://openweathermap.org/api/geocoding-api)

- [Current Weather](https://openweathermap.org/current)

- [5 day / 3 hour Forecast](https://openweathermap.org/forecast5)

- [Air Pollution API](https://openweathermap.org/api/air-pollution)

## 📄 License

- **Core SDK:** MIT — see [LICENSE](./LICENSE).
- **Examples & code snippets (`README.md`, `example/`):** MIT-0 — see [example/LICENSE](./example/LICENSE).

SPDX identifiers: `MIT` (core) and `MIT-0` (examples).

> The MIT license permits use, copy, modification, distribution, and sublicensing, provided the copyright and license
> notice are preserved. MIT-0 is a “no-attribution” variant intended to make examples freely copy-pasteable.
