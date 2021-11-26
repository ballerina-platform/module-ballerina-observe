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

function testCounterIncrementByOne() returns (int) {
    map<string> tags = { "method": "GET" };
    observe:Counter counter = new("RequestCounter", tags = tags);
    counter.increment(1);
    return counter.getValue();
}

function testCounterIncrement() returns (int) {
    observe:Counter counter = new("RequestCounter");
    counter.increment(5);
    return counter.getValue();
}

function testCounterError() returns (float) {
    map<string> tags = { "method": "PUT" };
    observe:Counter counter1 = new("requests_total", "Total requests.", tags);
    checkpanic counter1.register();
    counter1.increment(5);
    observe:Gauge gauge = new("requests_total", "Total requests.", tags);
    error? err = gauge.register();
    if err is error {
        panic err;
    } else {
        gauge.increment(5.0);
        return gauge.getValue();
    }
}

function testCounterWithoutTags() returns (int) {
    observe:Counter counter1 = new("new_requests_total");
    counter1.increment(2);
    counter1.increment(1);
    return counter1.getValue();
}

function testReset() returns (boolean) {
    map<string> tags = { "method": "PUT" };
    observe:Counter counter1 = new("requests_total", "Total requests.", tags);
    checkpanic counter1.register();
    int value = counter1.getValue();
    counter1.reset();
    int newValue = counter1.getValue();
    return (value != newValue && newValue == 0);
}
