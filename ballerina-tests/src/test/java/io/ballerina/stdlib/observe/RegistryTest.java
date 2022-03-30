/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.stdlib.observe;

import org.ballerinalang.test.BCompileUtil;
import org.ballerinalang.test.BRunUtil;
import org.ballerinalang.test.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test to verify the operations for global metrics registry.
 *
 * @since 0.980.0
 */
public class RegistryTest extends MetricTest {
    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        String resourceRoot = Paths.get("src", "test", "resources").toAbsolutePath().toString();
        Path testResourceRoot = Paths.get(resourceRoot, "test-src");
        compileResult = BCompileUtil.
                compile(testResourceRoot.resolve("metrics_registry_test.bal").toString());
    }

    @Test(groups = "RegistryTest.testGetAllMetrics", dependsOnGroups = "SummaryTest.testRegisteredGauge")
    public void testGetAllMetrics() {
        Object returns = BRunUtil.invoke(compileResult, "getAllMetricsSize");
        Assert.assertEquals(returns, 1L,
                "There shouldn't be any metrics registered in initial state.");
    }

    @Test(groups = "RegistryTest.testRegister", dependsOnMethods = "testGetAllMetrics")
    public void testRegister() {
        Object returns = BRunUtil.invoke(compileResult, "registerAngGetMetrics");
        Assert.assertEquals(returns, 2L,
                "One metric should have been registered.");
    }

    @Test(dependsOnMethods = "testRegister")
    public void lookupMetric() {
        Object returns = BRunUtil.invoke(compileResult, "lookupRegisteredMetrics");
        Assert.assertTrue((Boolean) returns, "Cannot be looked up a registered metric");
    }

    @Test(dependsOnMethods = "lookupMetric")
    public void lookupWithOnlyNameMetric() {
        Object returns = BRunUtil.invoke(compileResult, "lookupRegisteredNameOnlyMetrics");
        Assert.assertFalse((Boolean) returns, "No metric should be returned for only name without tags");
    }

    @Test(dependsOnMethods = "lookupWithOnlyNameMetric")
    public void lookupMetricAndIncrement() {
        Object returns = BRunUtil.invoke(compileResult, "lookupRegisterAndIncrement");
        Assert.assertTrue((Boolean) returns, "No metric should be returned for only name without tags");
    }

}
