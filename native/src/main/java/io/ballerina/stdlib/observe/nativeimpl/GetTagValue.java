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
