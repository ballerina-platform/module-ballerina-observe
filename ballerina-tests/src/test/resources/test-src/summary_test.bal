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

function testMaxSummary() returns (float) {
    map<string> tags = { "method": "POST" };
    observe:Gauge gauge = new("response_size", "Size of a response.", tags);
    gauge.setValue(1.0);
    gauge.setValue(2.0);
    gauge.setValue(3.0);
    observe:Snapshot[]? summarySnapshot = gauge.getSnapshot();
    if summarySnapshot is observe:Snapshot[] {
        return summarySnapshot[0].max;
    } else {
        return 0.0;
    }
}

function testMeanSummary() returns (float) {
    map<string> tags = { "method": "UPDATE" };
    observe:Gauge gauge = new("response_size", "Size of a response.", tags);
    gauge.setValue(1.0);
    gauge.setValue(2.0);
    gauge.setValue(3.0);
    observe:Snapshot[]? summarySnapshot = gauge.getSnapshot();
    if summarySnapshot is observe:Snapshot[] {
        return summarySnapshot[0].mean;
    } else {
        return 0.0;
    }
}

function testPercentileSummary() returns (observe:PercentileValue[]?) {
    map<string> tags = { "method": "DELETE" };
    observe:Gauge gauge = new("response_size", "Size of a response.", tags);
    gauge.setValue(1.0);
    gauge.setValue(2.0);
    gauge.setValue(3.0);
    gauge.setValue(4.0);
    gauge.setValue(5.0);
    gauge.setValue(6.0);
    observe:Snapshot[]? summarySnapshot = gauge.getSnapshot();
    if summarySnapshot is observe:Snapshot[] {
        return summarySnapshot[0].percentileValues;
    } else {
        return ();
    }
}

function testValueSummary() returns (float) {
    map<string> tags = { "method": "DELETE" };
    observe:Gauge gauge = new("response_size", "Size of a response.", tags);
    gauge.increment();
    gauge.increment(1000.0);
    gauge.decrement();
    gauge.decrement(500.0);
    return gauge.getValue();
}

function testSummaryWithoutTags() returns (float) {
    observe:Gauge gauge = new("new_response_size");
    gauge.setValue(1.0);
    gauge.setValue(2.0);
    gauge.setValue(3.0);
    observe:Snapshot[]? summarySnapshot = gauge.getSnapshot();
    if summarySnapshot is observe:Snapshot[] {
        return summarySnapshot[0].mean;
    } else {
        return 0.0;
    }
}

function registerAndIncrement() returns (float) {
    observe:Gauge gauge = new("register_response_size");
    checkpanic gauge.register();
    gauge.increment(1);
    return gauge.getValue();
}
