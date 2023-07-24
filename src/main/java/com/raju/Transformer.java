package com.raju;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

public class Transformer {
    public static TransformationResult transform(JsonNode docDataJson) {
        return new TransformationResult() {
            public boolean isSuccess() {
                return true;
            }

            @Override
            public JsonNode getTransformedJson() {
                return docDataJson;
            }
        };
    }
}
