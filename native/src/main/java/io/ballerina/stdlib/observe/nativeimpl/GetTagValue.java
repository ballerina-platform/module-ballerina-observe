/*
 * Copyright (c) 2025, WSO2 LLC. (http://wso2.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ballerina.stdlib.observe.nativeimpl;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.runtime.observability.ObserveUtils;
import io.ballerina.runtime.observability.ObserverContext;
import io.ballerina.runtime.observability.metrics.Tag;

public class GetTagValue {
    public static Object getTagValue(Environment env, BString tagKey) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = ObserveUtils.getObserverContextOfCurrentFrame(env);
            if (observerContext == null) {
                return null;
            }
            Tag tag = observerContext.getTag(tagKey.getValue());
            if (tag != null) {
                return StringUtils.fromString(tag.getValue());
            }
            return null;
        }
        return null;
    }
}
