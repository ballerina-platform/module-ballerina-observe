
# Ballerina Observe Internal Library

[![Build](https://github.com/ballerina-platform/module-ballerina-http/actions/workflows/build-timestamped-master.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinai-observe/actions/workflows/build-timestamped-master.yml)
[![Trivy](https://github.com/ballerina-platform/module-ballerina-http/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinai-observe/actions/workflows/trivy-scan.yml)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/module-ballerina-http.svg)](https://github.com/ballerina-platform/module-ballerinai-observe/commits/master)
[![codecov](https://codecov.io/gh/ballerina-platform/module-ballerina-observe/branch/master/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/module-ballerina-observe)

## Module Overview

This module provides internal configurations and an API for observing Ballerina services, enabled via the `--observability-included` build flag or `--b7a.observability.enabled=true` runtime flag.

## Tracing
Tracing provides information regarding the roundtrip of a service invocation based on the concept of spans, which are
structured in a hierarchy based on the cause and effect concept. The tracing API allows users to tap into that
tracing information, introduce new spans, and add additional information to existing spans using user-defined tags.

### Samples

#### Start a root span & attach a child span

The following code snippet shows an example of starting a root span with no parent and starting another span as a child of the first span.
Note: Make sure that all started spans are closed properly to ensure that all spans are reported properly.

```ballerina
int spanId = observe:startRootSpan("Parent Span");

// Do Something.

int spanId2 = checkpanic observe:startSpan("Child Span", parentSpanId = spanId);

// Do Something.

var ret1 = observe:finishSpan(spanId2);

// Do Something.

var ret2 = observe:finishSpan(spanId);
```

#### Start a span attached to a system trace

When no parentSpanId is given or a parentSpanId of -1 is given, a span is started as a child span to the current active span in the ootb system trace.

```ballerina
int spanId = checkpanic observe:startSpan("Child Span");

// Do Something.

var ret = observe:finishSpan(spanId);
```

#### Attach a tag to a span

It is possible to add tags to the span by using the `observe:addTagToSpan()` function by providing the span id and relevant tag key and tag value.

```ballerina
_ = observe:addTagToSpan(spanId = spanId, "Tag Key", "Tag Value");
```
#### Attach a tag to a span in the system trace
When no spanId is provided or -1 is given, the defined tags are added to the current active span in the ootb system trace.

```ballerina
var ret = observe:addTagToSpan("Tag Key", "Tag Value");
```

## Metrics
There are mainly two kind of metrics instances supported; Counter and Gauge. A counter is a cumulative metric that
represents a single monotonically increasing counter whose value can only increase or be reset to zero on restart.
For example, you can use a counter to represent the number of requests served, tasks completed, or errors.
The Gauge metric instance represents a single numerical value that can arbitrarily go up and down, and also based on the
statistics configurations provided to the Gauge, it can also report the statistics such as max, min, mean, percentiles, etc.

### Counter Samples

#### Create
The following code snippets provides the information on how Counter instances can be created. Instantiating the counter
will simply create an instance based on the params passed.

```ballerina
// Create counter with simply by name.
observe:Counter simpleCounter = new("SimpleCounter"); 

// Create counter with description.
observe:Counter counterWithDesc = new("CounterWithDesc", 
        desc = "This is a sample counter description");

// Create counter with tags.
map<string> counterTags = { "method": "GET" };
observe:Counter counterWithTags = new("CounterWithTags", 
        desc = "Some description", tags = counterTags);
```

#### Register
The counter can be registered with the global metrics registry. Therefore, it can be looked up later without having the
reference of the counter that was created. Also, only the registered counters will be reported to the Metrics reporter
such as Prometheus. In case, if there is already another non counter metric registered,
then there will be an error returned. But if it's another counter instance, then the registered counter instance will
be returned.

```ballerina
map<string> counterTags = { "method": "GET" };
observe:Counter counterWithTags = new("CounterWithTags", 
        desc = "Some description", tags = counterTags);
var anyError = counterWithTags.register();
if anyError is error {
    log:printError("Cannot register the counter", err = anyError);
}
```

#### Unregister
The counter can be unregistered with the global metrics registry if it is already registered. If a metrics is unregistered,
then further it'll not be included in metrics reporting.

```ballerina
map<string> counterTags = { "method": "GET" };
observe:Counter counterWithTags = new("CounterWithTags", 
        desc = "Some description", tags = counterTags);
var anyError = counterWithTags.register();
if anyError is error {
    log:printError("Cannot register the counter", err = anyError);
}
counterWithTags.unregister();
    
```

#### Increment
The counter can be incremented without passing any params (defaulted to 1), or by a specific amount.

```ballerina
map<string> counterTags = { "method": "GET" };
observe:Counter counterWithTags = new("CounterWithTags", 
        desc = "Some description", tags = counterTags);
// Increment by 1.
counterWithTags.increment(); 
// Increment by amount 10.
counterWithTags.increment(amount = 10);
```

#### Reset
The counter can be resetted to default amount = 0.

```ballerina
map<string> counterTags = { "method": "GET" };
observe:Counter counterWithTags = new("CounterWithTags", 
        desc = "Some description", tags = counterTags);
counterWithTags.reset();
```

#### Get Value
The current value can be retrieved by this operation.

```ballerina
map<string> counterTags = { "method": "GET" };
observe:Counter counterWithTags = new("CounterWithTags", 
        desc = "Some description", tags = counterTags);
int currentValue = counterWithTags.getValue();
```

### Gauge Samples

#### Create
The following code snippets provides the information on how Gauge instances can be created. Instantiating the gauge
will simply create an instance based on the params passed.

```ballerina
// Create gauge with simply by name. 
// Uses the default statistics configuration. 
observe:Gauge simpleGauge = new("SimpleGauge"); 

// Create gauge with description.
// Uses the default statistics configuration. 
observe:Gauge gaugeWithDesc = new("GaugeWithDesc", 
        desc = "This is a sample gauge description");

// Create gauge with tags.
// Uses the default statistics configuration. 
map<string> gaugeTags = { "method": "GET" };
observe:Counter gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);

// Create gauge with disabled statistics. 
observe:StatisticConfig[] statsConfigs = [];
observe:Gauge gaugeWithNoStats = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags, statisticConfig = statsConfigs);

// Create gauge with statistics config. 
observe:StatisticConfig config = { timeWindow: 30000, 
        percentiles: [0.33, 0.5, 0.9, 0.99], buckets: 3 };
statsConfigs[0]=config; 

observe:Gauge gaugeWithStats = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags, statisticConfig = statsConfigs);
```

#### Register
The gauge can be registered with the global metrics registry, therefore it can be looked up later without having the
reference of the gauge that was created. Also, only the registered counters will be reported to the Metrics reporter
such as Prometheus. In case, if there is already another non gauge metric registered,
then there will be an error returned. But if it's another gauge instance, then the registered gauge instance will
be returned.

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
var anyError = gaugeWithTags.register();
if anyError is error {
    log:printError("Cannot register the gauge", err = anyError);
}
```

#### Unregister
The gauge can be unregistered with the global metrics registry if it is already registered.
If a metrics is unregistered, then further it'll not be included in metrics reporting.

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
var anyError = gaugeWithTags.register();
if anyError is error {
    log:printError("Cannot register the gauge", err = anyError);
}
gaugeWithTags.unregister();
```

#### Increment
The gauge can be incremented without passing any params (defaulted to 1.0), or by a specific amount.

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
// Increment by 1.
gaugeWithTags.increment(); 
// Increment by amount 10.
gaugeWithTags.increment(amount = 10.0);  
```

#### Decrement
The gauge can be decremented without passing any params (defaulted to 1.0), or by a specific amount.

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
// Increment by 1.
gaugeWithTags.decrement(); 
// Increment by amount 10.
gaugeWithTags.decrement(amount = 10.0);
```

#### Set Value
This method sets the gauge's value with specific amount.

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
gaugeWithTags.setValue(100.0);
```

#### Get Value
The current value can be retrieved by this operation.

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
float currentValue = gaugeWithTags.getValue(); 
```

#### Get Snapshot
This method retrieves current snapshot of the statistics calculation based on the configurations passed to the gauge.
If the statistics are disabled, then it'll be returning nil ().

```ballerina
map<string> gaugeTags = { "method": "GET" };
observe:Gauge gaugeWithTags = new("GaugeWithTags", 
        desc = "Some description", tags = gaugeTags);
gaugeWithTags.setValue(1.0);
gaugeWithTags.setValue(2.0);
gaugeWithTags.setValue(3.0);

observe:Snapshot[]? summarySnapshot = gaugeWithTags.getSnapshot();
if summarySnapshot is observe:Snapshot[] {
    io:println(summarySnapshot);
} else {
    io:println("No statistics available!");
}
```

### Global Metrics Samples

#### Get All Metrics
This method returns all the metrics that are registered in the global metrics registry. This method is mainly useful for
metric reporters, where they can fetch all metrics, format those, and report.

```ballerina
observe:Metric[] metrics = observe:getAllMetrics();
foreach var metric in metrics {
    // Do something.
}
```

#### Lookup Metric
This method will lookup for the metric from the global metric registry and return it.

```ballerina
map<string> tags = { "method": "GET" };
observe:Counter|observe:Gauge|() metric = observe:lookupMetric("MetricName", 
        tags = tags);
if metric is observe:Counter {
    metric.increment(amount = 10);
} else if metric is observe:Gauge {
    metric.increment(amount = 10.0);
} else {
    io:println("No Metric Found!");
}
```

## Build from the source

### Set Up the prerequisites

1. Download and install Java SE Development Kit (JDK) version 21 (from one of the following locations).

    * [Oracle](https://www.oracle.com/java/technologies/downloads/)

    * [OpenJDK](https://adoptopenjdk.net/)

      > **Note:** Set the JAVA_HOME environment variable to the path name of the directory into which you installed JDK.

2. Export your GitHub Personal access token with read package permissions as follows.

        export packageUser=<Username>
        export packagePAT=<Personal access token>

### Build the source

Execute the commands below to build from source.

1. To build the library:
    ```
    ./gradlew clean build
    ```

2. To run the integration tests:
    ```
    ./gradlew clean test
    ```

3. To run a group of tests
    ```
    ./gradlew clean test -Pgroups=<test_group_names>
    ```

4. To build the package without the tests:
    ```
    ./gradlew clean build -x test
    ```

5. To debug the tests:
    ```
    ./gradlew clean test -Pdebug=<port>
    ```

6. To debug with Ballerina language:
    ```
    ./gradlew clean build -PbalJavaDebug=<port>
    ```

7. Publish the generated artifacts to the local Ballerina central repository:
    ```
    ./gradlew clean build -PpublishToLocalCentral=true
    ```

8. Publish the generated artifacts to the Ballerina central repository:
    ```
    ./gradlew clean build -PpublishToCentral=true
    ```

## Contribute to Ballerina

As an open-source project, Ballerina welcomes contributions from the community.

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

* For more information go to the [`Observe` library](https://lib.ballerina.io/ballerina/observe/latest).
* For example demonstrations of the usage, go to [Ballerina By Examples](https://ballerina.io/learn/by-example/).
* Chat live with us via our [Discord server](https://discord.gg/ballerinalang).
* Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
* View the [Ballerina performance test results](https://github.com/ballerina-platform/ballerina-lang/blob/master/performance/benchmarks/summary.md).
