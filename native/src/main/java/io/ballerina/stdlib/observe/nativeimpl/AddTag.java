package io.ballerina.stdlib.observe.nativeimpl;

import io.ballerina.runtime.api.values.BString;
import io.ballerina.runtime.observability.ObserveUtils;

public class AddTag {
    public static void addTag(BString tagKey, BString tagValue) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserveUtils.addTag(tagKey.getValue(), tagValue.getValue());
        }
    }
}
