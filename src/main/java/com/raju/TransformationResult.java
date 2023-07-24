package com.raju;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

public interface TransformationResult {
    boolean isSuccess();

    JsonNode getTransformedJson();
}
