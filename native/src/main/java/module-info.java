import io.ballerina.observe.mockextension.BMockTracerProvider;

module io.ballerina.observability {
    requires com.google.gson;
    requires io.ballerina.lang;
    requires io.ballerina.runtime;
    requires io.opentelemetry.api;
    requires io.opentelemetry.sdk.testing;
    requires io.opentelemetry.sdk.trace;
    requires io.opentelemetry.context;
    requires slf4j.jdk14;
    requires slf4j.api;

    provides io.ballerina.runtime.observability.tracer.spi.TracerProvider
            with BMockTracerProvider;
}
