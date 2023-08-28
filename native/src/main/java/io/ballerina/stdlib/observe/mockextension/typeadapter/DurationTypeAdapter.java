/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.observe.mockextension.typeadapter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * Provides a custom serializer and deserializer to convert {@link Duration} to and from JSON.
 *
 * @since 1.2.0
 */
public class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    private static final Gson gson = new Gson();

    @Override
    public JsonElement serialize(final Duration duration, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        long seconds = duration.getSeconds();
        int nanos = duration.getNano();
        DurationData durationData = new DurationData(seconds, nanos);
        return gson.toJsonTree(durationData);
    }

    @Override
    public Duration deserialize(final JsonElement json, final Type typeOfT,
                                 final JsonDeserializationContext context) throws JsonParseException {
        DurationData data = gson.fromJson(json, DurationData.class);
        return Duration.ofSeconds(data.seconds(), data.nanos());
    }

    private record DurationData(long seconds, int nanos) { }
}
