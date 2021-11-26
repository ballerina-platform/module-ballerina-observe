// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/observe;

function getAllMetricsSize() returns (int) {
    observe:Metric?[] metrics = observe:getAllMetrics();
    return (metrics.length());
}

function registerAngGetMetrics() returns (int) {
    map<string> tags = { "method": "PUT" };
    observe:Counter counter1 = new("service_requests_total", "Total requests.", tags);
    checkpanic counter1.register();
    counter1.increment(5);
    return getAllMetricsSize();
}

function lookupRegisteredMetrics() returns (boolean) {
    string name = "service_requests_total";
    map<string> tags = { "method": "PUT" };
    observe:Counter|observe:Gauge|() metric = observe:lookupMetric(name, tags);

    if metric is observe:Counter {
        int value = metric.getValue();
        return (value == 5);
    }

    return false;
}

function lookupRegisteredNameOnlyMetrics() returns (boolean) {
    string name = "service_requests_total";
    observe:Counter|observe:Gauge|() metric = observe:lookupMetric(name);
    if metric is observe:Counter {
        return true;
    }

    if metric is observe:Gauge {
        return true;
    }

    return false;
}

function lookupRegisterAndIncrement() returns (boolean) {
    string name = "service_requests_total";
    map<string> tags = { "method": "PUT" };
    observe:Counter|observe:Gauge|() metric = observe:lookupMetric(name, tags);
    if metric is observe:Counter {
        metric.increment(10);
        observe:Counter|observe:Gauge|() nextLookupMetric = observe:lookupMetric(name, tags);
        if nextLookupMetric is observe:Counter {
            return (nextLookupMetric.getValue() == 15);
        }
        return false;
    }
    return false;
}
