module io.ballerina.observability {
    requires com.google.gson;
    requires io.ballerina.lang;
    requires io.ballerina.runtime;
    requires io.opentelemetry.api;
    requires io.opentelemetry.sdk.testing;
    requires io.opentelemetry.sdk.trace;
    requires io.opentelemetry.context;

    provides io.ballerina.runtime.observability.tracer.spi.TracerProvider
            with io.ballerina.stdlib.observe.mockextension.BMockTracerProvider;
}
